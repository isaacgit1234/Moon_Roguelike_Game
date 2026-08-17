package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behaviour that drives a hostile actor to chase the nearest worker across
 * the entire map.
 *
 * <p>This is Alarm Consequence 1. It is injected at priority {@code 0} into a
 * {@link game.actors.BehaviouralActor}'s TreeMap when the alarm triggers,
 * automatically overriding all higher-keyed behaviours (attack at 1, wander
 * at 2) due to ascending TreeMap iteration order.</p>
 *
 * <p>The behaviour scans the full map for the nearest {@link GameAbilities#IS_WORKER}
 * actor using Manhattan distance, then steps one tile toward them via valid exits.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AlarmChaseOverrideBehaviour implements Behaviour<Actor, Action> {

    /**
     * Finds the nearest worker on the map and moves one step toward them.
     *
     * @param actor    the actor executing this behaviour
     * @param location the actor's current location
     * @return a {@link MoveActorAction} toward the nearest worker, or {@code null}
     *         if no worker is reachable
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();
        for (Exit exit : location.getExits()) {
            Actor adjacent = exit.getDestination().getActor();
            if (adjacent != null && adjacent.hasAbility(GameAbilities.IS_WORKER)) {
                return null;
            }
        }
        Actor nearestWorker = findNearestWorker(actor, location, map);
        if (nearestWorker == null) {
            return null;
        }
        return stepToward(actor, location, map.locationOf(nearestWorker));
    }

    /**
     * Scans every tile on the map and returns the closest actor with
     * {@link GameAbilities#IS_WORKER}, excluding the searching actor itself.
     *
     * @param self the searching actor
     * @param here the searching actor's location
     * @param map  the game map
     * @return the nearest worker, or {@code null} if none found
     */
    private Actor findNearestWorker(Actor self, Location here, GameMap map) {
        Actor nearest = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Actor candidate = map.at(x, y).getActor();
                if (candidate != null && candidate != self
                        && candidate.hasAbility(GameAbilities.IS_WORKER)) {
                    int dist = manhattanDistance(here, map.at(x, y));
                    if (dist < shortestDistance) {
                        shortestDistance = dist;
                        nearest = candidate;
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * Steps one tile closer to the target through any valid exit.
     *
     * @param actor  the actor moving
     * @param here   current location
     * @param target destination to approach
     * @return a move action, or {@code null} if no exit brings the actor closer
     */
    private Action stepToward(Actor actor, Location here, Location target) {
        int currentDistance = manhattanDistance(here, target);
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)
                    && manhattanDistance(destination, target) < currentDistance) {
                return new MoveActorAction(destination, exit.getName());
            }
        }
        // No step brings us closer — fall back to any valid random move
        List<Action> validMoves = new ArrayList<>();
        for (Exit exit : here.getExits()) {
            if (exit.getDestination().canActorEnter(actor)) {
                validMoves.add(new MoveActorAction(
                        exit.getDestination(), exit.getName()));
            }
        }
        if (!validMoves.isEmpty()) {
            return validMoves.get(new Random().nextInt(validMoves.size()));
        }
        return null;
    }

    /**
     * Computes the Manhattan distance between two locations.
     *
     * @param a first location
     * @param b second location
     * @return Manhattan distance
     */
    private int manhattanDistance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}