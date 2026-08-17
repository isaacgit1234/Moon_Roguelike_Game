package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.spawning.SpawnService;
import game.spawning.Spawner;

import java.util.Random;

/**
 * Abstract reusable ground for Hole and Vent spawning.
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 1.0
 */
public abstract class SpawnerGround extends Ground {

    private static final double HOLE_GROWTH_CHANCE = 0.01;

    private final Spawner spawner;
    private final boolean canGrow;
    private final SpawnService spawnService = new SpawnService();
    private final Random random = new Random();
    private final Display display = new Display();

    public SpawnerGround(char displayChar, String name, Spawner spawner, boolean canGrow) {
        super(displayChar, name);
        this.spawner = spawner;
        this.canGrow = canGrow;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public void tick(Location location) {
        super.tick(location);

        Actor creature = spawner.spawn(location);
        if (creature == null) {
            return;
        }

        Location spawnedLocation = spawnService.spawnNear(location, creature);

        if (spawnedLocation != null) {
            afterSuccessfulSpawn(location, spawnedLocation);

            if (canGrow && random.nextDouble() < HOLE_GROWTH_CHANCE) {
                grow(location);
                display.println("Hole grows bigger. Adjacent tile becomes a new Hole.");
            }
        }
    }

    protected void afterSuccessfulSpawn(Location spawnerLocation, Location spawnedLocation) {
    }

    private void grow(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.containsAnActor()) {
                destination.setGround(copySpawner());
                return;
            }
        }
    }
    protected abstract Ground copySpawner();
}
