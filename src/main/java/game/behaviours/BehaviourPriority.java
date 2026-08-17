package game.behaviours;

/**
 * Global priority constants for the behaviour system.
 *
 * <p>All {@link game.actors.BehaviouralActor} subclasses, and every world
 * system that injects behaviours through
 * {@link BehaviourControllable}, must register against these
 * constants rather than against raw integers. This enforces a single,
 * readable priority contract across the entire game and means the numeric
 * values can be re-spaced here without touching a single call site
 * (connascence of meaning, centralised — not connascence of value, scattered).</p>
 *
 * <p>Priority ordering: lower integer = higher priority (the behaviour map is a
 * {@code TreeMap}, iterated in ascending key order).</p>
 *
 * <ul>
 *   <li>{@link #ALARM_OVERRIDE_PRIORITY} — reserved exclusively for the alarm
 *       chase injection. No other behaviour may use this value.</li>
 *   <li>{@link #WEATHER_FREEZE_PRIORITY} — the cold branch of the temperature
 *       modifier immobilises an actor; it must outrank ordinary AI but yield to
 *       the alarm.</li>
 *   <li>{@link #WEATHER_FOG_PRIORITY} — fog disorientation overrides ordinary
 *       movement; ranked below freeze so a frozen-and-fogged actor still
 *       freezes.</li>
 *   <li>{@link #FIRST_PRIORITY} — urgent gameplay behaviours that run before
 *       normal logic.</li>
 *   <li>{@link #SECOND_PRIORITY} — standard combat or interaction behaviours.</li>
 *   <li>{@link #LAST_PRIORITY} — fallback behaviours such as wandering.</li>
 * </ul>
 *
 * <p><b>Why weather gets its own slots instead of slot 0:</b> the alarm reserves
 * {@link #ALARM_OVERRIDE_PRIORITY}. Earlier weather drafts injected at slot 0 as
 * well, silently overwriting the alarm's chase behaviour in the {@code TreeMap}.
 * Giving freeze and fog dedicated keys directly below the alarm removes that
 * collision while keeping the intended override semantics (weather beats normal
 * AI, alarm beats weather).</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class BehaviourPriority {

    /** Reserved exclusively for alarm chase override. Must never be used by any other behaviour. */
    public static final int ALARM_OVERRIDE_PRIORITY = 0;

    /** Cold-snap immobilisation injected by the temperature modifier. Outranks AI, yields to the alarm. */
    public static final int WEATHER_FREEZE_PRIORITY = 1;

    /** Fog disorientation injected by the fog condition. Ranked below freeze. */
    public static final int WEATHER_FOG_PRIORITY = 2;

    /** First gameplay priority — runs before normal logic. */
    public static final int FIRST_PRIORITY = 3;

    /** Second gameplay priority — standard combat or interaction. */
    public static final int SECOND_PRIORITY = 4;

    /** Last priority — last-resort fallback behaviour such as wandering. */
    public static final int LAST_PRIORITY = 999;

    private BehaviourPriority() {}
}