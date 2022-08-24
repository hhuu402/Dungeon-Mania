package dungeonmania.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.Player;
import dungeonmania.Entities.Enemies.Enemy;
import dungeonmania.util.Position;

public class Swamp extends Entity {
    private Map<String, Enemy> stuckEnemyMap = new HashMap<>();
    private Map<String, Integer> stuckEnemyDurationMap = new HashMap<>();
    private Map<String, Enemy> unstuckEnemyMap = new HashMap<>();
    private int movement_factor;

    public Swamp(String type, Position position, int movement_factor) {
        super(type, position);
        this.movement_factor = movement_factor;
    }

    /**
     * Add a stuck enemy when stepped on swamp
     * @param enemy
     */
    public void addStuckEnemy (Enemy enemy) {
        stuckEnemyMap.put(enemy.getId(), enemy);
        stuckEnemyDurationMap.put(enemy.getId(), movement_factor);
    }

    /**
     * updates the movement_factor duration for all enemies in stuckEnemydurationMap
     */
    public void updateStuckEnemy() {
        for (Map.Entry<String, Integer> stuckEnemy: stuckEnemyDurationMap.entrySet()) {
            stuckEnemy.setValue(stuckEnemy.getValue() - 1);
            if (stuckEnemy.getValue() < 1) {
                unstuckEnemyMap.put(stuckEnemy.getKey(), stuckEnemyMap.get(stuckEnemy.getKey()));
            }
        }

        removeUnstuckEnemy();
    }

    /**
     * Remove all unstuck enemies from duration map and stuck map
     */
    public void removeUnstuckEnemy() {
        for(String unstuck: unstuckEnemyMap.keySet()) {
            stuckEnemyDurationMap.remove(unstuck);
            stuckEnemyMap.remove(unstuck);
        }
    }

    /**
     * 
     * @return List of unstuck enemies
     */
    public List<Enemy> unStuckEnemyList() {
        List<Enemy> unstuckEnemyList = new ArrayList<>();
        for (Enemy unstuckEnemy: unstuckEnemyMap.values()) {
            unstuckEnemyList.add(unstuckEnemy);
        }
        unstuckEnemyMap.clear();
        return unstuckEnemyList;
    }

    // getters and setters
    public Map<String, Enemy> getStuckEnemyMap() {
        return stuckEnemyMap;
    }

    public void setStuckEnemyMap(Map<String, Enemy> stuckEnemyMap) {
        this.stuckEnemyMap = stuckEnemyMap;
    }

    public Map<String, Integer> getStuckEnemyDurationMap() {
        return stuckEnemyDurationMap;
    }

    public void setStuckEnemyDurationMap(Map<String, Integer> stuckEnemyDurationMap) {
        this.stuckEnemyDurationMap = stuckEnemyDurationMap;
    }

    public int getMovement_factor() {
        return movement_factor;
    }

    public void setMovement_factor(int movement_factor) {
        this.movement_factor = movement_factor;
    }

    public Map<String, Enemy> getUnstuckEnemyMap() {
        return unstuckEnemyMap;
    }

    public void setUnstuckEnemyMap(Map<String, Enemy> unstuckEnemyMap) {
        this.unstuckEnemyMap = unstuckEnemyMap;
    }

    @Override
    public boolean action(Player player) {
        player.setPosition(position);
        return false;
    }
    
}
