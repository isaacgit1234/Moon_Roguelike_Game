package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.Infectable;

/**
 * Action performed by an infecting actor to infect one adjacent target.
 * After infection, the infecting actor dies instantly.
 *
 * Uses polymorphism through Infectable to avoid concrete type checks.
 *
 * @author Kumali Wickremasinghe
 * @author Alia Anthony
 * @author Yong Leng Foong
 * @version 1.2
 */
public class InfectAction extends Action {

    private final Infectable target;
    private final Location targetLocation;

    /**
     * Constructs an InfectAction.
     *
     * @param target the infectable target
     * @param targetLocation the location of the target
     */
    public InfectAction(Infectable target, Location targetLocation) {
        this.target = target;
        this.targetLocation = targetLocation;
    }

    /**
     * Executes the infection.
     *
     * @param actor the actor performing the infection
     * @param map the current game map
     * @return description of the infection result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = target.infect(actor, targetLocation, map);

        actor.unconscious(map);

        return result + " " + actor + " dies after infection.";
    }

    /**
     * Returns menu description.
     *
     * @param actor the actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " infects a nearby target";
    }
}