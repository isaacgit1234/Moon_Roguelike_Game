package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;

import game.weather.condition.WeatherCondition;
import game.weather.modifier.WeatherModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Drives the continuous, per-turn side of weather: holds the modifiers selected
 * for this session and ticks every one of them each game turn.
 *
 * <p><b>Role in the design (DIP / Rule 3):</b> this is one of the higher-level
 * classes that depends strictly on the {@link WeatherModifier} abstraction and
 * never on a concrete modifier. It does not know whether it is ticking a
 * temperature, wind, or humidity modifier — it only knows the contract. Adding a
 * fourth modifier (pressure, visibility) requires no change here, satisfying OCP
 * and giving the weather subsystem its second abstraction-consumer alongside
 * {@link WeatherSystem}'s dependence on {@link WeatherCondition}.</p>
 *
 * <p><b>Why not a {@code Behaviour}:</b> the engine's {@code Behaviour} contract
 * is for an <i>actor</i> deciding its own action each turn. Weather is a world
 * system, not an actor's decision, so it is ticked directly from the game loop —
 * the same pattern the alarm system uses — rather than masquerading as a
 * behaviour.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class WeatherTicker {

    private final List<WeatherModifier> modifiers;

    /**
     * @param modifiers the modifiers active for this session; defensively copied
     *                  so later mutation of the caller's list cannot change what
     *                  is ticked
     */
    public WeatherTicker(List<WeatherModifier> modifiers) {
        this.modifiers = new ArrayList<>(modifiers);
    }

    /**
     * Ticks every modifier once and collects their descriptions.
     *
     * @param map the current game map
     * @return the concatenated, newline-separated descriptions of every modifier
     *         that did something this turn; an empty String if none fired
     */
    public String tick(GameMap map) {
        StringBuilder report = new StringBuilder();
        for (WeatherModifier modifier : modifiers) {
            String line = modifier.modify(map);
            if (line != null && !line.isEmpty()) {
                if (report.length() > 0) {
                    report.append(System.lineSeparator());
                }
                report.append(line);
            }
        }
        return report.toString();
    }
}