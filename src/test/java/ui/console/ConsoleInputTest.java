package ui.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Scanner;

import org.junit.jupiter.api.*;

import exceptions.InvalidInput;
import utils.ValidationRule;

public class ConsoleInputTest {

    @Test
    @Order(1)
    @DisplayName("Should accept a valid string and ignore whitespace")
    void testReadStringValid() {
        String simulatedInput = "   Supermercado   \n";
        Scanner scanner = new Scanner(simulatedInput);

        String input = scanner.nextLine();
        ValidationRule<String> rule = new ValidationRule<>(
                input,
                s -> !s.trim().isEmpty(),
                "Description cannot be empty."
        );
        
        try {
            rule.validate();
        } catch (InvalidInput e) {
            e.printStackTrace();
        }
        assertEquals("Supermercado", input.trim());
        scanner.close();
    }

    @Test
    @Order(2)
    @DisplayName("Should throw InvalidInput if the string is empty")
    void testReadStringEmpty() {
        String simulatedInput = "   \n";
        Scanner scanner = new Scanner(simulatedInput);

        String input = scanner.nextLine();
        ValidationRule<String> rule = new ValidationRule<>(
                input,
                s -> !s.trim().isEmpty(),
                "Description cannot be empty."
        );

        assertThrows(InvalidInput.class, rule::validate);
        scanner.close();
    }

    @Test
    @Order(3)
    @DisplayName("Should validate correct limits for readInt (between 1 and maximum allowed)")
    void testReadIntLogic() {
        int maxValidValue = 5;

        int validOpt = 3;
        ValidationRule<Integer> validRule = new ValidationRule<>(
                validOpt,
                v -> v <= maxValidValue && v > 0,
                "Invalid option."
        );
        try {
            validRule.validate();
        } catch (InvalidInput e) {
            e.printStackTrace();
        }

        int invalidOpt = 6;
        ValidationRule<Integer> invalidRule = new ValidationRule<>(
                invalidOpt,
                v -> v <= maxValidValue && v > 0,
                "Invalid option."
        );
        assertThrows(InvalidInput.class, invalidRule::validate);

        int zeroOpt = 0;
        ValidationRule<Integer> zeroRule = new ValidationRule<>(
                zeroOpt,
                v -> v <= maxValidValue && v > 0,
                "Invalid option."
        );
        assertThrows(InvalidInput.class, zeroRule::validate);
    }

    @Test
    @Order(4)
    @DisplayName("Should validate that the double value is strictly greater than zero")
    void testReadDoubleLogic() {
        double validVal = 150.50;
        ValidationRule<Double> validRule = new ValidationRule<>(
                validVal,
                v -> v > 0,
                "Value must be greater than zero."
        );
        try {
            validRule.validate();
        } catch (InvalidInput e) {
            e.printStackTrace();
        }

        double zeroVal = 0.0;
        ValidationRule<Double> zeroRule = new ValidationRule<>(
                zeroVal,
                v -> v > 0,
                "Value must be greater than zero."
        );
        assertThrows(InvalidInput.class, zeroRule::validate);

        double negativeVal = -45.90;
        ValidationRule<Double> negativeRule = new ValidationRule<>(
                negativeVal,
                v -> v > 0,
                "Value must be greater than zero."
        );
        assertThrows(InvalidInput.class, negativeRule::validate);
    }
}