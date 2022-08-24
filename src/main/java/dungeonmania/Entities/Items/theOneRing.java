package dungeonmania.Entities.Items;

import dungeonmania.Player;
import dungeonmania.Game.GameModes;
import dungeonmania.util.Position;

public class theOneRing extends Items {
    
    public theOneRing(String type, Position position) {
        super(type, position);
    }

    /**
     * restore player health to max health when he dies due to combat or health hits 0
     * @param player
     */
    public void restoreHealth (Player player) {
        GameModes mode = player.getGameMode();
        player.setHealth(mode.getPlayerHealth());
    }

    /**
     * OVERRIDE the use method of items to set key to used and invokes one ring effect
     */
    @Override
    public boolean use(Player player) {
        restoreHealth(player);
        setIsUsed(true);
        return true;
    }
}

