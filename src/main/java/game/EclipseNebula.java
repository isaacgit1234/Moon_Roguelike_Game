package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;

import game.actors.*;
import game.alarm.AlarmSystem;
import game.capabilities.GameAbilities;
import game.ground.*;
import game.items.*;
import game.quota.QuotaListener;
import game.quota.QuotaManager;
import game.reactions.ParasiteSpawnReaction;
import game.reactions.SlimeSpawnReaction;
import game.reactions.SpawnReactionManager;
import game.reactions.UndeadSpawnReaction;
import game.weather.OpenWeatherMapClient;
import game.weather.WeatherApiClient;
import game.weather.WeatherFactory;
import game.weather.WeatherSystem;
import game.weather.condition.ClearCondition;
import game.weather.condition.FogCondition;
import game.weather.condition.StormCondition;
import game.weather.modifier.HumidityModifier;
import game.weather.modifier.TemperatureModifier;
import game.weather.modifier.WindModifier;

import java.util.Arrays;
import java.util.List;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 *
 * Overrides gameLoop() to tick the AlarmSystem once per turn,
 * driving the alarm countdown and triggering expiry notifications to all
 * registered listeners.
 *
 * Design note: The engine provides no per-turn global event hook, so
 * overriding gameLoop() in the World subclass is the least invasive
 * approach to advancing alarm state without modifying the engine.
 *
 * @author Yong Leng Foong
 * @author Kumali Wickremasinghe
 * @version 1.2
 */
public class EclipseNebula extends World implements QuotaListener {

    private final Display display;
    private WeatherSystem weatherSystem;
    private GameMap weatherMap;

    public EclipseNebula(Display display) {
        super(display);
        this.display = display;
    }

    public void initialise() throws Exception {
        AlarmSystem.reset();
        MagicCircle.resetRegistry();
        QuotaManager.reset();
        QuotaManager.getInstance().register(this);

        // ── Ground registration ───────────────────────────────────────────────
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('o', Hole::new);
        groundCreator.registerGround('O', OverflowHole::new);
        groundCreator.registerGround('V', Vent::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('y', FleshySprout::new);
        groundCreator.registerGround('v', FleshySapling::new);
        groundCreator.registerGround('Y', MatureFleshyTree::new);
        groundCreator.registerGround('w', WarperSapling::new);
        groundCreator.registerGround('W', MatureWarperTree::new);

        // ── Map layouts ───────────────────────────────────────────────────────
        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#__________________#_________________o_#",
                "...#_____#..~~~.....########=###########___#############___#",
                "...#######.~~~~.....#______#_#_________#___#____________#__#",
                ".........~~~~.......#______#_=_________#####___________#####",
                "....................#______#_#_________#___________________#",
                "......~.............#______=_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#___________#___#___________#___#",
                "....................=______#___________=___=___________=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___o_______#___________________#",
                "....................#______#############___#############___#",
                "...............o....#______#___________#___#___________#___#",
                "..~.................#______=___________=___=______o____=___#",
                "o...................########################################"
        );

        List<String> moon20Overflow = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=..........#__________________#___________________#",
                "...#_____#..........########=###########___#############___#",
                "...#######..........#______#_#_________#___#____________#__#",
                "....................#______#_=_________#####___________#####",
                "....................#______#_#_________#___________________#",
                "....................#______=_#_________#___________________#",
                "....................#______#_###########___#############___#",
                "....................#______#___________#___#___________#___#",
                "....................=______#___________=___=_____V_____=___#",
                "....................#______#############___#############___#",
                "....................#______#___________#####################",
                "....................#______#___________=___________________#",
                "....................#______#___O_______#___________________#",
                "....................#______#############___#############___#",
                "...............O....#______#___________#___#___________#___#",
                "....................#______=___________=___=______O____=___#",
                "O...................########################################"
        );

        // ── Game maps ─────────────────────────────────────────────────────────
        GameMap moon99DeprecatedMap = new GameMap("99-Deprecated", groundCreator, moon99Deprecated);
        this.addGameMap(moon99DeprecatedMap);

        GameMap moon20OverflowMap = new GameMap("20-Overflow", groundCreator, moon20Overflow);
        this.addGameMap(moon20OverflowMap);

        // ── REQ1: Supercomputer ───────────────────────────────────────────────
        SuperComputer superComputer = new SuperComputer();
        superComputer.register(new FirstAidKit());
        superComputer.register(new SterilisationBox());
        superComputer.register(new AccessCardLevel1());
        superComputer.register(new AccessCardLevel2());
        superComputer.register(new AccessCardLevel3());
        superComputer.register(new PlasmaCutter());
        moon99DeprecatedMap.at(5, 3).setGround(superComputer);

