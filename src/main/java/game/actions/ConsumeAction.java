package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Consumable;

/**
 * A generic action for consuming any Consumable item,
 * whether from inventory or directly from the ground.
 *
 * @author Alia Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class ConsumeAction extends Action {

    private final Consumable consumable;
    private final Item item;
    private final boolean fromGround;

    /**
     * Constructor for consuming from inventory.
     *
     * @param consumable the consumable item
     */
    public ConsumeAction(Consumable consumable) {
        this.consumable = consumable;
        this.item = null;
        this.fromGround = false;
    }

    /**
     * Constructor for consuming directly from the ground.
     *
     * @param consumable the consumable item
     * @param item       the item to remove from ground after consuming
     */
    public ConsumeAction(Consumable consumable, Item item) {
        this.consumable = consumable;
        this.item = item;
        this.fromGround = true;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        String result = consumable.consume(actor);
        if (fromGround && item != null) {
            map.locationOf(actor).removeItem(item);
        }
        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return consumable.getMenuDescription(actor);
    }
}