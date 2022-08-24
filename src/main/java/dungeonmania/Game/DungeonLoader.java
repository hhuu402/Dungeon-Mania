package dungeonmania.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Entities.Items.Anduril;
import dungeonmania.Entities.Items.Armour;
import dungeonmania.Entities.Items.Arrows;
import dungeonmania.Entities.Items.Bomb;
import dungeonmania.Entities.Items.Bow;
import dungeonmania.Entities.Items.HealthPotion;
import dungeonmania.Entities.Items.InvincibilityPotion;
import dungeonmania.Entities.Items.InvisibilityPotion;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Entities.Items.Key;
import dungeonmania.Entities.Items.MidnightArmour;
import dungeonmania.Entities.Items.Sceptre;
import dungeonmania.Entities.Items.Shield;
import dungeonmania.Entities.Items.SunStone;
import dungeonmania.Entities.Items.Sword;
import dungeonmania.Entities.Items.Treasure;
import dungeonmania.Entities.Items.Wood;
import dungeonmania.Entities.Items.theOneRing;
import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Swamp;
import dungeonmania.Entities.Enemies.Assassin;
import dungeonmania.Entities.Enemies.Enemy;
import dungeonmania.Entities.Enemies.Hydra;
import dungeonmania.Entities.Enemies.Mercenary;
import dungeonmania.Entities.Enemies.Spider;
import dungeonmania.Entities.Enemies.ZombieToast;
import dungeonmania.Entities.StaticEntities.Boulder;
import dungeonmania.Entities.StaticEntities.Door;
import dungeonmania.Entities.StaticEntities.Exit;
import dungeonmania.Entities.StaticEntities.FloorSwitch;
import dungeonmania.Entities.StaticEntities.Portal;
import dungeonmania.Entities.StaticEntities.Wall;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawner;
import dungeonmania.Goal.AndGoal;
import dungeonmania.Goal.BoulderGoal;
import dungeonmania.Goal.EnemiesGoal;
import dungeonmania.Goal.ExitGoal;
import dungeonmania.Goal.Goal;
import dungeonmania.Goal.OrGoal;
import dungeonmania.Goal.TreasureGoal;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

/**
 * Loads a dungeon from a .json file.
 *
 * 
**/

public class DungeonLoader {
    
    private String dungeonName;
    private String gameMode;
    private JSONObject map;

    public DungeonLoader(String dungeon, String gameMode) throws IOException {
        map = new JSONObject(FileLoader.loadResourceFile("/dungeons/" + dungeon + ".json"));
        this.dungeonName = dungeon;
        this.gameMode = gameMode;
    }

    public DungeonLoader(String game) throws IOException {
        map = new JSONObject(FileLoader.loadResourceFile("/games/" + game + ".json"));
        this.dungeonName = map.getString("dungeonName");
        this.gameMode = map.getJSONObject("gameMode").getString("type");
    }

    public Game loadGame() throws IllegalArgumentException {
        // Generate base game
        Game game = new Game(dungeonName);
        int width = map.getInt("width");
        int height = map.getInt("height");

        // Load tickCount
        if (map.has("tickCount")) game.setTickCount(map.getInt("tickCount"));

        // Load GameMode
        if (gameMode != null) {
            game.setGameMode(loadGameMode(gameMode, width, height));
            game.setWidth(width);
            game.setHeight(height);
        }
    
        // Load player
        if (map.has("player")) game.setPlayer(loadPlayer(map.getJSONObject("player"), game.getGameMode()));

        // Load Entities
        JSONArray entitiesJSON = map.getJSONArray("entities");
        for (int i = 0; i < entitiesJSON.length(); i++) {
            JSONObject entityJSON = entitiesJSON.getJSONObject(i);
            String type = entityJSON.getString("type");
            if (type.equals("player")) {
                game.setPlayer(loadPlayer(entityJSON, game.getGameMode()));
                continue;
            } 

            if (type.equals("swamp_tile")) {
                game.addSwamp(loadSwamp(entityJSON, game.getGameMode()));
                continue;
            }
        
            game.addEntity(loadEntity(entityJSON, game.getGameMode()));
        }
        handlePortals(game);

        // Load swamps
        if (map.has("swamps")) {
            JSONArray swampsJSON = map.getJSONArray("swamps");
            for (int i = 0; i < swampsJSON.length(); i++) {
                JSONObject swampJSON = swampsJSON.getJSONObject(i);
                game.addSwamp(loadSwamp(swampJSON, game.getGameMode()));
            }
        }

        // Load start
        if (map.has("start")) {
            game.setStart(loadPosition(map.getJSONObject("start")));
        } else {
            game.setStart(game.getPlayer().getPosition());
        }

        // Load Goals
        if (map.has("goal-condition")) {
            game.setGoal(loadGoal(game, map.getJSONObject("goal-condition")));
        } else if (map.has("goal")) {
            game.setGoal(loadGoal(game, map.getJSONObject("goal")));
        } else {
            Goal goal = new Goal("");
            goal.setCompletionStatus(true);
            game.setGoal(goal);
            
        }
        game.updateGoal();
        
        return game;
    }

