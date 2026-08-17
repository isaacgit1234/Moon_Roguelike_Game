package game.actors;

import game.behaviours.BehaviourPriority;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.GameAbilities;
import game.inventories.BasicInventory;

/**
 * A parasitic creature that infects nearby valid targets then dies.
 * Prioritises infection over wandering.
 *
 * @author Kumali Wickremasinghe
 * @author Alia Anthony
 * @author Yong Leng Foong
 * @version 1.3
 */
public class Parasite extends BehaviouralActor {

    /**
     * Constructs a Parasite with 30 hit points, registering it as both a
     * parasite and a creature, then wiring up infection as its primary
     * behaviour and wandering as its fallback.
     */
    public Parasite() {
        super("Parasite", 'x', 30, new BasicInventory());
        this.enableAbility(GameAbilities.IS_PARASITE);
        this.enableAbility(GameAbilities.IS_CREATURE);
        addBehaviour(BehaviourPriority.FIRST_PRIORITY, new InfectBehaviour());
        addBehaviour(BehaviourPriority.SECOND_PRIORITY, new WanderBehaviour());
    }
}