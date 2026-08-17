package game.weather.modifier;

import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import game.behaviours.BehaviourControllable;
import game.behaviours.BehaviourPriority;
import game.capabilities.GameAbilities;
import game.ground.Fire;
import game.status.Burned;

/**
 * Per-turn temperature modifier.
 *
 * <p><b>High temperature (&gt; 30 degrees C):</b></p>
 * <ol>
 *     <li><b>Terrain mutation:</b> every {@link game.ground.Puddle} on the map evaporates
 *          into {@link Fire}, capturing the puddle as the fire's original ground for
 *          restoration.</li>
 *     <li><b>Chained actor effect:</b> any actor standing on a converted tile receives or
 *          receives or extends a {@link Burned} status via {@link Burned#applyOrExtend(edu.monash.fit2099.engine.actors.Actor)}.</li>
 * </ol>
 *
 * <p><b>Low temperature (&lt; 5 degrees C):</b></p>
 * <ol>
 *     <li><b>Behaviour injection:</b> injects a {@link DoNothingAction}-returning behaviour
 *          at {@link BehaviourPriority#WEATHER_FREEZE_PRIORITY} into every controllable actor,
 *          simulating movement paralysis from freezing temperatures.</li>
 * </ol>
 *
 * <p><b>Two-sided threshold:</b> temperature is the reason the base class exposes
 * {@link #valueAbove(double)} and {@link #valueBelow(double)} rather than a single
 * {@code exceedsThreshold} — heat and cold are independent gates, not one boundary.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class TemperatureModifier extends WeatherModifier {

    private static final double HIGH_TEMP_THRESHOLD = 30.0;
    private static final double LOW_TEMP_THRESHOLD = 5.0;

    /**
     * Constructs a TemperatureModifier with the given Celsius value.
     *
     * @param temperatureCelsius raw temperature value from the API
     */
    public TemperatureModifier(double temperatureCelsius) {
        super(temperatureCelsius);
    }

    /**
     * Applies the high-heat effect, the freeze effect, or neither, depending on the
     * reading.
     *
     * @param map the current game map to affect
     * @return a summary of what happened, or an empty String if the reading was temperate
     */
    @Override
    public String modify(GameMap map) {
        if (valueAbove(HIGH_TEMP_THRESHOLD)) {
            return ignitePuddles(map);
        }
        if (valueBelow(LOW_TEMP_THRESHOLD)) {
            return freezeActors(map);
        }
        return "";
    }

    /**
     * Converts every puddle to fire and scorches any actor on a converted tile.
     *
     * @param map the current game map
     * @return a summary, or an empty String if no puddle ignited
     */
    private String ignitePuddles(GameMap map) {
        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        int ignited = 0;
        int scorched = 0;
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);
                if (location.getGround().hasAbility(GameAbilities.IS_PUDDLE)) {
                    location.setGround(new Fire(location.getGround()));
                    ignited++;
                    if (location.containsAnActor()) {
                        Burned.applyOrExtend(location.getActor());
                        scorched++;
                    }
                }
            }
        }

        if (ignited == 0) {
            return "";
        }
        return String.format("Searing heat ignites %d puddle(s) into fire; %d actor(s) scorched.", ignited, scorched);
    }

    /**
     * Injects a freeze (do-nothing) override into every controllable actor.
     *
     * @param map the current game map
     * @return a summary, or an empty String if no actor was present
     */
    private String freezeActors(GameMap map) {
        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        int frozen = 0;
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                BehaviourControllable controllable = map.at(x, y).getActorAs(BehaviourControllable.class);
                if (controllable != null) {
                    controllable.addBehaviour(
                            BehaviourPriority.WEATHER_FREEZE_PRIORITY,
                            (actor, location) -> new DoNothingAction());
                    frozen++;
                }
            }
        }

        if (frozen == 0) {
            return "";
        }
        return String.format("Freezing cold paralyses %d creature(s).", frozen);
    }

    /**
     * @return the display name of this modifier
     */
    @Override
    public String getName() {
        return "Temperature";
    }
}