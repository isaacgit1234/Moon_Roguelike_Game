package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.alarm.AlarmSystem;
import game.behaviours.BehaviourPriority;
import game.behaviours.WanderBehaviour;
import game.capabilities.GameAbilities;
import game.inventories.BasicInventory;

/**
 * A security camera creature that roams the facility and triggers the alarm
 * whenever a contracted worker enters its detection radius.
 *
 * <p>The Watcher wanders the map and scans a configurable Manhattan-distance
 * radius each turn. If any actor with {@link GameAbilities#IS_WORKER} is
 * detected within range, the {@link AlarmSystem} is triggered and warning
 * messages are printed to the terminal.</p>
 *
 * <p>The Watcher does not attack. Its sole purpose is surveillance and alarm
 * activation. It has very high HP to be effectively unkillable without effort,
 * reinforcing its role as a persistent environmental hazard.</p>
 *
 * <p><b>SRP:</b> The Watcher only handles detection and alarm signalling.
 * What happens after the alarm triggers is entirely {@link AlarmSystem}'s
 * and the listeners' concern.</p>
 *
 * <p><b>OCP:</b> The trigger mechanism is isolated here. A second trigger type
 * requires a new class — not modifying this one.</p>
 *
 * @author Yong Leng Foong
 * @version 1.2
 */
public class Watcher extends BehaviouralActor {

    private final int detectionRadius;

    private static final int DEFAULT_RADIUS = 3;

    private static final int HIT_POINTS = 999;

    /**
     * Constructs a Watcher with the default detection radius.
     */
    public Watcher() {
        this(DEFAULT_RADIUS);
    }

    /**
     * Constructs a Watcher with a configurable detection radius.
     *
     * @param detectionRadius the Manhattan distance radius to scan for workers
     */
    public Watcher(int detectionRadius) {
        super("Watcher", 'W', HIT_POINTS, new BasicInventory());
        this.detectionRadius = detectionRadius;
        addBehaviour(BehaviourPriority.FIRST_PRIORITY, new WanderBehaviour());
        this.enableAbility(GameAbilities.IS_CREATURE);
    }

    /**
     * Each turn: scans the detection radius for workers and triggers the alarm
     * if found and not already active, then proceeds with normal behaviour.
     *
     * @param actions    available actions
     * @param lastAction the last action performed
     * @param map        current game map
     * @param display    display for terminal output
     * @return the chosen action
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location location = map.locationOf(this);
        AlarmSystem alarmSystem = AlarmSystem.getInstance();

        if (!alarmSystem.isActive() && isWorkerInRange(location, map)) {
            alarmSystem.triggerAlarm();
            display.println("*** FACILITY ALARM TRIGGERED! A Watcher has detected an intruder! ***");
            display.println("*** All doors are locked. Hostile entities are converging on your position! ***");
            display.println("*** Alarm expires in " + alarmSystem.getTurnsRemaining() + " turns. ***");
        }

        if (alarmSystem.isActive()) {
            display.println("*** ALARM ACTIVE — " + alarmSystem.getTurnsRemaining() + " turns remaining ***");
        }

        return super.playTurn(actions, lastAction, map, display);
    }

    /**
     * Scans all map tiles within {@code detectionRadius} using Manhattan distance.
     * Returns true if any actor with {@link GameAbilities#IS_WORKER} is found.
     *
     * @param location the Watcher's current location
     * @param map      the game map to scan
     * @return true if a worker is within detection range
     */
    private boolean isWorkerInRange(Location location, GameMap map) {
        int watcherX = location.x();
        int watcherY = location.y();

        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                int distance = Math.abs(x - watcherX) + Math.abs(y - watcherY);
                if (distance > 0 && distance <= detectionRadius) {
                    Actor candidate = map.at(x, y).getActor();
                    if (candidate != null && candidate.hasAbility(GameAbilities.IS_WORKER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}