package game.weather.modifier;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import game.capabilities.GameAbilities;
import game.ground.Puddle;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-turn humidity modifier.
 *
 * <p><b>High humidity (&gt; 80%):</b></p>
 * <ol>
 *     <li><b>Terrain spread:</b> every existing {@link Puddle} tile scans its adjacent tiles;
 *          any adjacent {@link game.ground.Dirt} is converted to a new {@link Puddle},
 *          simulating water creeping outward in saturated atmospheric conditions.</li>
 * </ol>
 *
 * <p>Over several turns the water terrain progressively spreads across the map, satisfying the
 * complex cross-component effect requirement via accumulated terrain transformation each turn.</p>
 *
 * <p><b>Presentation:</b> returns a per-turn summary String; the game loop prints it. No Display
 * is held here.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class HumidityModifier extends WeatherModifier {

    private static final double HUMIDITY_THRESHOLD = 80.0;

    /**
     * Constructs a HumidityModifier with the given humidity percentage.
     *
     * @param humidityPercent raw humidity percentage from the API
     */
    public HumidityModifier(double humidityPercent) {
        super(humidityPercent);
    }

    /**
     * Spreads Puddle terrain to adjacent Dirt tiles each turn when humidity is high.
     *
     * @param map the current game map to affect
     * @return a summary of how many dirt tiles became puddles, or an empty String if none
     */
    @Override
    public String modify(GameMap map) {
        if (!valueAbove(HUMIDITY_THRESHOLD)) {
            return "";
        }

        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        // Collect target Dirt tiles adjacent to existing Puddle first -
        // modifying ground while iterating the same loop causes missed or double updates.
        List<Location> dirtsToConvert = new ArrayList<>();

        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);
                if (!location.getGround().hasAbility(GameAbilities.IS_PUDDLE)) {
                    continue;
                }
                for (Exit exit : location.getExits()) {
                    Location adj = exit.getDestination();
                    if (adj.getGround().hasAbility(GameAbilities.IS_DIRT) && !dirtsToConvert.contains(adj)) {
                        dirtsToConvert.add(adj);
                    }
                }
            }
        }

        for (Location target : dirtsToConvert) {
            target.setGround(new Puddle());
        }

        if (dirtsToConvert.isEmpty()) {
            return "";
        }
        return String.format("Humid air spreads moisture: %d dirt tile(s) turn to puddles.", dirtsToConvert.size());
    }

    /**
     * @return the display name of this modifier
     */
    @Override
    public String getName() {
        return "Humidity";
    }
}