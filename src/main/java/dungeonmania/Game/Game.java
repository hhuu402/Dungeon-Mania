package dungeonmania.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import dungeonmania.Entities.Items.Bomb;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Entities.Items.Treasure;
import dungeonmania.Entities.StaticEntities.Boulder;
import dungeonmania.Entities.StaticEntities.Exit;
import dungeonmania.Entities.StaticEntities.FloorSwitch;
import dungeonmania.Entities.StaticEntities.Portal;
import dungeonmania.Entities.StaticEntities.StaticEntities;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawner;
import dungeonmania.Goal.Goal;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game {

    private int tickCount;
    private int width;
    private int height;

    private String dungeonName;
    private GameModes gameMode;

    private Position start;
    private Player player;
    private Goal goal;

    private List<Entity> entities = new ArrayList<>();
    private List<Swamp> swamps = new ArrayList<>();

    public Game(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void addSwamp(Swamp swamp) {
        swamps.add(swamp);
    }

    public void removeSwamp(Swamp swamp) {
        swamps.remove(swamp);
    }

    public void updateEnemies() {
        tickCount++;
        updateSwamps();
        moveEnemies();
        spawnEnemies();
    }

    public void updateGoal() {
        goal.updateStatus(this);
    }

    public void movePlayer(Direction moveDirection) {
        List <Entity> entitiesAtNewPosition = entitiesAtPosition(player.getPosition().translateBy(moveDirection));

        if (entitiesAtNewPosition.isEmpty() ||
            (boulderCheck(entitiesAtNewPosition, moveDirection) && 
            entitiesAtNewPosition.stream().allMatch(e -> e.action(player)))) {

            player.playerMove(moveDirection);
            entitiesAtPosition(player.getPosition()).stream().forEach(e -> removeEntity(e));
        }

    }

    public void useItem(String itemUsed) throws IllegalArgumentException, InvalidActionException {
        Inventory inventory = player.getInventory();
        Items item = inventory.findItemById(itemUsed);

        if (item instanceof Bomb) {
            item.setPosition(player.getPosition());
            addEntity(item);
            explodeBomb(item);
        }

        player.isClicked(itemUsed);

    }

    public void gameInteract(String entityId) throws IllegalArgumentException, InvalidActionException {
        List<String> entitiesId = entities.stream().map(Entity::getId).collect(Collectors.toList());

        if (!entitiesId.contains(entityId)) {
            throw new IllegalArgumentException("entity not in game");
        }

        // interact with
        List<Entity> entitiesWithId = entities.stream().filter(e -> e.getId().equals(entityId)).collect(Collectors.toList());
        
        for (Entity entity: entitiesWithId) {
            if (entity instanceof Mercenary) {
                Mercenary mercenary = (Mercenary) entity;
                if (!mercenary.interact(player)) {
                    throw new InvalidActionException(entityId);
                }

            } else if (entity instanceof ZombieToastSpawner) {
                ZombieToastSpawner zombieToastSpawner = (ZombieToastSpawner) entity;
                if (zombieToastSpawner.interact(player)) {
                    removeEntity(entity);
                } else {
                    throw new InvalidActionException(entityId);
                }
            }
        }
    }

    public void gameBuild(String buildable) throws InvalidActionException {
        if (buildable.equals("midnight_armour") && !getZombieToasts().isEmpty()) {
            throw new InvalidActionException(buildable);
        }
        player.craftItem(buildable);
    }

    public void updateSwitches() {
        List<Position> bouldersPosition = getBoulders().stream().map(Boulder::getPosition).collect(Collectors.toList());
        
        List<FloorSwitch> floorSwitchs = getFloorSwitches();
        for (FloorSwitch fs: floorSwitchs) {
            if (bouldersPosition.contains(fs.getPosition())) {
                fs.setIsActivated(true);
            } else {
                fs.setIsActivated(false);
            }
        }
    }

    public List<Entity> getAllEntities() {
        List<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(entities);
        allEntities.addAll(swamps);
        allEntities.addAll(getEnemiesInSwamp());
        return allEntities;
    }

    public List<Entity> entitiesAtPosition(Position position) {
        return getAllEntities().stream().filter(e -> e.getPosition().equals(position)).collect(Collectors.toList());
    }

    public List<Enemy> getEnemies() {
        return getAllEntities().stream().filter(Enemy.class::isInstance).map(Enemy.class::cast).collect(Collectors.toList());
    }

    public List<Enemy> getEnemiesNotInSwamp() {
        return entities.stream().filter(Enemy.class::isInstance).map(Enemy.class::cast).collect(Collectors.toList());
    }

    public List<Entity> getStaticEntities() {
        return entities.stream().filter(StaticEntities.class::isInstance).collect(Collectors.toList());
    }

    public List<Treasure> getTreasures() {
        return entities.stream().filter(Treasure.class::isInstance).map(Treasure.class::cast).collect(Collectors.toList());
    }

    public List<FloorSwitch> getFloorSwitches() {
        return entities.stream().filter(FloorSwitch.class::isInstance).map(FloorSwitch.class::cast).collect(Collectors.toList());
    }

    public List<Boulder> getBoulders() {
        return entities.stream().filter(Boulder.class::isInstance).map(Boulder.class::cast).collect(Collectors.toList());
    }

    public List<Bomb> getPlacedBombs() {
        return entities.stream().filter(Bomb.class::isInstance).map(Bomb.class::cast).filter(Bomb::getIsUsed).collect(Collectors.toList());
    }

    public List<Portal> getPortals() {
        return entities.stream().filter(Portal.class::isInstance).map(Portal.class::cast).collect(Collectors.toList());
    }

    public List<ZombieToastSpawner> getZombieToastSpawners() {
        return entities.stream().filter(ZombieToastSpawner.class::isInstance).map(ZombieToastSpawner.class::cast).collect(Collectors.toList());
    }
    
    public List<ZombieToast> getZombieToasts() {
        return getAllEntities().stream().filter(ZombieToast.class::isInstance).map(ZombieToast.class::cast).collect(Collectors.toList());
    }
    
    public List<Spider> getSpiders() {
        return getAllEntities().stream().filter(Spider.class::isInstance).map(Spider.class::cast).collect(Collectors.toList());
    }

    public List<Mercenary> getMercenaries() {
        return getAllEntities().stream().filter(Mercenary.class::isInstance).map(Mercenary.class::cast).collect(Collectors.toList());
    }

    public List<Enemy> getEnemiesInSwamp() {
        List<Enemy> enemies= new ArrayList<>();
        for (Swamp swamp: getSwamps()) {
            enemies.addAll(swamp.getStuckEnemyMap().values());
        }
        return enemies;
    }

    public boolean atExit() {
        return entitiesAtPosition(player.getPosition()).stream().anyMatch(Exit.class::isInstance);
    }

    //------------------------------------------------------------------------//
    // Helper functions

    private void updateSwamps() {
        for (Swamp swamp: getSwamps()) {
            swamp.updateStuckEnemy();
            swamp.unStuckEnemyList().forEach(e -> addEntity(e));
        }
    }

    private void moveEnemies() {
        // Move enemies
        List<Enemy> enemies = getEnemiesNotInSwamp();
        List<Position> bouldersPosition = getBoulders().stream().map(Boulder::getPosition).collect(Collectors.toList());
        
        for (Enemy enemy: enemies) {
            ArrayList <Position> newEnemyPosition = enemy.enemyMove(player, getStaticEntities());

            for(Position pos: newEnemyPosition) {
                if (enemy instanceof Spider) {
                    if (bouldersPosition.contains(pos)) {
                        enemy.enemyMoveOpposite(player);
                    }
                    enemy.setPosition(pos);
                    swampCheck(enemy);
                } else if (entitiesAtPosition(pos).stream().allMatch(e -> canMoveToPosition(e))) {
                    enemy.setPosition(pos);
                    swampCheck(enemy);
                    break;
                }
            }

            // If enemy position is equal to player position
            if (enemy.getPosition().equals(player.getPosition())) {
                if (enemy.action(player)) entities.remove(enemy);
            }

        }
    }

    private void spawnEnemies() {
        // spawn new enemies if needed
        List<String> spawnableEnemies = gameMode.canSpawn(tickCount);

        for (String enemy: spawnableEnemies) {
            switch(enemy) {
                case "zombie_toast":
                    spawnZombieToast();
                    break;
                case "spider":
                    spawnSpider();
                    break;
                case "mercenary":
                    spawnMercenary();
                    break;
                case "assassin":
                    spawnAssassin();
                    break;
                case "hydra":
                    spawnHydra();
                    break;
            }
        }

    }

    private void spawnZombieToast() {
        for (ZombieToastSpawner spawner: getZombieToastSpawners()) {
            Position newZombiePosition = spawner.spawnPosition();
            if (entitiesAtPosition(newZombiePosition).isEmpty()) {
                ZombieToast zombie = new ZombieToast("zombie_toast", newZombiePosition, gameMode);
                addEntity(zombie);
            }
        }
    }

    private void spawnSpider() {
        // // Spider
        if (getSpiders().size() < 4) {
            Position newSpiderPosition = Spider.spawnPosition(width, height);
            Spider spider = new Spider("spider", newSpiderPosition, gameMode);
            addEntity(spider);
        }
    }

    private void spawnMercenary() {
        Position newMercenaryPosition = start;
        if (entitiesAtPosition(newMercenaryPosition).stream().allMatch(e -> canMoveToPosition(e))) {
            Mercenary mercenary = new Mercenary("mercenary", newMercenaryPosition, gameMode);
            addEntity(mercenary);
        }
    }

    private void spawnAssassin() {
        Position newAssassinPosition = start;
        if (entitiesAtPosition(newAssassinPosition).stream().allMatch(e -> canMoveToPosition(e))) {
            Assassin assassin = new Assassin("mercenary", newAssassinPosition, gameMode);
            addEntity(assassin);
        }
    }

    private void spawnHydra() {
        Position newHydraPosition = start;
        if (entitiesAtPosition(newHydraPosition).stream().allMatch(e -> canMoveToPosition(e))) {
            Hydra hydra = new Hydra("mercenary", newHydraPosition, gameMode);
            addEntity(hydra);
        }
    }

    private boolean canMoveToPosition(Entity entity) {

        if (entity instanceof StaticEntities ||
            entity instanceof Enemy) {
            return false;
        }

        return true;
    }

    // Swamp Check
    private void swampCheck(Enemy enemy) {
        List<Swamp> swamps = getSwamps();
        for (Swamp swamp: swamps) {
            if (swamp.getPosition().equals(enemy.getPosition())) {
                swamp.addStuckEnemy(enemy);
                removeEntity(enemy);
                break;
            }
        }
    }

    // Boulder Check
    private boolean boulderCheck(List<Entity> curEntities, Direction moveDirection) {
        for (Entity curEntity: curEntities) {
            if (!(curEntity instanceof Boulder)) continue;
            
            Position newBoulderPosition = curEntity.getPosition().translateBy(moveDirection);

            List<Entity> entitiesAtBoulderPosition = entitiesAtPosition(newBoulderPosition);
            if (entitiesAtBoulderPosition.stream().anyMatch(e -> !canMoveToPosition(e))) {
                return false;
            }
            
            curEntity.setPosition(newBoulderPosition);
            updateSwitches();
            getPlacedBombs().forEach(bomb -> explodeBomb(bomb));
        }
        return true;
    }

    // Bomb 
    private void explodeBomb(Items item) {
        Bomb bomb = (Bomb) item;
        List<FloorSwitch> floorSwitches = getFloorSwitches();
        List<Position> blastZone = bomb.getBlastZone();

        for (FloorSwitch floorSwitch: floorSwitches) {
            if (!floorSwitch.getIsActivated()) continue;
            if (blastZone.contains(floorSwitch.getPosition())) {
                blastZone.stream().forEach(pos -> removeEntitiesAtPosition(pos));
            } 
        }
    }

    private void removeEntitiesAtPosition(Position position) {
        List<Entity> removed = entitiesAtPosition(position);
        removed.stream().forEach(e -> removeEntity(e));
    }

    //------------------------------------------------------------------------//
    // Getters and setters
    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public GameModes getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameModes gameMode) {
        this.gameMode = gameMode;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Swamp> getSwamps() {
        return swamps;
    }

    public void setSwamps(List<Swamp> swamps) {
        this.swamps = swamps;
    }


}
