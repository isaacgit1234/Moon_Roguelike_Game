package edu.monash.fit2099.demo.mars.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GameAbilities;


public class Floor extends Ground {

    public Floor() {
        super('.', "Floor");
        this.enableAbility(GameAbilities.IS_FLOOR);

    }
}
