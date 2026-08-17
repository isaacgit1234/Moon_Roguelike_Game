package game.ground;

import game.actors.Slime;
import game.behaviours.ActorSpawnBehaviour;
import game.behaviours.GrowthBehaviour;

/**
 * First growth stage of a fleshy tree.
 *
 * <p>Registers two behaviours at construction time:</p>
 * <ul>
 *   <li>{@link GrowthBehaviour} — 25% chance to grow into {@link FleshySapling} every 20 turns</li>
 *   <li>{@link ActorSpawnBehaviour} — spawns a Slime when a worker is nearby</li>
 * </ul>
 *
 * @author Yong Leng Foong
 * @author Liviru
 * @version 2.0
 */
public class FleshySprout extends Flora {

    public FleshySprout() {
        super('y', "Fleshy Sprout");
        addBehaviour(new GrowthBehaviour(20, 0.25, FleshySapling::new));
        addBehaviour(new ActorSpawnBehaviour(Slime::new));
    }
}