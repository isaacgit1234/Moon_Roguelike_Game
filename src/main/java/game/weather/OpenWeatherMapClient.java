package game.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Production {@link WeatherApiClient} that calls the OpenWeatherMap current-weather
 * endpoint over HTTPS and parses the four fields the game cares about into a
 * {@link WeatherData}.
 *
 * <p><b>Responsibility:</b> this class does one thing — turn a coordinate into a
 * {@code WeatherData} by talking to OpenWeatherMap. It holds no game state and
 * mutates no map. Everything about <i>what to do</i> with the reading lives in
 * {@link WeatherSystem}; everything about <i>how to obtain</i> it lives here.</p>
 *
 * <p><b>Why hand-rolled parsing instead of a JSON library:</b> only four scalar
 * fields are needed ({@code weather[0].id}, {@code main.temp}, {@code wind.speed},
 * {@code main.humidity}), so a dependency-free regex extraction keeps the build
 * with no external Maven artefacts. If the schema demand grows, swapping in
 * {@code org.json} or Jackson is a localised change behind the
 * {@link WeatherApiClient} interface and touches no other class. The API key is
 * supplied by the composition root from the {@code OPENWEATHERMAP_API_KEY}
 * environment variable and never hardcoded.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class OpenWeatherMapClient implements WeatherApiClient {

    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";

    private static final int TIMEOUT_MS = 5000;

    /** Sensible neutral fallbacks if an optional numeric field is missing from the body. */
    private static final double DEFAULT_TEMPERATURE = 20.0;
    private static final double DEFAULT_WIND = 0.0;
    private static final double DEFAULT_HUMIDITY = 50.0;

    // weather[0].id — matched inside the "weather":[ ... ] array so the city/sys ids are not picked up.
    private static final Pattern CONDITION_CODE =
            Pattern.compile("\"weather\"\\s*:\\s*\\[\\s*\\{[^}]*?\"id\"\\s*:\\s*(\\d+)");
    private static final Pattern TEMPERATURE = Pattern.compile("\"temp\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern WIND_SPEED = Pattern.compile("\"speed\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern HUMIDITY = Pattern.compile("\"humidity\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");

    private final String apiKey;

    /**
     * @param apiKey the OpenWeatherMap API key, supplied by the composition root
     *               from the {@code OPENWEATHERMAP_API_KEY} environment variable
     */
    public OpenWeatherMapClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public WeatherData fetch(double latitude, double longitude) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OpenWeatherMap API key is missing — set the OPENWEATHERMAP_API_KEY environment variable.");
        }
        String url = String.format(BASE_URL, latitude, longitude, apiKey);
        String body = httpGet(url);
        return parse(body);
    }

    /**
     * Issues a GET and returns the response body as a String, throwing on any
     * non-200 status so the caller can fall back to a default condition.
     *
     * @param url the fully-formed request URL
     * @return the response body
     * @throws Exception on connection failure or non-200 response
     */
    private String httpGet(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new Exception("OpenWeatherMap returned HTTP " + status);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Extracts the four required fields from a response body.
     *
     * @param body the raw JSON response
     * @return the parsed weather data
     * @throws Exception if the condition code — the one mandatory field — is absent
     */
    private WeatherData parse(String body) throws Exception {
        Matcher codeMatcher = CONDITION_CODE.matcher(body);
        if (!codeMatcher.find()) {
            throw new Exception("Response did not contain a weather condition code: " + body);
        }
        int conditionCode = Integer.parseInt(codeMatcher.group(1));

        double temperature = extract(TEMPERATURE, body, DEFAULT_TEMPERATURE);
        double windSpeed = extract(WIND_SPEED, body, DEFAULT_WIND);
        double humidity = extract(HUMIDITY, body, DEFAULT_HUMIDITY);

        return new WeatherData(conditionCode, temperature, windSpeed, humidity);
    }

    /**
     * Returns the first capture of {@code pattern} in {@code body} as a double,
     * or {@code fallback} if the pattern does not match.
     *
     * @param pattern  the compiled extraction pattern (group 1 is the number)
     * @param body     the response body
     * @param fallback the value to use when the field is absent
     * @return the parsed value or the fallback
     */
    private double extract(Pattern pattern, String body, double fallback) {
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return fallback;
    }
}