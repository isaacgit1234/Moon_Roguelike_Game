package game.behaviours;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import game.ground.Flora;
import game.reactions.SpawnReactionManager;

import java.util.function.Supplier;

/**
 * A {@link FloraBehaviour} that spawns an actor on an adjacent tile
 * when a worker is nearby.
 *
 * <p>Previously, {@code SlimeSpawnBehaviour} and {@code UndeadSpawnBehaviour}
 * were two separate classes with identical logic differing only in actor type.
 * That duplication violates DRY. The actor type is now injected as a
 * {@link Supplier} at construction time, making this one class reusable
 * for any actor type.</p>
 *
 * <p><b>DRY:</b> Spawn logic defined once, parameterised by actor type.</p>
 * <p><b>OCP:</b> New spawn types require no new class — just a new supplier.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class ActorSpawnBehaviour implements FloraBehaviour {

    private final Supplier<Actor> actorSupplier;

    /**
     * Constructs an ActorSpawnBehaviour for the given actor type.
     *
     * @param actorSupplier supplier that constructs the actor to spawn
     */
    public ActorSpawnBehaviour(Supplier<Actor> actorSupplier) {
        this.actorSupplier = actorSupplier;
    }

    /**
     * Spawns an actor on an empty adjacent tile if a worker is nearby,
     * then applies post-spawn reactions via {@link SpawnReactionManager}.
     *
     * @param flora    the flora performing this behaviour
     * @param location the flora's current location
     * @return always null — result is a side-effect
     */
    @Override
    public Void operate(Flora flora, Location location) {
        if (flora.isWorkerNearby(location)) {
            Location spawnLocation = flora.getEmptyAdjacentLocation(location);
            if (spawnLocation != null) {
                Actor actor = actorSupplier.get();
                try {
                    spawnLocation.addActor(actor);
                    SpawnReactionManager.getInstance().applyReaction(actor, spawnLocation);
                } catch (GameEngineException e) {
                    // spawnLocation pre-validated as empty — engine conflict should not occur
                }
            }
        }
        return null;
    }
}