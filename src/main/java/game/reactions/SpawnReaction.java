package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Reaction triggered immediately after a creature enters the map.
 * @author Kumali Wickremasinghe
 * @version 1.0
 */
public interface SpawnReaction {

    /**
     * @param actor the actor to check
     * @return {@code true} if this reaction applies to the given actor type
     */
    boolean supports(Actor actor);

    /**
     * Executes the spawn reaction for the given actor at the given location.
     *
     * @param actor    the actor that just spawned
     * @param location the tile the actor spawned on
     */
    void apply(Actor actor, Location location);
}