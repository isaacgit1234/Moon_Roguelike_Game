package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.Parasite;
import game.actors.Slime;
import game.capabilities.GameAbilities;

import java.util.List;
import java.util.Random;

/**
 * A {@link Spawner} strategy for the Overflow Vent.
 *
 * <p>Spawns a {@link Parasite} or {@link Slime} only when a worker is detected
 * in an adjacent tile. The proximity condition and creature selection are
 * unified in a single {@link #spawn(Location)} call, eliminating the temporal
 * coupling that existed in the old {@code SpawnRule} design where
 * {@code canAttemptSpawn} had to be called before {@code createCreature}.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 2.0
 */
public class OverflowVentRule implements Spawner {

    private final List<Spawner> spawnPool = List.of(
            location -> new Parasite(),
            location -> new Slime()
    );

    private final Random random = new Random();

    /**
     * Spawns a random creature if a worker is adjacent to the given location.
     * Returns {@code null} if no worker is nearby — the ground tile that holds
     * this spawner does not need to know why spawning was skipped.
     *
     * @param location the location at which spawning is attempted
     * @return a new {@link Actor} if a worker is adjacent, {@code null} otherwise
     */
    @Override
    public Actor spawn(Location location) {
        for (Exit exit : location.getExits()) {
            Actor actor = exit.getDestination().getActor();
            if (actor != null && actor.hasAbility(GameAbilities.IS_WORKER)) {
                return spawnPool.get(random.nextInt(spawnPool.size())).spawn(location);
            }
        }
        return null;
    }
}