package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.actors.Parasite;
import game.capabilities.Consumable;
import game.capabilities.GameAbilities;
import game.capabilities.Infectable;
import game.capabilities.Sellable;
import game.spawning.SpawnService;

/**
 * A consumable packet of cookies.
 *
 * Cookies may heal or permanently reduce maximum health
 * depending on whether the consumer is sterilised.
 * When infected, cookies gradually disappear and
 * periodically spawn Parasites.
 *
 * <p><b>SRP:</b> Handles only cookie-specific
 * consumable and infection behaviour.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Yong Leng Foong
 * @version 3.0
 */

public class Cookies extends AbstractItem implements Consumable, Infectable, Sellable {

    private static final int HEAL_AMOUNT = 1;
    private static final int TOTAL_COOKIES = 5;
    private static final int MAX_HP_PENALTY = 1;

    private int cookiesRemaining = TOTAL_COOKIES;
    private boolean infected = false;

    /**
     * Constructs a packet of cookies.
     * Registers cookie weight for inventory capacity.
     */
    public Cookies() {
        super("Cookies", '◍', 2);
        this.enableAbility(GameAbilities.IS_INFECTABLE);
    }

    // ── Consumable ────────────────────────────────────────────────────────────

    /**
     * Eats one Cookie. Effect depends on whether the consumer has the
     * {@link GameAbilities#STERILISED} ability. The pack removes itself
     * from the actor's inventory after the last cookie is consumed.
     *
     * @param actor the actor consuming a cookie
     * @return a description of what happened
     */
    @Override
    public String consume(Actor actor) {
        cookiesRemaining--;

        String result;
        if (actor.hasAbility(GameAbilities.STERILISED)) {
            actor.heal(HEAL_AMOUNT);
            result = actor + " eats a sterilised Cookie, healing " + HEAL_AMOUNT + " HP. (" + cookiesRemaining + " left)";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, MAX_HP_PENALTY);
            result = actor + " eats a spoiled Cookie. MAX HP permanently reduced by " + MAX_HP_PENALTY + ". (" + cookiesRemaining + " left)";
        }

        if (actor.getMaximumStatistic(ActorStatistics.HEALTH) <= 0) {
            actor.hurt(actor.getStatistic(ActorStatistics.HEALTH));
        }

        if (cookiesRemaining == 0) {
            actor.getInventory().remove(this);
        }

        return result;
    }

    /**
     * @return true while at least one cookie remains in the packet
     */
    @Override
    public boolean canConsume() {
        return cookiesRemaining > 0;
    }

    /**
     * Returns the menu description for consuming a Cookie and the remaining count.
     *
     * @param actor the actor consuming the Cookie
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor) {
        return actor + " eats a Cookie (" + cookiesRemaining + " left).";
    }

    // ── Sellable ────────────────────────────────────────────────────────

    /**
     * Dynamic price: 1 credit per remaining cookie.
     *
     * @param seller the actor selling
     * @return credits equal to cookies remaining
     */
    @Override
    public int getSellPrice(Actor seller) {
        return cookiesRemaining;
    }

    /**
     * Organic processing fee: instantly deducts 1 HP per cookie sold.
     *
     * @param seller the actor selling the Cookies
     * @param map    the current game map (unused)
     * @return description of the HP deduction
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        int damage = cookiesRemaining;
        seller.hurt(damage);
        return seller + " paid an organic processing fee of " + damage + " HP for selling the cookies.";
    }

    // ── Infectable ──────────────────────────────────────────────────────────────

    /**
     * Handles infected cookie behaviour while on the ground.
     * Delegates to {@link #tickInfection(Location)} so that
     * infection logic is not duplicated across both tick overrides.
     *
     * @param currentLocation current item location
     */
    @Override
    public void tick(Location currentLocation) {
        tickInfection(currentLocation);
    }

    /**
     * Handles infected cookie behaviour while carried in inventory.
     * Infection must persist regardless of whether the cookies are
     * on the ground or in an actor's inventory.
     *
     * @param currentLocation location of the carrying actor
     * @param actor           the actor carrying the cookies
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        tickInfection(currentLocation);
    }

    /**
     * Core infection tick shared by both {@link #tick(Location)} and
     * {@link #tick(Location, Actor)}. Each turn, one cookie is consumed
     * and a {@link Parasite} is spawned nearby while cookies remain.
     *
     * @param currentLocation the location used to spawn the Parasite
     */
    private void tickInfection(Location currentLocation) {
        if (infected && cookiesRemaining > 0) {
            cookiesRemaining--;
            new SpawnService().spawnNear(currentLocation, new Parasite());
        }
    }

    /**
     * Infects the cookies.
     * Once infected, cookies will gradually disappear
     * and spawn Parasites over time.
     *
     * @param source actor performing infection
     * @param location infection location
     * @param map current game map
     * @return infection result description
     */
    @Override
    public String infect(Actor source, Location location, GameMap map) {
        infected = true;
        return "The Cookies are infected and lose 1 content while spawning Parasites.";
    }
}