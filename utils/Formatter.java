package utils;

import java.util.List;
import java.util.Scanner;

import model.Transaction;

public class Formatter {
    
    public static void clearScreen() {
        System.out.println("\n".repeat(40));
    }

    public static void pause(Scanner sc) {
        System.out.print("\nPressione ENTER para continuar...");
        sc.nextLine();
    }
    
    public static void showMenu() { 
        System.out.print("""
        ──────────────────────────────────────────────────
                        💰 FINTRACK
        ──────────────────────────────────────────────────

        [1] ➜ Adicionar transação
        [2] ➜ Listar transações
        [3] ➜ Remover transação
        [4] ➜ Calcular saldo total
        [5] ➜ Sair

        ──────────────────────────────────────────────────
        Opção: \
        """);
     };

    public static void showInputDescription() {
        System.out.print("""
        ──────────────────────────────────────────────────
                        ➕ NOVA TRANSAÇÃO
        ──────────────────────────────────────────────────

        Descrição: \
        """);
    }


    public static void showInputType() {
        System.out.print("""

        Tipo da transação:

        [1] ➜ Receita
        [2] ➜ Despesa

        Opção: \
        """);
    }

      public static void showInputValue() {
        System.out.print("""

        Valor da transação (R$): \
        """);
    }
    
    public static void showTransactions(List<Transaction> transactions) {
        System.out.print("""
                
        ──────────────────────────────────────────────────────────────────────────────
         ID │ Tipo      │ Valor       │ Data       │ Descrição
        ──────────────────────────────────────────────────────────────────────────────
        """);
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            System.out.printf(
                " %2d │ %-9s │ R$ %8.2f │ %s │ %s%n",
                i + 1,
                transaction.isReceipt() ? "Receita" : "Despesa",
                transaction.getValue(),
                transaction.getDate(),
                transaction.getDescription()
            );
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
    }

    // public static void showTitle(String title) { ... }

    // public static void showError(String message) { ... }

}