    private Swamp loadSwamp(JSONObject swampJSON, GameModes gameMode) {
        assert(swampJSON.getString("type").equals("swamp_tile"));
        Position position= loadPositionFromEntity(swampJSON);
        int movement_factor = swampJSON.getInt("movement_factor");
        
        Swamp swamp = new Swamp("swamp_tile", position, movement_factor);
        
        // Load stuckEnemyMap
        if (swampJSON.has("stuckEnemyMap")) {
            JSONObject stuckEnemyMap = swampJSON.getJSONObject("stuckEnemyMap");
            Map<String, Enemy> stuckEnemy = new HashMap<>();
            stuckEnemyMap.keySet().forEach(keyStr -> stuckEnemy.put(keyStr, (Enemy) loadEntity(stuckEnemyMap.getJSONObject(keyStr), gameMode)));
            swamp.setStuckEnemyMap(stuckEnemy);
        }
        
        // Load stuckEnemyMap
        if (swampJSON.has("stuckEnemyDurationMap")) {
            JSONObject stuckEnemyDurationMap = swampJSON.getJSONObject("stuckEnemyMap");
            Map<String, Integer> stuckEnemyDuration = new HashMap<>();
            stuckEnemyDurationMap.keySet().forEach(keyStr -> stuckEnemyDuration.put(keyStr, stuckEnemyDurationMap.getInt(keyStr)));
            swamp.setStuckEnemyDurationMap(stuckEnemyDuration);
        }

        // Load unstuckEnemyMap
        if (swampJSON.has("unstuckEnemyMap")) {
            JSONObject unstuckEnemyMap = swampJSON.getJSONObject("unstuckEnemyMap");
            Map<String, Enemy> unstuckEnemy = new HashMap<>();
            unstuckEnemyMap.keySet().forEach(keyStr -> unstuckEnemy.put(keyStr, (Enemy) loadEntity(unstuckEnemyMap.getJSONObject(keyStr), gameMode)));
            swamp.setUnstuckEnemyMap(unstuckEnemy);
        }

        return swamp;
    }

    private Position loadPositionFromEntity(JSONObject entityJSON) {
        Position position;
        if (entityJSON.has("position")) {
            position = loadPosition(entityJSON.getJSONObject("position"));
        } else {
            position = new Position(entityJSON.getInt("x"), entityJSON.getInt("y"));
        }
        return position;
    }

    private Position loadPosition(JSONObject positionJSON) {
        return new Position(positionJSON.getInt("x"), positionJSON.getInt("y"), positionJSON.getInt("layer"));
    }

