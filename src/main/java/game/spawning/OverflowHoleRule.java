package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Undead;

import java.util.List;
import java.util.Random;

/**
 * A {@link Spawner} strategy for the 20-Overflow Hole.
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 2.0
 * @author Kumali Wickremasinghe
 */
public class OverflowHoleRule implements Spawner {

    private static final int SPAWN_INTERVAL = 20;

    private final List<Spawner> spawnPool = List.of(
            location -> new Undead(),
            location -> new Parasite()
    );

    private final Random random = new Random();
    private int turns = 0;

    /**
     * Attempts to spawn a creature.
     *
     * Every 20 turns, one creature from the
     * spawn pool is returned.
     *
     * @param location spawn location
     * @return spawned actor, or null if no spawn occurs
     */
    @Override
    public Actor spawn(Location location) {
        turns++;
        if (turns % SPAWN_INTERVAL == 0) {
            return spawnPool.get(random.nextInt(spawnPool.size())).spawn(location);
        }
        return null;
    }
}