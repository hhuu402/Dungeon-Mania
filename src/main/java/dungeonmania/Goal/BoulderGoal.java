package dungeonmania.Goal;

import dungeonmania.Entities.StaticEntities.FloorSwitch;
import dungeonmania.Game.Game;

public class BoulderGoal extends Goal {
    
    public BoulderGoal(String type) {
        super(type);
        setIsSubgoal(true);
    }

    public void updateStatus(Game game) {
        game.updateSwitches();
        setCompletionStatus(game.getFloorSwitches().stream().allMatch(FloorSwitch::getIsActivated));
    }
}
