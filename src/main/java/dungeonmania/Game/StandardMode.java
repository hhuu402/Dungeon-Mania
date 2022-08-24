package dungeonmania.Game;

import java.util.ArrayList;
import java.util.List;

public class StandardMode extends GameModes {
    
    public StandardMode(String type) {
        this.type = type;
        this.playerHealth = 100;
        this.enemyHealth = 50;
        this.playerAttack = 10;
        this.enemyAttack = 10;
    }

    public StandardMode(String type, int width, int height) {
        this(type);
        this.width = width;
        this.height = height;
    }

    @Override
    public List<String> canSpawn(int tickCount) {
        List<String> spawnableEnemies = new ArrayList<>();
        if (tickCount >= 20 && (tickCount % 20) == 0) {
            spawnableEnemies.add("spider");
            spawnableEnemies.add("zombie_toast");
            
        }

        if (tickCount >= 40 && (tickCount % 40) == 0) {
            if (Math.random() * 10 > 3) {
                spawnableEnemies.add("assassin");
            } else {
                spawnableEnemies.add("mercenary");
            }
        }
        return spawnableEnemies;
    }
}
