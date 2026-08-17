package game.weather;

import game.weather.condition.WeatherCondition;
import game.weather.condition.ClearCondition;
import game.weather.modifier.WeatherModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

/**
 * The single composition point permitted to know concrete {@link WeatherCondition}
 * and {@link WeatherModifier} types.
 *
 * <p>Condition selection and modifier construction are driven by external
 * registration from the composition root ({@code EclipseNebula}); the concretes
 * are supplied as lambdas, so this class imports no concrete weather class with
 * one deliberate exception — {@link ClearCondition} is referenced directly as the
 * safe default for unmatched codes, so the factory can never hand back a null
 * condition.</p>
 *
 * <p>Modifiers receive a single {@link WeatherData} value object rather than a raw
 * parameter list. Adding a new API field only requires a new field in
 * {@link WeatherData} — the {@link ModifierSupplier} interface, this class, and all
 * registered lambdas remain untouched.</p>
 *
 * <p><b>OCP:</b> new conditions and modifiers are added by registering new entries
 * from {@code EclipseNebula}; this class is not modified.</p>
 *
 * <p><b>DIP:</b> the rest of the system ({@link WeatherSystem}, {@link WeatherTicker})
 * depends only on the abstractions. This factory is the one class allowed to bridge
 * to concretes — that is its single responsibility.</p>
 *
 * @author Yong Leng Foong
 * @version 3.0
 */
public class WeatherFactory {

    /**
     * Functional interface for a modifier factory that receives all parsed API
     * values via {@link WeatherData} and produces a {@link WeatherModifier}.
     *
     * <p>Using {@link WeatherData} instead of raw primitives means the signature
     * never changes when new API fields are introduced.</p>
     */
    @FunctionalInterface
    public interface ModifierSupplier {
        /**
         * Creates a modifier from the parsed API data.
         *
         * @param data all numerical values from the API response
         * @return a configured {@link WeatherModifier}
         */
        WeatherModifier create(WeatherData data);
    }

    /** Registered condition entries evaluated in order; first match wins. */
    private final List<ConditionEntry> conditionEntries = new ArrayList<>();

    /** Registered modifier factories, each producing one modifier per API call. */
    private final List<ModifierSupplier> modifierSuppliers = new ArrayList<>();

    /** Internal record pairing a code-range predicate with its condition supplier. */
    private record ConditionEntry(IntPredicate predicate, Supplier<WeatherCondition> supplier) {}

    /**
     * Registers a condition for a given range of weather condition codes.
     *
     * <p>Predicates are evaluated in registration order; first match wins.
     * Falls back to {@link ClearCondition} if no entry matches.</p>
     *
     * @param predicate returns true when the condition code matches this entry
     * @param supplier  constructs the concrete {@link WeatherCondition}
     */
    public void registerCondition(IntPredicate predicate,
                                  Supplier<WeatherCondition> supplier) {
        conditionEntries.add(new ConditionEntry(predicate, supplier));
    }

    /**
     * Registers a modifier factory.
     *
     * <p>All registered suppliers are called on every {@link #createModifiers}
     * invocation — one modifier per supplier.</p>
     *
     * @param supplier the modifier factory to register
     */
    public void registerModifierSupplier(ModifierSupplier supplier) {
        modifierSuppliers.add(supplier);
    }

    /**
     * Selects and constructs the appropriate {@link WeatherCondition} for the given
     * condition code by evaluating registered predicates in order.
     *
     * @param conditionCode {@code weather[0].id} from the API response
     * @return the matching concrete {@link WeatherCondition}, or {@link ClearCondition}
     *         as a safe fallback for unknown codes (never null)
     */
    public WeatherCondition createCondition(int conditionCode) {
        for (ConditionEntry entry : conditionEntries) {
            if (entry.predicate().test(conditionCode)) {
                return entry.supplier().get();
            }
        }
        return new ClearCondition();
    }

    /**
     * Constructs all registered {@link WeatherModifier} instances from the parsed
     * API data.
     *
     * @param data all numerical values parsed from the API response
     * @return list of configured modifiers, one per registered supplier
     */
    public List<WeatherModifier> createModifiers(WeatherData data) {
        List<WeatherModifier> modifiers = new ArrayList<>();
        for (ModifierSupplier supplier : modifierSuppliers) {
            modifiers.add(supplier.create(data));
        }
        return modifiers;
    }
}