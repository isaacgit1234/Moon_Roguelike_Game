package game.behaviours;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;
import game.ground.Flora;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link FloraBehaviour} that teleports any adjacent worker to a
 * random valid location on the current map.
 *
 * <p>Previously hardcoded inside {@code MatureWarperTree.performFloraAction()}.
 * Extracted here so any flora can gain warp behaviour by registering
 * this behaviour at construction time.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class WarpBehaviour implements FloraBehaviour {

    private final Random random = new Random();
    private final Display display = new Display();

    /**
     * Teleports any adjacent worker to a random valid location on the map.
     *
     * @param flora    the flora performing this behaviour
     * @param location the flora's current location
     * @return always null — result is a side-effect
     */
    @Override
    public Void operate(Flora flora, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Actor actor = destination.getActor();

            if (actor != null && actor.hasAbility(GameAbilities.IS_WORKER)) {
                List<Location> validLocations = new ArrayList<>();

                for (int x = location.map().getXRange().min(); x <= location.map().getXRange().max(); x++) {
                    for (int y = location.map().getYRange().min(); y <= location.map().getYRange().max(); y++) {
                        Location candidate = location.map().at(x, y);
                        if (!candidate.containsAnActor() && candidate.canActorEnter(actor)) {
                            validLocations.add(candidate);
                        }
                    }
                }

                if (!validLocations.isEmpty()) {
                    Location randomLocation = validLocations.get(random.nextInt(validLocations.size()));
                    location.map().moveActor(actor, randomLocation);
                    display.println(actor + " was warped by " + flora + "!");
                }
            }
        }
        return null;
    }
}