package ui.console;

import java.util.InputMismatchException;
import java.util.Scanner;
import exceptions.InvalidInput;
// import utils.InputValidator;

public class ConsoleInput {

    public static String readString(Scanner sc) {

        while (true) {
            try {
                String description = sc.nextLine();

                // if (InputValidator.isValid(description)) {
                //     return description;
                // }
                
                throw new InvalidInput("A descrição não pode estar vazia: ");

            } catch (InvalidInput e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public static int readInt(Scanner sc, int id) {

        while (true) {
            try {
                int value = sc.nextInt();
                // if (value != 1 && value != 2 && id == 0 || !InputValidator.isValid(value)) {
                //     throw new InputMismatchException();
                // }
                sc.nextLine();
                return value;

            } catch (InputMismatchException e) {
                System.out.print("Digite uma opção válida: ");
                sc.nextLine();
            }
        }
    }

    public static double readDouble(Scanner sc, boolean receipt) {

        while (true) {
            try {
                double value = sc.nextDouble();

                // if (InputValidator.isValid(value)) {
                //     sc.nextLine();
                //     return receipt ? value : -value;
                // }

                throw new InputMismatchException();

            } catch (InputMismatchException e) {
                System.out.print("Valor inválido por favor digite novamente: ");
                sc.nextLine();
            }
        }
    }
}
