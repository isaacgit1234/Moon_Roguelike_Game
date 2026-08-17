package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;
import game.ground.ToxicWaste;

/**
 * Moves the actor away from actors with a specified ability and corrupts
 * the tile it just left with ToxicWaste as it flees.
 *
 * This is a new behaviour — no existing behaviour combines retreat
 * movement with environmental corruption of the source tile.
 *
 * Used by VoidStalker in DefensiveState.
 *
 * OCP: threat type is determined by ability, not hardcoded class.
 * DIP: depends on Capability abstraction, not concrete actor types.
 *
 * @author Ashley
 * @version 1.0
 */
public class RetreatBehaviour implements Behaviour<Actor, Action> {

    private final GameAbilities threatAbility;

    /**
     * @param threatAbility the ability that identifies threats to retreat from
     */
    public RetreatBehaviour(GameAbilities threatAbility) {
        this.threatAbility = threatAbility;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        Location bestRetreat = null;
        int minThreatCount = Integer.MAX_VALUE;

        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.canActorEnter(actor)) continue;

            int threatCount = countThreatsAdjacent(dest);
            if (threatCount < minThreatCount) {
                minThreatCount = threatCount;
                bestRetreat = dest;
            }
        }

        if (bestRetreat != null) {
            // Corrupt current tile with ToxicWaste before leaving
            if (location.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                location.setGround(new ToxicWaste());
            }
            return bestRetreat.getMoveAction(actor, "away from threat",
                    getHotKey(location, bestRetreat));
        }

        return null;
    }

    /**
     * Counts threats adjacent to the given location.
     *
     * @param location location to check around
     * @return number of adjacent threats
     */
    private int countThreatsAdjacent(Location location) {
        int count = 0;
        for (Exit exit : location.getExits()) {
            Actor actor = exit.getDestination().getActor();
            if (actor != null && actor.hasAbility(threatAbility)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the hotkey for the exit leading to the destination.
     */
    private String getHotKey(Location from, Location to) {
        for (Exit exit : from.getExits()) {
            if (exit.getDestination().equals(to)) {
                return exit.getHotKey();
            }
        }
        return "";
    }
}