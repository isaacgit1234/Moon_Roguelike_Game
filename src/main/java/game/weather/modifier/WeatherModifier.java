package game.weather.modifier;

import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.condition.WeatherCondition;

/**
 * Base type for a continuous, per-turn weather effect driven by a single
 * numerical reading from the weather API (temperature, wind speed, or humidity).
 * Applied every game turn for the duration of the session.
 *
 * <p><b>Why an abstract class and not an interface:</b> every modifier wraps one
 * raw API number and must hold onto it — that shared <i>state</i> is exactly what
 * an interface cannot express. The base owns the {@link #value} field and its
 * construction once; subclasses add only their distinct per-turn behaviour.</p>
 *
 * <p><b>On thresholds:</b> deciding whether a reading is "extreme" is deliberately
 * left to each subclass rather than hoisted into a shared {@code exceedsThreshold}
 * method, because the thresholds are not uniform — temperature is two-sided (hot
 * <i>and</i> cold), whereas wind and humidity are one-sided. A single shared
 * one-sided check could not model temperature without lying about it. The shared
 * comparison helpers {@link #valueAbove(double)} and {@link #valueBelow(double)}
 * give subclasses the building blocks without dictating a single fixed gate.</p>
 *
 * <p><b>Presentation note:</b> {@link #modify(GameMap)} returns a description
 * String rather than printing, for the same reason as
 * {@link WeatherCondition}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public abstract class WeatherModifier {

    /**
     * The raw numerical reading from the API that drives this modifier
     * (e.g. {@code 35.2} for 35.2 degrees Celsius). Shared state across all
     * modifiers — the reason this is an abstract class rather than an interface.
     */
    protected final double value;

    /**
     * @param value the raw API reading this modifier responds to
     */
    protected WeatherModifier(double value) {
        this.value = value;
    }

    /**
     * Applies this modifier's per-turn effect to the map.
     *
     * @param map the map to act on this turn
     * @return a description of what changed, or an empty String if nothing fired
     */
    public abstract String modify(GameMap map);

    /**
     * @return the display name of this modifier, for messages and debugging
     */
    public abstract String getName();

    /**
     * @param limit the upper limit to test against
     * @return {@code true} if the reading is strictly above {@code limit}
     */
    protected boolean valueAbove(double limit) {
        return value > limit;
    }

    /**
     * @param limit the lower limit to test against
     * @return {@code true} if the reading is strictly below {@code limit}
     */
    protected boolean valueBelow(double limit) {
        return value < limit;
    }
}