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
    @DisplayName("Deve aceitar string válida e ignorar espaços em branco")
    void testReadStringValid() {
        String simulatedInput = "   Supermercado   \n";
        Scanner scanner = new Scanner(simulatedInput);

        String input = scanner.nextLine();
        ValidationRule<String> rule = new ValidationRule<>(
                input,
                s -> !s.trim().isEmpty(),
                "A descrição não pode estar vazia."
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
    @DisplayName("Deve lançar InvalidInput se a string estiver vazia")
    void testReadStringEmpty() {
        String simulatedInput = "   \n";
        Scanner scanner = new Scanner(simulatedInput);

        String input = scanner.nextLine();
        ValidationRule<String> rule = new ValidationRule<>(
                input,
                s -> !s.trim().isEmpty(),
                "A descrição não pode estar vazia."
        );

        assertThrows(InvalidInput.class, rule::validate);
        scanner.close();
    }

    @Test
    @Order(3)
    @DisplayName("Deve validar limites corretos para o readInt (entre 1 e o maximo permitido)")
    void testReadIntLogic() {
        int maxValidValue = 5;

        int validOpt = 3;
        ValidationRule<Integer> validRule = new ValidationRule<>(
                validOpt,
                v -> v <= maxValidValue && v > 0,
                "Opção inválida."
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
                "Opção inválida."
        );
        assertThrows(InvalidInput.class, invalidRule::validate);

        int zeroOpt = 0;
        ValidationRule<Integer> zeroRule = new ValidationRule<>(
                zeroOpt,
                v -> v <= maxValidValue && v > 0,
                "Opção inválida."
        );
        assertThrows(InvalidInput.class, zeroRule::validate);
    }

    @Test
    @Order(4)
    @DisplayName("Deve validar que o valor double seja obrigatoriamente maior que zero")
    void testReadDoubleLogic() {
        double validVal = 150.50;
        ValidationRule<Double> validRule = new ValidationRule<>(
                validVal,
                v -> v > 0,
                "O valor deve ser maior que zero."
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
                "O valor deve ser maior que zero."
        );
        assertThrows(InvalidInput.class, zeroRule::validate);

        double negativeVal = -45.90;
        ValidationRule<Double> negativeRule = new ValidationRule<>(
                negativeVal,
                v -> v > 0,
                "O valor deve ser maior que zero."
        );
        assertThrows(InvalidInput.class, negativeRule::validate);
    }
}