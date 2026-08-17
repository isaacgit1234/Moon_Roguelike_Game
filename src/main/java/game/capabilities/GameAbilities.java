package game.capabilities;

/**
 * Enumeration of game-level abilities that can be attached to {@link edu.monash.fit2099.engine.actors.Actor}s or
 * {@link edu.monash.fit2099.engine.items.Item}s to gate access, modify behaviour, or categorise entities.
 *
 * <p>Abilities act as lightweight, stateless flags — think of them as ID badges
 * clipped onto an entity. Holding the right badge lets you through a locked door;
 * holding none means the door stays shut.</p>
 *
 * @author Yong Leng Foong
 * @author Kumali Wickremasinghe
 * @version 2.3
 */
public enum GameAbilities {

    /**
     * Grants passage through Aluminium doors.
     * Carried by Level 1+ Access Cards and transitively by any actor holding one.
     */
    OPEN_DOOR,

    /**
     * Grants passage through Iron doors.
     * Carried by Level 2+ Access Cards and transitively by any actor holding one.
     */
    OPEN_IRON_DOOR,

    /**
     * Grants passage through Titanium doors.
     * Carried by Level 3 Access Cards and transitively by any actor holding one.
     */
    OPEN_TITANIUM_DOOR,

    /**
     * Indicates the actor possesses a {@link game.items.SterilisationBox}.
     * Alters the effect of consumable items when present — sterilised consumables
     * apply their secondary (cleansed) effect instead of the default one.
     */
    STERILISED,

    /**
     * Marks an actor as a contracted worker, making them a valid target
     * for Moon's creatures.
     */
    IS_WORKER,

    /**
     * Allows an actor to pass through locked Doors during an alarm lockdown.
     * Granted to {@link game.actors.Undead} so they can chase workers
     * through secured areas when the facility alarm is active.
     */
    BYPASSES_LOCKDOWN,

    /**
     * Tags a ground tile as floor-type terrain.
     * Used to distinguish walkable floor from other ground surfaces during
     * movement and spawning validation.
     */
    IS_FLOOR,

    /**
     * Tags an actor as an Undead entity.
     * Used to identify and apply Undead-specific game rules, such as
     * lockdown traversal and targeted attack logic.
     */
    IS_UNDEAD,

    /**
     * Tags an actor as a creature originating from the Moon.
     * Used to apply Moon-creature-specific behaviours and interaction rules.
     */
    IS_CREATURE,

    /**
     * Tags an actor as a Slime entity.
     * Used to apply Slime-specific behaviours such as splitting or merging mechanics.
     */
    IS_SLIME,

    /**
     * Tags an actor as a Parasite entity.
     * Used to distinguish parasitic actors for infection logic and targeting rules.
     */
    IS_PARASITE,

    /**
     * Marks an actor as susceptible to infection by a Parasite.
     * Actors without this ability cannot be infected, even if a Parasite
     * attempts to target them.
     */
    IS_INFECTABLE,

    /**
     * Tags a ground tile as the SuperComputer terminal.
     * Used by EclipseNebula to locate the SuperComputer on the map
     * during quota failure
     */
    IS_SUPERCOMPUTER,

    /**
     * Tags a ground tile as dirt-type terrain.
     * Used by weather systems to identify spreading targets without
     * instanceof checks, upholding OCP.
     */
    IS_DIRT,

    /**
     * Tags a ground tile as flora-type terrain.
     * Used by weather systems to identify flora-adjacent tiles for
     * terrain spawning without instanceof checks, upholding OCP.
     */
    IS_FLORA,

    /**
     * Tags an actor as behaviour-controllable.
     * Granted by {@link game.actors.BehaviouralActor} to allow external systems
     * (weather, alarm) to inject and remove behaviours without instanceof checks.
     */
    IS_BEHAVIOURAL,

    /**
     * Tags a ground tile as puddle-type terrain.
     * Used by weather systems to identify evaporation targets without
     * instanceof checks, upholding OCP.
     */
    IS_PUDDLE,
}