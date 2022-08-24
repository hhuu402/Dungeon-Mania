package dungeonmania.Entities.Enemies;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Position;


public abstract class Enemy extends Entity {
    private int health, attackDamage;
    protected static int sideWidth, sideHeight;

    public Enemy(String type, Position position, GameModes mode) {
        super(type, position);
        this.health = mode.getEnemyHealth();
        this.attackDamage = mode.getEnemyAttack();
        sideWidth = mode.getWidth();
        sideHeight = mode.getHeight();
    }

    // Returns the list of position that the enemy should move to
    public abstract ArrayList <Position> enemyMove(Player player, List<Entity> staticEntities);

    /**
     * Get Health of entity
     * @return
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Set Health of entity
     */
    public void setHealth(int newHealth) {
        this.health = newHealth;
    }

    /**
     * Get Attack Damage
     * @return int attack damage
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * Method for general enemy attacking
     */
    public boolean action(Player player) {
        if(!getPosition().equals(player.getPosition())) {
            return false;
        }
        
        Map<String, Integer> invisActive = player.getEffectList();

        if (invisActive.containsKey("invisibilityMod")) {
            return false;
        }

        // Enemy and Player will attack one another
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
    
    // Enemy will attack Player
    public void enemyAttack(Player player) {
        player.setDamageReceived(getHealth(), getAttackDamage());
    }
    
    // Player will attack enemy
    public void playerAttack(Player player) {
        setHealth(getHealth() - player.getFinalAttack(this));
    }

    public List<Position> enemyMoveOpposite(Player player) {
        return null;
    }

}
