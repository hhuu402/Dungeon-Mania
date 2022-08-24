package dungeonmania.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Entities.Enemies.Enemy;
import dungeonmania.Entities.Enemies.Mercenary;
import dungeonmania.Game.Game;

public class EnemiesGoal extends Goal {

    public EnemiesGoal(String type) {
        super(type);
        setIsSubgoal(true);
    }

    public void updateStatus(Game game) {
        List<Enemy> enemies = new ArrayList<>();
        enemies.addAll(game.getEnemies());
        enemies.removeAll(game.getMercenaries().stream().filter(Mercenary::isBribed).collect(Collectors.toList()));
        
        setCompletionStatus(enemies.isEmpty());
    }
}
