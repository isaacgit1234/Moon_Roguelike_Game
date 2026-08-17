package game.weather.condition;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import game.capabilities.GameAbilities;
import game.ground.ToxicWaste;
import game.status.Burned;

/**
 * A violent storm that corrupts every walkable floor tile and burns all living actors.
 *
 * <p><b>Cross-component effects (satisfies complexity requirement):</b></p>
 * <ol>
 *     <li><b>Terrain mutation:</b> every {@link game.ground.Floor} tile identified by
 *         {@link GameAbilities#IS_FLOOR} is replaced with {@link ToxicWaste}, permanently
 *         corrupting the map's walkable surface.</li>
 *     <li><b>Chained AoE status:</b> every actor currently standing on the map receives a
 *         {@link Burned} status (1 dmg, 5 turns). The effect chains from terrain to actor
 *         without the caller knowing concrete actor types.</li>
 * </ol>
 *
 * <p>Condition code range: 200 - 699 (thunderstorm, drizzle, rain, snow, atmosphere).</p>
 *
 * <p><b>Presentation:</b> mutates the map and returns a one-line summary; the game loop owns
 * the {@link edu.monash.fit2099.engine.displays.Display} and prints it. Per-actor narration is
 * collapsed into a count so this domain class neither holds a Display nor floods the console.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class StormCondition implements WeatherCondition {

    /**
     * Corrupts every floor tile to toxic waste, then ignites every actor on the map.
     *
     * @param map the current game map to affect
     * @return a summary of how many tiles were corrupted and how many actors ignited
     */
    @Override
    public String apply(GameMap map) {
        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        int corrupted = 0;
        int burned = 0;

        // Effect 1: terrain mutation - Floor -> ToxicWaste
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);

                // Check for terrain mutation
                if (location.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                    location.setGround(new ToxicWaste());
                    corrupted++;
                }

                // Effect 2: chained AoE Burned on every actor currently on the map
                if (location.containsAnActor()) {
                    Burned.applyOrExtend(location.getActor());
                    burned++;
                }
            }
        }

        return String.format(
                "A toxic storm sweeps the facility: %d floor tile(s) corrupted into toxic waste, %d actor(s) set alight.",
                corrupted, burned);
    }

    /**
     * @return the name of this weather condition
     */
    @Override
    public String getConditionName() {
        return "Storm";
    }
}