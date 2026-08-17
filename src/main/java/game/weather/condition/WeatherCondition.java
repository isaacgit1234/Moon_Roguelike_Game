package game.weather.condition;

import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Contract for a broad, map-wide weather effect applied exactly once on game start.
 *
 * <p>Chosen as an <b>interface</b> (not abstract class) because each concrete condition is
 * entirely self-contained - there is no shared state or helper logic that would justify a base
 * class. Forcing a parent class would violate ISP by bundling unrelated implementation details.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface WeatherCondition {
    /**
     * Applies the one-time broad would effect to the given map.
     *
     * @param map the current game map to affect
     */
    String apply(GameMap map);

    /**
     * Returns a human-readable name for this condition.
     * Used for console messages and debugging output.
     *
     * @return the name of this weather condition
     */
    String getConditionName();
}
