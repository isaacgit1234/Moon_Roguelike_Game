package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 * Once depleted, the empty flask remains in the worker's inventory.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class Flask extends AbstractItem implements Consumable{

    private static final int MAX_USES = 5;
    private int remainingUses =  MAX_USES;

    /**
     * Constructor for the Flask class.
     * Registers weight statistics for WeightLimitedInventory enforcement.
     *
     */
    public Flask() {
        super("Flask", 'u', 3);
    }

    /**
     * Consumes one uses of the flask, healing the actor by 1 HP.
     *
     * @param actor the actor consuming the flask
     * @return description of the result
     */
    @Override
    public String consume(Actor actor){
        remainingUses--;
        actor.heal(1);
        return actor + " drinks from Flask, healing 1 HP. (" + remainingUses +")";
    }

    /**
     * Returns whether the flask still has remaining uses.
     *
     * @return true if uses remain, false if depleted
     */
    @Override
    public boolean canConsume(){
        return remainingUses > 0;
    }

    /**
     * Exposes a {@link ConsumeAction} when carried.
     * Satisfies AbstractItem's abstract method contract.
     * Eliminates instanceof checks in the actor's playTurn (OCP, polymorphism).
     *
     * @param owner the actor carrying this item
     * @return list of allowable actions while carried
     */
    @Override
    public ActionList allowableActions(Actor owner) {
        ActionList actions = new ActionList();
        if (canConsume()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Engine-facing override — delegates to {@link #allowableActions(Actor)}.
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return list of allowable actions while carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return allowableActions(owner);
    }


    /**
     * Returns the menu description for consuming this flask.
     *
     * @param actor the actor consuming the flask
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor){
        return actor + " drinks from the Flask";
    }
}
