package ui.console;

import java.util.Scanner;
import controller.FinTracker;
import exceptions.InvalidInput;

public class ConsoleUI {

    private final FinTracker finTracker;

    public ConsoleUI() {
        this.finTracker = new FinTracker();
    }

    public void start(Scanner sc) {

        int option = 0;

        do {
            option = 0;

            ConsoleFormatter.showMenu();
            option = ConsoleInput.readInt(sc, 1);

            switch (option) {

                case 1 -> this.addTransaction(sc);

                case 2 -> this.listTransaction(sc);

                case 3 -> this.removeTransaction(sc);

                case 4 -> this.calculateTotalBalance(sc);

                case 5 -> System.out.println("\nSaindo....");

                default -> {

                    System.err.println("\nDigite uma opção válida");
                    ConsoleFormatter.pause(sc);

                }
            }
        } while (option != 5);

        sc.close();
    }

    private void addTransaction(Scanner sc) {

        ConsoleFormatter.showInputDescription();
        String description = ConsoleInput.readString(sc);

        ConsoleFormatter.showInputType();
        boolean receipt = ConsoleInput.readInt(sc, 0) == 1;

        ConsoleFormatter.showInputValue();
        double value = ConsoleInput.readDouble(sc, receipt);

        finTracker.addTransaction(description, value, receipt);

        System.out.println("\n✅ Transação adicionada com sucesso!");

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

    private void listTransaction(Scanner sc) {

        ConsoleFormatter.showTransactions(finTracker.listTransaction());
        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();

    }

    private void removeTransaction(Scanner sc) {

        ConsoleFormatter.showTransactions(finTracker.listTransaction());
        System.out.print("Se deseja apagar diga o numero da conta a ser apagada da lista: ");

        int option = ConsoleInput.readInt(sc, 1);
        try {
            finTracker.removeTransaction(option);
            System.out.println("\n✅ Removido com sucesso!");
        } catch (InvalidInput e) {
            System.err.println(e.getMessage());
        }

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

    private void calculateTotalBalance(Scanner sc) {

        double total = finTracker.calculateTotalBalance();
        System.out.println("O total do mês: " + total);

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

}