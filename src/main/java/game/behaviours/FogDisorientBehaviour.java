package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.condition.FogCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behaviour that moves the actor to a random adjacent tile, ignoring all targets.
 *
 * <p>Injected at {@link BehaviourPriority#ALARM_OVERRIDE_PRIORITY} (0) by
 * {@link FogCondition} so it runs before attack, chase and wander behaviours -
 * overriding them without destroying them. When fog clears, the injection key is
 * removed and the original behaviour tree resumes unmodified.</p>
 *
 * <p>This is structurally identical in purpose to {@link AlarmChaseOverrideBehaviour} -
 * both borrow the same injection pattern to hijack actor decision-making at priority 0.
 * The key difference: AlarmChase moves <em>toward</em> a target; FogDisorient moves
 * <em>randomly</em>, ignoring all targets.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class FogDisorientBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();
    /**
     * Selects a random adjacent passable location and returns a move action.
     * Returns null only if the actor is completely surrounded and cannot move at all.
     *
     * @param actor the actor executing this behaviour
     * @param location the actor's current location
     * @retun a random move action, or null if no valid moves exist
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Action> moves = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(actor)) {
                moves.add(dest.getMoveAction(actor, "disoriented", exit.getHotKey()));
            }
        }
        if (!moves.isEmpty()) {
            return moves.get(random.nextInt(moves.size()));
        }
        return null;
    }
}
