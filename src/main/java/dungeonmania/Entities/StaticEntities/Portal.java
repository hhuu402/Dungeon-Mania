package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public class Portal extends StaticEntities {
    Position otherPortal;

    public Portal(String type, Position position) {
        super(type, position);
    }
    /**
     * Method to transport position of player in a portal
     * @param player
     * @return
     */

    public boolean action(Player player) {
        player.setPosition(otherPortal);
        return false;
    }

    /**
     * Setter and Getter for position of connecting portal
     */
    public Position getOtherPortal() {
        return otherPortal;
    }

    public void setOtherPortal(Position otherPortal) {
        this.otherPortal = otherPortal;
    }
}

