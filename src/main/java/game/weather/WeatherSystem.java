package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;

import game.weather.condition.WeatherCondition;

/**
 * Orchestrates the weather system: derive coordinates from live game state, obtain
 * a reading through a {@link WeatherApiClient}, apply one {@link WeatherCondition}
 * on game start, and drive the per-turn modifiers through a {@link WeatherTicker}.
 *
 * <p><b>SRP / DIP:</b> this class no longer performs HTTP or JSON parsing — that
 * lives behind {@link WeatherApiClient}, injected via the constructor. It depends
 * only on the {@link WeatherCondition} abstraction and the factory that builds the
 * concretes, never on a concrete weather class, so it can be unit-tested with a
 * stub client and no network.</p>
 *
 * <p><b>Presentation:</b> {@link #initialise(GameMap, Actor)} and
 * {@link #tick(GameMap)} return description Strings; the caller (the game loop in
 * {@code EclipseNebula}) owns the {@code Display}.</p>
 *
 * <p><b>Resilience:</b> any failure to obtain a reading (missing key, network
 * error, malformed body) falls back to a clear-skies condition rather than
 * crashing — and the fallback assigns a real condition <i>before</i> applying it.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class WeatherSystem {

    /** Map identity literals, named once here instead of scattered as magic strings. */
    private static final String MAP_DEPRECATED = "99-Deprecated";
    private static final String MAP_OVERFLOW = "20-Overflow";

    /** Condition code used for the clear-skies fallback. */
    private static final int CLEAR_CONDITION_CODE = 800;

    /** HP percentage above which the player counts as "healthy" for coordinate selection. */
    private static final int HEALTHY_HP_PERCENT = 50;

    private final WeatherApiClient client;
    private final WeatherFactory factory;

    private WeatherCondition condition;
    private WeatherTicker ticker;

    /**
     * Constructs a WeatherSystem with its collaborators injected — neither is
     * created internally, which is what keeps the class testable.
     *
     * @param client  supplies atmospheric readings (live or stubbed)
     * @param factory builds the concrete condition and modifiers from a reading
     */
    public WeatherSystem(WeatherApiClient client, WeatherFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    /**
     * Initialises the weather for this session. Derives coordinates from two
     * simultaneous live game-state variables — the player's current map and HP
     * percentage — fetches the reading, applies the selected condition once, and
     * builds the per-turn {@link WeatherTicker}.
     *
     * @param map    the current game map (used for identity and to apply the condition)
     * @param player the player actor (used for the HP-percentage variable)
     * @return a description of the weather applied, for the caller to display
     */
    public String initialise(GameMap map, Actor player) {
        Coordinate coordinate = coordinatesFor(map.toString(), isHealthy(player));
        try {
            WeatherData data = client.fetch(coordinate.latitude(), coordinate.longitude());
            condition = factory.createCondition(data.conditionCode);
            ticker = new WeatherTicker(factory.createModifiers(data));
            return "[Weather] " + condition.apply(map);
        } catch (Exception e) {
            return applyFallback(map, e.getMessage());
        }
    }

    /**
     * Applies every active modifier for this turn.
     *
     * @param map the current game map
     * @return the modifiers' combined description, or an empty String if weather
     *         never initialised or nothing fired
     */
    public String tick(GameMap map) {
        if (ticker == null) {
            return "";
        }
        return ticker.tick(map);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    /**
     * @param player the player actor
     * @return {@code true} if the player is above {@link #HEALTHY_HP_PERCENT}% of max HP
     */
    private boolean isHealthy(Actor player) {
        int maxHp = player.getMaximumStatistic(ActorStatistics.HEALTH);
        int currentHp = player.getStatistic(ActorStatistics.HEALTH);
        return maxHp > 0 && (currentHp * 100 / maxHp) > HEALTHY_HP_PERCENT;
    }

    /**
     * Maps the two live game-state variables to one of four real-world coordinates.
     *
     * @param mapName   the current map's name
     * @param isHealthy whether the player is above the HP threshold
     * @return the latitude/longitude pair to query
     */
    private Coordinate coordinatesFor(String mapName, boolean isHealthy) {
        if (MAP_DEPRECATED.equals(mapName)) {
            return isHealthy ? new Coordinate(-37.8, 144.9)  // Melbourne
                    : new Coordinate(51.5, -0.1);    // London
        }
        if (MAP_OVERFLOW.equals(mapName)) {
            return isHealthy ? new Coordinate(35.6, 139.6)    // Tokyo
                    : new Coordinate(1.3, 103.8);     // Singapore
        }
        return new Coordinate(-37.8, 144.9);                  // default: Melbourne
    }

    /**
     * Applies a clear-skies condition when no live reading is available. Assigns a
     * real condition before applying it — the bug-free counterpart of a fallback
     * that previously dereferenced a null condition.
     *
     * @param map    the map to apply the fallback to
     * @param reason the failure reason, for the displayed message
     * @return the fallback description for the caller to display
     */
    private String applyFallback(GameMap map, String reason) {
        condition = factory.createCondition(CLEAR_CONDITION_CODE);
        ticker = new WeatherTicker(
                factory.createModifiers(new WeatherData(CLEAR_CONDITION_CODE, 20.0, 5.0, 50.0)));
        return "[Weather] live data unavailable (" + reason + ") — defaulting to clear skies."
                + System.lineSeparator() + condition.apply(map);
    }

    /** Immutable latitude/longitude pair. */
    private record Coordinate(double latitude, double longitude) {}
}