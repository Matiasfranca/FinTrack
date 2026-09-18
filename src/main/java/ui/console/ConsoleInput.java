package ui.console;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidInput;
import utils.ValidationRule;

public class ConsoleInput {

    public static String readString(Scanner sc) {
        while (true) {
            try {
                String input = sc.nextLine();

                ValidationRule<String> rule = new ValidationRule<>(
                        input,
                        s -> !s.trim().isEmpty(),
                        "A descrição não pode estar vazia. Digite novamente: "
                );
                rule.validate();

                return input.trim();

            } catch (InvalidInput e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public static int readInt(Scanner sc, int minValidValue) {
        while (true) {
            try {
                int value = sc.nextInt();
                sc.nextLine();

                ValidationRule<Integer> rule = new ValidationRule<>(
                        value,
                        v -> v <= minValidValue && v > 0 ,
                        "Opção inválida. Digite uma opção válida: "
                );
                rule.validate();

                return value;

            } catch (InputMismatchException e) {
                System.out.print("Formato inválido. Digite um número inteiro: ");
                sc.nextLine();
            } catch (InvalidInput e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public static double readDouble(Scanner sc) {
        while (true) {
            try {
                double value = sc.nextDouble();
                sc.nextLine();

                ValidationRule<Double> rule = new ValidationRule<>(
                        value,
                        v -> v > 0,
                        "O valor deve ser maior que zero. Digite novamente: "
                );
                rule.validate();

                return value;

            } catch (InputMismatchException e) {
                System.out.print("Formato inválido. Digite um número válido (ex: 150,50): ");
                sc.nextLine();
            } catch (InvalidInput e) {
                System.out.print(e.getMessage());
            }
        }
    }
}