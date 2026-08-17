package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.weather.condition.ClearCondition;
import game.weather.condition.StormCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeatherSystemTest {

    private WeatherFactory factory;
    private GameMap mockMap;
    private Actor mockPlayer;

    /**
     * The HD Requirement Secret Weapon: The Stub.
     * This class pretends to be your network client but never touches the internet.
     * It just returns whatever hardcoded JSON/Data we give it in the test.
     */
    private static class WeatherApiClientStub implements WeatherApiClient {
        private final WeatherData fixedResponse;

        public WeatherApiClientStub(WeatherData fixedResponse) {
            this.fixedResponse = fixedResponse;
        }

        @Override
        public WeatherData fetch(double lat, double lon) {
            return fixedResponse;
        }
    }

    @BeforeEach
    public void setUp() {
        // Set up the factory with our test conditions
        factory = new WeatherFactory();
        factory.registerCondition(code -> code == 800, () -> new ClearCondition());
        factory.registerCondition(code -> code >= 200 && code < 300, () -> new StormCondition());

        // 1. Create the smart mock map with a 1x1 grid to avoid the engine's NumberRange(0,0) crash
        mockMap = mock(GameMap.class);
        when(mockMap.getXRange()).thenReturn(new NumberRange(0, 1));
        when(mockMap.getYRange()).thenReturn(new NumberRange(0, 1));

        // 2. Create the fake tile and ground so condition loops don't hit a NullPointerException
        Location mockLocation = mock(Location.class);
        Ground mockGround = mock(Ground.class);

        when(mockMap.at(0, 0)).thenReturn(mockLocation);
        when(mockLocation.getGround()).thenReturn(mockGround);
        when(mockGround.hasAbility(any())).thenReturn(false);
        when(mockLocation.containsAnActor()).thenReturn(false);
        when(mockLocation.getExits()).thenReturn(new ArrayList<>());

        // 3. Create a mock player to pass into initialise() safely
        mockPlayer = mock(Actor.class);
        when(mockPlayer.getMaximumStatistic(any())).thenReturn(100);
        when(mockPlayer.getStatistic(any())).thenReturn(80);
    }

    @Test
    public void testWeatherSystem_NormalCondition_ClearSkies() {
        // Setup: Normal clear skies weather code
        WeatherData normalData = new WeatherData(800, 22.5, 3.1, 45.0);
        WeatherApiClient clientStub = new WeatherApiClientStub(normalData);
        WeatherSystem system = new WeatherSystem(clientStub, factory);

        // Act: Initialise the system (which fetches weather and applies conditions)
        String report = system.initialise(mockMap, mockPlayer);

        // Assert
        assertNotNull(report);
        assertTrue(report.toLowerCase().contains("clear"), "System should apply Clear skies for code 800.");
    }

    @Test
    public void testWeatherSystem_BoundaryCondition_SevereStorm() {
        // Setup: Extreme boundary storm code
        WeatherData extremeData = new WeatherData(200, 18.0, 25.4, 95.0);
        WeatherApiClient clientStub = new WeatherApiClientStub(extremeData);
        WeatherSystem system = new WeatherSystem(clientStub, factory);

        // Act
        String report = system.initialise(mockMap, mockPlayer);

        // Assert
        assertNotNull(report);
        assertTrue(report.toLowerCase().contains("storm"), "System should trigger Storm condition for code 200.");
    }

    @Test
    public void testWeatherSystem_EdgeCase_InvalidApiCode() {
        // Setup: Invalid/unknown code
        WeatherData corruptedData = new WeatherData(9999, 0.0, 0.0, 0.0);
        WeatherApiClient clientStub = new WeatherApiClientStub(corruptedData);
        WeatherSystem system = new WeatherSystem(clientStub, factory);

        // Act
        String report = system.initialise(mockMap, mockPlayer);

        // Assert: Unknown codes fall back to ClearCondition — the system should NOT crash
        // and should still return a meaningful report.
        assertNotNull(report);
        assertFalse(report.isEmpty(), "System should fall back gracefully, not silently disappear.");
        assertTrue(report.toLowerCase().contains("clear"),
                "Unknown codes should safely default to clear skies, got: " + report);
    }
}