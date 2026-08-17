package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.Damageable;

/**
 * Abstract base class for damage-over-time status effects.
 *
 * <p>Encapsulates the shared tick logic for any status that deals repeated damage
 * to a {@link Damageable} entity over a duration. Subclasses supply a name, damage
 * value, and duration.</p>
 *
 * <p><b>DRY:</b> eliminates duplicated tick/active logic across {@link Burned} and
 * {@link Poisoned}.</p>
 *
 * <p><b>OCP:</b> new damage-over-time effects can extend this class without touching
 * existing code.</p>
 *
 * <p><b>Re-application policy:</b> the base exposes {@link #extendDuration()} so a
 * subclass can choose to <i>stack in time</i> on re-application rather than reset.
 * This is the single mutator of the remaining duration, which is otherwise private.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public abstract class DamageOverTime implements Status {

    private final String effectName;
    private final int damagePerTurn;
    private final int maxDuration;
    private int turnsRemaining;

    private final Display display = new Display();

    /**
     * Constructs a DamageOverTime status with the given parameters.
     *
     * @param effectName    display name shown in console messages (e.g. "Burning")
     * @param damagePerTurn damage dealt each tick
     * @param duration      number of turns this status remains active
     */
    protected DamageOverTime(String effectName, int damagePerTurn, int duration) {
        this.effectName = effectName;
        this.damagePerTurn = damagePerTurn;
        this.maxDuration = duration;
        this.turnsRemaining = duration;
    }

    /**
     * Extends the remaining duration by one full original cycle, so that re-applying
     * an already-active effect lengthens it (stacks in time) rather than resetting it.
     * Intended for subclasses whose design is to stack on re-application (e.g. {@link Burned}).
     */
    protected void extendDuration() {
        this.turnsRemaining += maxDuration;
    }

    /**
     * Applies damage to the host each turn if it implements {@link Damageable}.
     * Duration decrements regardless, preventing the status from getting stuck on
     * non-damageable hosts.
     *
     * @param entity   the entity this status is attached to
     * @param location the location of the entity
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        entity.asCapability(Damageable.class).ifPresent(target -> {
            target.takeDamage(damagePerTurn);
            display.println(entity + " is " + effectName + "! (" + (turnsRemaining - 1) + " turns remaining)");
        });
        turnsRemaining--;
    }

    /**
     * @return true while turns remain; false when expired
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}