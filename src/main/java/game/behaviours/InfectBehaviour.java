package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.InfectAction;
import game.capabilities.GameAbilities;
import game.capabilities.Infectable;

/**
 * Finds one adjacent valid infection target
 * and returns an InfectAction.
 *
 * Uses capabilities instead of instanceof
 * to reduce coupling and improve extensibility.
 *
 * @author Kumali Wickremasinghe
 * @author Alia Anthony
 * @author Yong Leng Foong
 * @version 1.3
 */
public class InfectBehaviour
        implements Behaviour<Actor, Action> {

    /**
     * Scans all exits adjacent to the parasite's current location, returning
     * an {@link InfectAction} targeting the first infectable entity found —
     * either an {@link Actor} standing on a neighbouring tile or an
     * {@link Item} lying on the ground there.
     *
     * <p>Checks actors before items at each exit. Returns {@code null} if no
     * infectable target exists in any adjacent tile, signalling that this
     * behaviour is not applicable this turn.</p>
     *
     * @param actor    the parasite performing the scan
     * @param location the parasite's current location
     * @return an {@link InfectAction} aimed at the nearest infectable target,
     *         or {@code null} if none are found
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Actor targetActor = destination.getActor();

            if (targetActor != null && targetActor.hasAbility(GameAbilities.IS_INFECTABLE)) {
                return new InfectAction((Infectable) targetActor, destination);
            }

            for (Item item : destination.getItems()) {
                if (item.hasAbility(GameAbilities.IS_INFECTABLE)) {
                    return new InfectAction((Infectable) item, destination);
                }
            }
        }
        return null;
    }
}