package dungeonmania;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.Entities.Enemies.Enemy;
import dungeonmania.Entities.Items.*;
import dungeonmania.Game.GameModes;
import dungeonmania.exceptions.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class Player {
    private String id;
    private String type;
    private Position position;
    private GameModes gameMode;
    private int health;
    private int attack;
    private Map<String, Integer> effectList = new HashMap<>();

    private Inventory inventory;

    public Player(String type, Position position, GameModes gameMode) {
        this.type = type;
        this.position = position;
        this.gameMode = gameMode;
        this.health = gameMode.getPlayerHealth();
        this.attack = gameMode.getPlayerAttack();

        inventory = new Inventory(this.id);

    }

    public Inventory getInventory() {
        return inventory;
    }

    // Methods functions
    /**
     * Player movement
     * @param direction
     */
    public void playerMove (Direction direction) {
        this.position = position.translateBy(direction);
        updatePotionEffects();
        inventory.updateBuildableList();
    }

    /**
     * 
     * @return boolean of whether the player is dead or not
     */
    public void isDead() {

        if (this.getHealth() <= 0) {
            List<Items> ringInInventory = inventory.searchInventoryFor("one_ring");
            if (ringInInventory.size() >= 1) {
                Items theOneRing = ringInInventory.get(0);
                useItem(theOneRing.getId());
                inventory.removeItem(theOneRing);
            } else {
                setType("Dead Player");
            }
        }
    }

    /**
     * update hashmap of effects 
     * @param key
     * @param value
     */
    public void updateHashMapValue(String key, int value) {
        effectList.put(key, value);
    }


    // effect map methods
    /**
     * remove effect on player
     * @param effect
     */
    public void removeEffect(String effect) {
        effectList.remove(effect);
    }

    /**
     * add effect to player 
     * @param effect
     * @param duration
     */
    public void addEffect(String effect, Integer duration) {
        effectList.put(effect, duration);

    }

    /**
     * check if an effect exists in effectsList
     * @param effect the given key
     * @return true if it does exist, false if it does not
     */
    public boolean checkIfEffectExists(String effect) {
        return effectList.containsKey(effect);
    }

    /**
     * get the matching value to a given key in effectsList
     * @param effect the given key
     * @return the matching value or
     * @throws InvalidActionException if the key is not found
     */
    public int getEffectValue(String effect) throws InvalidActionException {
        if(checkIfEffectExists(effect)) {
            return effectList.get(effect);
        }
        throw new InvalidActionException("Error: Attempted to Access Value of Invalid Item");
    }
    
    /**
     * clickable items for front end - should set potions and bomb to used status but should not use other item types
     * @param id
     */
    public void isClicked(String id) throws IllegalArgumentException {
        List<String> canBeUsed = new ArrayList<String>();
        canBeUsed.add("health_potion");
        canBeUsed.add("invincibility_potion");
        canBeUsed.add("invisibility_potion");
        canBeUsed.add("bomb");

        if(canBeUsed.contains(inventory.findItemById(id).getType())) {
            useItem(id);
        }
    }

    /**
     * given an id, use that item from player's inventory
     * @param id the id of the item intended to be used
     * @throws IllegalActionArguement if the item is not found in player's inventory
     */
    public void useItem(String id) throws IllegalArgumentException {

        ItemTypeIdentifier itemTypeIdentifier = new ItemTypeIdentifier();
        Items item = inventory.findItemById(id);
        String itemType = item.getType();

        boolean canExpire = true;
        boolean isRarePotion = false;
        if(itemType == "key" || itemType == "sun_stone") {

            canExpire = false;
        }
        if(itemType == "invincibility_potion" || itemType == "invisibility_potion") {
            isRarePotion = true;
        }
        
        if(itemTypeIdentifier.isOneUseItem(item)) {
            if(item.use(this)) {
                if(item.getIsUsed() && canExpire) {
                    inventory.removeItem(item);
                }
            }       
        } else if(itemTypeIdentifier.isMultiuseItem(item)) {
            if(item.use(this)) {
                inventory.removeItem(item);
                MultipleUseItems mItem = (MultipleUseItems)item;
                if(!isRarePotion) {

                    removeEffect(mItem.getEffect());
                }
            }
        }

        inventory.updateBuildableList();
        
    }

        /**
     * given a craftable item type, craft the item
     * @param buildableItem - craftable item type
     * @throws IllegalArgumentException if the given item type is not craftable
     */
    public void craftItem(String buildableItem) throws InvalidActionException{
        if (!inventory.isInBuildableList(buildableItem)) {
            throw new InvalidActionException("");
        }

        switch(buildableItem) {
        case "bow":
            craftBow();
            break;
        case "shield":
            craftShield();
            break;
        case "sceptre":
            craftSceptre();
            break;
        case "midnight_armour":
            craftMidnightArmour();
            break;
        }

        inventory.removeAllUsedItems();
    }

    /**
     * 
     * Craft item of bow type
     */
    private void craftBow() {
        if (inventory.searchInventoryFor("bow").size() == 1) {
            return;
        }

        List<Items> resources = inventory.searchInventoryFor("wood");
        Items wood = resources.get(0);
        useItem(wood.getId());

        resources.clear();
        resources = inventory.searchInventoryFor("arrow");
        for (int count = 0; count < 3; count++) {
            Items i = resources.get(count);
            useItem(i.getId());
        }

        Bow bow = new Bow("bow", this.position);
        inventory.addItem(bow);
    }

    /**
     * craft item of shield type - will always prioritise using used Keys before using treasure to craft shield
     * */    
    private void craftShield() {
        //check if any of the keys can be used
        List<Items> resources = inventory.getUsedKeys();
        if(!resources.isEmpty()) { 
            Items i = resources.get(0);
            useItem(i.getId());
            inventory.removeItem(i);

            List<Items> wood = inventory.searchInventoryFor("wood");

            Items w = wood.get(0);
            useItem(w.getId());
            w = wood.get(1);
            useItem(w.getId());

            Shield shield = new Shield("shield", this.position);
            inventory.addItem(shield);

            return;
        }

        int numTreasure = inventory.howManyInInventory("treasure");
        if(numTreasure > 0) {
            List<Items> resource = inventory.searchInventoryFor("wood");
            Items w = resource.get(0);
            useItem(w.getId());
            w = resource.get(1);
            useItem(w.getId());

            resource.clear();
            resource = inventory.searchInventoryFor("treasure");
            useItem(resource.get(0).getId());

            Shield shield = new Shield("shield", this.position);
            inventory.addItem(shield);

            return;
        }   
    }

    private void craftSceptre() {
        List<Items> wood = inventory.searchInventoryFor("wood");
        List<Items> arrow = inventory.searchInventoryFor("arrow");

        List<Items> sun_stone = inventory.searchInventoryFor("sun_stone");
        inventory.removeItem(sun_stone.get(0));

        //always use used keys first, then treasure
        List<Items> resources = inventory.getUsedKeys();
        if(!resources.isEmpty()) { 
            Items i = resources.get(0);
            useItem(i.getId());
            inventory.removeItem(i);
        } else {
            List<Items> treasure = inventory.searchInventoryFor("treasure");
            Items i = treasure.get(0);
            useItem(i.getId());
            inventory.removeItem(i);
        }

        //always use wood first, then arrows
        if(!wood.isEmpty()) {
            Items w = wood.get(0);
            useItem(w.getId());
        } else {
            for(int count = 0; count < 2; count++) {
                Items a = arrow.get(count);
                useItem(a.getId());
            }
        }

        Sceptre sceptre = new Sceptre("sceptre", this.position);
        inventory.addItem(sceptre);

    }

    private void craftMidnightArmour() {
        List<Items> armour = inventory.searchInventoryFor("armour");
        Items i = armour.get(0);
        //useItem(i.getId());
        inventory.removeItem(i);

        List<Items> sun_stone = inventory.searchInventoryFor("sun_stone");
        i = sun_stone.get(0);
        useItem(i.getId());

        MidnightArmour midnight_armour = new MidnightArmour("midnight_armour", this.position);
        inventory.addItem(midnight_armour);

        return;
    }


    // Attack && health modification
    // player attack modification - player can only have 1 bow and sword at a time
    /**
     * 
     * @return final calculated attack from player to enemy
     */
    public int getFinalAttack(Enemy enemy) {
        int finalAttack = getAttack() * getHealth() / 10;
        List<Items> swordInInventory = inventory.searchInventoryFor("sword");
        List<Items> bowInInventory = inventory.searchInventoryFor("bow");
        List<Items> andurilInInventory = inventory.searchInventoryFor("anduril");

        if (enemy.getType().equals("hydra") && andurilInInventory.size() == 1) {
            Items weapon = andurilInInventory.get(0);
            useItem(weapon.getId());
            return enemy.getHealth();
        }
        
        if (swordInInventory.size() == 1) {
            Items weapon = swordInInventory.get(0);
            useItem(weapon.getId());
            finalAttack += 10;
        }

        if (enemy.getType().equals("assassin") && andurilInInventory.size() == 1) {
            Items weapon = andurilInInventory.get(0);
            useItem(weapon.getId());
            finalAttack = 3 * finalAttack;
        }

        if (bowInInventory.size() == 1) {
            Items weapon = bowInInventory.get(0);
            useItem(weapon.getId());
            finalAttack = 2 * finalAttack;
        }

        return finalAttack;
    }

    /**
     * calculate the damage the player is to receive from enemy
     * @param enemyHealth
     * @param enemyDamage
     */
    public void setDamageReceived(int enemyHealth, int enemyDamage) {
        int finalDamage = getFinalDamage(enemyHealth, enemyDamage);
        setHealth(this.health - finalDamage);
    }

    // player defence modification - player can only have 1 armour and shield at a time
    /**
     * Calculate the overall damage reduced
     * @param enemyHealth
     * @param enemyDamage
     * @return int
     */
    public int getFinalDamage(int enemyHealth, int enemyDamage) {
        if (effectList.containsKey("invincibilityMod")) {
            return 0;
        }

        int finalDamage = (enemyDamage * enemyHealth) / 10;
        List<Items> armourInInventory = inventory.searchInventoryFor("armour");
        List<Items> shieldInInventory = inventory.searchInventoryFor("shield");

        if (armourInInventory.size() == 1) {
            Items defence = armourInInventory.get(0);
            useItem(defence.getId());
            finalDamage = (int) Math.ceil(finalDamage / 2);
        }
        
        if (shieldInInventory.size() == 1) {
            Items defence = shieldInInventory.get(0);
            useItem(defence.getId());
            finalDamage -= 5;
        }

        if (finalDamage <= 0) {
            finalDamage = 0;
        }

        return finalDamage;
    }


    /**
     * updates the potion duration when player moves and removes the effect when the potion effect is used up
     */
    public void updatePotionEffects() {
        boolean removeInvis = false;
        boolean removeInvic = false;
        for (Map.Entry<String, Integer> potionEffect: effectList.entrySet()) {
            if (potionEffect.getKey().equals("invincibilityMod")) {
                potionEffect.setValue(potionEffect.getValue() - 1);
                if(potionEffect.getValue() <= 0) {
                    removeInvic = true;
                }
            }
            if (potionEffect.getKey().equals("invisibilityMod")) {
                potionEffect.setValue(potionEffect.getValue() - 1);
                if(potionEffect.getValue() <= 0) {
                    removeInvis = true;
                }
            }
        }

        
        if(removeInvic) {
            removeEffect("invincibilityMod");
        }

        if(removeInvis) {
            removeEffect("invisibilityMod");
        }

    }

    // getters and setters
    /**
     * 
     * @return player attack
     */
    public int getAttack() {
        return attack;
    }

    /**
     * set the player attack damage
     * @param attack
     */
    public void setAttack(int attack) {
        this.attack = attack;
    }

    /**
     * 
     * @return player's current health 
     */
    public int getHealth() {
        return health;
    }

    /**
     * set current health 
     * @param health
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * 
     * @return player type
     */
    public String getType() {
        return type;
    }

    /**
     * set player type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return get player position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * set player position
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * 
     * @return return gamemode of player
     */
    public GameModes getGameMode() {
        return gameMode;
    }

    /**
     * set gamemode of player (difficulty and its corresponding health and attack)
     * @param gameMode
     */
    public void setGameMode(GameModes gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * 
     * @return player id
     */
    public String getId() {
        return id;
    }

    /**
     * set player id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return effects currently applied
     */
    public Map<String, Integer> getEffectList() {
        return effectList;
    }

    /**
     * set player effect list
     * @param effectList
     */
    public void setEffectList(Map<String, Integer> effectList) {
        this.effectList = effectList;
    }

    public List<String> getBuildableList() {
        return inventory.getBuildableList();
    }

    public void setBuildableList(List<String> newList) {
        inventory.setBuildableList(newList);
    }

}
