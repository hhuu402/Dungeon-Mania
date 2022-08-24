package dungeonmania.Entities.Items;
import dungeonmania.util.Position;

public class Anduril extends MultipleUseItems {

    public Anduril(String type, Position position) {
        super(type, position);
        this.setDurability(5);
    }
    
}
