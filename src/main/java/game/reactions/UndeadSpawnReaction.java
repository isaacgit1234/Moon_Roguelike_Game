package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.capabilities.GameAbilities;


/**
 * Undead gains 1 max health for each nearby creature.
 * @author Kumali Wickremasinghe
 *  * @version 1.0
 */
public class UndeadSpawnReaction implements SpawnReaction {

    private final Display display = new Display();

    /**
     * @param actor the actor to check
     * @return {@code true} if the actor is an Undead
     */
    @Override
    public boolean supports(Actor actor) {
        return actor.hasAbility(GameAbilities.IS_UNDEAD);
    }


    /**
     * Counts all creatures on adjacent tiles, then increases the Undead's
     * maximum HP and current HP by that count.
     *
     * @param actor    the Undead that just spawned
     * @param location the tile the Undead spawned on
     */
    @Override
    public void apply(Actor actor, Location location) {
        int nearbyCreatures = 0;

        for (Exit exit : location.getExits()) {
            Actor nearby = exit.getDestination().getActor();
            if (nearby != null && nearby.hasAbility(GameAbilities.IS_CREATURE)) {
                nearbyCreatures++;
            }
        }

        actor.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                nearbyCreatures
        );
        actor.heal(nearbyCreatures);

        display.println(actor + " gains +" + nearbyCreatures
                + " max HP from nearby creatures when it spawns.");
    }
}