package game.alarm;

/**
 * Interface for any entity that wishes to react to the facility alarm system.
 *
 * <p>This is the Observer contract. The {@link AlarmSystem} (Subject) notifies
 * all registered implementors when the alarm state changes, without needing to
 * know their concrete types.</p>
 *
 * <p><b>ISP:</b> Two methods only — nothing more is forced onto implementors.</p>
 * <p><b>DIP:</b> {@link AlarmSystem} depends on this abstraction, not on concrete
 * classes like {@code Door} or {@code Undead}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface AlarmListener {

    /**
     * Called when the facility alarm is triggered.
     * Implementors should begin their alarm-response behaviour here.
     */
    void onAlarmTriggered();

    /**
     * Called when the alarm period has expired and the facility returns to normal.
     * Implementors should revert any alarm-response behaviour here.
     */
    void onAlarmExpired();
}