package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.alarm.AlarmListener;
import game.alarm.AlarmSystem;
import game.behaviours.AlarmChaseOverrideBehaviour;
import game.behaviours.AttackBehaviour;
import game.behaviours.BehaviourPriority;
import game.behaviours.WanderBehaviour;
import game.capabilities.GameAbilities;
import game.capabilities.Infectable;
import game.inventories.BasicInventory;

/**
 * A reanimated former worker hostile to {@link ContractedWorker}s.
 * Prioritises attacking nearby workers; wanders otherwise.
 * Hit Points: 15, Attack: 1 damage at 10% hit chance.
 * Supports infection by Parasites. When infected, the Undead explodes instantly.
 * <p>Implements {@link AlarmListener} as Alarm Consequence 1. When the alarm
 * triggers, a priority-0 {@link AlarmChaseOverrideBehaviour} is injected into
 * the behaviour map, overriding wandering (priority 2) and attack (priority 1)
 * due to TreeMap's ascending key iteration. On alarm expiry it is removed,
 * restoring normal behaviour — without any conditional logic in
 * {@code playTurn()}.</p>
 *
 * <p><b>OCP:</b> The alarm consequence is added by injecting a behaviour.
 * {@code playTurn()} in {@link BehaviouralActor} is never touched.</p>
 *
 * <p><b>DIP:</b> Undead depends on the {@link AlarmListener} abstraction.
 * {@link AlarmSystem} calls back through the interface, not Undead directly.</p>
 *
 * @author Yong Leng Foong
 * @author Kumali Wickremasinghe
 * @version 1.2
 */
public class Undead extends BehaviouralActor implements AlarmListener, Infectable {

    private static final int HIT_POINTS = 15;
    private static final int ATTACK_DAMAGE = 1;
    private static final int HIT_CHANCE_PERCENT = 10;

    /**
     * Instant death damage used when infected.
     * Avoids magic numbers.
     */
    private static final int INSTANT_DEATH_DAMAGE =
            Integer.MAX_VALUE;

    /** Reusable chase behaviour instance injected on alarm trigger. */
    private final AlarmChaseOverrideBehaviour alarmChaseBehaviour;

    /**
     * Constructs an Undead with attack behaviour prioritised over wander,
     * and registers it with the {@link AlarmSystem}.
     * If the alarm is already active at spawn time, chase behaviour is
     * applied immediately.
     */
    public Undead() {
        super("Undead", 'Ѫ', HIT_POINTS, new BasicInventory());
        addBehaviour(BehaviourPriority.FIRST_PRIORITY, new AttackBehaviour(ATTACK_DAMAGE, HIT_CHANCE_PERCENT));
        addBehaviour(BehaviourPriority.SECOND_PRIORITY, new WanderBehaviour());
        this.alarmChaseBehaviour = new AlarmChaseOverrideBehaviour();

        this.enableAbility(GameAbilities.BYPASSES_LOCKDOWN);
        this.enableAbility(GameAbilities.IS_UNDEAD);
        this.enableAbility(GameAbilities.IS_CREATURE);
        this.enableAbility(GameAbilities.IS_INFECTABLE);

        AlarmSystem.getInstance().register(this);

        if (AlarmSystem.getInstance().isActive()) {
            onAlarmTriggered();
        }
    }

    /**
     * Injects the chase behaviour at priority {@link BehaviourPriority#ALARM_OVERRIDE_PRIORITY},
     * which overrides attack (1) and wander (2) via TreeMap ascending order.
     */
    @Override
    public void onAlarmTriggered() {
        addBehaviour(BehaviourPriority.ALARM_OVERRIDE_PRIORITY, alarmChaseBehaviour);
    }

    /**
     * Removes the chase override, restoring normal attack-then-wander behaviour.
     */
    @Override
    public void onAlarmExpired() {
        removeBehaviour(BehaviourPriority.ALARM_OVERRIDE_PRIORITY);
    }

    /**
     * On death, unregisters from the AlarmSystem to prevent stale listener
     * references and memory leaks.
     *
     * @param map the map this actor is on
     * @return result string describing the death
     */
    @Override
    public String unconscious(GameMap map) {
        AlarmSystem.getInstance().unregister(this);
        return super.unconscious(map);
    }

    /**
     * Infection effect for Undead.
     * Causes immediate explosion/death.
     *
     * @param source actor infecting the Undead
     * @param location location of infection
     * @param map current game map
     * @return infection result description
     */
    @Override
    public String infect(Actor source, Location location, GameMap map) {
        this.hurt(INSTANT_DEATH_DAMAGE);
        return source + " infects " + this + ". The undead explodes instantly.";
    }
}