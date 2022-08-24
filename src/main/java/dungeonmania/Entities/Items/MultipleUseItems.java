package dungeonmania.Entities.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.util.Position;

public class MultipleUseItems extends Items {
    protected int durability = 10;
    protected String effect;
    private List<String> uniqueItems = new ArrayList<String>();

    //This category includes: armour, sword, bow, shield, invincibility potion, invisibility potion
    
    public MultipleUseItems(String type, Position position) {
        super(type, position);
        this.pickedUp = false;
        this.isUsed = false;
        this.effect = type + "Mod";
        addUniqueItemsList();
        
    }

    private void addUniqueItemsList() {
        if(uniqueItems.isEmpty()) {
            uniqueItems.add("sword");
            uniqueItems.add("bow");
            uniqueItems.add("shield");
            uniqueItems.add("armour");
            uniqueItems.add("midnight_armour");
            uniqueItems.add("sceptre");
            uniqueItems.add("sun_stone");
            uniqueItems.add("anduril");
        }
    }

    private boolean isUniqueItem() {
        return uniqueItems.contains(this.type);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public String getEffect() {
        return effect;
    }

    private void decreaseDurability() {
        this.durability -= 1;
    }

    /**
     * OVERRIDE use method of items -> use item and update its durability
     */
    @Override
    public boolean use(Player player) {
        Map<String, Integer> playerEffectList = player.getEffectList();
        boolean existingEffect = playerEffectList.containsKey(this.effect);
        if(!existingEffect) {
            player.addEffect(this.effect, this.durability);
        }

        decreaseDurability();
        player.updateHashMapValue(this.effect, this.durability);
        if(isUniqueItem() && this.durability > 0) {
            return false;
        }
        this.setIsUsed(true);
        return true;

    }

    /**
     * OVERRIDE action of item -> check if player already has a sword before picking up
     */
    @Override
    public boolean action(Player player) {
        Inventory inventory = player.getInventory();
        List<Items> numInInventory = inventory.searchInventoryFor(this.type);
        
        if(isUniqueItem()) {
            if(numInInventory.size() > 1) {
                player.setPosition(this.getPosition());
                return false;
            }
        }
        inventory.addItem(this);
        return true;

    }

}
