package dungeonmania.Game;

import java.util.ArrayList;
import java.util.List;

public class PeacefulMode extends GameModes {
    
    public PeacefulMode(String type) {
        this.type = type;
        this.playerHealth = 100;
        this.enemyHealth = 1;
        this.playerAttack = 0;
        this.enemyAttack = 0;
    }

    public PeacefulMode(String type, int width, int height) {
        this(type);
        this.width = width;
        this.height = height;
    }

    @Override
    public List<String> canSpawn(int tickCount) {
        return new ArrayList<>();
    }
}