    private Player loadPlayer(JSONObject playerJSON, GameModes gameMode) {
        assert(playerJSON.getString("type").equals("player"));
        Position position= loadPositionFromEntity(playerJSON);
        position = position.asLayer(2);

        Player player = new Player("player", position, gameMode);
        // Load health, attack if it is there
        if (playerJSON.has("health")) {
            player.setHealth(playerJSON.getInt("health"));
        }

        if (playerJSON.has("attack")) {
            player.setAttack(playerJSON.getInt("attack"));
        }

        // Load effectList
        if (playerJSON.has("effectList")) {
            JSONObject effectMap = playerJSON.getJSONObject("effectList");
            Map<String, Integer> effectList = new HashMap<>();
            effectMap.keySet().forEach(keyStr -> effectList.put(keyStr, effectMap.getInt(keyStr)));
            player.setEffectList(effectList);
        }

        // Load inventory
        if (playerJSON.has("inventoryList")) {
            loadInventory(player, playerJSON.getJSONArray("inventoryList"));
        }

        // Load buildables
        if (playerJSON.has("buildableList")) {
            JSONArray buildable = playerJSON.getJSONArray("buildableList");
    
            List<String> buildableList = new ArrayList<>();
            for (int i = 0; i < buildable.length(); i++) {
                buildableList.add(buildable.getString(i));
            }

            player.setBuildableList(buildableList);
        }

        return player;
    }

