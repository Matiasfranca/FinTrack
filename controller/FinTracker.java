package controller;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import exceptions.InvalidInput;
import model.Transaction;
import utils.Formatter;
import model.MonthlyTransaction;

public class FinTracker {

    static MonthlyTransaction monthlyTransaction = new MonthlyTransaction();

    public static void addTransaction (Scanner sc) {
        String description;
        double value;
        boolean receipt;

        Formatter.showInputDescription();
        description = readString(sc);

        Formatter.showInputType();
        receipt = readInt(sc, 0) == 1;

        Formatter.showInputValue(); 
        value = readDouble(sc, receipt);

        Transaction transaction;

        transaction = new Transaction(description, value, receipt);
        monthlyTransaction.add(transaction);

        System.out.println("\n✅ Transação adicionada com sucesso!");
    }

    public static void listTransaction() {
        List<Transaction> transactions = monthlyTransaction.getTransactions();
        Formatter.showTransactions(transactions);

    }

    public static void removeTransaction(Scanner sc) {
        int option;

        listTransaction();
        System.out.print("Se deseja apagar diga o numero da conta a ser apagada da lista: ");

        try {
            option = readInt(sc, 1);
            monthlyTransaction.del(option);
            System.out.println("\n✅ Removido com sucesso!");

        } catch (InvalidInput e) {

            System.out.println(e.getMessage());

        }
    }

    public static void calculateTotalBalance() {
        double total = monthlyTransaction.totalBalance();
        System.out.println("O total do mês: "+ total);
    }

    private static String readString(Scanner sc) {
        
        while (true) {
            try {
                String description = sc.nextLine();

                if (description.isBlank()) {
                    throw new InvalidInput("A descrição não pode estar vazia: ");
                }

                return description;

            } catch (InvalidInput e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private static int readInt(Scanner sc, int id) {

        while (true) {
            try {
                int value = sc.nextInt();
                if (value != 1 && value != 2 && id == 0) {
                    throw new InputMismatchException();
                }
                sc.nextLine();
                return value;

            } catch (InputMismatchException e) {
                System.out.print("Digite uma opção válida: ");
                sc.nextLine();
            }
        }
    }
    
    private static double readDouble(Scanner sc, boolean receipt) {

        while (true) {
            try {
                double value = sc.nextDouble();

                if (value <  0) {
                    throw new InputMismatchException();
                }

                sc.nextLine();
                return receipt ? value : -value;

            } catch (InputMismatchException e) {
                System.out.print("Valor inválido por favor digite novamente: ");
                sc.nextLine();
            }
        }
    }
}
