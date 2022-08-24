package dungeonmania.Entities.StaticEntities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public class Exit extends StaticEntities {

    public Exit(String type, Position position) {
        super(type, position);
    }

    public boolean action(Player player) {
        player.setPosition(this.getPosition());
        return false;
        
    }
    
}
