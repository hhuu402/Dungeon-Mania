package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public class Boulder extends StaticEntities {

    public Boulder(String type, Position position) {
        super(type, position);
    }

    public boolean action(Player player) {
        return true;
        
    }
}
