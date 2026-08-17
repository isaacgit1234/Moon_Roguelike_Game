package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;

/**
 * Parasite spawn damages adjacent workers by 2.
 * @author Kumali Wickremasinghe
 * @version 1.0
 */
public class ParasiteSpawnReaction implements SpawnReaction {

    private final Display display = new Display();

    /**
     * @param actor the actor to check
     * @return {@code true} if the actor is a Parasite
     */
    public boolean supports(Actor actor) {
        return actor.hasAbility(GameAbilities.IS_PARASITE);
    }

    /**
     * Scans all exits from the spawn location and deals 2 damage to every
     * adjacent worker, printing a notification for each hit.
     *
     * @param actor    the Parasite that just spawned
     * @param location the tile the Parasite spawned on
     */
    @Override
    public void apply(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Actor nearby = exit.getDestination().getActor();

            if (nearby != null && nearby.hasAbility(GameAbilities.IS_WORKER)) {
                nearby.hurt(2);
                display.println(nearby + " takes 2 damage because a Parasite spawned nearby.");
            }
        }
    }
}