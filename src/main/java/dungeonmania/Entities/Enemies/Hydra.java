package dungeonmania.Entities.Enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Inventory;
import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Hydra extends Enemy{

    public Hydra(String type, Position position, GameModes mode) {
        super(type, position, mode);
    }

    @Override
    public ArrayList<Position> enemyMove(Player player, List<Entity> staticEntities){
        Random rand = new Random();
        int n = rand.nextInt(3);
        n += 1;

        ArrayList<Position> desPosition = new ArrayList<Position>();

        if(n == 1) {
            desPosition.add(getPosition().translateBy(Direction.UP));
        } else if (n == 2) {
            desPosition.add(getPosition().translateBy(Direction.DOWN));
        } else if (n == 3) {
            desPosition.add(getPosition().translateBy(Direction.RIGHT));
        } else if (n == 4) {
            desPosition.add(getPosition().translateBy(Direction.LEFT));
        }
        return desPosition;
    }

    @Override
    // Player will attack enemy
    // It will have a 50% Chance of gaining health rather than losing health
    public void playerAttack(Player player) {
        // Random Seed Generator
        Random random = new Random();
        Inventory inventory = player.getInventory();
        List<Items> andurilInInventory = inventory.searchInventoryFor("anduril");

        if(random.nextInt(100) >= 50 || andurilInInventory.size() == 1){
            setHealth(getHealth() - player.getFinalAttack(this));
            return;
        }

        setHealth(getHealth() + player.getFinalAttack(this));
    }
}