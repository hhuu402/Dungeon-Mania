package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public class FloorSwitch extends StaticEntities {

    private Boolean isActivated;

    public FloorSwitch(String type, Position position) {
        super(type, position);
        this.isActivated = false;
    }
    
    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean action(Player player) {
        if (!isActivated) {
            player.setPosition(this.getPosition());
        }
        return false;
        
    }

}
