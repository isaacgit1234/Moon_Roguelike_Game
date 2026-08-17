package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Unlockable;

/**
 * A generic action for unlocking any unlockable target.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class UnlockAction extends Action{

    private final Unlockable unlockable;

    /**
     * Constructor for UnlockAction
     *
     * @param unlockable the target to unlock
     */
    public UnlockAction(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

    /**
     * Executes the unlocking logic.This method call the unlock method.
     * on the unlockable target
     *
     * @param actor the actor unlocking the item
     * @param map the map the actor is on
     * @return description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map){
        return unlockable.unlock(actor);
    }

    /**
     * Returns a string for the menu, allowing the user to select the unlock action.
     *
     * @param actor the actor performing the action
     * @return A string representing the menu option
     */
    @Override
    public String menuDescription(Actor actor) {
        return unlockable.getMenuDescription(actor);
    }
}
