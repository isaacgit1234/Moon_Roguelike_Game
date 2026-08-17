package game.ground;

import edu.monash.fit2099.engine.positions.Ground;
import game.spawning.OverflowHoleRule;

/**
 * Hole in 20-overflow.
 *
 * @author Kumali Wickremasinghe
 * @version 1.0
 */
public class OverflowHole extends SpawnerGround {

    public OverflowHole() {
        super('O', "Overflow Hole", new OverflowHoleRule(), true);
    }

    @Override
    protected Ground copySpawner() {
        return new OverflowHole();
    }
}