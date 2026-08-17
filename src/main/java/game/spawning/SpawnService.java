package game.spawning;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.reactions.SpawnReactionManager;

/**
 * A shared service responsible for placing a newly spawned creature adjacent
 * to a source location.
 *
 * <p>Iterates over exits from the source, selects the first passable and
 * unoccupied tile, places the creature there, and notifies the
 * {@link SpawnReactionManager} of the spawn event.</p>
 *
 * <p><b>SRP:</b> This class handles only <em>where</em> to place a creature.
 * The decision of <em>what</em> to create and <em>when</em> is owned by
 * the relevant {@link game.spawning.Spawner} implementation.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 1.0
 */
public class SpawnService {

    private final SpawnReactionManager reactionManager = SpawnReactionManager.getInstance();

    /**
     * Attempts to place {@code creature} in the first adjacent tile that is
     * passable and unoccupied.
     *
     * @param source   the origin location from which exits are scanned
     * @param creature the actor to place
     * @return the {@link Location} where the creature was placed, or {@code null} if no valid adjacent tile was found
     */
    public Location spawnNear(Location source, Actor creature) {
        for (Exit exit : source.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.containsAnActor() && destination.canActorEnter(creature)) {
                try {
                    destination.addActor(creature);
                    reactionManager.applyReaction(creature, destination);
                    return destination;
                } catch (GameEngineException exception) {
                    return null;
                }
            }
        }
        return null;
    }
}