package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.Slime;
import game.actors.Undead;

import java.util.List;
import java.util.Random;

/**
 * A {@link Spawner} strategy for the 99-Deprecated Hole.
 *
 * <p>Spawns either an {@link Undead} or a {@link Slime} at random every
 * {@value #SPAWN_INTERVAL} turns. Both the timing condition and creature
 * selection are encapsulated here, keeping the ground tile that owns this
 * spawner free of spawn logic (SRP).</p>
 *
 * <p><b>OCP:</b> Adding a new creature to the pool requires only modifying
 * this class — the {@code Hole} ground and the {@code Spawner} interface
 * remain untouched.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 2.0
 */
public class DeprecatedHoleRule implements Spawner {

    private static final int SPAWN_INTERVAL = 20;

    /**
     * Pool of lambdas — each produces a fresh actor instance.
     * Using lambdas (not constructor references) preserves compatibility with
     * the {@link Spawner#spawn(Location)} signature.
     */
    private final List<Spawner> spawnPool = List.of(
            location -> new Undead(),
            location -> new Slime()
    );

    private final Random random = new Random();
    private int turns = 0;

    /**
     * Increments the turn counter and spawns a random creature every
     * {@value #SPAWN_INTERVAL} turns.
     *
     * @param location the location at which spawning is attempted
     * @return a new {@link Actor}, or {@code null} if the interval has not elapsed
     */
    @Override
    public Actor spawn(Location location) {
        turns++;
        if (turns % SPAWN_INTERVAL != 0) {
            return null;
        }
        return spawnPool.get(random.nextInt(spawnPool.size())).spawn(location);
    }
}