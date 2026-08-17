package game.behaviours;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ground.Flora;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A {@link FloraBehaviour} that grows the flora into its next stage
 * after a fixed number of turns with a given probability.
 *
 * <p>Previously, each flora subclass hardcoded its growth interval,
 * probability, and next-stage class directly inside {@code performFloraAction()}.
 * Those values are now injected at construction time, making this behaviour
 * reusable across all flora tiers without modification.</p>
 *
 * <p><b>OCP:</b> New growth patterns require only a new {@link GrowthBehaviour}
 * instance with different parameters — no existing class is modified.</p>
 *
 * <p><b>DRY:</b> Growth logic is defined once here instead of duplicated across
 * FleshySprout, FleshySapling, and WarperSapling.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class GrowthBehaviour implements FloraBehaviour {

    private final int interval;
    private final double probability;
    private final Supplier<Ground> nextStage;
    private final Random random = new Random();

    /**
     * Constructs a GrowthBehaviour.
     *
     * @param interval    number of turns between growth attempts
     * @param probability probability of growing when the interval is reached
     * @param nextStage   supplier that constructs the next-stage ground
     */
    public GrowthBehaviour(int interval, double probability, Supplier<Ground> nextStage) {
        this.interval = interval;
        this.probability = probability;
        this.nextStage = nextStage;
    }

    /**
     * Attempts to grow the flora into its next stage.
     * Only fires when {@code flora.getTurnsAlive() % interval == 0}.
     *
     * @param flora    the flora attempting to grow
     * @param location the flora's current location
     * @return always null — result is a side-effect
     */
    @Override
    public Void operate(Flora flora, Location location) {
        if (flora.getTurnsAlive() % interval == 0 && random.nextDouble() < probability) {
            location.setGround(nextStage.get());
        }
        return null;
    }
}