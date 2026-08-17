package game.ground;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.Undead;
import game.capabilities.Cuttable;
import game.items.IndustrialFan;
import game.spawning.OverflowVentRule;
import game.spawning.SpawnService;
import game.status.Poisoned;

/**
 * A motion-activated vent found in the 20-overflow moon.
 *
 * The vent uses an {@link OverflowVentRule} to determine when
 * spawning occurs. After a successful spawn, all adjacent actors
 * and the newly spawned creature are poisoned using
 * {@link Poisoned}.
 *
 * <p><b>SRP:</b> Vent is responsible only for vent-specific
 * spawning consequences, while shared spawning logic is handled
 * by {@link SpawnerGround}.</p>
 *
 * <p><b>OCP:</b> Vent extends {@link SpawnerGround} and reuses
 * existing spawning infrastructure without modifying parent logic.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 1.1
 */
public class Vent extends SpawnerGround implements Cuttable {

    private static final int POISON_DAMAGE = 1;
    private static final int POISON_DURATION = 5;

    /**
     * Constructs a motion-activated vent.
     *
     * Uses {@link OverflowVentRule} for spawn conditions and
     * disables hole growth behaviour.
     */
    public Vent() {
        super('V', "Vent", new OverflowVentRule(), false);
    }

    /**
     * Applies poison to all adjacent actors and the spawned
     * creature after a successful spawn.
     *
     * @param spawnerLocation the vent location
     * @param spawnedLocation the location where the creature spawned
     */
    @Override
    protected void afterSuccessfulSpawn(
            Location spawnerLocation,
            Location spawnedLocation) {

        for (Exit exit : spawnerLocation.getExits()) {
            Actor actor = exit.getDestination().getActor();

            if (actor != null) {
                actor.addStatus(new Poisoned(POISON_DAMAGE, POISON_DURATION));
            }
        }

        if (spawnedLocation != null
                && spawnedLocation.containsAnActor()) {
            spawnedLocation.getActor()
                    .addStatus(new Poisoned(POISON_DAMAGE, POISON_DURATION));
        }
    }

    /**
     * Creates a copy of this vent for spawning duplication.
     *
     * @return a new Vent instance
     */
    @Override
    protected Ground copySpawner() {
        return new Vent();
    }

    /**
     * Cuts the Vent, dropping an Industrial Fan on the floor,
     * transforming the tile into a Floor, and spawning an Undead
     * on that exact tile.
     *
     * @param actor    the actor performing the cut
     * @param location the location of this vent
     * @param map      the current game map
     * @return description of what happened
     */
    @Override
    public String onCut(Actor actor, Location location, GameMap map) {
        location.addItem(new IndustrialFan());
        location.setGround(new Floor());

        try {
            location.addActor(new Undead());
        } catch (GameEngineException e) {
            // tile already occupied — spawn nearby instead
            new SpawnService().spawnNear(location, new Undead());
        }
        return actor + " cuts the Vent! Industrial Fan dropped. An Undead rises from the vent!";
    }
}