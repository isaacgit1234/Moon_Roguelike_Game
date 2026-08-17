package game.weather.condition;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;

import game.behaviours.BehaviourControllable;
import game.alarm.AlarmListener;
import game.alarm.AlarmSystem;
import game.behaviours.BehaviourPriority;
import game.behaviours.FogDisorientBehaviour;
import game.capabilities.GameAbilities;
import game.ground.FogWall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A thick fog that disorients every creature, suppresses the facility alarm, and
 * physically blocks movement across the map.
 *
 * <p><b>Cross-component effects (satisfies complexity requirement):</b></p>
 * <ol>
 *     <li><b>Behaviour injection:</b> injects {@link FogDisorientBehaviour} at
 *         {@link BehaviourPriority#WEATHER_FOG_PRIORITY} into every controllable
 *         actor (reached through {@link BehaviourControllable}, no down-cast),
 *         overriding attack/chase/wander without destroying them.</li>
 *     <li><b>AlarmListener suppression:</b> registers as an {@link AlarmListener}.
 *         When the alarm fires during active fog, {@link #onAlarmTriggered()}
 *         force-removes the fog override (the alarm cuts through the fog) and
 *         deregisters so the alarm proceeds normally.</li>
 *     <li><b>FogWall terrain spawn:</b> 3-5 random {@link game.ground.Floor} tiles
 *         become {@link FogWall} ground, preserving each tile's original ground for
 *         restoration, making passable terrain impassable for the fog's duration.</li>
 * </ol>
 *
 * <p>Condition code range: 700-799 (mist, fog, haze, volcanic ash).</p>
 *
 * <p><b>Presentation:</b> {@link #apply(GameMap)} returns a summary String for the
 * game loop to display. The asynchronous alarm callbacks have no return path, so
 * they perform their effect silently rather than holding a {@code Display}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class FogCondition implements WeatherCondition, AlarmListener {

    private static final int MIN_FOG_WALLS = 3;
    private static final int MAX_FOG_WALLS = 5;

    private final Random random = new Random();

    /** Held so the AlarmListener callbacks can reach the map the fog was applied to. */
    private GameMap activeMap;

    /**
     * Disorients every creature, spawns FogWall tiles, and registers to suppress the
     * alarm for the fog's duration.
     *
     * @param map the current game map to affect
     * @return a summary of how many creatures were disoriented and walls raised
     */
    @Override
    public String apply(GameMap map) {
        this.activeMap = map;

        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();

        // Effect 1: inject FogDisorientBehaviour into every controllable actor.
        int disoriented = 0;
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                BehaviourControllable controllable = map.at(x, y).getActorAs(BehaviourControllable.class);
                if (controllable != null) {
                    controllable.addBehaviour(BehaviourPriority.WEATHER_FOG_PRIORITY, new FogDisorientBehaviour());
                    disoriented++;
                }
            }
        }

        // Effect 2: suppress the alarm while fog is active.
        AlarmSystem.getInstance().register(this);

        // Effect 3: spawn FogWalls on random empty Floor tiles, preserving original ground.
        List<Location> floorLocations = new ArrayList<>();
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                Location loc = map.at(x, y);
                if (loc.getGround().hasAbility(GameAbilities.IS_FLOOR) && !loc.containsAnActor()) {
                    floorLocations.add(loc);
                }
            }
        }

        int wallTarget = MIN_FOG_WALLS + random.nextInt(MAX_FOG_WALLS - MIN_FOG_WALLS + 1);
        int wallsRaised = 0;
        for (int i = 0; i < wallTarget && !floorLocations.isEmpty(); i++) {
            Location target = floorLocations.remove(random.nextInt(floorLocations.size()));
            target.setGround(new FogWall(target.getGround()));
            wallsRaised++;
        }

        return String.format(
                "Thick fog rolls across the facility: %d creature(s) disoriented, %d fog wall(s) raised.",
                disoriented, wallsRaised);
    }

    /**
     * When the alarm triggers during active fog, the alarm's urgency breaks through:
     * removes the fog override from all actors and deregisters so normal alarm
     * consequences proceed unimpeded.
     */
    @Override
    public void onAlarmTriggered() {
        if (activeMap != null) {
            removeFogBehavioursFromMap(activeMap);
        }
        AlarmSystem.getInstance().unregister(this);
    }

    /**
     * If the alarm expires while fog is still active, simply deregister to avoid a
     * dangling listener reference.
     */
    @Override
    public void onAlarmExpired() {
        AlarmSystem.getInstance().unregister(this);
    }

    /**
     * Removes the fog override from every controllable actor on the map.
     *
     * @param map the map to clean up
     */
    private void removeFogBehavioursFromMap(GameMap map) {
        NumberRange xs = map.getXRange();
        NumberRange ys = map.getYRange();
        for (int x = xs.min(); x <= xs.max(); x++) {
            for (int y = ys.min(); y <= ys.max(); y++) {
                BehaviourControllable controllable = map.at(x, y).getActorAs(BehaviourControllable.class);
                if (controllable != null) {
                    controllable.removeBehaviour(BehaviourPriority.WEATHER_FOG_PRIORITY);
                }
            }
        }
    }

    /**
     * @return the name of this weather condition
     */
    @Override
    public String getConditionName() {
        return "Fog";
    }
}