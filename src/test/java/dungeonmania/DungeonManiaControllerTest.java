package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;


public class DungeonManiaControllerTest {

    @Test
    public void testNewGameValid() {
        DungeonManiaController controller = new DungeonManiaController();

        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();
        
        for (String mode: gameModes) {
            assertDoesNotThrow(() -> dungeons.forEach(d -> controller.newGame(d, mode)));
        }

    }

    @Test
    public void testNewGameInvalid() {
        DungeonManiaController controller = new DungeonManiaController();
        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();
        String dungeon = "randomdungeon";
        String mode = "randomgamemode";
        
        // Invalid dungeon
        assertFalse(gameModes.isEmpty());
        assertFalse(dungeons.contains(dungeon));
        assertThrows(IllegalArgumentException.class, () -> controller.newGame(dungeon, gameModes.get(0)));
        
        // Invalid gameMode
        assertFalse(dungeons.isEmpty());
        assertFalse(gameModes.contains(mode));
        assertThrows(IllegalArgumentException.class, () -> controller.newGame(dungeons.get(0), mode));
    }

    @Test
    public void testSaveAndLoadGameValid() {
        DungeonManiaController controller = new DungeonManiaController();
        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();
        String gameName = "savedGame";

        assertDoesNotThrow(() -> controller.newGame(dungeons.get(0), gameModes.get(0)));
        assertDoesNotThrow(() -> controller.saveGame(gameName));
        assertDoesNotThrow(() -> controller.loadGame(gameName));
        
    }

    @Test
    public void testLoadGameInvalid() {
        DungeonManiaController controller = new DungeonManiaController();
        
        String gameName = "randomGame";
        assertFalse(controller.allGames().contains(gameName));
        assertThrows(IllegalArgumentException.class, () -> controller.loadGame(gameName));

    }    
    

    @Test
    public void testTickMove() {
        DungeonManiaController controller = new DungeonManiaController();
        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();


        for (String mode: gameModes) {
            assertDoesNotThrow(() -> dungeons.forEach(d -> {
                controller.newGame(d, mode);
                controller.tick("", Direction.DOWN);
                controller.tick("", Direction.UP);
                controller.tick("", Direction.LEFT);
                controller.tick("", Direction.RIGHT);
                
            }
                ));
        }
    }

    @Test
    public void testTickUseItemInvalid() {
        DungeonManiaController controller = new DungeonManiaController();
        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();

        assertDoesNotThrow(() -> controller.newGame(dungeons.get(0), gameModes.get(0)));
        assertThrows(InvalidActionException.class, () -> controller.tick("some-random-item", Direction.NONE));
    }

    @Test 
    public void testInteract() {

    }

    @Test
    public void testBuildInvalid() {
        DungeonManiaController controller = new DungeonManiaController();
        List<String> dungeons = DungeonManiaController.dungeons();
        List<String> gameModes = controller.getGameModes();

        assertDoesNotThrow(() -> controller.newGame(dungeons.get(0), gameModes.get(0)));
        assertThrows(IllegalArgumentException.class, () -> controller.build("key"));
        assertThrows(InvalidActionException.class, () -> controller.build("bow"));
        assertThrows(InvalidActionException.class, () -> controller.build("shield"));
    }


    @Test
    public void testDungeons() {
        assertTrue(DungeonManiaController.dungeons().size() > 0);
        assertTrue(DungeonManiaController.dungeons().contains("maze"));
    }
}