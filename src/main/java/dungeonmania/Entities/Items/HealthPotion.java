package dungeonmania.Entities.Items;

import dungeonmania.Player;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Position;

public class HealthPotion extends Items {

    public HealthPotion(String type, Position position) {
        super(type, position);
    }
    
    /**
     * restore the players's full health
     * @param player
     */
    public void restoreHealth (Player player) {
        GameModes gameMode = player.getGameMode();
        int maxHealth = gameMode.getPlayerHealth();
        player.setHealth(maxHealth);
    }

    @Override
    public boolean use(Player player) {
        restoreHealth(player);
        setIsUsed(true);
        return true;
    }
}
