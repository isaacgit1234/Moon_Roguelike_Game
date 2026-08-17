package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import game.actors.BehaviouralActor;

/**
 * Capability for an actor whose prioritised behaviour set can be modified at
 * runtime by external world systems (weather, alarm) without those systems
 * knowing the actor's concrete type.
 *
 * <p><b>Why this exists (DIP):</b> the fog condition, the freeze branch of the
 * temperature modifier, and the alarm listeners all need to inject and remove
 * behaviours on actors. Without this interface they would down-cast to
 * {@link BehaviouralActor} after a {@code hasAbility} guard - a concrete
 * dependency the engine explicitly lets you avoid via
 * {@link edu.monash.fit2099.engine.positions.Location#getActorAs(Class)}. Callers
 * now depend on this abstraction and obtain it with
 * {@code location.getActorAs(BehaviourControllable.class)}: no cast, no concrete
 * coupling.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface BehaviourControllable {

    /**
     * Registers a behaviour at the given priority (lower integer = higher
     * priority). Replaces any behaviour already registered at that priority.
     *
     * @param priority  the priority key, drawn from {@link game.behaviours.BehaviourPriority}
     * @param behaviour the behaviour to register
     */
    void addBehaviour(int priority, Behaviour<Actor, Action> behaviour);

    /**
     * Removes the behaviour registered at the given priority, if any.
     *
     * @param priority the priority key to clear
     */
    void removeBehaviour(int priority);
}