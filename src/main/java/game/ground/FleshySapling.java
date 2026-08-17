package game.ground;

import game.behaviours.GrowthBehaviour;

/**
 * Second growth stage of a fleshy tree.
 *
 * <p>Registers one behaviour at construction time:</p>
 * <ul>
 *   <li>{@link GrowthBehaviour} — 50% chance to grow into {@link MatureFleshyTree} every 25 turns</li>
 * </ul>
 *
 * @author Yong Leng Foong
 * @author Liviru
 * @version 2.0
 */
public class FleshySapling extends Flora {

    public FleshySapling() {
        super('v', "Fleshy Sapling");
        addBehaviour(new GrowthBehaviour(25, 0.50, MatureFleshyTree::new));
    }
}