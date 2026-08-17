package game.ground;

import edu.monash.fit2099.engine.positions.Ground;
import game.spawning.DeprecatedHoleRule;

/**
 * Hole in deprecated.
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 1.0
 */
public class Hole extends SpawnerGround {

    public Hole() {
        super('O', "Hole", new DeprecatedHoleRule(), true);
    }

    @Override
    protected Ground copySpawner() {
        return new Hole();
    }
}