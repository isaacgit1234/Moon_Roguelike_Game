package game.weather.condition;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import game.actors.Slime;
import game.capabilities.GameAbilities;
import game.ground.FleshySprout;
import game.weather.modifier.TemperatureModifier;
import game.spawning.SpawnService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clear skies bring life: growth spreads from existing flora and a slime emerges
 * where sunlight breaks in.
 *
 * <p><b>Cross-component effects:</b></p>
 * <ol>
 *     <li><b>Terrain spawning:</b> {@link game.ground.Dirt} tiles adjacent to existing
 *         {@link game.ground.Flora} ground become {@link FleshySprout}, propagating life
 *         outward.</li>
 *     <li><b>Actor spawning:</b> a {@link Slime} is placed near a random empty
 *         {@link game.ground.Floor} tile via {@link SpawnService}.</li>
 * </ol>
 *
 * <p>Condition code range: 800 - 804 (clear sky, few clouds).</p>
 *
 * <p><b>Why this no longer clears creature behaviours:</b> a weather-injected behaviour
 * belongs to the layer that injects it — fog disorientation to {@link FogCondition}
 * (lifted by the alarm), freeze to {@link TemperatureModifier}
 * (its own responsibility). A condition applied <i>once</i> at session start cannot
 * police a modifier that re-asserts its effect <i>every turn</i>; the old reset loop
 * ran before any weather behaviour existed and never ran again, so it only created the
 * illusion of coordination. Each injector owns its own removal.</p>
 *
 * <p><b>Presentation:</b> {@link #apply(GameMap)} returns a summary String; this class
 * holds no {@code Display}.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class ClearCondition implements WeatherCondition {

    private final SpawnService spawnService = new SpawnService();
    private final Random random = new Random();

    /**
     * Grows life near flora and spawns a slime under the clear sky.
     *
     * @param map the current game map to affect
     * @return a summary of how many sprouts emerged and whether a slime spawned
     */
    @Override
    public String apply(GameMap map) {
        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        // Effect 1: FleshySprout on Dirt adjacent to existing Flora.
        java.util.Set<Location> dirtNextToFlora = new java.util.HashSet<>();

        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);
                if (location.getGround().hasAbility(GameAbilities.IS_FLORA)) {
                    for (Exit exit : location.getExits()) {
                        Location adj = exit.getDestination();
                        // The Set automatically ignores duplicates, so you don't even need the !contains() check!
                        if (adj.getGround().hasAbility(GameAbilities.IS_DIRT)) {
                            dirtNextToFlora.add(adj);
                        }
                    }
                }
            }
        }

        for (Location target : dirtNextToFlora) {
            target.setGround(new FleshySprout());
        }

        // Effect 2: spawn a Slime near a random empty Floor tile.
        List<Location> floorTiles = new ArrayList<>();
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location location = map.at(x, y);
                if (location.getGround().hasAbility(GameAbilities.IS_FLOOR) && !location.containsAnActor()) {
                    floorTiles.add(location);
                }
            }
        }
        boolean slimeSpawned = false;
        if (!floorTiles.isEmpty()) {
            Location spawnLoc = floorTiles.get(random.nextInt(floorTiles.size()));
            slimeSpawned = spawnService.spawnNear(spawnLoc, new Slime()) != null;
        }

        return String.format(
                "Clear skies settle over the facility: %d sprout(s) emerged%s.",
                dirtNextToFlora.size(), slimeSpawned ? ", a slime stirs to life" : "");
    }

    /**
     * @return the name of this weather condition
     */
    @Override
    public String getConditionName() {
        return "Clear";
    }
}