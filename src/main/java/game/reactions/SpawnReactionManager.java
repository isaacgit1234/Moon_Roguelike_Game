package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies the correct spawn reaction for a newly spawned creature.
 * <p>Previously, concrete reaction types ({@link UndeadSpawnReaction},
 * {@link SlimeSpawnReaction}, {@link ParasiteSpawnReaction}) were hard-coded inside
 * this class, tightly coupling it to every concrete implementation and requiring
 * a source change every time a new reaction type was introduced.</p>
 *
 * <p>Reactions are now <em>registered externally</em> via {@link #register(SpawnReaction)}.
 * This class knows only about the {@link SpawnReaction} abstraction — it never
 * imports or constructs a concrete reaction class.</p>
 *
 * @author Kumali Wickremasinghe
 * @author Liviru
 * @author Yong Leng Foong
 * @version 2.0
 */
public class SpawnReactionManager {

    private static final SpawnReactionManager INSTANCE = new SpawnReactionManager();

    private final List<SpawnReaction> reactions = new ArrayList<>();

    private SpawnReactionManager() {}

    /**
     * Returns the single shared instance of this manager.
     *
     * @return the singleton {@code SpawnReactionManager}
     */
    public static SpawnReactionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new spawn reaction with this manager.
     *
     * <p>Reactions are evaluated in registration order. Each reaction that
     * {@link SpawnReaction#supports(Actor) supports} the spawned actor will
     * be applied — multiple reactions may fire for a single actor.</p>
     *
     * <p>Concrete types are constructed and injected by the caller
     * (e.g. {@code EclipseNebula}), not by this class.</p>
     *
     * @param reaction the reaction to register (must not be null)
     */
    public void register(SpawnReaction reaction) {
        reactions.add(reaction);
    }

    /**
     * Iterates all registered reactions and delegates to each one that
     * supports the given actor type.
     *
     * @param actor    the actor that just spawned
     * @param location the tile the actor spawned on
     */
    public void applyReaction(Actor actor, Location location) {
        for (SpawnReaction reaction : reactions) {
            if (reaction.supports(actor)) {
                reaction.apply(actor, location);
            }
        }
    }
}