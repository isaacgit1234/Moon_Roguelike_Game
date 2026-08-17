package game.quota;

/**
 * Observer contract for entities that react to quota cycle outcomes.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface QuotaListener {
    /**
     * Called when the worker meets the quota before deadline
     */
    void onQuotaMet();

    /**
     * Called when the deadline expires without meeting the quota
     */
    void onQuotaFailed();
}
