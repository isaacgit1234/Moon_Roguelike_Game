package game.actors;

import game.behaviours.BehaviourPriority;
import game.behaviours.ConsumeBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.GameAbilities;
import game.inventories.BasicInventory;

/**
 * A non-hostile gluttonous slime that wanders and consumes items on the ground.
 * Hit points: 25. Cannot attack. Suffers the same effects as workers when consuming.
 *
 * @author Yong Leng Foong
 * @version 1.1
 *  @author Kumali Wickremasinghe
 *
 */
public class Slime extends BehaviouralActor {

    private static final int HIT_POINTS = 25;

    /**
     * Constructs a Slime that prioritises consuming before wandering.
     */
    public Slime() {
        super("Slime", '⍾', HIT_POINTS, new BasicInventory());
        addBehaviour(BehaviourPriority.FIRST_PRIORITY, new ConsumeBehaviour());
        addBehaviour(BehaviourPriority.SECOND_PRIORITY, new WanderBehaviour());
        this.enableAbility(GameAbilities.IS_SLIME);
        this.enableAbility(GameAbilities.IS_CREATURE);
    }
}