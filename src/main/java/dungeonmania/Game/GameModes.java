package dungeonmania.Game;

import java.util.List;

public abstract class GameModes {

    protected String type;

    protected int width = 50;
    protected int height = 50;

    protected int playerHealth;
    protected int enemyHealth;
    protected int playerAttack;
    protected int enemyAttack;

    public abstract List<String> canSpawn(int tickCount);

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public int getPlayerAttack() {
        return playerAttack;
    }

    public int getEnemyAttack() {
        return enemyAttack;
    }
}
