package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.DepositAction;
import game.actions.PurchaseAction;
import game.actions.SellAction;
import game.capabilities.Depositable;
import game.capabilities.GameAbilities;
import game.capabilities.Purchasable;
import game.capabilities.Sellable;

import java.util.ArrayList;
import java.util.List;

/**
 * The Supercomputer terminal ({@code ≡}) inside the armoured ship.
 *
 * <p>Workers standing on this tile are offered actions to sell or deposit their
 * scrap and to purchase equipment. Items that are neither {@link Sellable},
 * {@link Depositable}, nor in the purchase catalogue simply do not appear.</p>
 *
 * <p><b>OCP:</b> new sellable/depositable items only need to implement the
 * matching capability; new purchasable items only need to be registered via
 * {@link #register(Purchasable)}. This class never needs modification.</p>
 *
 * <p><b>DIP:</b> this class depends only on the {@link Purchasable},
 * {@link Sellable}, and {@link Depositable} abstractions — never on a concrete
 * item type. Concretes are wired in at the composition root ({@code EclipseNebula}).</p>
 *
 * <p><b>SRP:</b> this class only decides which actions to expose. Transaction
 * logic lives in {@link SellAction}, {@link DepositAction}, and
 * {@link PurchaseAction}; side-effects live in each item's
 * {@code onSell} / {@code onDeposit} / {@code onPurchase}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.2
 */
public class SuperComputer extends Ground {

    /**
     * The catalogue of items available for purchase. Each entry is a fresh
     * {@link Purchasable} whose {@code onPurchase} mints and injects the actual
     * item into the buyer's inventory.
     */
    private final List<Purchasable> catalogue = new ArrayList<>();

    /**
     * Constructs the Supercomputer. The purchase catalogue is populated
     * separately via {@link #register(Purchasable)} at the composition root.
     */
    public SuperComputer() {
        super('≡', "Supercomputer");
        this.enableAbility(GameAbilities.IS_SUPERCOMPUTER);
    }

    /**
     * Registers a new item in the purchase catalogue.
     *
     * <p>Concrete item types are constructed and injected by the caller
     * (e.g. {@code EclipseNebula}) — this class never imports or instantiates a
     * concrete {@link Purchasable}.</p>
     *
     * @param purchasable the item to add to the shop catalogue (must not be null)
     * @throws IllegalArgumentException if {@code purchasable} is null
     */
    public void register(Purchasable purchasable) {
        if (purchasable == null) {
            throw new IllegalArgumentException("Cannot register a null purchasable.");
        }
        catalogue.add(purchasable);
    }

    /**
     * Builds the menu when an actor stands directly on this tile.
     *
     * <p>For each carried item, a {@link SellAction} and/or {@link DepositAction}
     * is offered based on the capability the item exposes (an item may be both,
     * letting the worker choose). A {@link PurchaseAction} is offered for every
     * catalogue entry.</p>
     *
     * <p>Note: carried-item actions such as the Plasma Cutter's cut actions are
     * surfaced by the engine itself ({@code World.processActorTurn}), so this
     * method deliberately does not re-query {@code item.allowableActions(...)} —
     * doing so would duplicate them.</p>
     *
     * @param actor     the actor querying for available actions
     * @param location  this Supercomputer's location
     * @param direction empty when the actor stands here; non-empty for adjacent tiles
     * @return list of available sell, deposit, and buy actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty()) {
            return actions;
        }

        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(Sellable.class)
                    .ifPresent(sellable -> actions.add(new SellAction(item, sellable)));
            item.asCapability(Depositable.class)
                    .ifPresent(depositable -> actions.add(new DepositAction(item, depositable)));
        }

        for (Purchasable purchasable : catalogue) {
            actions.add(new PurchaseAction(purchasable.toString(), purchasable));
        }

        return actions;
    }
}