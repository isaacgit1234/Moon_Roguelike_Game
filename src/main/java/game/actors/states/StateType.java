package game.actors.states;

/**
 * Identifies each CreatureState type without requiring instanceof checks.
 * Used by onExit() to determine which transition effect to apply.
 *
 * <p><b>OCP:</b> new states add a new enum constant without modifying existing states.</p>
 * <p><b>DIP:</b> onExit() depends on this abstraction, not concrete state classes.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 1.0
 */
public enum StateType {
    IDLE, HUNTING, DEFENSIVE, FRENZY
}