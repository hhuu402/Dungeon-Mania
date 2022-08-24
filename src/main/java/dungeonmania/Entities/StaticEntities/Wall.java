package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public class Wall extends StaticEntities {

    public Wall(String type, Position position) {
        super(type, position);
    }

    public boolean action(Player player) {
        return false; 
    }
}
