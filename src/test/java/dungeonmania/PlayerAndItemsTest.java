package dungeonmania;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.*;
import dungeonmania.Entities.Items.*;
import dungeonmania.Entities.StaticEntities.*;
import dungeonmania.Game.*;
import dungeonmania.util.*;

public class PlayerAndItemsTest {
    @Test
    public void testCreatePlayer() {
        
        //GameModes(String type)
        GameModes hardMode = new HardMode("HardMode");
        GameModes easyMode = new StandardMode("StandardMode");

        //Position(int x, int y, int layer)
        Position position1 = new Position(1, 1, 1);
        Position position2 = new Position(2, 2, 2);

        //String type, Position position, GameModes gameMode
        Player hardPlayer = new Player("player", position1, hardMode);
        Player easyPlayer = new Player("player", position2, easyMode);

        assertTrue(hardPlayer.getType() == "player");
        assertTrue(easyPlayer.getType() == "player");

        assertTrue(hardPlayer.getPosition() == position1);
        assertTrue(easyPlayer.getPosition() == position2);
        
        assertTrue(hardPlayer.getHealth() == 70);
        assertTrue(hardPlayer.getAttack() == 10);
        assertFalse(hardPlayer.getHealth() == 100);
        assertFalse(hardPlayer.getAttack() == 100);

        assertTrue(easyPlayer.getHealth() == 100);
        assertTrue(easyPlayer.getAttack() == 10);
        assertFalse(easyPlayer.getHealth() == 70);
        assertFalse(easyPlayer.getAttack() == 1);
    }

