package dungeonmania.Entities.Items;

import dungeonmania.util.Position;

public class InvisibilityPotion extends MultipleUseItems {

    public InvisibilityPotion(String type, Position position) {
        super(type, position);
        this.setDurability(6);
        this.effect = "invisibilityMod";
    }
}