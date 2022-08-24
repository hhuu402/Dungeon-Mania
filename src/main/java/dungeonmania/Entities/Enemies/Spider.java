package dungeonmania.Entities.Enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Spider extends Enemy {
    private int square = 0;
    public Spider(String type, Position position, GameModes mode) {
        super(type, position, mode);
    }

    /**
     * Get value representing current square of spider's position
     * @return int
     */
    public int getSquare() {
        return square;
    }

    /**
     * Set value representing current square of spider's position
     * Synonymous with moving
     * @param int
     */
    public void setSquare(int square) {
        this.square = square;
    }

    /**
     * Generates a random spawn position for a spider within the dungeon spider
     * @return Position
     */
    public static Position spawnPosition(int width, int height) {
        return new Position((int)Math.floor(Math.random()*width), (int)Math.floor(Math.random()*height));
    }

    @Override
    public ArrayList<Position> enemyMove(Player player, List<Entity> staticEntities) {
        ArrayList<Position> possiblePositions = new ArrayList<Position>();

        Position move = getPosition();
        
        if (square%8 == 0 || square%8 == 7) {
           possiblePositions.add(move.translateBy(Direction.RIGHT));
        } else if (square%8 == 1 || square%8 == 2) {
            possiblePositions.add(move.translateBy(Direction.DOWN));
        } else if (square%8 == 3 || square%8 == 4) {
            possiblePositions.add(move.translateBy(Direction.LEFT));
        } else if (square%8 == 5 || square%8 == 6) {
            possiblePositions.add(move.translateBy(Direction.UP));
        }
        this.square += 1;
        return possiblePositions;
    }

    /**
     * Simulates movement of the spider in the anticlockwise direction
     * @return Position
     */
    public List<Position> enemyMoveOpposite(Player player) {
        ArrayList<Position> possiblePositions = new ArrayList<Position>();
        
        Position move = getPosition();
        if (square%8 == 0 || square%8 == 1) {
            possiblePositions.add(move.translateBy(Direction.LEFT));
        } else if (square%8 == 2 || square%8 == 3) {
            possiblePositions.add(move.translateBy(Direction.UP));
        } else if (square%8 == 4 || square%8 == 5) {
            possiblePositions.add(move.translateBy(Direction.RIGHT));
        } else if (square%8 == 6 || square%8 == 7) {
            possiblePositions.add(move.translateBy(Direction.DOWN));
        }
        this.square += 1;
        return possiblePositions;

    }
}