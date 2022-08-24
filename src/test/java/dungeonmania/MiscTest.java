package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.*;
import dungeonmania.Entities.Items.*;
import dungeonmania.Entities.StaticEntities.Door;
import dungeonmania.Game.*;
import dungeonmania.util.*;

public class MiscTest {
    @Test
    public void testDungeons() {
        assertTrue(DungeonManiaController.dungeons().size() > 0);
        assertTrue(DungeonManiaController.dungeons().contains("maze"));
    }

    // test for swamp with movement factor 2 -> two ticks required before player can leave swamp
    // @Test
    // public void testSwamp() {
    //     GameModes standardMode = new StandardMode("StandardMode");
    //     Position pos1 = new Position(0,1,1);
    //     Position pos2 = new Position(1,1,1);
    //     Player player = new Player("player", pos1, standardMode);

    //     // create swamp entity
    //     SwampTile swamp = new SwampTile("swamp_tile", pos2);

    //     // move player into swamp
    //     player.playerMove(Direction.RIGHT);
    //     assertEquals(player.getPosition(), pos2);

    //     // try moving out of swap
    //     player.playerMove(Direction.RIGHT);
    //     assertEquals(player.getPosition(), pos2);
    //     player.playerMove(Direction.RIGHT);
    //     assertEquals(player.getPosition(), pos2);

    //     // move out of swamp
    //     player.playerMove(Direction.RIGHT);
    //     assertEquals(player.getPosition(), new Position(2,1,1));
    // }
}