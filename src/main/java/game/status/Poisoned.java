package game.status;

/**
 * A damage-over-time status effect that deals poison damage each turn.
 * Self-expires when remaining turns reach zero.
 *
 * Used by multiple sources (Apple, Puddle, Vent, VoidStalker) with differing
 * damage and duration — both injected via constructor.
 *
 * @author Isaac
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 2.0
 */
public class Poisoned extends DamageOverTime {

    /**
     * Constructs a Poisoned status with configurable damage and duration.
     *
     * @param damagePerTurn damage dealt to the host each tick
     * @param duration      number of turns this status remains active
     */
    public Poisoned(int damagePerTurn, int duration) {
        super("Poisoned", damagePerTurn, duration);
    }

    /**
     * Returns a string representation of the poisoned status.
     *
     * @return "Poisoned"
     */
    @Override
    public String toString() {
        return "Poisoned";
    }
}