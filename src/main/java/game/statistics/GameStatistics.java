package game.statistics;

/**
 * Enumeration of game-level statistics that extend beyond the engine's built-in
 * {@link edu.monash.fit2099.engine.actors.ActorStatistics}.
 *
 * Adding a new game-wide numeric attribute (e.g. stamina, reputation) only requires
 * a new-entry here - no existing class is modified.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public enum GameStatistics {
    /**
     * The worker's credit balance. Capped at {@link game.economy.Wallet#MAX_CREDITS}.
     * Used by {@link game.economy.Wallet} and read/written via the engine's statistic system.
     */
    CREDITS
}