    @Test
    public void testAddToAndRemoveFromInventory() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);

        Inventory playeInventory = player.getInventory();

        //initialise some items
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");
        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");
        Wood wood2 = new Wood("wood", position1);
        wood2.setId("wood2");
        Bomb bomb1 = new Bomb("bomb", position1);
        bomb1.setId("bomb1");
        Key key = new Key("key", position1, 1);
        key.setId("key");
        Sword sword = new Sword("sword", position1);
        sword.setId("sword");
        Arrows arrows1 = new Arrows("arrow", position1);
        arrows1.setId("arrows1");

        playeInventory.addItem(treasure);
        playeInventory.addItem(wood1);
        playeInventory.addItem(wood2);

        //test howManyInInventory
        assertTrue(playeInventory.howManyInInventory("treasure") == 1);
        assertTrue(playeInventory.howManyInInventory("wood") == 2);
        assertTrue(playeInventory.howManyInInventory("arrow") == 0);

        playeInventory.removeItem(wood2);
        assertTrue(playeInventory.howManyInInventory("wood") == 1);

        //test removeAllFromInvenotry
        playeInventory.addItem(arrows1);
        playeInventory.addItem(bomb1);
        playeInventory.addItem(key);

        player.useItem(arrows1.getId());
        player.useItem(bomb1.getId());
        player.useItem(key.getId());

        playeInventory.removeAllUsedItems();
        assertTrue(playeInventory.howManyInInventory("bomb") == 0);
        //key should never be moved from inventory unless through crafting
        assertTrue(playeInventory.howManyInInventory("key") == 1);
        assertTrue(playeInventory.howManyInInventory("arrow") == 0);

    }

    @Test
    public void testFindItemById() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);

        Inventory playeInventory = player.getInventory();

        //initialise some items
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");
        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");
        Wood wood2 = new Wood("wood", position1);
        wood2.setId("wood2");
        Arrows arrows1 = new Arrows("arrows", position1);
        arrows1.setId("arrows1");

        playeInventory.addItem(treasure);
        playeInventory.addItem(wood1);
        playeInventory.addItem(wood2);
        playeInventory.removeItem(wood2);

        assertDoesNotThrow(() -> playeInventory.findItemById(treasure.getId()));
        assertDoesNotThrow(() -> playeInventory.findItemById(wood1.getId()));

        assertThrows(InvalidActionException.class, () -> playeInventory.findItemById(arrows1.getId()));
        assertThrows(InvalidActionException.class, () -> playeInventory.findItemById(wood2.getId()));
    }

    @Test
    public void testPlayerUseItemAndMovement() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);
        Position movedPosition = new Position(0, 1, 1);

        Player player = new Player("player", position1, hardMode);
        Inventory playeInventory = player.getInventory();

        Key key = new Key("key", position1, 1);
        key.setId("key");
        Sword sword = new Sword("sword", position1);
        sword.setId("sword");
        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");

        playeInventory.addItem(wood1);
        playeInventory.addItem(key);
        playeInventory.addItem(sword);

        //use items
        assertDoesNotThrow(() -> player.isClicked(wood1.getId()));
        assertDoesNotThrow(() -> player.isClicked(key.getId()));
        assertDoesNotThrow(() -> player.isClicked(sword.getId()));

        //wood should no longer be in inventory
        assertDoesNotThrow(() -> playeInventory.findItemById(wood1.getId()));
        //sword and key should be in inventory
        assertDoesNotThrow(() -> playeInventory.findItemById(key.getId()));
        assertDoesNotThrow(() -> playeInventory.findItemById(sword.getId()));

        // sword durability before movement
        assertTrue(sword.getDurability() == 10);

        // move player now
        player.playerMove(Direction.LEFT);
        assertTrue(player.getPosition().equals(movedPosition));

        //check durability of sword
        
        player.useItem(sword.getId());
        assertTrue(sword.getDurability() == 9);
        assertTrue(player.checkIfEffectExists("swordMod"));

        //use up sword's durability and then see if it is removed from inventory
        for(int i = 8; i > 0; i--) {
            assertDoesNotThrow(() -> player.useItem(sword.getId()));
            assertTrue(player.checkIfEffectExists("swordMod"));
        }
        
        assertDoesNotThrow(() -> player.useItem(sword.getId()));        
        assertThrows(InvalidActionException.class, () -> player.useItem(sword.getId()));
        assertThrows(InvalidActionException.class, () -> player.getEffectValue("swordMod"));
    }

    @Test
    public void testCraftBow() {
        
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);

        Inventory playeInventory = player.getInventory();

        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");
        Wood wood2 = new Wood("wood", position1);
        wood2.setId("wood2");
        Wood wood3 = new Wood("wood", position1);
        wood3.setId("wood3");
        Wood wood4 = new Wood("wood", position1);
        wood4.setId("wood4");
        Wood wood5 = new Wood("wood", position1);
        wood5.setId("wood5");
        Arrows arrows1 = new Arrows("arrow", position1);
        arrows1.setId("arrows1");
        Arrows arrows2 = new Arrows("arrow", position1);
        arrows2.setId("arrows2");
        Arrows arrows3 = new Arrows("arrow", position1);
        arrows3.setId("arrows3");
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");
        Key key = new Key("key", position1, 1);
        key.setId("key");

        playeInventory.addItem(wood1);
        playeInventory.addItem(arrows1);
        playeInventory.addItem(arrows2);
        playeInventory.addItem(arrows3);

        assertTrue(playeInventory.howManyInInventory("arrow") == 3);
        assertTrue(playeInventory.howManyInInventory("wood") == 1);

        assertTrue(playeInventory.buildableBow());
        
        List<String> buildables = playeInventory.getBuildableList();
        assertTrue(buildables.contains("bow"));
        
        assertDoesNotThrow(() -> player.craftItem("bow"));

        assertTrue(playeInventory.howManyInInventory("arrows") == 0);
        assertTrue(playeInventory.howManyInInventory("bow") == 1);

        assertThrows(InvalidActionException.class, () -> player.craftItem("bow"));

        playeInventory.addItem(wood2);
        playeInventory.addItem(arrows1);
        playeInventory.addItem(arrows2);
        playeInventory.addItem(arrows3);

        assertTrue(playeInventory.buildableBow());

        assertDoesNotThrow(() -> player.craftItem("bow"));
        assertTrue(playeInventory.howManyInInventory("arrows") == 0);
        
    }

    @Test
    public void testCraftShield() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);

        Inventory playeInventory = player.getInventory();

        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");
        Wood wood2 = new Wood("wood", position1);
        wood2.setId("wood2");
        Wood wood3 = new Wood("wood", position1);
        wood3.setId("wood3");
        Wood wood4 = new Wood("wood", position1);
        wood4.setId("wood4");
        Wood wood5 = new Wood("wood", position1);
        wood5.setId("wood5");
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");
        Key key = new Key("key", position1, 1);
        key.setId("key");

        playeInventory.addItem(wood1);
        playeInventory.addItem(wood2);
        playeInventory.addItem(wood3);
        playeInventory.addItem(wood4);
        playeInventory.addItem(wood5);

        playeInventory.addItem(treasure);
        playeInventory.addItem(key);

        assertTrue(playeInventory.buildableShield());
        assertDoesNotThrow(() -> player.craftItem("shield"));

        assertFalse(playeInventory.buildableShield());

        //check that buildableShield priortises used Key > treasure
        Treasure treasure2 = new Treasure("treasure", position1);
        treasure2.setId("treasure2");

        playeInventory.addItem(treasure2);
        player.useItem(key.getId());
        assertTrue(playeInventory.howManyInInventory("treasure") == 1);
        assertTrue(playeInventory.howManyInInventory("key") == 1);

        assertTrue(playeInventory.buildableShield());
        assertDoesNotThrow(() -> player.craftItem("shield"));

        assertTrue(playeInventory.howManyInInventory("treasure") == 1);
        assertTrue(playeInventory.howManyInInventory("key") == 0);

    }

    @Test
    public void testPickups() {

        // positions initialised
        Position position = new Position(0, 1, 1);
        Position position1 = new Position(1, 1, 1);

        // create new entities
        Key key = new Key("key", position1, 1);
        key.setId("key");
        Sword sword = new Sword("sword", position1);
        sword.setId("sword");
        Wood wood1 = new Wood("wood", position1);
        wood1.setId("wood1");
        Bomb bomb = new Bomb("bomb", position1);
        bomb.setId("bomb");
        HealthPotion health_potion = new HealthPotion("health_potion", position1);
        health_potion.setId("health_potion");
        InvincibilityPotion invincibility_potion = new InvincibilityPotion("invincibility_potion", position1);
        invincibility_potion.setId("invincibility_potion");
        InvisibilityPotion invisibility_position = new InvisibilityPotion("invisibility_potion", position1);
        invisibility_position.setId("invisibility_potion");
        Armour armour = new Armour("armour", position1);
        armour.setId("armour");
        Arrows arrow = new Arrows("arrow", position1);
        arrow.setId("arrow");
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");

        // new gamemode
        GameModes hardMode = new HardMode("HardMode");

        // initialise player and move
        Player player = new Player("player", position, hardMode);

        Inventory playeInventory = player.getInventory();

        player.playerMove(Direction.RIGHT);

        // items actions
        treasure.action(player);
        key.action(player);
        sword.action(player);
        bomb.action(player);
        health_potion.action(player);
        invincibility_potion.action(player);
        invisibility_position.action(player);
        armour.action(player);
        arrow.action(player);
        wood1.action(player);

        // test position 
        assertEquals(player.getPosition(), position1);

        // check items are now in inventory
        assertTrue(playeInventory.howManyInInventory("treasure") == 1);
        assertTrue(playeInventory.howManyInInventory("key") == 1);
        assertTrue(playeInventory.howManyInInventory("wood") == 1);
        assertTrue(playeInventory.howManyInInventory("bomb") == 1);
        assertTrue(playeInventory.howManyInInventory("sword") == 1);
        assertTrue(playeInventory.howManyInInventory("health_potion") == 1);
        assertTrue(playeInventory.howManyInInventory("invincibility_potion") == 1);
        assertTrue(playeInventory.howManyInInventory("invisibility_potion") == 1);
        assertTrue(playeInventory.howManyInInventory("health_potion") == 1);
        assertTrue(playeInventory.howManyInInventory("arrow") == 1);
        assertTrue(playeInventory.howManyInInventory("wood") == 1);
        assertTrue(playeInventory.howManyInInventory("armour") == 1);

        // use items 
        player.isClicked(bomb.getId());
        player.isClicked(health_potion.getId());
        player.isClicked(invincibility_potion.getId());
        player.isClicked(invisibility_position.getId());

        // update effect list
        Map<String, Integer> effectList = player.getEffectList();
        assertTrue(player.checkIfEffectExists(invincibility_potion.getEffect()));
        assertTrue(player.checkIfEffectExists(invisibility_position.getEffect()));

        assertTrue(effectList.containsKey(invincibility_potion.getEffect()));
        assertTrue(effectList.containsKey(invisibility_position.getEffect()));

        // check effect duration updates
        player.playerMove(Direction.DOWN);
        assertTrue(invincibility_potion.getDurability() == 5);
        assertTrue(invisibility_position.getDurability() == 5);

        assertDoesNotThrow(() -> player.isClicked("arrow"));

    }

    @Test
    public void testClickingInteractions() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);
        Inventory playeInventory = player.getInventory();

        //isClicked(String id) throws IllegalArgumentException
        Bomb bomb = new Bomb("bomb", position1);
        bomb.setId("bomb");
        HealthPotion health_potion = new HealthPotion("health_potion", position1);
        health_potion.setId("health_potion");
        InvincibilityPotion invincibility_potion = new InvincibilityPotion("invincibility_potion", position1);
        invincibility_potion.setId("invincibility_potion");
        InvisibilityPotion invisibility_position = new InvisibilityPotion("invisibility_potion", position1);
        invisibility_position.setId("invisibility_potion");
        Treasure treasure = new Treasure("treasure", position1);
        treasure.setId("treasure");
        Key key = new Key("key", position1, 1);
        key.setId("key");

        playeInventory.addItem(bomb);
        playeInventory.addItem(health_potion);
        playeInventory.addItem(invincibility_potion);
        playeInventory.addItem(invisibility_position);
        playeInventory.addItem(treasure);
        playeInventory.addItem(key);

        assertTrue(bomb.getIsUsed() == false);
        assertTrue(health_potion.getIsUsed() == false);
        assertTrue(invincibility_potion.getIsUsed() == false);
        assertTrue(invisibility_position.getIsUsed() == false);
        assertTrue(treasure.getIsUsed() == false);
        assertTrue(key.getIsUsed() == false);

        player.isClicked(bomb.getId());
        player.isClicked(health_potion.getId());
        player.isClicked(invincibility_potion.getId());
        player.isClicked(invisibility_position.getId());
        player.isClicked(treasure.getId());
        player.isClicked(key.getId());

        assertTrue(bomb.getIsUsed());
        assertTrue(health_potion.getIsUsed());
        assertTrue(invincibility_potion.getIsUsed());
        assertTrue(invisibility_position.getIsUsed());
        assertTrue(treasure.getIsUsed() == false);
        assertTrue(key.getIsUsed() == false);

    }

    @Test
    public void testPlayerHealthAndDeath() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);
        Inventory playeInventory = player.getInventory();

        HealthPotion health_potion = new HealthPotion("health_potion", position1);
        health_potion.setId("health_potion");
        theOneRing oneRing = new theOneRing("one_ring", position1);
        oneRing.setId("oneRing");

        playeInventory.addItem(oneRing);
        playeInventory.addItem(health_potion);
        assertTrue(playeInventory.howManyInInventory("health_potion") == 1);

        int maxHP = player.getHealth();
        player.setHealth(maxHP - 1);
        player.useItem(health_potion.getId());
        assertTrue(player.getHealth() == maxHP);
        assertTrue(playeInventory.howManyInInventory("one_ring") == 1);
        assertTrue(playeInventory.howManyInInventory("health_potion") == 0);

        player.setHealth(0);
        player.isDead();
        assertTrue(player.getType() == "player");
        assertTrue(player.getHealth() == maxHP);
        assertTrue(playeInventory.howManyInInventory("one_ring") == 0);

        player.setHealth(0);
        player.isDead();
        assertTrue(player.getType() == "Dead Player");
    }

    @Test
    public void testPlayerEffects() {
        GameModes hardMode = new HardMode("HardMode");

        Position position1 = new Position(1, 1, 1);

        Player player = new Player("player", position1, hardMode);
        Inventory playeInventory = player.getInventory();

        InvincibilityPotion invincibility_potion = new InvincibilityPotion("invincibility_potion", position1);
        invincibility_potion.setId("invincibility_potion");
        InvisibilityPotion invisibility_position = new InvisibilityPotion("invisibility_potion", position1);
        invisibility_position.setId("invisibility_potion");
        Sword sword = new Sword("sword", position1);
        sword.setId("sword");
        Armour armour = new Armour("armour", position1);
        armour.setId("armour");
        Bow bow = new Bow("bow", position1);
        bow.setId("bow");
        Shield shield = new Shield("shield", position1);
        shield.setId("shield");

        playeInventory.addItem(invincibility_potion);
        playeInventory.addItem(invisibility_position);
        playeInventory.addItem(sword);
        playeInventory.addItem(armour);
        playeInventory.addItem(bow);
        playeInventory.addItem(shield);

        player.useItem(armour.getId());
        player.useItem(sword.getId());
        player.useItem(invincibility_potion.getId());
        player.useItem(invisibility_position.getId());
        
        
        player.useItem(bow.getId());
        player.useItem(shield.getId());

        assertTrue(player.checkIfEffectExists(armour.getEffect()));
        assertTrue(player.checkIfEffectExists(sword.getEffect()));
        
        assertTrue(player.checkIfEffectExists(invisibility_position.getEffect()));
        assertTrue(player.checkIfEffectExists(invincibility_potion.getEffect()));
        assertTrue(player.checkIfEffectExists(bow.getEffect()));
        assertTrue(player.checkIfEffectExists(shield.getEffect()));

        for(int i = 5; i > 0; i--) {
            assertTrue(player.getEffectValue(invisibility_position.getEffect()) == i);
            assertTrue(player.getEffectValue(invincibility_potion.getEffect()) == i);
            player.updatePotionEffects();
        }
        player.updatePotionEffects();
        assertFalse(player.checkIfEffectExists(invisibility_position.getEffect()));
        assertFalse(player.checkIfEffectExists(invincibility_potion.getEffect()));
    
        assertTrue(bow.getDurability() == 9);
        //use up sword's durability and then see if it is removed from inventory
        for(int i = 8; i > 0; i--) {
            assertDoesNotThrow(() -> player.useItem(bow.getId()));
            assertTrue(player.checkIfEffectExists("bowMod"));
        }
        assertDoesNotThrow(() -> player.useItem(bow.getId()));
        assertFalse(player.checkIfEffectExists("bowMod"));

        assertTrue(shield.getDurability() == 9);
        //use up sword's durability and then see if it is removed from inventory
        for(int i = 8; i > 0; i--) {
            assertDoesNotThrow(() -> player.useItem(shield.getId()));
            assertTrue(player.checkIfEffectExists("shieldMod"));
        }
        assertDoesNotThrow(() -> player.useItem(shield.getId()));
        assertFalse(player.checkIfEffectExists("shieldMod"));
    }
    
    // -------------------- NEW TESTS FOR MILESTONE 3 ------------------------//
    //    Midnight armour crafting test
    @Test
    public void testCraftAndUseMidnightArmour() {
        GameModes hardMode = new HardMode("HardMode");
        Position position1 = new Position(1, 1, 1);
        Player player = new Player("player", position1, hardMode);
        Inventory inv = player.getInventory();

        Armour armour = new Armour("armour", position1);
        armour.setId("armour");
        SunStone SunStone = new SunStone("sun_stone", position1);
        SunStone.setId("sun_stone");
        
        // add items to player inventory
        inv.addItem(armour);
        inv.addItem(SunStone);

        // check for adequate resources
        assertTrue(inv.howManyInInventory("armour") == 1);
        assertTrue(inv.howManyInInventory("sun_stone") == 1);

        
        assertTrue(inv.buildableMidnightArmour());

        // craft midnight armour
        assertDoesNotThrow(() -> player.craftItem("midnight_armour"));
        assertTrue(inv.howManyInInventory("armour") == 0);
        assertTrue(inv.howManyInInventory("sun_stone") == 0);
        assertTrue(inv.howManyInInventory("midnight_armour") == 1);

        // craft another midnight_armour -> not enough resources
        assertThrows(InvalidActionException.class, () -> player.craftItem("midnight_armour"));

        List<Items> list = inv.searchInventoryFor("midnight_armour");
        MidnightArmour m = (MidnightArmour)list.get(0);
        m.setId("MNight");

        assertDoesNotThrow(() -> player.useItem(m.getId()));
        
        for(int i = 8; i > 0; i--) {
            assertDoesNotThrow(() -> player.useItem(m.getId()));
            assertTrue(player.checkIfEffectExists(m.getEffect()));
        }
        assertDoesNotThrow(() -> player.useItem(m.getId()));
        assertFalse(player.checkIfEffectExists(m.getEffect()));

    }

    
    // Sceptre crafting test
    @Test
    public void testTreasureCrafSceptre() {
        GameModes hardMode = new HardMode("HardMode");
        Position position = new Position(1, 1, 1);
        Player player = new Player("player", position, hardMode);
        Inventory inv = player.getInventory();

        // set sceptre crafting condition
        setSceptreCraftConditions(player, position);
        assertTrue(inv.howManyInInventory("sun_stone") == 1);
        assertTrue(inv.howManyInInventory("wood") == 2);

        // build sceptre
        assertTrue(inv.buildableSceptre());
        assertDoesNotThrow(() -> player.craftItem("sceptre"));

        // not enough sun_stones
        assertFalse(inv.buildableSceptre());

        // check resources used
        assertTrue(inv.howManyInInventory("wood") == 1);
        assertTrue(inv.howManyInInventory("treasure") == 0);
        assertTrue(inv.howManyInInventory("key") == 1);
        assertTrue(inv.howManyInInventory("sun_stone") == 0);
        assertTrue(inv.howManyInInventory("arrow") == 2);
    }

    @Test
    public void testUsedKeyPriorityInCraftSceptre() {
        GameModes hardMode = new HardMode("HardMode");
        Position position = new Position(1, 1, 1);
        Player player = new Player("player", position, hardMode);
        Inventory inv = player.getInventory();

        assertTrue(inv.howManyInInventory("key") == 0);

        // set sceptre craft conditions
        setSceptreCraftConditions(player, position);

        assertTrue(inv.howManyInInventory("wood") == 2);
        assertTrue(inv.howManyInInventory("treasure") == 1);
        assertTrue(inv.howManyInInventory("key") == 1);
        assertTrue(inv.howManyInInventory("sun_stone") == 1);
        assertTrue(inv.howManyInInventory("arrow") == 2);
        
        // use key
        Items k = inv.searchInventoryFor("key").get(0);
        player.useItem(k.getId());

        // build sceptre
        assertTrue(inv.buildableSceptre());
        assertDoesNotThrow(() -> player.craftItem("sceptre"));

        // not enough sun_stones
        assertFalse(inv.buildableSceptre());

        // check resources used
        assertTrue(inv.howManyInInventory("wood") == 1);
        assertTrue(inv.howManyInInventory("treasure") == 1);
        assertTrue(inv.howManyInInventory("key") == 0);
        assertTrue(inv.howManyInInventory("sun_stone") == 0);
        assertTrue(inv.howManyInInventory("arrow") == 2);
    }

    @Test
    public void testKeyInCraftSceptre() {
        GameModes hardMode = new HardMode("HardMode");
        Position position = new Position(1, 1, 1);
        Player player = new Player("player", position, hardMode);

        Inventory inv = player.getInventory();

        // set sceptre craft conditions
        setSceptreCraftConditions(player, position);

        // use treasure
        player.useItem(inv.searchInventoryFor("treasure").get(0).getId());

        // build sceptre should fail because no treasure and no unused key
        assertFalse(inv.buildableSceptre());

        // use key so we now have a used key
        player.useItem(inv.searchInventoryFor("key").get(0).getId());
        assertTrue(inv.buildableSceptre());

        assertTrue(inv.howManyInInventory("wood") == 2);
        assertTrue(inv.howManyInInventory("treasure") == 0);
        assertTrue(inv.howManyInInventory("key") == 1);
        assertTrue(inv.howManyInInventory("sun_stone") == 1);
        assertTrue(inv.howManyInInventory("arrow") == 2);

        assertDoesNotThrow(() -> player.craftItem("sceptre"));

        // not enough sun_stones
        assertFalse(inv.buildableSceptre());

        // check resources used
        assertTrue(inv.howManyInInventory("wood") == 1);
        assertTrue(inv.howManyInInventory("treasure") == 0);
        assertTrue(inv.howManyInInventory("key") == 0);
        assertTrue(inv.howManyInInventory("sun_stone") == 0);
        assertTrue(inv.howManyInInventory("arrow") == 2);
    }

    @Test
    public void testPickupSunStoneAndAnduril() {
        GameModes hardMode = new HardMode("HardMode");
        Position position = new Position(0, 1, 1);
        Position position1 = new Position(1, 1, 1);
        Player player = new Player("player", position, hardMode);

        // spawn items
        SunStone sunStone = new SunStone("sun_stone", position1);
        sunStone.setId("sun_stone");
        Anduril anduril = new Anduril("anduril", position1);
        anduril.setId("anduril");

        // player walk onto items
        player.playerMove(Direction.RIGHT);
        Inventory inventory = player.getInventory();

        sunStone.action(player);
        anduril.action(player);

        // check items are in inventory
        assertEquals(inventory.howManyInInventory("sun_stone"), 1);
        assertEquals(inventory.howManyInInventory("anduril"), 1);

    }

    // sun_stone to open doors
    @Test
    public void testOpenDoorSunStone() {
        // Initialise
        GameModes hardMode = new HardMode("HardMode");
        Position position = new Position (1, 1, 1);
        Position position1 = new Position (2, 1, 1);
        Position playerPos = new Position(0, 1, 1);
        Player player = new Player("player", playerPos, hardMode);


        // create resources
        SunStone sunStone = new SunStone("sun_stone", position);
        sunStone.setId("sun_stone");
        Door door = new Door("door", position1, 1);
        door.setId("door");

        // pick up sun_stone
        player.playerMove(Direction.RIGHT);
        Inventory inventory = player.getInventory();

        sunStone.action(player);

        // check sun_stone in inventory
        assertTrue(inventory.howManyInInventory("sun_stone") == 1);

        // open the door with sun stone
        player.playerMove(Direction.RIGHT);
        door.action(player);
        assertTrue(inventory.howManyInInventory("sun_stone") == 1);
        assertTrue(sunStone.getIsUsed() == true);
        assertTrue(door.isOpened() == true);

    }
    // Helper functions
    /**
     * condition setting for sceptre crafting - helper function
     * @param player
     * @param position
     */
    public void setSceptreCraftConditions(Player player, Position position) {
        // create resources
        Wood wood1 = new Wood("wood", position);
        wood1.setId("wood1");
        Wood wood2 = new Wood("wood", position);
        wood2.setId("wood2");
        Arrows arrow1 = new Arrows("arrow", position);
        arrow1.setId("arrow1");
        Arrows arrow2 = new Arrows("arrow", position);
        arrow2.setId("arrow2");
        Treasure treasure = new Treasure("treasure", position);
        treasure.setId("treasure");
        Key key = new Key("key", position, 1);
        key.setId("key");
        SunStone sunStone = new SunStone("sun_stone", position);
        sunStone.setId("sunStone");
        
        // add resources to inventory
        Inventory inv = player.getInventory();
        inv.addItem(wood1);
        inv.addItem(wood2);
        inv.addItem(arrow1);
        inv.addItem(arrow2);
        inv.addItem(sunStone);
        inv.addItem(treasure);
        inv.addItem(key);
    }
    
}
