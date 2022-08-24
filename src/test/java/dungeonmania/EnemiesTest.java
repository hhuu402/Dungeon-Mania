package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.Entities.Enemies.Mercenary;
import dungeonmania.Entities.Enemies.Spider;
import dungeonmania.Entities.Enemies.ZombieToast;
import dungeonmania.Entities.Items.Sword;
import dungeonmania.Entities.Items.Treasure;
import dungeonmania.Game.*;
import dungeonmania.util.*;


public class EnemiesTest {
    @Test
    public void testSimpleMoveMercenary(){

        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(7,4,1);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);

        ArrayList<Position> possibleMove = new ArrayList<Position>();
        possibleMove.add(new Position(7, 3, 1));
        possibleMove.add(new Position(6, 4, 1));
        assertEquals(mercenary.enemyMove(player, null), possibleMove);
    }

    @Test
    public void testHarderMoveMercenary(){

        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(-4,-6,1);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);

        ArrayList<Position> possibleMove = new ArrayList<Position>();
        possibleMove.add(new Position(-4, -5, 1));
        possibleMove.add(new Position(-3, -6, 1));
        assertEquals(mercenary.enemyMove(player, null), possibleMove);
    }

    @Test
    public void testMercenaryAction() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(1,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);
        Mercenary mercenary1 = new Mercenary("mercenary", pos2, standardMode);
        Mercenary mercenary2 = new Mercenary("mercenary", pos2, standardMode);

        Inventory playeInventory = player.getInventory();

        Sword sword = new Sword("sword", pos1);
        sword.setId("sword");
        playeInventory.addItem(sword);        

        assertEquals(mercenary.getHealth(), 50);
        assertEquals(player.getHealth(), 100);

        // Peform action 
        assertFalse(mercenary.action(player));
        
        // Enemey will die
        assertEquals(mercenary.getHealth(), 30);
        assertEquals(player.getHealth(), 50);

        // re-enact killing player
        assertFalse(mercenary1.action(player));
        assertFalse(mercenary2.action(player));
        assertTrue(player.getType().equals("Dead Player"));

    }

    @Test
    public void testMercenaryNoAction() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(2,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);

        assertEquals(mercenary.action(player), false);
    }

    @Test
    public void testactionMercenaryBribed() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(1,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);
        mercenary.setBribed(true);

        assertEquals(mercenary.action(player), false);
    }

    @Test
    public void testactionMercenaryBribe() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(1,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);

        Inventory playeInventory = player.getInventory();
        
        Treasure treasure = new Treasure("treasure", pos1);
        treasure.setId("treasure");
        playeInventory.addItem(treasure);
        
        assertEquals(mercenary.action(player), false);
    }

    @Test
    public void testinteractMercenary() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(4,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);
        assertEquals(mercenary.interact(player), false);
    }

    @Test
    public void testinteractMercenaryBribe() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(3,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);

        Inventory playeInventory = player.getInventory();
        
        Treasure treasure = new Treasure("treasure", pos1);
        treasure.setId("treasure");
        
        playeInventory.addItem(treasure);
        
        assertEquals(mercenary.interact(player), true);
    }

    @Test
    public void testinteractMercenaryBribed() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(3,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);
        mercenary.setBribed(true);
        assertEquals(mercenary.interact(player), false);
    }

    @Test
    public void testinteractMercenaryNoTreasure() {
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(1,1,2);
        Mercenary mercenary = new Mercenary("mercenary", pos2, standardMode);
        assertEquals(mercenary.interact(player), false);
    }

    @Test
    public void testSpiderSpawn(){
        GameModes standardMode = new StandardMode("StandardMode");

        // random spawn
        assertDoesNotThrow(()-> Spider.spawnPosition(10, 10));

        // spawn a spider
        Position spawnSpider = Spider.spawnPosition(10, 10);
        assertDoesNotThrow(()-> new Spider("spider", spawnSpider, standardMode));
    }

    @Test
    public void testSpiderMove(){
        GameModes standardMode = new StandardMode("StandardMode");

        // spawn a player
        Position spawnPlayer = new Position(1,1,1);
        Player player = new Player("player", spawnPlayer, standardMode);

        // random spawn
        assertDoesNotThrow(()-> Spider.spawnPosition(10, 10));

        // spawn a spider
        Position spawnSpider = Spider.spawnPosition(10, 10);
        assertDoesNotThrow(()-> new Spider("spider", spawnSpider, standardMode));
        Spider spider = new Spider("spider", spawnSpider, standardMode);
        
        // move the spider
        assertDoesNotThrow(()-> spider.enemyMove(player, null));
    }

    @Test
    public void testSpiderMove1(){
        GameModes standardMode = new StandardMode("StandardMode");

        // spawn a player
        Position spawnPlayer = new Position(1,1,1);
        Player player = new Player("player", spawnPlayer, standardMode);

        // spawn a spider
        Position spawnSpider = Spider.spawnPosition(10, 10);
        Spider spider = new Spider("spider", spawnSpider, standardMode);

        int spawnSquare = spider.getSquare();

        // move the spider
        assertDoesNotThrow(()-> spider.enemyMove(player, null));

        // check spider has moved
        assertEquals(spawnSquare + 1, spider.getSquare());
    }

    @Test
    public void testSpiderMoveOpposite(){
        GameModes standardMode = new StandardMode("StandardMode");

        // spawn a player
        Position spawnPlayer = new Position(1,1,1);
        Player player = new Player("player", spawnPlayer, standardMode);

        // spawn a spider
        Position spawnSpider = Spider.spawnPosition(10, 10);
        Spider spider = new Spider("spider", spawnSpider, standardMode);

        int spawnSquare = spider.getSquare();

        // move the spider
        assertDoesNotThrow(()-> spider.enemyMove(player, null));

        // check spider has moved
        assertEquals(spawnSquare + 1, spider.getSquare());

        // move the spider in the opposite direction
        assertDoesNotThrow(()-> spider.enemyMoveOpposite(player));
    }
    
    @Test
    public void testSimpleMoveZombie(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(7,4,1);
        ZombieToast zombie = new ZombieToast("zombie_toast", pos2, standardMode);
        assertDoesNotThrow(()->zombie.enemyMove(player, null));
    }

    @Test
    public void testSimpleZombieAttack(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(1,1,1);
        ZombieToast zombie = new ZombieToast("zombie_toast", pos2, standardMode);
        assertDoesNotThrow(()->zombie.action(player));
        assertEquals(zombie.getHealth(), 40);
        assertEquals(player.getHealth(), 50);
    }

    @Test
    public void testNoZombieAttack(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a mercenary
        Position pos2 = new Position(2,1,1);
        ZombieToast zombie = new ZombieToast("zombie_toast", pos2, standardMode);
        assertDoesNotThrow(()->zombie.action(player));
    }
}
