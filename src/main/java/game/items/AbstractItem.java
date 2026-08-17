package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

import game.statistics.ItemStatistics;

/**
 * Abstract base class for all concrete items in the game, providing a
 * shared foundation of weight tracking, portability, and location awareness.
 *
 * <p>Think of this as the item equivalent of a shipping label — every physical
 * object gets stamped with a weight and marked as portable before it enters
 * the world.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public abstract class AbstractItem extends Item {

    protected AbstractItem(String name, char displayChar, int weight) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(weight));
        enableAbility(ItemAbility.PORTABLE);
    }

    protected Location currentLocation;

    /**
     * Tracks the ground location of this item each turn while it lies on the
     * floor. Called automatically by the engine.
     *
     * <p>This is used by {@link #removeFromWorld(Actor)} to know which ground
     * tile to remove the item from when consumed off the floor.</p>
     *
     * @param currentLocation the location this item is currently lying on
     */
    @Override
    public void tick(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Removes this item from the world after consumption.
     *
     * <p>If this item is currently in the actor's inventory, it is removed
     * from there. Otherwise it is removed from the ground tile it was last
     * seen on (tracked via {@link #tick(Location)}).</p>
     *
     * <p>Called by each consumable item's {@code consume(Actor)} method.
     * Centralised here so no consumable has to duplicate the
     * inventory-vs-ground detection logic (DRY).</p>
     *
     * @param actor the actor who consumed this item
     */
    protected void removeFromWorld(Actor actor) {
        if (actor.getInventory().getItems().contains(this)) {
            actor.getInventory().remove(this);
        } else if (currentLocation != null) {
            currentLocation.removeItem(this);
        }
    }

    // Only show teleport action when carried (allowableActions on Item is called from inventory)
    public ActionList allowableActions(Actor owner) {
        return new ActionList();
    }
}
