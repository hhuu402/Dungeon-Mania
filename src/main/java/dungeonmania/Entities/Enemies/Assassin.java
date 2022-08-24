package dungeonmania.Entities.Enemies;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Items.*;
import dungeonmania.Game.GameModes;
import dungeonmania.util.*;

public class Assassin extends Mercenary {

    public Assassin(String type, Position position, GameModes mode) {
        super(type, position, mode);
        setInteractable(true);
        this.setAttackDamage(this.getAttackDamage() + 10);
    }

    @Override
    // Function for interaction with a player
    public boolean interact(Player player) {
        // Player is not in range of the mercenary
        Position rel = Position.calculatePositionBetween(player.getPosition(), getPosition());
        if(rel.getX() > 2 || rel.getY() > 2 || rel.getX() < -2 || rel.getY() < -2) {
            return false;
        }
        
        //If player is already bribed nothing will occur
        if(isBribed()) {
            return false;
        }

        // Else if in range attempt to bribe
        Inventory playerInventory = player.getInventory();
        List<Items> treasureList = playerInventory.searchInventoryFor("treasure");
        List<Items> oneRingList = playerInventory.searchInventoryFor("one_ring");

        //If can be bribed by Treasure
        if(treasureList.size() >= 1 && oneRingList.size() >= 1) {
            player.useItem(treasureList.get(0).getId());
            player.useItem(oneRingList.get(0).getId());
            setBribed(true);
            return true;
        }
        // If player has no treasure
        return false;
    }
}
