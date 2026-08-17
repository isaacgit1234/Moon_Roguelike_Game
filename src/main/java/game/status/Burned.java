package game.status;

import edu.monash.fit2099.engine.actors.Actor;

import java.util.List;

/**
 * A status effect that deals burn damage per turn for a configurable duration.
 *
 * <p><b>Stacking (in time):</b> re-applying Burned to an already-burning actor does
 * NOT reset the timer — it {@link #extend() extends} the remaining duration by one
 * full original cycle. Two consecutive default burns therefore last roughly twice as
 * long, at the same damage per turn. The original burn's damage value is retained;
 * subsequent applications only add time. This is the intended "burns stack" design.</p>
 *
 * <p><b>DRY:</b> the apply-or-extend decision is centralised in
 * {@link #applyOrExtend(Actor)} / {@link #applyOrExtend(Actor, int, int)} so
 * {@link game.ground.Fire}, {@link game.items.Lantern}, the storm condition, and the
 * temperature modifier share one implementation instead of each duplicating the
 * statusesOf/branch block.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class Burned extends DamageOverTime {

    private static final int DEFAULT_DAMAGE = 1;
    private static final int DEFAULT_DURATION = 5;

    /**
     * Constructs a Burned status with the default fire values (1 dmg, 5 turns).
     */
    public Burned() {
        super("Burning", DEFAULT_DAMAGE, DEFAULT_DURATION);
    }

    /**
     * Constructs a Burned status with configurable damage and duration.
     *
     * @param damagePerTurn damage dealt each tick
     * @param duration      number of turns the burn lasts
     */
    public Burned(int damagePerTurn, int duration) {
        super("Burning", damagePerTurn, duration);
    }

    /**
     * Extends this burn by one full original cycle instead of resetting it,
     * implementing the stacking-in-time design.
     */
    public void extend() {
        extendDuration();
    }

    /**
     * Applies a default Burned to the actor, or extends the existing one if the actor
     * is already burning.
     *
     * @param actor the actor to ignite or prolong burning on
     */
    public static void applyOrExtend(Actor actor) {
        applyOrExtend(actor, DEFAULT_DAMAGE, DEFAULT_DURATION);
    }

    /**
     * Applies a Burned with the given damage and duration, or extends the existing one
     * if the actor is already burning. When extending, the existing burn's own values
     * are kept — only its duration grows.
     *
     * @param actor         the actor to ignite or prolong burning on
     * @param damagePerTurn damage for a freshly-applied burn
     * @param duration      duration for a freshly-applied burn
     */
    public static void applyOrExtend(Actor actor, int damagePerTurn, int duration) {
        List<Burned> active = actor.statusesOf(Burned.class);
        if (active.isEmpty()) {
            actor.addStatus(new Burned(damagePerTurn, duration));
        } else {
            active.get(0).extend();
        }
    }

    /**
     * Returns a string representation of burned status.
     *
     * @return burned status
     */
    @Override
    public String toString() {
        return "Burned";
    }
}