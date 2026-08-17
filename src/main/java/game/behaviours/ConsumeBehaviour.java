package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A behaviour that searches the current location for a Consumable item
 * on the ground and returns a ConsumeAction if found.
 * Used by Slime.
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class ConsumeBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        for (Item item : location.getItems()) {
            ActionList actions = item.allowableActions(location);
            for (Action action : actions) {
                return action;
            }
        }
        return null;
    }
}