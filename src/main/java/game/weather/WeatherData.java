package game.weather;

/**
 * Immutable value object carrying all numerical data parsed from the
 * OpenWeatherMap API response.
 *
 * <p>By bundling all API values into a single object, {@link WeatherFactory}
 * and {@link ModifierSupplier} depend on this class rather than on a fixed
 * parameter list. Adding a new API field (e.g. pressure, visibility) only
 * requires adding a field here — no signature changes propagate to
 * {@code WeatherFactory}, {@code ModifierSupplier}, or any registered lambda.</p>
 *
 * <p><b>OCP:</b> New API fields are added here only. Everything else is untouched.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class WeatherData {

    /** {@code weather[0].id} from the API response. */
    public final int conditionCode;

    /** {@code main.temp} from the API response, in degrees Celsius. */
    public final double temperatureCelsius;

    /** {@code wind.speed} from the API response, in metres per second. */
    public final double windSpeedMs;

    /** {@code main.humidity} from the API response, as a percentage. */
    public final double humidityPercent;

    /**
     * Constructs a WeatherData from the four parsed API fields.
     *
     * @param conditionCode      {@code weather[0].id}
     * @param temperatureCelsius {@code main.temp}
     * @param windSpeedMs        {@code wind.speed}
     * @param humidityPercent    {@code main.humidity}
     */
    public WeatherData(int conditionCode,
                       double temperatureCelsius,
                       double windSpeedMs,
                       double humidityPercent) {
        this.conditionCode = conditionCode;
        this.temperatureCelsius = temperatureCelsius;
        this.windSpeedMs = windSpeedMs;
        this.humidityPercent = humidityPercent;
    }
}