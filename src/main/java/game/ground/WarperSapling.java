package game.ground;

import game.behaviours.GrowthBehaviour;

/**
 * First growth stage of a warper tree.
 *
 * <p>Registers one behaviour at construction time:</p>
 * <ul>
 *   <li>{@link GrowthBehaviour} — 25% chance to grow into {@link MatureWarperTree} every 20 turns</li>
 * </ul>
 *
 * @author Liviru
 * @author Yong Leng Foong
 * @version 2.0
 */
public class WarperSapling extends Flora {

    public WarperSapling() {
        super('w', "Warper Sapling");
        addBehaviour(new GrowthBehaviour(20, 0.25, MatureWarperTree::new));
    }
}