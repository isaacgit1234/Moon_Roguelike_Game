package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Depositable;

import java.util.Random;

/**
 * A jagged piece of aluminium scrap dropped when an AluminiumDoor is cut.
 *
 * Can only be deposited at the SuperComputer for 50 company credits.
 * Cannot be sold. Depositing has a 20% chance of cutting the worker for 5 damage -
 * shoving jagged metal into the deposit chute is dangerous.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AluminiumScrap extends AbstractItem implements Depositable {

    private final static int DEPOSIT_VALUE = 50;
    private final static int CUT_DAMAGE = 5;
    private final static double CUT_CHANCE = 0.20;

    private final Random rand = new Random();

    /**
     * Constructs an Aluminium Scrap, registering its weight for inventory capacity enforcement.
     */
    public AluminiumScrap() {
        super("Aluminium Scrap", '%', 2);
    }

    /** @return {@value #DEPOSIT_VALUE} credits */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * A 20% chance the worker gets cut and takes 5 damage from shoving jagged metal into the deposit chute.
     *
     * @param actor the actor depositing
     * @param map the current game mao
     * @return description of what happened
     */
    @Override
    public String onDeposit(Actor actor, GameMap map) {
        if (rand.nextDouble() < CUT_CHANCE) {
            actor.hurt(CUT_DAMAGE);
            return actor + " shoves the jagged scrap into the chute and gets cut! Takes " +
                    CUT_DAMAGE + " damage.";
        }
        return actor + " safely deposits the Aluminium Scrap.";
    }
}
