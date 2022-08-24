package dungeonmania.Entities.Items;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.util.Position;

public class Key extends Items {
    private int uniqueKey;

    public Key(String type, Position position, int uniqueKey) {
        super(type, position);
        this.uniqueKey = uniqueKey;
    }

    public boolean action(Player player) {
        Inventory inventory = player.getInventory();
        List<Items> searchList = inventory.searchInventoryFor("key");
        List<Key> keyList = new ArrayList<>();
        
        for (Items searchItem: searchList) {
            keyList.add((Key) searchItem);
        }

        if(keyList.isEmpty()) {
            inventory.addItem(this);
            inventory.updateBuildableList();
            return true;
        }

        player.setPosition(this.position);
        return false;
    }

    @Override
    public boolean use(Player player) {
        if(getIsUsed()) {
            return false;
        } else {
            setIsUsed(true);
            return true;
        }
    }

    public boolean hasUniqueKey(List<Key> keyList) {
        for (Key key: keyList) {
            if (key.getUniqueKey() == this.uniqueKey) {
                return true;
            }
        }
        return false;
    }

    public int getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(int uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
