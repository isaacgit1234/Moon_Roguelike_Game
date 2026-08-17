package game.capabilities;

/**
 * A contract for any game entity that can receive damage.
 * Decouples status effects like {@link game.status.Poisoned} and
 * {@link game.status.Burned} from the engine's {@code Actor} class.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Damageable {
    /**
     * Applies damage to this entity.
     *
     * @param amount the amount of damage to deal
     */
    void takeDamage(int amount);
}
