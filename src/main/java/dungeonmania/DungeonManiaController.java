package dungeonmania;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Entities.Items.Key;
import dungeonmania.Entities.StaticEntities.Door;
import dungeonmania.Game.DungeonLoader;
import dungeonmania.Game.Game;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    private Game curGame;

    public DungeonManiaController() {
    }

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public List<String> getGameModes() {
       return Arrays.asList("standard", "peaceful", "hard");
    }

    /**
     * /dungeons
     * 
     * Done for you.
     */
    public static List<String> dungeons() {
        try {
            return FileLoader.listFileNamesInResourceDirectory("/dungeons");
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String gameMode) throws IllegalArgumentException {
        
        return null;
    }

    public DungeonResponse newGame(String dungeonName, String gameMode) throws IllegalArgumentException {
        try {
            DungeonLoader dungeon = new DungeonLoader(dungeonName, gameMode);
            curGame = dungeon.loadGame();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error: Invalid gamemode/dungeon");
        }
        
        return gameResponse(curGame);
    }
    
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        
        try {
            Gson gameGson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter("src/main/resources/games/" + name + ".json");
            gameGson.toJson(curGame, writer);
            writer.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error: Cannot save game");
        }
        
        return gameResponse(curGame);
    }

    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        
        try {
            DungeonLoader game = new DungeonLoader(name);
            curGame = game.loadGame();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error: Cannot load game");
        }
        
        return gameResponse(curGame);
    }

    public List<String> allGames() {
        try {
            List<String> previousGames = FileLoader.listFileNamesInResourceDirectory("/games");
            return previousGames;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public DungeonResponse tick(String itemUsed, Direction movementDirection) throws IllegalArgumentException, InvalidActionException {

        // itemUsed given
        // So we use an item
        if (movementDirection.equals(Direction.NONE)) {
            curGame.useItem(itemUsed);
        } 
        // MovementDirection given 
        // So we move a player
        else {
            curGame.movePlayer(movementDirection);
        }

        // Update enemies i.e move/spawn enemies
        curGame.updateEnemies();

        // Update goals
        curGame.updateGoal();

        return gameResponse(curGame);
    }

    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        curGame.gameInteract(entityId);
        return gameResponse(curGame);
    }

    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> possibleBuildables = new ArrayList<String>() {{
            add("bow");
            add("shield");
            add("sceptre");
            add("midnight_armour");
        }};
        
        if (!possibleBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Not a buildable item type");
        }
        curGame.gameBuild(buildable);


        return gameResponse(curGame);
    }

    
    /**
     * Helper function that converts game into a DungeonReponse
     * @param game
     * @return
     */
    public DungeonResponse gameResponse(Game game) {

        List<EntityResponse> entities = new ArrayList<>();
        
        // Load player
        Player player = game.getPlayer();
        entities.add(new EntityResponse("player",
                                        player.getType(), 
                                        player.getPosition(), 
                                        false));
        
        // Load entities
        Integer entityCount = 0;
        for (Entity curEntity: game.getAllEntities()) {
            String entityId = "entity-" + Integer.toString(entityCount);
            entities.add(new EntityResponse(entityId,
                                            getType(curEntity),
                                            curEntity.getPosition(),
                                            curEntity.isInteractable()));
            curEntity.setId(entityId);
            entityCount++;
            
        }
        
        // Load inventory
        List<ItemResponse> inventory = new ArrayList<>();
        Integer itemCount = 0;
        Inventory playerInventory = player.getInventory();
        for (Items item: playerInventory.getInventoryList()) {
            String itemId = "item-" + Integer.toString(itemCount);
            inventory.add(new ItemResponse(itemId,
                                            getType(item)));
            item.setId(itemId);
            itemCount++;
        }
        
        // Load buildables
        List<String> buildables = player.getBuildableList();

        // Load goals
        String goals = "";
        if (curGame.getGoal() != null) {
            goals = curGame.getGoal().toString();
        }
        
        return new DungeonResponse("some-random-id", game.getDungeonName(), entities, inventory, buildables, goals);
    }

    // Deal with different door types
    private String getType(Entity entity) {
        String type = entity.getType();
        if (type.equals("door")) {
            Door door = (Door) entity;
            if (door.isOpened()) {
                type = "door_unlocked";
            } else {
                type += Integer.toString(door.getUniqueKey());
            }
        }

        if (type.equals("key")) {
            Key key = (Key) entity;
            type += Integer.toString(key.getUniqueKey());
        }

        return type;
    }

}
