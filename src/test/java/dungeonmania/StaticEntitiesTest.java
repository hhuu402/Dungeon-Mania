package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.Entities.Enemies.Mercenary;
import dungeonmania.Entities.Enemies.Spider;
import dungeonmania.Entities.Enemies.ZombieToast;
import dungeonmania.Entities.Items.Sword;
import dungeonmania.Entities.Items.Treasure;
import dungeonmania.Entities.StaticEntities.Boulder;
import dungeonmania.Entities.StaticEntities.Door;
import dungeonmania.Entities.StaticEntities.Exit;
import dungeonmania.Entities.StaticEntities.FloorSwitch;
import dungeonmania.Entities.StaticEntities.Portal;
import dungeonmania.Entities.StaticEntities.StaticEntities;
import dungeonmania.Entities.StaticEntities.Wall;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawner;
import dungeonmania.Game.*;
import dungeonmania.util.*;


public class StaticEntitiesTest {
    @Test
    public void testPortalSimple(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        assertEquals(player.getPosition(), new Position(1,1,1));

        // Create a portal at the spot the player is on and create a second portal
        Position pos2 = new Position(1,4,1);
        Portal newPortal1 = new Portal("portal", pos1);
        Portal newPortal2 = new Portal("portal", pos2);
        newPortal1.setOtherPortal(newPortal2.getPosition());   
        newPortal2.setOtherPortal(newPortal1.getPosition());
        assertEquals(newPortal1.getOtherPortal(), new Position(1,4,1));
        assertEquals(newPortal2.getOtherPortal(), new Position(1,1,1));

        assertDoesNotThrow(()-> newPortal1.action(player));
        assertEquals(player.getPosition(), new Position(1,4,1));
    }

    @Test
    public void testCreateStatic(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        StaticEntities StaticEntities = new StaticEntities("static_entities", pos1);
        Player player = new Player("player", pos1, standardMode);
        assertDoesNotThrow(()-> StaticEntities.action(player));
    }

    @Test
    public void testFloorSwitch(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);
        FloorSwitch floor = new FloorSwitch("floor_switch", pos1);
        assertDoesNotThrow(()-> floor.action(player));
    }

    @Test
    public void testFloorSwitch2(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);
        FloorSwitch floor = new FloorSwitch("floor_switch", pos1);
        floor.setIsActivated(true);
        assertEquals(floor.getIsActivated(), true);
        assertDoesNotThrow(()-> floor.action(player));
    }

    @Test
    public void testCreate(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        Exit exit = new Exit("exit", pos1);
        assertDoesNotThrow(()-> exit.action(player));

        Boulder boulder = new Boulder("boulder", pos1);
        assertDoesNotThrow(()-> boulder.action(player));

        Wall wall = new Wall("wall", pos1);
        assertDoesNotThrow(()-> wall.action(player));

        // Door door = new Door("door", pos1);
        // assertDoesNotThrow(()-> door.action(player));
    }
    
    @Test
    public void testZombieToastSpawnerSimple(){
        // Create a Spawner
        Position pos1 = new Position(7,4,1);
        ZombieToastSpawner spawner = new ZombieToastSpawner("zombie_toast_spawner", pos1);
        assertDoesNotThrow(()->spawner.spawnPosition());
    }

    @Test
    public void testZombieToastSpawnerInteractNoWeapon(){
        // No Weapon
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a Spawner
        Position pos2 = new Position(2,1,1);
        ZombieToastSpawner spawner = new ZombieToastSpawner("zombie_toast_spawner", pos2);
        assertDoesNotThrow(()->spawner.interact(player));
    }

    @Test
    public void testZombieToastSpawnerInteractWeapon(){
        // No Weapon
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);
        Inventory playeInventory = player.getInventory();

        // Create a Spawner
        Position pos2 = new Position(2,1,1);
        ZombieToastSpawner spawner = new ZombieToastSpawner("zombie_toast_spawner", pos2);
    
        Sword sword = new Sword("sword", pos1);
        sword.setId("sword");
        playeInventory.addItem(sword); 
        assertDoesNotThrow(()->spawner.interact(player));
    }

    @Test
    public void testZombieToastSpawnerNoInteract(){
        GameModes standardMode = new StandardMode("StandardMode");
        Position pos1 = new Position(1,1,1);
        Player player = new Player("player", pos1, standardMode);

        // Create a Spawner
        Position pos2 = new Position(3,1,1);
        ZombieToastSpawner spawner = new ZombieToastSpawner("zombie_toast_spawner", pos2);
        assertDoesNotThrow(()->spawner.interact(player));
        assertDoesNotThrow(()->spawner.action(player));
    }
}
