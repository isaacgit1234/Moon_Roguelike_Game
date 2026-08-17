package game.quota;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that manages the global quota cycle.
 *
 * Tracks company credits deposited, the current quota target, and the
 * turn countdown. Each tick() call advances the deadline. When the deadline
 * hits zero, notifies all QuotaListeners of the outcome and resets or ends
 * the cycle.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class QuotaManager {

    private static QuotaManager instance;

    private static final int BASE_QUOTA = 100;
    private static final int BASE_TURN_LIMIT = 200;
    private static final double QUOTA_SCALE = 1.05;
    private static final double TURN_SCALE = 1.10;

    private int companyCredits;
    private int quota;
    private int turnLimit;
    private int turnsRemaining;

    private final List<QuotaListener> listeners = new ArrayList<>();

    /**
     * Terminal flag. Once the quota is failed, the cycle is over: the spec
     * has the company fire adjacent workers once, after which survivors simply
     * wind down. This flag stops {@link #tick()} from re-evaluating (and
     * re-firing {@link QuotaListener#onQuotaFailed()}) on every subsequent turn.
     */
    private boolean ended;

    /**
     * Private constructor enforces Singleton pattern
     */
    private QuotaManager() {
        this.companyCredits = 0;
        this.quota = BASE_QUOTA;
        this.turnLimit = BASE_TURN_LIMIT;
        this.turnsRemaining = BASE_TURN_LIMIT;
        this.ended = false;
    }

    /**
     * Returns the single global instance, creating it on first call.
     */
    public static QuotaManager getInstance() {
        if (instance == null) {
            instance = new QuotaManager();
        }
        return instance;
    }

    /**
     * Resets the singleton. Call at game start to avoid stale state.
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Registers a QuotaListener. Duplicates are ignored.
     */
    public void register(QuotaListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Advances the quota countdown by one turn.
     *
     * <p>Does nothing once the cycle has ended. When {@code turnsRemaining}
     * reaches zero, evaluates the quota: if met, scales the quota/turn limit up
     * and restarts the countdown; if not met, ends the cycle and notifies
     * listeners exactly once.</p>
     */
    public void tick() {
        if (ended) {
            return;
        }
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            if (companyCredits >= quota) {
                quota = (int) Math.ceil(quota * QUOTA_SCALE);
                turnLimit = (int) Math.ceil(turnLimit * TURN_SCALE);
                companyCredits = 0;
                turnsRemaining = turnLimit;
                notifyQuotaMet();
            } else {
                ended = true;
                notifyQuotaFailed();
            }
        }
    }

    /**
     * Returns current company credits - useful for display / debugging.
     */
    public int getCompanyCredits() {
        return companyCredits;
    }

    /**
     * Adds credits to the current company credit pool toward the quota.
     *
     * @param credits number of credits to add
     */
    public void addCompanyCredits(int credits) {
        if (credits > 0 && !ended) {
            this.companyCredits += credits;
        }
    }

    /**
     * Returns current quota target
     */
    public int getQuota() {
        return quota;
    }

    /**
     * Returns turns remaining until deadline
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * @return {@code true} once the quota has been failed and the cycle ended
     */
    public boolean isEnded() {
        return ended;
    }

    private void notifyQuotaMet() {
        for (QuotaListener listener : listeners) {
            listener.onQuotaMet();
        }
    }

    private void notifyQuotaFailed() {
        for (QuotaListener listener : listeners) {
            listener.onQuotaFailed();
        }
    }
}
