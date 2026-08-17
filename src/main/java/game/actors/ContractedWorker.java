package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;
import game.capabilities.Infectable;
import game.economy.Wallet;
import game.inventories.WeightLimitedInventory;
import game.items.Flask;
import game.status.Poisoned;

/**
 * This brave soul who work in Garbage Collection Inc is capable of performing complex tasks
 * such as picking up trash off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive in the moon.
 *
 * @author Yong Leng Foong
 * @version 3.0
 */
public class ContractedWorker extends GameCharacter implements Infectable {
    /**
     * Constructor for ContractedWorker.
     * Creates a weight-limited inventory of 50 units internally.
     *
     * @param name the name of the worker
     * @param displayChar the character representing the worker on the map
     * @param hitPoints the starting hit points of the worker
     */
    public ContractedWorker(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints, new WeightLimitedInventory());
        this.getInventory().add(new Flask());
        this.enableAbility(GameAbilities.IS_WORKER);
        this.enableAbility(GameAbilities.IS_INFECTABLE);
        Wallet.of(this);
    }

    /**
     * The playTurn method checks whether the current actor is unconscious due to environmental hazards.
     * It will generate a pick up action for each item found on the ground so that the player can pick up items
     * from the ground.
     * Next, it will check if the player is carrying an access card. If so, they can open doors.
     * If the flask is available in the inventory, the player will be able to consume its content.
     * Additionally, ut will also handle multi-turn actions by getting the subsequent action returned by the previous action.
     * Finally, it adds all possible actions that the actor can perform in the current turn and show it on the
     * console menu for the player to choose.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action that is chosen in the current turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * When a worker dies, all carried items are dropped at their last location.
     *
     * @param map the map where the worker died
     * @return description of what happened
     */
    @Override
    public String unconscious(GameMap map) {
        Location location = map.locationOf(this);
        for (Item item : this.getInventory().getItems()) {
            location.addItem(item);
        }
        return super.unconscious(map);
    }

    @Override
    public String infect(Actor source, Location location, GameMap map) {
        this.addStatus(new Poisoned(2, 5));
        return source + " infects " + this + "!";
    }
}