        // ── REQ2: Teleportation tubes ─────────────────────────────────────────
        TeleportationTube tube99 = new TeleportationTube();
        tube99.addDestination(moon20OverflowMap.at(6, 2));    // cross-map
        tube99.addDestination(moon99DeprecatedMap.at(50, 2)); // within-map
        moon99DeprecatedMap.at(4, 3).setGround(tube99);

        TeleportationTube tube20 = new TeleportationTube();
        tube20.addDestination(moon99DeprecatedMap.at(6, 2));  // cross-map
        tube20.addDestination(moon20OverflowMap.at(50, 2));   // within-map
        moon20OverflowMap.at(4, 3).setGround(tube20);

        // ── REQ2: Magic circles ───────────────────────────────────────────────
        moon20OverflowMap.at(15, 5).setGround(new MagicCircle());
        moon20OverflowMap.at(35, 5).setGround(new MagicCircle());
        moon20OverflowMap.at(15, 15).setGround(new MagicCircle());

        // ── REQ2: Doors ─────────────────────────────────────
        moon99DeprecatedMap.at(39, 2).setGround(new AluminiumDoor());
        moon99DeprecatedMap.at(28, 4).setGround(new AluminiumDoor());
        moon99DeprecatedMap.at(27, 8).setGround(new AluminiumDoor());
        moon99DeprecatedMap.at(20, 11).setGround(new AluminiumDoor());

        moon20OverflowMap.at(20, 11).setGround(new Door('M', "Titanium Door", 3,
                (actor, location) -> {
                    actor.heal(5);
                    return "Decontamination sequence triggered — " + actor + " heals 5 HP.";
                },
                "unlock the Titanium Door"));

        moon20OverflowMap.at(27, 8).setGround(new Door('N', "Iron Door", 2,
                (actor, location) -> {
                    for (Exit exit : location.getExits()) {
                        Location adj = exit.getDestination();
                        Ground original = adj.getGround();
                        if (original.hasAbility(GameAbilities.IS_FLOOR)) {
                            adj.setGround(new Fire(original, 2));
                        }
                    }
                    return "The mechanism overheats — adjacent tiles catch fire!";
                },
                "unlock the Iron Door"));

        // ── REQ1: Items on 99-deprecated ─────────────────────────────────────
        moon99DeprecatedMap.at(7, 2).addItem(new AccessCardLevel1());
        moon99DeprecatedMap.at(56, 1).addItem(new Apple());
        moon99DeprecatedMap.at(6, 3).addItem(new Apple());
        moon99DeprecatedMap.at(49, 11).addItem(new Cookies());
        moon99DeprecatedMap.at(33, 17).addItem(new Lantern());
        moon99DeprecatedMap.at(58, 17).addItem(new CRTMonitor());
        moon99DeprecatedMap.at(28, 7).addItem(new FloppyDisk());
        moon99DeprecatedMap.at(40, 5).addItem(new SterilisationBox());
        moon99DeprecatedMap.at(50, 11).addItem(new FirstAidKit());

        // ── REQ2: Alien cubes on 20-overflow ─────────────────────────────────
        moon20OverflowMap.at(25, 3).addItem(new AlienCube());
        moon20OverflowMap.at(40, 8).addItem(new AlienCube());
        moon20OverflowMap.at(50, 14).addItem(new AlienCube());

        // ── Workers ───────────────────────────────────────────────────────────
        ContractedWorker contractedWorker1 = new ContractedWorker("#1 Bob", 'ඞ', 10);
//        ContractedWorker contractedWorker2 = new ContractedWorker("#2 Tom", 'ඞ', 10);
//        ContractedWorker contractedWorker3 = new ContractedWorker("#3 Sarah", 'ඞ', 10);
//        ContractedWorker contractedWorker4 = new ContractedWorker("#4 Julie", 'ඞ', 10);
//        ContractedWorker contractedWorker5 = new ContractedWorker("#5 Rick", 'ඞ', 10);


        this.addPlayer(contractedWorker1, moon99DeprecatedMap.at(6, 2));
//        this.addPlayer(contractedWorker2, moon99DeprecatedMap.at(7, 2));
//        this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(8, 2));
//        this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(6, 4));
//        this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(8, 4));

        // ── REQ4: Actors on 99-deprecated ────────────────────────────────────
        moon99DeprecatedMap.at(12, 5).addActor(new Watcher());
        moon99DeprecatedMap.at(15, 2).addActor(new Undead());
        moon99DeprecatedMap.at(7, 3).addActor(new Parasite());
        moon99DeprecatedMap.at(8, 3).addItem(new Cookies());
        moon99DeprecatedMap.at(8, 4).addItem(new Lantern());

