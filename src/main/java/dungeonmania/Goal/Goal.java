package dungeonmania.Goal;

import dungeonmania.Game.Game;

public class Goal {
    
    private String goal;
    private Boolean completionStatus;
    private Boolean isSubgoal;

    public Goal(String goal) {
        this.goal = goal;
        this.completionStatus = false;
    }

    public void updateStatus(Game game) {};
    public void addSubgoal(Goal goal) {};

    // Getters and setters
    
    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Boolean getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(Boolean completionStatus) {
        this.completionStatus = completionStatus;
    }

    public Boolean getIsSubgoal() {
        return isSubgoal;
    }

    public void setIsSubgoal(Boolean isSubgoal) {
        this.isSubgoal = isSubgoal;
    }
    
    @Override
    public String toString() {
        if (completionStatus) {
            return "";
        }
        return ":" + goal;
    }
    
}
