package game.ground;

import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GameAbilities;

/**
 * Not lava. Not spikes. Not an elaborate trap. Just a perfectly flat surface
 * whose sole responsibility is preventing the ContractedWorker from
 * plummeting into the infinite vacuum of the Eclipse Nebula.
 *
 * IS_FLOOR ability is granted to allow other game systems (IronDoor fire
 * spawning, AlienCube ToxicWaste corruption, TeleportationTube burning)
 * to identify floor tiles without using instanceof checks — upholding OCP.
 *
 * @author Adrian Kristanto
 * @version 1.1
 */
public class Floor extends Ground {

    public Floor() {
        super('_', "Floor");
        this.enableAbility(GameAbilities.IS_FLOOR);
    }
}