        // ── REQ4: Actors on 20-overflow ──────────────────────────────────────
        moon20OverflowMap.at(25, 9).addActor(new Parasite());
        moon20OverflowMap.at(24, 9).addActor(new Slime());
        moon20OverflowMap.at(23, 9).addActor(new Undead());

        // ── REQ4: Spawn reactions — registered externally ──────────
        SpawnReactionManager.getInstance().register(new UndeadSpawnReaction());
        SpawnReactionManager.getInstance().register(new SlimeSpawnReaction());
        SpawnReactionManager.getInstance().register(new ParasiteSpawnReaction());

        // ── REQ3: Flora on 20-overflow ────────────────────────────────────────
        moon20OverflowMap.at(20, 10).setGround(new FleshySprout());
        moon20OverflowMap.at(22, 10).setGround(new FleshySapling());
        moon20OverflowMap.at(24, 10).setGround(new MatureFleshyTree());
        moon20OverflowMap.at(30, 10).setGround(new WarperSapling());
        moon20OverflowMap.at(32, 10).setGround(new MatureWarperTree());

        // ── REQ5: VoidStalker on 20-overflow ─────────────────────────────────
        moon20OverflowMap.at(35, 10).addActor(new VoidStalker());

        // ── REQ5: WeatherFactory — registered externally (OCP/DIP) ───────────
        WeatherFactory weatherFactory = new WeatherFactory();

        weatherFactory.registerCondition(
                code -> code >= 200 && code <= 699,
                StormCondition::new);
        weatherFactory.registerCondition(
                code -> code >= 700 && code <= 799,
                FogCondition::new);
        weatherFactory.registerCondition(
                code -> code >= 800 && code <= 804,
                ClearCondition::new);

        weatherFactory.registerModifierSupplier(
                data -> new TemperatureModifier(data.temperatureCelsius));
        weatherFactory.registerModifierSupplier(
                data -> new WindModifier(data.windSpeedMs));
        weatherFactory.registerModifierSupplier(
                data -> new HumidityModifier(data.humidityPercent));

        WeatherApiClient weatherClient = new OpenWeatherMapClient(System.getenv("OPENWEATHERMAP_API_KEY"));
        this.weatherSystem = new WeatherSystem(weatherClient, weatherFactory);
        this.weatherMap = moon99DeprecatedMap;
        display.println(weatherSystem.initialise(moon99DeprecatedMap, contractedWorker1));
    }

    @Override
    protected void gameLoop() throws edu.monash.fit2099.engine.GameEngineException {
        boolean wasActive = AlarmSystem.getInstance().isActive();
        AlarmSystem.getInstance().tick();
        if (wasActive && !AlarmSystem.getInstance().isActive()) {
            display.println("*** FACILITY ALARM EXPIRED. Returning to normal operations. ***");
        }

        QuotaManager.getInstance().tick();

        GameMap currentMap = getCurrentPlayerMap();
        if (weatherSystem != null) {
            String weatherReport = weatherSystem.tick(weatherMap);
            if (!weatherReport.isEmpty()) {
                display.println(weatherReport);
            }
        }

        super.gameLoop();
    }

    /**
     * Called when quota is met — scales up quota and turn limit,
     * prints a congratulatory message to the console.
     */
    @Override
    public void onQuotaMet() {
        display.println("=== QUOTA MET! Well done workers. New quota: " +
                QuotaManager.getInstance().getQuota() +
                " | New turn limit: " +
                QuotaManager.getInstance().getTurnsRemaining() + " ===");
    }

    /**
     * Called when quota is failed — fires any workers adjacent
     * to the SuperComputer by making them unconscious.
     */
    @Override
    public void onQuotaFailed() {
        display.println("=== QUOTA FAILED. The SuperComputer fires underperforming workers. ===");
        for (GameMap map : this.gameMaps) {
            for (int x : map.getXRange()) {
                for (int y : map.getYRange()) {
                    Location location = map.at(x, y);
                    if (location.getGround().hasAbility(GameAbilities.IS_SUPERCOMPUTER)) {
                        for (Exit exit : location.getExits()) {
                            Actor actor = exit.getDestination().getActor();
                            if (actor != null && actor.hasAbility(GameAbilities.IS_WORKER)) {
                                display.println(actor + " is fired and falls unconscious!");
                                actor.unconscious(map);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the game map where the first human-controlled player currently is.
     * Uses the inherited {@code actorLocations} and {@code players} fields from {@link World}
     * to resolve the player's current map without scanning every tile.
     *
     * @return the current player's GameMap, or the first game map as fallback
     */
    private GameMap getCurrentPlayerMap() {
        if (!players.isEmpty()) {
            var location = actorLocations.locationOf(players.get(0));
            if (location != null) {
                return location.map();
            }
        }
        return gameMaps.isEmpty() ? null : gameMaps.get(0);
    }
}