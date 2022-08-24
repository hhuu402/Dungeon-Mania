package dungeonmania.Goal;

import dungeonmania.Game.Game;

public class TreasureGoal extends Goal {
    
    public TreasureGoal(String type) {
        super(type);
        setIsSubgoal(true);
    }

    public void updateStatus(Game game) {
        setCompletionStatus(game.getTreasures().isEmpty());
    }
}
