package game.ground;

import game.behaviours.WarpBehaviour;

/**
 * Final growth stage of a warper tree.
 *
 * <p>Registers one behaviour at construction time:</p>
 * <ul>
 *   <li>{@link WarpBehaviour} — teleports adjacent workers to a random map location</li>
 * </ul>
 *
 * @author Liviru
 * @author Yong Leng Foong
 * @version 2.0
 */
public class MatureWarperTree extends Flora {

    public MatureWarperTree() {
        super('W', "Mature Warper Tree");
        addBehaviour(new WarpBehaviour());
    }
}