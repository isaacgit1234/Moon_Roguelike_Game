package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing a solid wall.
 * Blocks all actors from entering.
 * @author Yong Leng Foong
 * @version 1.1
 */
public class Wall extends Ground {

    /**
     * Constructor for wall
     */
    public Wall() {
        super('#', "Wall");
    }

    /**
     * Walls cannot be entered by any actor
     *
     * @param actor The actor to check
     * @return false always - walls block all movement
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

}
