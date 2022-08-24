package dungeonmania.Entities.Enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Items.*;
import dungeonmania.Game.GameModes;
import dungeonmania.util.*;

public class Mercenary extends Enemy {

    private boolean bribe;
    private int sceptreBribeCount = 0;

    public Mercenary(String type, Position position, GameModes mode) {
        super(type, position, mode);
        setInteractable(true);
    }

    @Override
    public ArrayList<Position> enemyMove(Player player, List<Entity> staticEntities) {
        ArrayList<Position> moveList= new ArrayList<Position>();


        // Find relative position vector from mercenary to player
        Position rel = Position.calculatePositionBetween(this.getPosition(), player.getPosition());
        //Consider moving up or down first
        if(rel.getY() > 0) {
            moveList.add(this.getPosition().translateBy(Direction.DOWN));
        } else if (rel.getY() < 0) {
            moveList.add(this.getPosition().translateBy(Direction.UP));
        }
        //Consider moving left or rightokay
        if(rel.getX() > 0 ) {
            moveList.add(this.getPosition().translateBy(Direction.RIGHT));
        } else if (rel.getX() < 0) {
            moveList.add(this.getPosition().translateBy(Direction.LEFT));
        }
        return moveList;
    }

    // Function for interaction with a player
    public boolean interact(Player player) {
        // Player is not in range of the mercenary
        Position rel = Position.calculatePositionBetween(player.getPosition(), getPosition());
        if(rel.getX() > 2 || rel.getY() > 2 || rel.getX() < -2 || rel.getY() < -2) {
            return false;
        }

        //If player is already bribed nothing will occur
        if(isBribed()) {
            // Check for sceptor count
            return false;
        }

        // Else if in range attempt to bribe
        Inventory playerInventory = player.getInventory();
        List<Items> sceptreList = playerInventory.searchInventoryFor("sceptre");
        List<Items> treasureList = playerInventory.searchInventoryFor("treasure");

        //If can be bribed by sceptre
        if(sceptreList.get(0) != null){
            setBribed(true);
            sceptreBribeCount = 1;
            return true;
        }

        //If can be bribed by Treasure
        for(Items treasure: treasureList){
            player.useItem(treasure.getId());
            setBribed(true);
            return true;
        }

        // If player has no treasure
        return false;
    }

    @Override
    public boolean action(Player player) {
        //If player is already bribed nothing will occur
        if(isBribed()) {
            //If sceptre has already set bribe as true for 10 times
            // Mercenary is no longer bribed 
            // Will continue to attack player
            if(sceptreBribeCount == 11) {
                sceptreBribeCount = 0;
                setBribed(false);
            // Increment bribe counter for sceptre
            } else if (sceptreBribeCount != 0) {
                sceptreBribeCount++;
                return false;
            // For cases bribed by treasure
            } else if (sceptreBribeCount == 0){
                return false;
            }
        }

        // Check for invisible
        Map<String, Integer> invisActive = player.getEffectList();

        if (invisActive.containsKey("invisibilityMod")) {
            return false;
        }

        // Player is not bribed then player will attack and enemy will attack
        enemyAttack(player);
        playerAttack(player);

        // Player is dead 
        player.isDead();

        // Enemy is dead
        if(getHealth() <= 0) {
            return true;
        }
        
        // Enemy is not dead
        return false;
    }

    /**
     * Setter and getter to check if mercernary is bribed
     * @return
     */
    public boolean isBribed() {
        return bribe;
    }

    public void setBribed(Boolean bribe) {
        this.bribe = bribe;
    }

    
}