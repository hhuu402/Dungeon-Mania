package dungeonmania.Goal;

import dungeonmania.Game.Game;

public class ExitGoal extends Goal {
    
    public ExitGoal(String type) {
        super(type);
        setIsSubgoal(true);
    }

    public void updateStatus(Game game) {
        setCompletionStatus(game.atExit());
    }
}
