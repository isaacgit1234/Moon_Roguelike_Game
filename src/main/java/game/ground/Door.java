package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.UnlockAction;
import game.alarm.AlarmListener;
import game.alarm.AlarmSystem;
import game.capabilities.ClearanceLevel;
import game.capabilities.GameAbilities;
import game.capabilities.Unlockable;

/**
 * A fully parameterised facility door.
 *
 * <p>Previously, IronDoor and TitaniumDoor existed as separate subclasses despite
 * differing from Door only in configuration — clearance level, display character,
 * and unlock side-effect. Since Door already provides the full behaviour, these
 * differences are now parameterised at construction time rather than modelled
 * through inheritance.</p>
 *
 * <p>AluminiumDoor retains its subclass because it implements {@link game.capabilities.Cuttable},
 * which is genuinely unique behaviour not shared by other door tiers.</p>
 *
 * @author Yong Leng Foong
 * @version 3.0
 */
public class Door extends Ground implements Unlockable, AlarmListener {

    private boolean isUnlocked;
    private boolean lockdown;

    /** Cached each tick so UnlockEffect implementations can access adjacent tiles. */
    private Location currentLocation;

    private final int requiredClearance;
    private final UnlockEffect unlockEffect;
    private final String menuLabel;

    /**
     * Constructs a fully parameterised Door.
     *
     * @param displayChar      character shown on the map
     * @param name             display name of this door
     * @param requiredClearance minimum clearance level needed to unlock
     * @param unlockEffect     side-effect applied after a successful unlock;
     *                         use {@link UnlockEffect#NONE} for no side-effect
     * @param menuLabel        text shown in the action menu
     */
    public Door(char displayChar, String name,
                int requiredClearance,
                UnlockEffect unlockEffect,
                String menuLabel) {
        super(displayChar, name);
        this.requiredClearance = requiredClearance;
        this.unlockEffect = unlockEffect;
        this.menuLabel = menuLabel;
        this.isUnlocked = false;
        this.lockdown = false;
        AlarmSystem.getInstance().register(this);
    }

    /**
     * Caches this door's location each tick so the {@link UnlockEffect}
     * can access adjacent tiles without a subclass field.
     *
     * @param location this door's current location
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        this.currentLocation = location;
    }

    /**
     * Unlocks this door if not in lockdown, then fires the injected
     * {@link UnlockEffect}.
     *
     * @param actor the actor attempting to unlock
     * @return description of the result including any side-effect message
     */
    @Override
    public String unlock(Actor actor) {
        if (lockdown) {
            return actor + " cannot unlock the door — facility lockdown is active!";
        }
        isUnlocked = true;
        String base = actor + " unlocked the " + this + ".";
        String effect = unlockEffect.apply(actor, currentLocation);
        return effect.isEmpty() ? base : base + " " + effect;
    }

    /**
     * Returns the menu description for the unlock action.
     *
     * @param actor the actor performing the action
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor) {
        return menuLabel;
    }

    /**
     * Returns whether the actor can enter this door.
     * During lockdown, only actors with BYPASSES_LOCKDOWN may pass.
     * Otherwise, only unlocked doors are passable.
     *
     * @param actor the actor attempting to enter
     * @return true if entry is permitted
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        if (lockdown) {
            return actor.hasAbility(GameAbilities.BYPASSES_LOCKDOWN);
        }
        return isUnlocked;
    }

    /**
     * Offers an {@link UnlockAction} when the actor is adjacent, the door is
     * locked, and the actor's clearance meets the requirement.
     *
     * @param actor     the actor querying for actions
     * @param location  this door's location
     * @param direction non-empty when the actor is adjacent
     * @return list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty() && !isUnlocked
                && getActorClearanceLevel(actor) >= requiredClearance) {
            actions.add(new UnlockAction(this));
        }
        return actions;
    }

    /** Activates lockdown when the facility alarm is triggered. */
    @Override
    public void onAlarmTriggered() {
        this.lockdown = true;
    }

    /** Deactivates lockdown when the alarm expires. */
    @Override
    public void onAlarmExpired() {
        this.lockdown = false;
    }

    /** @return true if this door is currently unlocked */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /** @return true if this door is currently in alarm lockdown */
    public boolean isInLockdown() {
        return lockdown;
    }

    /**
     * Finds the highest clearance level in the actor's inventory.
     * Uses the {@link ClearanceLevel} capability — no instanceof checks.
     *
     * @param actor the actor to check
     * @return highest clearance level found, or 0 if none
     */
    protected int getActorClearanceLevel(Actor actor) {
        int highest = 0;
        for (Item item : actor.getInventory().getItems()) {
            var level = item.asCapability(ClearanceLevel.class);
            if (level.isPresent()) {
                highest = Math.max(highest, level.get().getClearanceLevel());
            }
        }
        return highest;
    }
}