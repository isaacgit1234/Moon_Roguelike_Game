package game.alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that manages the global state of the facility alarm system.
 *
 * <p>Exactly one alarm system exists per game session. Entities that react to
 * the alarm register as {@link AlarmListener}s. When triggered, all listeners
 * are notified. Each game turn {@link #tick()} must be called — when the
 * countdown reaches zero, all listeners are notified of expiry.</p>
 *
 * <p><b>Design Pattern — Singleton:</b> One alarm panel for the whole facility.
 * All parts of the game reference the same alarm state without constructor
 * coupling.</p>
 *
 * <p><b>Design Pattern — Observer:</b> This is the Subject. {@link AlarmListener}s
 * are Observers. The Subject knows nothing about concrete observer types —
 * it only calls the interface methods.</p>
 *
 * <p><b>SRP:</b> One responsibility — track alarm state, countdown, and
 * broadcast state changes to listeners.</p>
 *
 * <p><b>OCP:</b> New alarm consequences require only a new {@link AlarmListener}
 * implementation and a call to {@link #register} — zero changes here.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AlarmSystem {

    private static AlarmSystem instance;

    private static final int ALARM_DURATION = 20;

    private boolean active;

    private int turnsRemaining;

    private final List<AlarmListener> listeners;

    /**
     * Private constructor enforces Singleton pattern.
     */
    private AlarmSystem() {
        this.active = false;
        this.turnsRemaining = 0;
        this.listeners = new ArrayList<>();
    }

    /**
     * Returns the single global instance, creating it on first call.
     *
     * @return the singleton AlarmSystem instance
     */
    public static AlarmSystem getInstance() {
        if (instance == null) {
            instance = new AlarmSystem();
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     * Must be called at game start to avoid stale state between runs.
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Registers an {@link AlarmListener}. Duplicate registrations are ignored.
     *
     * @param listener the listener to register
     */
    public void register(AlarmListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Unregisters a previously registered {@link AlarmListener}.
     * Safe to call even if the listener was never registered.
     *
     * @param listener the listener to remove
     */
    public void unregister(AlarmListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns whether the alarm is currently active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns turns remaining on the current alarm countdown.
     *
     * @return turns remaining, 0 if inactive
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Triggers the alarm if not already active.
     * Resets the countdown and notifies all listeners.
     */
    public void triggerAlarm() {
        if (!active) {
            active = true;
            turnsRemaining = ALARM_DURATION;
            notifyTriggered();
        }
    }

    /**
     * Advances the alarm countdown by one turn.
     * Must be called once per game turn from {@code EclipseNebula.gameLoop()}.
     * When the countdown hits zero, the alarm expires and listeners are notified.
     */
    public void tick() {
        if (active) {
            turnsRemaining--;
            if (turnsRemaining <= 0) {
                active = false;
                notifyExpired();
            }
        }
    }

    /**
     * Notifies all listeners that the alarm triggered.
     * Iterates a copy to safely handle listeners that unregister mid-callback.
     */
    private void notifyTriggered() {
        for (AlarmListener listener : new ArrayList<>(listeners)) {
            listener.onAlarmTriggered();
        }
    }

    /**
     * Notifies all listeners that the alarm expired.
     * Iterates a copy for the same safety reason.
     */
    private void notifyExpired() {
        for (AlarmListener listener : new ArrayList<>(listeners)) {
            listener.onAlarmExpired();
        }
    }
}