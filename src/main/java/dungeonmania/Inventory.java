package dungeonmania;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Entities.Items.*;
import dungeonmania.exceptions.*;

public class Inventory {
    private String playerId;
    private List<Items> inventoryList = new ArrayList<Items>();
    private List<String> buildableList = new ArrayList<>();

    public Inventory(String playerId) {
        this.playerId = playerId;
    }    

    public String getPlayerId() {
        return playerId;
    }

    /**
     * 
     * @return inventory list of items
     */
    public List<Items> getInventoryList() {
        return inventoryList;
    }

    /**
     * set inventory list of player
     * @param inventoryList
     */
    public void setInventoryList(List<Items> inventoryList) {
        this.inventoryList = inventoryList;
    }


    /**
     * 
     * @return buildable list of items that can be crafted
     */
    public List<String> getBuildableList() {
        return buildableList;
    }

    /**
     * set buildable list
     * @param buildableList
     */
    public void setBuildableList(List<String> buildableList) {
        this.buildableList = buildableList;
    }


    // inventory methods
    /**
     * Add item to list and updates buildable list 
     * @param item
     */
    public void addItem(Items item) {
        inventoryList.add(item);
        updateBuildableList();
    }

    /**
     * remove item from inventory
     * @param item
     */
    public void removeItem(Items item) {
        inventoryList.remove(item);
        updateBuildableList();
    }

    /**
    * this function returns the type this item is (e.g. Arrow, Armour...etc)
    * @param item
    * @return
    */
    public String getItemType(Items item) {
        return item.getType();
    }


    /**
     * search for all inventory of searchItem type in player's inventory
     * @param searchedItem
     * @return
     */
    public List<Items> searchInventoryFor(String searchedItem) {
        List<Items> foundItems = new ArrayList<Items>();

        for(Items i : inventoryList) {
            if(i.getType().equals(searchedItem)) {
                foundItems.add(i);
            }
        }
        return foundItems;
    }

    public List<Items> getUsedKeys() {
        List<Items> keyList = new ArrayList<>();
        List<Items> resources = searchInventoryFor("key");
        for(Items i : resources) {
            if(i.getIsUsed()) {
                keyList.add(i);
            }
        }

        return keyList;
    }

    /**
     * finds number of items of searchItem type in player's inventory
     * @param searchedItem the item type being searched for
     * @return how many items of searchItem type is in player's inventory
     */
    public int howManyInInventory(String searchedItem) {
        List<Items> foundItems = searchInventoryFor(searchedItem);
        return foundItems.size();
    }

    /**
     * find an item in player's inventory by its id
     * @param id the searched for item's id
     * @return the item or
     * @throws InvalidItemException when the item is not found
     */
    public Items findItemById(String id) throws InvalidActionException {
        for(Items i : inventoryList) {
            if(i.getId().equals(id)) {
                return i;
            }
        }
        throw new InvalidActionException("Error: Attempted to Access Invalid Item");
    }


    /**
     * cleans up player's inventory by removing all the items that have been used (have isUsed set to true)
     */
    public void removeAllUsedItems() {
        List<Items> usedItems = new ArrayList<Items>();
        for(Items i : inventoryList) {
            if(i.getIsUsed() && (!i.getType().equals("key"))) {
                usedItems.add(i);
            }
        }
        inventoryList.removeAll(usedItems);
        updateBuildableList();
    }

    /**
     * updates list of buildable items if all crafting materials are in inventory
     */
    public void updateBuildableList() {
        boolean buildable = buildableBow();

        if(buildable && !buildableList.contains("bow")) {
            buildableList.add("bow");            
        } else if(!buildable && buildableList.contains("bow")){
            removeFromBuildableList("bow");
        }

        buildable = buildableShield();
        if(buildable && !buildableList.contains("shield")) {
            buildableList.add("shield");
        } else if(!buildable && buildableList.contains("shield")){
            removeFromBuildableList("shield");
        }

        buildable = buildableSceptre();
        if(buildable && !buildableList.contains("sceptre")) {
            buildableList.add("sceptre");
        } else if(!buildable && buildableList.contains("sceptre")){
            removeFromBuildableList("sceptre");
        }

        buildable = buildableMidnightArmour();
        if(buildable && !buildableList.contains("midnight_armour")) {
            buildableList.add("midnight_armour");
        } else if(!buildable && buildableList.contains("midnight_armour")){
            removeFromBuildableList("midnight_armour");
        }

    }


    /**
     * check if bow is craftable from items in inventory
     * @return true if it can be craft, false if it cannot
     */
    public boolean buildableBow() {
        if (howManyInInventory("wood") >= 1 && howManyInInventory("arrow") >= 3) {
            return true;
        }
        return false;
         
     }

    /**
     * check if sceptre is craftable from items in inventory
     * @return true if it can be craft, false if it cannot
     */
    public boolean buildableSceptre() {
        if(howManyInInventory("wood") >= 1 || howManyInInventory("arrow") >= 2) {
            if(howManyInInventory("sun_stone") >= 1) {
                if(howManyInInventory("treasure") >= 1) {
                    return true;
                } else {
                    if(!getUsedKeys().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * check if midnight armour is craftable from items in inventory
     * @return true if it can be craft, false if it cannot
     */
    public boolean buildableMidnightArmour() {
        if(howManyInInventory("armour") >= 1 && howManyInInventory("sun_stone") >= 1) {
            return true;
        }
        return false;
    }


    /**
     * check if shield is craftable from items in inventory
     * @return true if it can be craft, false if it cannot
     */
    public boolean buildableShield() {
        if(howManyInInventory("wood") >= 2) {
            if(howManyInInventory("treasure") >= 1) {
                return true;
            } else if(!getUsedKeys().isEmpty()) {
                return true;
            }
            
        }
        return false;
         
     }

     /**
      * remove all used items
      * @param removeThis
      */
    public void removeFromBuildableList(String removeThis) {
        buildableList.remove(removeThis);
        return;
    }


    public boolean isInBuildableList(String itemType) {
        return buildableList.contains(itemType);
    }

    

}
