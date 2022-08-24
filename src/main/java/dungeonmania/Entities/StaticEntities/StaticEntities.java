package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.Entities.Entity;
import dungeonmania.util.Position;

public class StaticEntities extends Entity {

    public StaticEntities(String type, Position position) {
        super(type, position);
    }

    public boolean action(Player player) {
        return true;
        
    }
}
