package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A functional interface representing a self-contained spawning strategy.
 *
 * <p>A {@code Spawner} encapsulates the complete spawn decision in a single
 * method: it evaluates the given {@link Location} to determine whether spawning
 * should occur this turn, and if so, returns the newly created {@link Actor}.
 * Returning {@code null} signals that spawning should not occur.</p>
 *
 * <p>This collapses the previously split {@code SpawnRule} design
 * ({@code canAttemptSpawn} + {@code createCreature}) into a single atomic
 * operation, eliminating the implicit temporal coupling between those two calls
 * and the hidden state dependency that came with it.</p>
 *
 * <p><b>OCP:</b> New spawn strategies (e.g. probability-based, proximity-based,
 * cooldown-based) are added by implementing this interface — no existing ground
 * or spawner class needs modification.</p>
 *
 * <p><b>SRP:</b> Each implementation owns exactly one spawn strategy. The ground
 * tile that holds a {@code Spawner} is only responsible for calling it at the
 * right time — not for deciding when or what to spawn.</p>
 *
 * <p><b>ISP:</b> Callers only ever need {@code spawn(Location)} — there is no
 * bloated interface forcing implementors to separate condition from creation.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 2.0
 */
@FunctionalInterface
public interface Spawner {

    /**
     * Evaluates the location and attempts to produce a new actor.
     *
     * <p>Implementations define both the condition (e.g. turn interval, adjacency
     * check) and the actor to create, keeping the full spawning decision cohesive.</p>
     *
     * @param location the location at which spawning is attempted
     * @return a new {@link Actor} ready to be placed at the location,
     *         or {@code null} if spawning should not occur this turn
     */
    Actor spawn(Location location);
}