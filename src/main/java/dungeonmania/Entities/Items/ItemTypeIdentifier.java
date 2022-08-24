package dungeonmania.Entities.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemTypeIdentifier {
    private List<String> oneUseItems = new ArrayList<>();
    private List<String> multipleUseItems = new ArrayList<>();

    public ItemTypeIdentifier() {
        oneUseItems.add("key");
        oneUseItems.add("sceptre");
        oneUseItems.add("arrow");
        oneUseItems.add("bomb");
        oneUseItems.add("health_potion");
        oneUseItems.add("sun_stone");
        oneUseItems.add("treasure");
        oneUseItems.add("one_ring");
        oneUseItems.add("wood");

        multipleUseItems.add("anduril");
        multipleUseItems.add("armour");
        multipleUseItems.add("bow");
        multipleUseItems.add("invincibility_potion");
        multipleUseItems.add("invisibility_potion");
        multipleUseItems.add("midnight_armour");
        multipleUseItems.add("shield");
        multipleUseItems.add("sword");
    }

    public boolean isInList(List<String> list, String itemType) {
        if(list.contains(itemType)) {
            return true;
        }
        return false;
    }

    public boolean isOneUseItem(Items item) {
        String itemType = item.getType();
        return isInList(oneUseItems, itemType);
    }

    public boolean isMultiuseItem(Items item) {
        String itemType = item.getType();
        return isInList(multipleUseItems, itemType);
    }
}
