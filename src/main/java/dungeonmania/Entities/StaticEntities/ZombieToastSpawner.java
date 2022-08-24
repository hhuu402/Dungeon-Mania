package dungeonmania.Entities.StaticEntities;
import java.util.Random;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Items.*;
import dungeonmania.util.*;

public class ZombieToastSpawner extends StaticEntities {

    public ZombieToastSpawner(String type, Position position) {
        super(type, position);
        setInteractable(true);
    }
    
    // Method to return the spawn position of a Zombie Toast 
    public Position spawnPosition() {
        Random rand = new Random();
        int n = rand.nextInt(3);
        n += 1;
        Position spawn = null;
        if(n == 1) {
            spawn = getPosition().translateBy(Direction.UP);
        } else if (n == 2) {
            spawn = getPosition().translateBy(Direction.DOWN);
        } else if (n == 3) {
            spawn = getPosition().translateBy(Direction.RIGHT);
        } else if (n == 4) {
            spawn = getPosition().translateBy(Direction.LEFT);
        }
        return spawn;
    }

    /**
     * Method for player to automatically attack spawner if it is UP, DOWN, LEFT or RIGHT
     * of spawner
     */

    public boolean interact(Player player) {
        // Player is not in range of the spawner
        if(!Position.isAdjacent(getPosition(), player.getPosition())){
            return false;
        }

        // Player in range of spawner
        Inventory inventory = player.getInventory();
        for(Items item : inventory.getInventoryList()) {
            // Player has a weapon - Sword
            if(item instanceof Sword) {
                // Decrease durability of sword
                player.useItem(item.getId());
                return true;
            }
        }

        // Player does not have a weapon
        return false;
    }
    
    public boolean action(Player player) {
        return false;
    }
}