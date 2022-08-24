package dungeonmania.Entities.Items;

import dungeonmania.util.Position;

public class InvincibilityPotion extends MultipleUseItems {

    public InvincibilityPotion(String type, Position position) {
        super(type, position);
        this.setDurability(6);
        this.effect = "invincibilityMod";
    }
}
