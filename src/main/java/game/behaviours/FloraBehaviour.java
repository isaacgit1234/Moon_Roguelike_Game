package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import game.ground.Flora;

/**
 * Convenience type alias for a behaviour that operates on {@link Flora}.
 *
 * <p>Flora behaviours produce side-effects (ground replacement, actor spawning,
 * teleportation) rather than returning a result, so the result type is
 * {@link Void} and {@code operate()} always returns {@code null}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface FloraBehaviour extends Behaviour<Flora, Void> {
}