package dungeonmania.Entities.Items;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.util.Position;

public class Items extends Entity {
    protected boolean pickedUp; 
    protected boolean isUsed;
    
    public Items(String type, Position position) {
        super(type, position);
        this.pickedUp = false;
        this.isUsed = false;
    }

    public boolean getIsUsed() {
        return this.isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }
    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public boolean use(Player player) {
        this.setIsUsed(true);
        return true;
    }

    public boolean action(Player player) {
        Inventory inventory = player.getInventory();
        inventory.addItem(this);
        inventory.updateBuildableList();
        return true;
    }

}
