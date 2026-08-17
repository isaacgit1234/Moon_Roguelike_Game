package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;

import game.capabilities.Damageable;

/**
 * Abstract base class for all living characters in Garbage Collection Inc.
 * Bridges the unmodifiable engine {@link Actor} with the game's
 * {@link Damageable} contract, so that status effects like
 * {@link game.status.Poisoned} and {@link game.status.Burned} can deal
 * damage without coupling to engine's Actor class directly
 *
 *  @author Yong Leng Foong
 *  @version 2.0
 */
public abstract class GameCharacter extends Actor implements Damageable {
    /**
     * Constructs a GameCharacter with the given attributes and inventory
     *
     * @param name The name of the character
     * @param displayChar The character used to display this actor on the map
     * @param hitPoints The starting hit points
     * @param inventory The inventory this character carries
     */
    public GameCharacter(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
    }

    /**
     * Delegates damage to the engine's {@code hurt()} method.
     *
     * @param amount the amount of damage to deal
     */
    @Override
    public void takeDamage(int amount) {
        this.hurt(amount);
    }
}
