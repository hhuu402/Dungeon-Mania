package dungeonmania.Game;

import java.util.ArrayList;
import java.util.List;

public class HardMode extends GameModes {

    public HardMode(String type) {
        this.type = type;
        this.playerHealth = 70;
        this.enemyHealth = 50;
        this.playerAttack = 10;
        this.enemyAttack = 10;
    }

    public HardMode(String type, int width, int height) {
        this(type);
        this.width = width;
        this.height = height;
    }

    @Override
    public List<String> canSpawn(int tickCount) {
        List<String> spawnableEnemies = new ArrayList<>();
        if (tickCount >= 15 && (tickCount % 15) == 0) {
            spawnableEnemies.add("spider");
            spawnableEnemies.add("zombie_toast");
        } 

        if (tickCount >= 30 && (tickCount % 30) == 0) {
            if (Math.random() * 10 > 3) {
                spawnableEnemies.add("assassin");
            } else {
                spawnableEnemies.add("mercenary");
            }
        }

        if (tickCount >= 50 && (tickCount % 50) == 0) {
            spawnableEnemies.add("hydra");
        }
        return spawnableEnemies;
    }

    
}
