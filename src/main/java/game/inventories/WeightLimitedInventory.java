package game.inventories;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import game.statistics.ItemStatistics;

/**
 * At its core, this is just an oversized {@code ArrayList}.
 * It prevents the player from simply carrying the entire {@code EclipseNebula}
 * with them by enforcing an arbitrary capacity limit.
 *
 * Weight is read from each item's WEIGHT statistic via ItemStatistics.WEIGHT.
 * Items without a WEIGHT statistic are treated as weightless — safe by default
 * @author Adrian Kristanto
 * @author Yong Leng Foong
 * version 1.1
 */
public class WeightLimitedInventory extends Inventory {
    private static final int WEIGHT_LIMIT = 50;
    private final int weightLimit;

    /**
     * Constructor for WeightLimitedInventory.
     * Enforces a fixed weight limit of 50 units.
     */
    public WeightLimitedInventory() {
        this.weightLimit = WEIGHT_LIMIT;
    }

    /**
     * Perform additional check when adding an item, making sure that by adding the item,
     * the weight limit of the inventory is not exceeded.
     *
     * @param item The Item to add.
     * @return true if the item is successfully added, false otherwise.
     */
    @Override
    public boolean add(Item item) {
        int itemWeight = item.hasStatistic(ItemStatistics.WEIGHT) ?
                item.getStatistic(ItemStatistics.WEIGHT) : 0;
        if (currentWeight() + itemWeight <= weightLimit){
            return items.add(item);
        }
        return false;
    }

    /**
     * Remove the item from the inventory.
     *
     * @param item The Item to remove.
     * @return true if the item is successfully removed, false otherwise.
     */
    @Override
    public boolean remove(Item item) {
        return items.remove(item);
    }

    /**
     * Dynamically calculates total weight of all carried items.
     * Computed fresh each call to prevent weight desyncing.
     *
     * @return total current weight
     */
    private int currentWeight(){
        int total = 0;
        for (Item item : items){
            total += item.hasStatistic(ItemStatistics.WEIGHT) ?
                    item.getStatistic(ItemStatistics.WEIGHT) : 0;
        }
        return total;
    }

    /**
     * Returns a string representation of inventory status to check how many spaces left
     *
     * @return player's inventory weight status
     */
    @Override
    public String toString() {
        int total = currentWeight();
        return "Inventory (" + total + "/" + WEIGHT_LIMIT + " units)";
    }
}
