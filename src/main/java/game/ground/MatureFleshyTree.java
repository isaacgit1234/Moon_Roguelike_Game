package game.ground;

import game.actors.Undead;
import game.behaviours.ActorSpawnBehaviour;

/**
 * Final growth stage of a fleshy tree.
 *
 * <p>Registers one behaviour at construction time:</p>
 * <ul>
 *   <li>{@link ActorSpawnBehaviour} — spawns an Undead when a worker is nearby</li>
 * </ul>
 *
 * @author Yong Leng Foong
 * @author Liviru
 * @version 2.0
 */
public class MatureFleshyTree extends Flora {

    public MatureFleshyTree() {
        super('Y', "Mature Fleshy Tree");
        addBehaviour(new ActorSpawnBehaviour(Undead::new));
    }
}