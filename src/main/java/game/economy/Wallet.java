package game.economy;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.statistics.GameStatistics;
/**
 * Manages a {@link Actor}'s  credit balance by wrapping the engine's statistic system.
 *
 * Enforces a hard cap of {@value #MAX_CREDITS} credits. Provides a clean,
 * intention-revealing API so no other class needs to know about the underlying
 * {@link GameStatistics#CREDITS} enum or {@link StatisticOperations} calls
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class Wallet {
    /**
     * Maximum credits a worker's wallet can hold.
     */
    public static final int MAX_CREDITS = 1000;

    private final Actor owner;

    /**
     * Private constructor - use {@link #of(Actor)} to obtain a wallet
     *
     * @param owner the worker whose credit balance this wallet manage
     * @param initialise if true, registers the CREDITS statistics at zero
     */
    private Wallet(Actor owner, boolean initialise) {
        this.owner = owner;
        if (initialise) {
            // BaseStatistic(max) sets current = max, so we decrease back to 0.
            // This is the only way to set a cap without a starting balance.
            owner.addNewStatistic(GameStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
            owner.modifyStatistic(GameStatistics.CREDITS, StatisticOperations.DECREASE, MAX_CREDITS);
        }
    }

    /**
     * Retrieves the Wallet for the given worker, initialise it if necessary.
     * This is the single entry point for Wallet access - prevents duplicate
     * statistic registration.
     *
     * @param actor the actor whose wallet to retrieve
     * @return the Wallet for this worker
     */
    public static Wallet of (Actor actor){
        boolean needsInit = !actor.hasStatistic(GameStatistics.CREDITS);
        return new Wallet(actor,needsInit);
    }

    /**
     * Returns the current credit balance.
     *
     * @return credit balance
     */
    public int getCredits(){
        return owner.getStatistic(GameStatistics.CREDITS);
    }

    /**
     * Adds credits, clamping the result to {@value #MAX_CREDITS}.
     * Any excess beyond the cap is silently discarded.
     *
     * @param amount the number of credits to add (non-negative)
     */
    public void addCredits(int amount){
        int current = getCredits();
        int actualGain = Math.min(amount, MAX_CREDITS -  current);
        if (actualGain > 0) {
            owner.modifyStatistic(GameStatistics.CREDITS, StatisticOperations.INCREASE, actualGain);
        }
    }

    /**
     * Deducts credits from the balance if the actor can afford it.
     *
     * @param amount the amount of credits to deduct (non-negative)
     * @return {@code true} if the deduction was successful,
     *         {@code false} if insufficient funds
     */
    public boolean deductCredits(int amount){
        if (!canAfford(amount)) return false;
        owner.modifyStatistic(GameStatistics.CREDITS, StatisticOperations.DECREASE, amount);
        return true;
    }

    /**
     * Deducts up to {@code amount} credits, clamping to current balance.
     * Used for penalty/glitch effects that drain whatever the actor has.
     *
     * @param amount the intended deduction
     */
    public void deductCreditsClamped(int amount) {
        int actual = Math.min(amount, getCredits());
        if (actual > 0) {
            owner.modifyStatistic(GameStatistics.CREDITS, StatisticOperations.DECREASE, actual);
        }
    }

    /**
     * Returns whether the worker can afford a given cost
     *
     * @param cost the cost to buy an item
     * @return true if current balance is at least {@code cost}
     */
    public boolean canAfford(int cost){
        return getCredits() >= cost;
    }
}