    private Entity loadEntity(JSONObject entityJSON, GameModes gameMode) {
        String type = entityJSON.getString("type");
        Position position= loadPositionFromEntity(entityJSON);

        switch(type) {
            // Items
            case "anduril":
                Anduril anduril = new Anduril(type, position);
                if (entityJSON.has("pickedUp")) {
                    anduril.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    anduril.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    anduril.setDurability(entityJSON.getInt("durability"));
                }
                return anduril;
            
            case "armour":
                Armour armour = new Armour(type, position);
                if (entityJSON.has("pickedUp")) {
                    armour.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    armour.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    armour.setDurability(entityJSON.getInt("durability"));
                }
                return armour;
            case "arrow":
                Arrows arrow = new Arrows(type, position);
                if (entityJSON.has("pickedUp")) {
                    arrow.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    arrow.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return arrow;
            case "bomb":
                Bomb bomb = new Bomb(type, position);
                if (entityJSON.has("pickedUp")) {
                    bomb.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    bomb.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return bomb;
            case "bow":
                Bow bow = new Bow(type, position);
                if (entityJSON.has("pickedUp")) {
                    bow.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    bow.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    bow.setDurability(entityJSON.getInt("durability"));
                }
                return bow;
            case "health_potion":
                HealthPotion healthPotion = new HealthPotion(type, position);
                if (entityJSON.has("pickedUp")) {
                    healthPotion.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    healthPotion.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return healthPotion;
            case "invincibility_potion":
                InvincibilityPotion invincibilityPotion = new InvincibilityPotion(type, position);
                if (entityJSON.has("pickedUp")) {
                    invincibilityPotion.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    invincibilityPotion.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return invincibilityPotion;
            case "invisibility_potion":
                InvisibilityPotion invisibility = new InvisibilityPotion(type, position);
                if (entityJSON.has("pickedUp")) {
                    invisibility.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    invisibility.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return invisibility;
            case "key":
                int uniqueKey;
                if (entityJSON.has("key")) {
                    uniqueKey = entityJSON.getInt("key");
                } else {
                    uniqueKey = entityJSON.getInt("uniqueKey");
                }
                
                Key key = new Key(type, position, uniqueKey);
                if (entityJSON.has("pickedUp")) {
                    key.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    key.setPickedUp(entityJSON.getBoolean("isUsed"));
                }
                return key;

            case "midnight_armour":
                MidnightArmour midnightArmour = new MidnightArmour(type, position);
                
                if (entityJSON.has("pickedUp")) {
                    midnightArmour.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    midnightArmour.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    midnightArmour.setDurability(entityJSON.getInt("durability"));
                }
                return midnightArmour;
            case "sceptre":
                Sceptre sceptre = new Sceptre(type, position);
                if (entityJSON.has("pickedUp")) {
                    sceptre.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    sceptre.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return sceptre;
            case "shield":
                Shield shield = new Shield(type, position);
                if (entityJSON.has("pickedUp")) {
                    shield.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    shield.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    shield.setDurability(entityJSON.getInt("durability"));
                }
                return shield;
            case "sun_stone":
                SunStone sunStone = new SunStone(type, position);
                if (entityJSON.has("pickedUp")) {
                    sunStone.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    sunStone.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return sunStone;
            case "sword":
                Sword sword = new Sword(type, position);
                if (entityJSON.has("pickedUp")) {
                    sword.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    sword.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                if (entityJSON.has("durability")) {
                    sword.setDurability(entityJSON.getInt("durability"));
                }
                return sword;
            case "one_ring":
                theOneRing ring = new theOneRing(type, position);
                if (entityJSON.has("pickedUp")) {
                    ring.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    ring.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return ring;
            case "treasure":
                Treasure treasure = new Treasure(type, position);
                if (entityJSON.has("pickedUp")) {
                    treasure.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    treasure.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return treasure;
            case "wood":
                Wood wood = new Wood(type, position);
                if (entityJSON.has("pickedUp")) {
                    wood.setPickedUp(entityJSON.getBoolean("pickedUp"));
                }
                if (entityJSON.has("isUsed")) {
                    wood.setIsUsed(entityJSON.getBoolean("isUsed"));
                }
                return wood;

            // Moving entities/ Enemies
            case "mercenary":
                position = position.asLayer(2);
                Mercenary mercenary = new Mercenary(type, position, gameMode);
                // Load health, attackDamage if it is there
                if (entityJSON.has("health")) {
                    mercenary.setHealth(entityJSON.getInt("health"));
                }

                if (entityJSON.has("attackDamage")) {
                    mercenary.setAttackDamage(entityJSON.getInt("attackDamage"));
                }

                if (entityJSON.has("bribe")) {
                    mercenary.setBribed(entityJSON.getBoolean("bribe"));
                }

                return mercenary;
            case "spider":
                position = position.asLayer(2);
                Spider spider = new Spider(type, position, gameMode);

                // Load health, attackDamage if it is there
                if (entityJSON.has("health")) {
                    spider.setHealth(entityJSON.getInt("health"));
                }
                if (entityJSON.has("square")) {
                    spider.setSquare(entityJSON.getInt("square"));
                }
                if (entityJSON.has("attackDamage")) {
                    spider.setAttackDamage(entityJSON.getInt("attackDamage"));
                }

                return spider;
            case "zombie_toast":
                position = position.asLayer(2);
                ZombieToast zombieToast = new ZombieToast(type, position, gameMode);

                // Load health, attackDamage if it is there
                if (entityJSON.has("health")) {
                    zombieToast.setHealth(entityJSON.getInt("health"));
                }

                if (entityJSON.has("attackDamage")) {
                    zombieToast.setAttackDamage(entityJSON.getInt("attackDamage"));
                }

                return zombieToast;
            case "hydra":
                position = position.asLayer(2);
                Hydra hydra = new Hydra(type, position, gameMode);

                // Load health, attackDamage if it is there
                if (entityJSON.has("health")) {
                    hydra.setHealth(entityJSON.getInt("health"));
                }

                if (entityJSON.has("attackDamage")) {
                    hydra.setAttackDamage(entityJSON.getInt("attackDamage"));
                }

                return hydra;

            case "assassin":
                position = position.asLayer(2);
                Assassin assassin = new Assassin(type, position, gameMode);
                // Load health, attackDamage if it is there
                if (entityJSON.has("health")) {
                    assassin.setHealth(entityJSON.getInt("health"));
                }

                if (entityJSON.has("attackDamage")) {
                    assassin.setAttackDamage(entityJSON.getInt("attackDamage"));
                }

                if (entityJSON.has("bribe")) {
                    assassin.setBribed(entityJSON.getBoolean("bribe"));
                }

                return assassin;


            // Static entities
            case "boulder":
                position = position.asLayer(1);
                Boulder boulder = new Boulder(type, position);
                return boulder;
            case "door":
                int keyUnique;
                if (entityJSON.has("key")) {
                    keyUnique = entityJSON.getInt("key");
                } else {
                    keyUnique = entityJSON.getInt("uniqueKey");
                }
                Door door = new Door(type, position, keyUnique);
                return door;
            case "exit":
                Exit exit = new Exit(type, position);
                return exit;
            case "switch":
                FloorSwitch floorSwitch = new FloorSwitch(type, position);
                if (entityJSON.has("isActivated")) {
                    floorSwitch.setIsActivated(entityJSON.getBoolean("isActivated"));
                }
                return floorSwitch;
            case "portal":
                Portal portal = new Portal(type, position);
                if (entityJSON.has("otherPortal")) {
                    JSONObject otherPortalJSON = entityJSON.getJSONObject("otherPortal");
                    Position otherPortal = new Position(otherPortalJSON.getInt("x"), otherPortalJSON.getInt("y"));
                    portal.setOtherPortal(otherPortal);
                }
                return portal;
            case "wall":
                Wall wall = new Wall(type, position);
                return wall;
            case "zombie_toast_spawner":
                ZombieToastSpawner zombieToastSpawner = new ZombieToastSpawner(type, position);
                return zombieToastSpawner;
        }

        return null;
    }

    private Goal loadGoal(Game game, JSONObject goalCondition) {
        String type = goalCondition.getString("goal");

        switch(type) {
            case "AND":
                AndGoal andGoal = new AndGoal(type);

                // add subgoals
                JSONArray andSubgoalsJSON = goalCondition.getJSONArray("subgoals");
                for (int i = 0; i < andSubgoalsJSON.length(); i++) {
                    andGoal.addSubgoal(loadGoal(game, andSubgoalsJSON.getJSONObject(i)));
                }
                
                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    andGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    andGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return andGoal;

            case "OR":
                OrGoal orGoal = new OrGoal(type);

                // add subgoals
                JSONArray orSubgoalsJSON = goalCondition.getJSONArray("subgoals");
                for (int i = 0; i < orSubgoalsJSON.length(); i++) {
                    orGoal.addSubgoal(loadGoal(game, orSubgoalsJSON.getJSONObject(i)));
                }

                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    orGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    orGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return orGoal;

            case "boulders":
                BoulderGoal boulderGoal = new BoulderGoal(type);

                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    boulderGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    boulderGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return boulderGoal;

            case "enemies":
                EnemiesGoal enemiesGoal = new EnemiesGoal(type);
                
                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    enemiesGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    enemiesGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return enemiesGoal;

            case "exit":
                ExitGoal exitGoal = new ExitGoal(type);
                
                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    exitGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    exitGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return exitGoal;
            
            case "treasure":
                TreasureGoal treasureGoal = new TreasureGoal(type);

                // Load completionStatus
                if (goalCondition.has("completionStatus")) {
                    treasureGoal.setCompletionStatus(goalCondition.getBoolean("completionStatus"));
                }

                // Load isSubgoal
                if (goalCondition.has("isSubgoal")) {
                    treasureGoal.setCompletionStatus(goalCondition.getBoolean("isSubgoal"));
                }

                return treasureGoal;
        }

        return new Goal("");
    }

    private GameModes loadGameMode(String gameMode, int width, int height) throws IllegalArgumentException {
        switch(gameMode) {
            case "standard":
                return new StandardMode(gameMode, width, height);
            case "peaceful":
                return new PeacefulMode(gameMode, width, height);
            case "hard":
                return new HardMode(gameMode, width, height);
        }
        throw new IllegalArgumentException("Invalid Game Mode");
    }

    // load inventory
    private void loadInventory(Player player, JSONArray inventory) {
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < inventory.length(); i++) {
            Items item = (Items) loadEntity(inventory.getJSONObject(i), player.getGameMode());
            playerInventory.addItem(item);
        }
    }

    // handle portals
    private void handlePortals(Game game) {
        List<Portal> portals = game.getPortals();
        List<Position> portalPositions = portals.stream().map(Portal::getPosition).collect(Collectors.toList());
        
        for (Portal portal: portals) {
            for (Position position: portalPositions) {
                if (portal.getPosition().equals(position)) continue;
                portal.setOtherPortal(position);
            }
        }
    }
}
