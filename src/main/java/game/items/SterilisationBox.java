package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

import game.capabilities.GameAbilities;
import game.capabilities.Purchasable;
import game.economy.Wallet;

import java.util.List;
import java.util.Random;

/**
 * A sterilisation box weighing 7 units.
 *
 * <p><b>Passive:</b> While carried, grants {@link GameAbilities#STERILISED} to
 * the owner, changing the behaviour of consumable items.</p>
 *
 * <p><b>Purchase:</b> Costs 750 credits. The intense radiation emitted during
 * calibration instantly and permanently erases one random item from the buyer's
 * inventory.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class SterilisationBox extends AbstractItem implements Purchasable {

    private static final int PURCHASE_PRICE = 750;

    private final Random random = new Random();

    /**
     * Constructs a SterilisationBox, registering weight and the STERILISED ability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣', 7);
        this.enableAbility(GameAbilities.STERILISED);
    }

    // ── Purchasable ───────────────────────────────────────────────────────────

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Adds the SterilisationBox to the buyer's inventory, then erases one
     * random item from that inventory (radiation side-effect).
     *
     * @param buyer the actor buying
     * @param map   the current game map (unused)
     * @return description of the outcome
     */
    @Override
    public String onPurchase(Actor buyer, GameMap map) {
        SterilisationBox box = new SterilisationBox();
        buyer.getInventory().add(box);

        List<Item> currentInventory = buyer.getInventory().getItems();

        StringBuilder result = new StringBuilder(buyer + " purchased a Sterilisation Box. ");

        if (!currentInventory.isEmpty()) {
            Item erased = currentInventory.get(random.nextInt(currentInventory.size()));
            buyer.getInventory().remove(erased);
            result.append("Radiation erased ").append(erased).append(" from inventory!");
        } else {
            result.append("No items to erase.");
        }

        Wallet wallet = Wallet.of(buyer);
        result.append(" Remaining balance: ").append(wallet.getCredits()).append(" credits.");
        return result.toString();
    }
}