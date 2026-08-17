package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behaviour that moves the actor to a random adjacent passable location.
 * Used by both {@link game.actors.Undead} and {@link game.actors.Slime}
 *
 * @author Yong Leng Foong
 * @version 2.3
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();

    /**
     * Selects a random adjacent passable location and returns a move action.
     *
     * @param actor the actor performing the behaviour
     * @param location the current location of the actor
     * @return a move action to a random adjacent location, or null if no valid moves exist
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Action> moves = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(actor)) {
                moves.add(dest.getMoveAction(actor, "around", exit.getHotKey()));
            }
        }
        if (!moves.isEmpty()) {
            return moves.get(random.nextInt(moves.size()));
        }
        return null;
    }
}
