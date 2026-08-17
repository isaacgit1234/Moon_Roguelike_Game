package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Slime spawn makes nearby workers drop all inventory items.
 * @author Kumali Wickremasinghe
 * @author Isaac
 * @version 1.2
 */
public class SlimeSpawnReaction implements SpawnReaction {

    private final Display display = new Display();

    /**
     * @param actor the actor to check
     * @return {@code true} if the actor is a Parasite
     */
    public boolean supports(Actor actor) {
        return actor.hasAbility(GameAbilities.IS_SLIME);
    }

    /**
     * Scans all exits from the spawn location and deals 2 damage to every
     * adjacent worker, printing a notification for each hit.
     *
     * @param actor    the Parasite that just spawned
     * @param location the tile the Parasite spawned on
     */
    @Override
    public void apply(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Actor nearby = destination.getActor();

            if (nearby != null && nearby.hasAbility(GameAbilities.IS_WORKER)) {
                List<Item> items = new ArrayList<>(nearby.getInventory().getItems());

                if (!items.isEmpty()) {
                    for (Item item : items) {
                        nearby.getInventory().remove(item);
                        destination.addItem(item);
                    }
                    display.println(nearby + " drops " + items.size() + " item(s) because a Slime spawned nearby.");
                }
            }
        }
    }
}