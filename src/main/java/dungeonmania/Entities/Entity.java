package dungeonmania.Entities;

import dungeonmania.Player;
import dungeonmania.util.Position;

public abstract class Entity {
    protected String id;
    protected String type;
    protected Position position;
    protected boolean isInteractable = false;

    public Entity(String type, Position position) {
        this.type = type;
        this.position = position;
    }

    // abstract methods
    public abstract boolean action(Player player);

    // getters & setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return get entity type
     */
    public String getType() {
        return this.type;
    }

    /**
     * set entity type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return entity position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * set entity position
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * 
     * @return boolean of entity interactability
     */
    public boolean isInteractable() {
        return isInteractable;
    }

    /**
     * set interactability of entity
     * @param isInteractable
     */
    public void setInteractable(boolean isInteractable) {
        this.isInteractable = isInteractable;
    }
}
