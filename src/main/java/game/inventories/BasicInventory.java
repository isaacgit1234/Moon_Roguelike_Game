package game.inventories;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;

/**
 * A simple inventory with no capacity restrictions.
 * Used by creatures that don't need weight tracking (e.g. Slime, Undead).
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class BasicInventory extends Inventory {

    /**
     * Adds an item without capacity checks.
     *
     * @param item the item to add
     * @return true if added successfully
     */
    @Override
    public boolean add(Item item) {
        return items.add(item);
    }

    /**
     * Removes an item from the inventory.
     *
     * @param item the item to remove
     * @return true if removed successfully
     */
    @Override
    public boolean remove(Item item) {
        return items.remove(item);
    }
}