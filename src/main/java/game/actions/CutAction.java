package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.Cuttable;

/**
 * An action that cuts a Cuttable target using the PlasmaCutter.
 *
 * Delegates all cut logic and side effects to the Cuttable target
 * via onCut() - CutAction never needs to know what kind of target
 * it is cutting.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class CutAction extends Action {

    private final Cuttable cuttable;
    private final Location targetLocation;

    /**
     * Constructs a CutAction for the given cuttable target.
     *
     * @param cuttable the target being cut
     * @param targetLocation the location of the target
     */
    public CutAction(Cuttable cuttable, Location targetLocation) {
        this.cuttable = cuttable;
        this.targetLocation = targetLocation;
    }

    /**
     * Executes the cut by delegating to the target's onCut() method.
     *
     * @param actor the actor performing the cut
     * @param map   the current game map
     * @return description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return cuttable.onCut(actor, targetLocation, map);
    }

    /**
     * Menu description shown to the player
     *
     * @param actor the actor performing the cut
     * @return menu string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " cuts " + cuttable + " with Plasma Cutter. ";
    }
}
