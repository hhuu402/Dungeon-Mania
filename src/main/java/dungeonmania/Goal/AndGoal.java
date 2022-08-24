package dungeonmania.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game.Game;

public class AndGoal extends Goal {
    
    private List<Goal> subgoals = new ArrayList<Goal>();

    public AndGoal(String type) {
        super(type);
        setIsSubgoal(false);
    }

    @Override
    public void addSubgoal(Goal goal) {
        subgoals.add(goal);
    }

    @Override
    public void updateStatus(Game game) {
        subgoals.forEach(g -> g.updateStatus(game));
        setCompletionStatus(subgoals.stream().allMatch(Goal::getCompletionStatus));
    }

    @Override
    public String toString() {
        List <String> uncompletedGoals = subgoals.stream().filter(g -> !g.getCompletionStatus())
                                                .map(Goal::toString).collect(Collectors.toList());

        String goals = "";
        if (uncompletedGoals.size() > 0) {
            goals = "(" + uncompletedGoals.get(0);
            for (int i = 1; i < uncompletedGoals.size(); i++) {
                goals += " AND ";
                goals += uncompletedGoals.get(i);
            }
            goals += ")";
        }
        
        return goals;
    }

}
