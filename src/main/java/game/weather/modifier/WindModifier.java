package game.weather.modifier;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Per-turn wind modifier.
 *
 * <p><b>High wind (&gt; 10 m/s):</b></p>
 * <ol>
 *     <li><b>AoE item relocation:</b> iterates every tile on the map; for each tile that
 *          contains ground items, selects a random adjacent tile via {@link Location#getExits()}
 *          and physically relocates the item via {@link Location#removeItem(Item)} on the source
 *          plus {@code addItem} on the destination.</li>
 * </ol>
 *
 * <p>This is the most spatially broad effect in the system — it can affect every item on the
 * entire map in a single turn, satisfying the complex cross-component effect requirement.</p>
 *
 * <p><b>Presentation:</b> returns a per-turn summary String; the game loop prints it.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class WindModifier extends WeatherModifier {

    private static final double WIND_THRESHOLD = 10.0;

    private final Random random = new Random();

    /**
     * Constructs a WindModifier with the given wind speed value.
     *
     * @param windSpeedMs raw wind speed in metres per second from the API
     */
    public WindModifier(double windSpeedMs) {
        super(windSpeedMs);
    }

    /**
     * Scatters all ground items to random adjacent tiles when wind exceeds the threshold.
     *
     * @param map the current game map to affect
     * @return a summary of how many items were scattered, or an empty String if none
     */
    @Override
    public String modify(GameMap map) {
        if (!valueAbove(WIND_THRESHOLD)) {
            return "";
        }

        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        // Collect (item, source, destination) triples first to avoid mutating a tile's item
        // list while iterating over the map.
        List<ItemMove> moves = new ArrayList<>();

        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);
                List<Item> items = new ArrayList<>(location.getItems());
                if (items.isEmpty()) {
                    continue;
                }

                List<Location> neighbours = new ArrayList<>();
                for (Exit exit : location.getExits()) {
                    neighbours.add(exit.getDestination());
                }
                if (neighbours.isEmpty()) {
                    continue;
                }

                for (Item item : items) {
                    Location dest = neighbours.get(random.nextInt(neighbours.size()));
                    moves.add(new ItemMove(item, location, dest));
                }
            }
        }

        // Execute all moves after iteration completes.
        for (ItemMove move : moves) {
            move.source.removeItem(move.item);
            move.destination.addItem(move.item);
        }

        if (moves.isEmpty()) {
            return "";
        }
        return String.format("Strong winds scatter %d item(s) across the facility.", moves.size());
    }

    /**
     * @return the display name of this modifier
     */
    @Override
    public String getName() {
        return "Wind";
    }

    /**
     * Internal value class carrying the data for one item relocation. Separating detection from
     * execution avoids mutating a tile's item list mid-iteration. Field order matches the
     * constructor's logical order — source, then destination — so the call site cannot silently
     * swap them.
     */
    private static class ItemMove {
        final Item item;
        final Location source;
        final Location destination;

        ItemMove(Item item, Location source, Location destination) {
            this.item = item;
            this.source = source;
            this.destination = destination;
        }
    }
}