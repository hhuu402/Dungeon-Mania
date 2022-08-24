package dungeonmania.Entities.Items;

import dungeonmania.util.Position;

import java.util.List;

import dungeonmania.Inventory;
import dungeonmania.Player;


public class Bomb extends Items {
    // implement pickedup as a bomb cannot be repicked up once placed

    public Bomb(String type, Position position) {
        super(type, position);
    }

    public List<Position> getBlastZone() {
        Position position = this.getPosition();
        
        return position.getAdjacentPositions();
    }

    @Override
    public boolean action(Player player) {
        Inventory inventory = player.getInventory();
        if (getIsUsed() == false) {
            setPickedUp(true);
            inventory.addItem(this);
            return true;
        }
        
        if (pickedUp == true) {
            return false;
        }
        
        player.setPosition(this.position);
        return false;
    }

    @Override
    public boolean use(Player player) {
        if(player.getPosition() == null) {
            return false;
        }
        
        setPosition(player.getPosition());

        setIsUsed(true);

        return true;
    }

}
