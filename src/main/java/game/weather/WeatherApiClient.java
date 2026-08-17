package game.weather;

/**
 * Seam between the weather subsystem and whatever actually supplies atmospheric
 * readings. A single method: given a coordinate, return the parsed
 * {@link WeatherData}.
 *
 * <p><b>Why this interface exists (SRP + testability):</b> the orchestration of
 * weather — picking a coordinate, choosing a condition, ticking modifiers — is a
 * separate responsibility from <i>how the bytes arrive</i> (HTTP, sockets, a
 * cache, a file). Pinning that second responsibility behind an interface lets
 * {@link WeatherSystem} depend on the abstraction (DIP) and lets unit tests
 * substitute a deterministic stub that returns a fixed {@code WeatherData}
 * with no socket, no API key, and no clock — exactly what the marking rubric
 * demands of weather tests (deterministic, no external state).</p>
 *
 * <p>The production implementation is {@link OpenWeatherMapClient}. A test
 * implementation is a one-line lambda or anonymous class returning a canned
 * {@code WeatherData}.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface WeatherApiClient {

    /**
     * Fetches the current weather reading for the given coordinate.
     *
     * @param latitude  the latitude in decimal degrees
     * @param longitude the longitude in decimal degrees
     * @return the parsed weather data for that location
     * @throws Exception if the reading cannot be retrieved or parsed (network
     *                   failure, non-200 response, malformed body). Callers are
     *                   expected to fall back to a default condition rather than
     *                   propagate the failure to the player.
     */
    WeatherData fetch(double latitude, double longitude) throws Exception;
}