package dungeonmania.Entities.Enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Items.Items;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy{

    public ZombieToast(String type, Position position, GameModes mode) {
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
}