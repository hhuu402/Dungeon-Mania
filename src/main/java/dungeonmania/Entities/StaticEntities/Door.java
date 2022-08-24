package dungeonmania.Entities.StaticEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Entities.Items.Key;
import dungeonmania.util.Position;

public class Door extends StaticEntities {
    private boolean isOpened;
    private int uniqueKey;

    public Door(String type, Position position, int uniqueKey) {
        super(type, position);
        this.isOpened = false;
        this.uniqueKey = uniqueKey;
    }

    @Override
    public boolean action(Player player) {
        if (isOpened) {
            player.setPosition(position);
            return false;
        }
        
        Inventory inventory = player.getInventory();
        List<Key> searchKeyList = inventory.searchInventoryFor("key").stream().map(Key.class::cast).collect(Collectors.toList());
        List<Items> searchSunStone = inventory.searchInventoryFor("sun_stone");
        
        if (findUniqueKey(searchKeyList).size() == 1) {
            player.useItem(findUniqueKey(searchKeyList).get(0).getId());
            setOpened(true);
            player.setPosition(position);
            return false;
        }

        if (searchSunStone.size() == 1) {
            player.useItem(searchSunStone.get(0).getId());
            setOpened(true);
            player.setPosition(position);
            return false;
        }
        
        return false;
    }

    public List<Key> findUniqueKey(List<Key> keyList) {
        List<Key> uniqueKey = new ArrayList<>();
        for (Key key: keyList) {
            if (key.getUniqueKey() == this.uniqueKey) {
                uniqueKey.add(key);
            }
        }
        return uniqueKey;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public int getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(int uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
    
}
