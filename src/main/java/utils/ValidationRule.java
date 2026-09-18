package utils;

import java.util.function.Predicate;
import exceptions.InvalidInput;

// A generic class to hold a validation rule for any type T
public class ValidationRule<T> {
    private final T value;
    private final Predicate<T> rule;
    private final String errorMessage;

    public ValidationRule(T value, Predicate<T> rule, String errorMessage) {
        this.value = value;
        this.rule = rule;
        this.errorMessage = errorMessage;
    }

    // Evaluates the rule and throws an exception if it fails
    public void validate() throws InvalidInput {
        if (value == null || !rule.test(value)) {
            throw new InvalidInput(errorMessage);
        }
    }
}
