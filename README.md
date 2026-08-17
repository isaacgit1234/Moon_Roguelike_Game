# Eclipse Nebula

Eclipse Nebula is a turn-based, text-based roguelike built for Monash's FIT2099 unit on top of the provided FIT2099 game engine (`edu.monash.fit2099`). You play a Contracted Worker dismantling a derelict moon facility under a rising production quota, while a live real-world weather feed reshapes the map as you play.

This document explains what the game is, how it plays, and what every folder and file in the project is for — useful both as a project reference and as something you can point to on a CV/portfolio.

---

## Contents

- [What the game is](#what-the-game-is)
- [Core gameplay systems](#core-gameplay-systems)
- [Running the game](#running-the-game)
- [Project structure](#project-structure)
- [Folder-by-folder breakdown](#folder-by-folder-breakdown)
- [File-by-file breakdown (src/main/java/game)](#file-by-file-breakdown-srcmainjavagame)

---

## What the game is

You control **Bob**, a Contracted Worker (`ContractedWorker`) dropped into an abandoned moon facility spread across two maps: **99-Deprecated** and **20-Overflow**, connected by teleportation tubes. Each turn you're shown a text menu of available actions (move, attack, pick up, cut, deposit, sell, purchase, unlock, teleport...) and you choose one by typing its key.

The facility has a **quota**: a target amount of "Company Credit" you must generate before a turn limit expires, tracked and enforced by the `SuperComputer` ground tile. You dismantle the facility's fixtures (aluminium doors, vents, alien cubes) with a `PlasmaCutter` into raw resources (`AluminiumScrap`, `IndustrialFan`, `AlienArtifact`), which you either **deposit** at the SuperComputer toward the quota, or **sell** for personal worker credit to buy gear (first-aid kits, access cards, sterilisation boxes). Meet the quota in time and it escalates (5% higher target, 10% longer deadline); miss it and any worker standing near the SuperComputer is fired (knocked unconscious).

Along the way you'll run into hostile and neutral creatures (`Watcher`, `Undead`, `Parasite`, `Slime`, `VoidStalker`), growing flora that spreads and blocks/opens paths, an alarm system that can put enemies on alert, locked doors requiring access cards of increasing clearance, and a live weather system (see below) that physically mutates the terrain and creatures every turn based on a real-world city's current weather.

## Core gameplay systems

- **Quota & economy** — Two separate currencies: your personal worker credit (spent at the SuperComputer's shop) and the facility's Company Credit (only counts toward the quota). `QuotaManager` tracks the quota, the countdown, and fires listeners when it's met or failed.
- **Dismantling loop** — Buy a `PlasmaCutter` (50 worker credit), then `CutAction` fixtures like `AluminiumDoor`, `Vent`, and `AlienCube` into resources, each with its own hazard (explosions, monster spawns, poison gas). Resources are then deposited or sold.
- **Teleportation & access control** — `TeleportationTube` ground tiles link locations within and across maps; `MagicCircle` tiles offer another means of movement; `Door`/`AluminiumDoor` tiles gate progress behind access cards of escalating clearance level (`AccessCardLevel1/2/3`).
- **Creatures & behaviours** — Enemies are built from composable `Behaviour` objects (chase, attack, wander, retreat, infect, warp...) rather than hard-coded AI, and some (`BehaviouralActor`) can have behaviours injected/removed at runtime — which is exactly how weather effects like fog disorientation or freezing get applied to them.
- **Flora growth** — `FleshySprout` → `FleshySapling` → `MatureFleshyTree` (and a parallel "Warper" tree line) grow over time via `GrowthBehaviour` and `FloraBehaviour`, changing the map's walkable layout as a session progresses.
- **Alarm system** — `AlarmSystem` is a facility-wide countdown that, once triggered, puts registered `AlarmListener`s (e.g. hostile actors) into an alert state; it ticks once per game turn from the main game loop.
- **Live weather system (the standout feature)** — On startup, the game reads two pieces of live game state (which map you're on, and whether your HP is above/below 50%) and turns that into one of four real-world city coordinate pairs (Melbourne, London, Tokyo, or Singapore). It queries the OpenWeatherMap API for that city's actual current weather and translates the response into in-game effects:
  - A one-time **`WeatherCondition`** applied at game start based on the weather's condition code: `StormCondition` corrupts ~25% of floor tiles into toxic waste and burns every actor on the map; `FogCondition` disorients every creature's movement and spawns temporary fog walls; `ClearCondition` grows flora and spawns a `Slime` (this is also the automatic fallback if the API key is missing or the request fails, so the game never crashes).
  - Three **`WeatherModifier`**s applied every single turn based on the live temperature, wind speed, and humidity: `TemperatureModifier` turns puddles into fire when hot and freezes creatures in place when cold; `WindModifier` scatters items across adjacent tiles when windy; `HumidityModifier` spreads puddles across nearby dirt tiles when humid.

  This means no two play sessions look the same — the map you get is shaped by whatever the weather is actually doing right now in Melbourne, London, Tokyo, or Singapore. Full design rationale is in `feature-proposal.md`.

## Running the game

1. Open the project in IntelliJ IDEA with a JDK compatible with the FIT2099 engine.
2. Get a free API key from [openweathermap.org](https://openweathermap.org/) (Sign in → your username → **My API keys**). New keys can take up to ~2 hours to activate.
3. In IntelliJ: **Run → Edit Configurations… → Application** (create one with main class `Application` if it doesn't exist) → add an environment variable `OPENWEATHERMAP_API_KEY=your_key_here` → Apply → OK.
4. Click **Run** (or Shift+F10). The game runs in the console; each turn shows a menu of available actions — type the key for the one you want and press Enter.
5. If the key is missing or invalid, the game still runs fine — it just falls back to a default Clear weather condition and prints a warning instead of crashing.

## Project structure

```
project-main/
├── README.md                  # this file
├── feature-proposal.md        # detailed design write-up for the weather system (REQ5)
├── .gitignore
├── src/
│   ├── main/java/
│   │   ├── edu/monash/fit2099/   # provided game engine — not modified by this project
│   │   └── game/                 # all of this project's own game code (see breakdown below)
│   └── test/java/game/           # unit tests (quota manager, weather system)
└── target/                    # Maven/compiler build output (.class files) — generated, not source
```

## Folder-by-folder breakdown

- **`src/main/java/edu/monash/fit2099/`** — The engine supplied by the unit (map/actor/action/display framework). This is the foundation everything else is built on and is left untouched.
- **`src/main/java/game/`** — Every class written for this assignment lives here, organised by responsibility (see the file-by-file section below).
- **`src/test/java/game/`** — JUnit tests: `QuotaManagerTest` (quota logic) and `WeatherSystemTest` (weather fetch/apply logic, using a stub API client so tests don't hit the real network).
- **`target/`** — Compiler output from Maven (`classes/`, `test-classes/`). Auto-generated; safe to delete/rebuild, not something you'd edit.
- **`feature-proposal.md`** — The design proposal and rationale for the live weather feature (REQ5): the pitch, mechanics, coordinate-mapping table, architecture/class diagrams, and API schema.
- **`.gitignore`** — Tells Git to ignore build artifacts and IDE files.

## File-by-file breakdown (`src/main/java/game`)

**Top level**
- `Application.java` — the actual `public static void main` entry point; constructs the display and world and starts the game.
- `EclipseNebula.java` — the heart of the game. Builds both maps from ASCII layouts, registers ground types, places every item/actor/door/teleporter/tree on the map, wires up the weather system and quota listener, and overrides `gameLoop()` to tick the alarm system, quota manager, and weather system once per turn.
- `FancyMessage.java` — cosmetic ASCII-art/banner text helper.

**`actions/`** — one class per player-triggerable action: `AttackAction`, `MultiAttackAction`, `ConsumeAction` (eat/drink), `CutAction` (dismantle with the plasma cutter), `DepositAction`/`SellAction`/`PurchaseAction` (economy), `TeleportAction`, `UnlockAction`, `InfectAction`.

**`actors/`** — the characters. `GameCharacter` is a shared base; `ContractedWorker` is the player; `BehaviouralActor` is the base class for NPCs whose AI is built from swappable `Behaviour`s; `Watcher`, `Undead`, `Parasite`, `Slime`, `VoidStalker` are the concrete creatures. `actors/states/` holds a state-machine (`IdleState`, `HuntingState`, `DefensiveState`, `FrenzyState`, `CreatureState`, `StateType`) some creatures use to change behaviour based on conditions.

**`alarm/`** — `AlarmSystem` (a facility-wide countdown/alert singleton) and `AlarmListener` (interface for anything that reacts when the alarm triggers or expires — including the fog weather condition, which suppresses alarm responses while active).

**`behaviours/`** — the composable AI building blocks assigned to `BehaviouralActor`s: movement (`WanderBehaviour`, `ChaseBehaviour`, `RetreatBehaviour`), combat (`AttackBehaviour`), spawning (`ActorSpawnBehaviour`), plant growth (`GrowthBehaviour`, `FloraBehaviour`), status effects (`InfectBehaviour`, `InfectionDrainBehaviour`), hazards (`OilLeakBehaviour`, `OilSellEffectBehaviour`), alarm interaction (`AlarmChaseOverrideBehaviour`), teleport-like movement (`WarpBehaviour`), and the two weather-injected behaviours (`FogDisorientBehaviour`, plus a freeze behaviour used by cold temperatures). `BehaviourControllable` is the interface that lets the weather system add/remove behaviours on an actor without unsafe casting; `BehaviourPriority` ranks which behaviour wins when several apply at once.

**`capabilities/`** — shared tag/ability contracts used across the codebase instead of `instanceof` checks: `GameAbilities` (enum of tags like IS_WORKER, IS_FLOOR, IS_SUPERCOMPUTER), plus specific interfaces `Consumable`, `Cuttable`, `Damageable`, `Depositable`, `Infectable`, `Purchasable`, `Sellable`, `Teleportable`, `Unlockable`, and `ClearanceLevel` (access-card tiers).

**`economy/`** — `Wallet.java`, tracking a character's worker credit balance.

**`ground/`** — every terrain/fixture tile type: basics (`Dirt`, `Floor`, `Wall`, `Hole`, `OverflowHole`), water/fire/hazard (`Puddle`, `Fire`, `ToxicWaste`), structures (`Door`, `AluminiumDoor`, `Vent`, `SuperComputer`, `TeleportationTube`, `MagicCircle`, `FogWall`), and flora (`Flora` base class plus `FleshySprout`/`FleshySapling`/`MatureFleshyTree` and `WarperSapling`/`MatureWarperTree`), plus `SpawnerGround` (ground that can spawn actors) and `UnlockEffect` (what happens when a lock is opened).

**`inventories/`** — `BasicInventory` and `WeightLimitedInventory` (the player's 50-unit-capacity inventory implementation).

**`items/`** — everything pickupable: the dismantling tool `PlasmaCutter`; dismantled resources (`AluminiumScrap`, `IndustrialFan`, `AlienArtifact`); access cards (`AbstractAccessCard`, `AccessCardLevel1/2/3`); consumables (`Apple`, `Cookies`, `Flask`, `FirstAidKit`); utility items (`Lantern`, `CRTMonitor`, `FloppyDisk`, `SterilisationBox`, `IndustrialFan`); and `AlienCube`/`AlienArtifact` (the alien-tech items on the Overflow map). `AbstractItem` is the shared base class.

**`quota/`** — `QuotaManager` (singleton tracking the current quota amount, deadline, and escalation) and `QuotaListener` (interface implemented by `EclipseNebula` to react to quota met/failed).

**`reactions/`** — `SpawnReaction` (interface) and `SpawnReactionManager` (registry) plus concrete reactions (`UndeadSpawnReaction`, `SlimeSpawnReaction`, `ParasiteSpawnReaction`) that decide what spawns in response to in-game events.

**`spawning/`** — the rules that decide when/where actors spawn: `Spawner` (interface), `SpawnService`, `OverflowHoleRule`, `OverflowVentRule`, `DeprecatedHoleRule`.

**`statistics/`** — `GameStatistics` and `ItemStatistics`, small enums/trackers for stat keys used across actors and items.

**`status/`** — status effects that persist on an actor over time: `DamageOverTime` (shared base), `Burned`, `Poisoned`.

**`weather/`** — the live weather system (REQ5), Eclipse Nebula's signature feature:
  - `WeatherSystem` — orchestrator; `initialise()` derives coordinates from game state and applies the one-time condition, `tick()` runs the per-turn modifiers.
  - `WeatherTicker` — holds and runs the list of per-turn `WeatherModifier`s.
  - `WeatherFactory` — the only class allowed to construct concrete conditions/modifiers, built from registrations wired in `EclipseNebula`.
  - `WeatherApiClient` (interface) / `OpenWeatherMapClient` (implementation) — abstracts the actual HTTP call to OpenWeatherMap so the rest of the system doesn't depend on networking directly.
  - `WeatherData` — immutable value object holding the parsed condition code, temperature, wind speed, and humidity.
  - `weather/condition/` — `WeatherCondition` (interface) plus `StormCondition`, `FogCondition`, `ClearCondition` (one-time, game-start effects).
  - `weather/modifier/` — `WeatherModifier` (abstract base with shared threshold-checking) plus `TemperatureModifier`, `WindModifier`, `HumidityModifier` (per-turn effects).

**`src/test/java/game/`** — `quota/QuotaManagerTest.java` and `weather/WeatherSystemTest.java` (using a stub `WeatherApiClient` so weather logic can be tested without real network calls).
