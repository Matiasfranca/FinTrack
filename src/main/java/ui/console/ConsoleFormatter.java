package ui.console;

import java.util.List;
import java.util.Scanner;

import model.BankAccount;
import model.Category;
import model.Transaction;
import model.dto.CardData;
import model.dto.CategoryChartData;

public class ConsoleFormatter {
    
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
        [4] ➜ Resumo Financeiro (Dashboard)
        [5] ➜ Sair

        ──────────────────────────────────────────────────
        Opção: \
        """);
    }

    public static void showHeader(String title) {
        System.out.println("\n──────────────────────────────────────────────────");
        System.out.println("                " + title);
        System.out.println("──────────────────────────────────────────────────");
    }

    public static void showInputDescription() {
        System.out.print("\nDescrição da transação: ");
    }

    public static void showInputType() {
        System.out.print("""

        Tipo da transação:

        [1] ➜ Receita (Entrada)
        [2] ➜ Despesa (Saída)
        [3] ➜ Investimento

        Opção: \
        """);
    }

    public static void showInputValue() {
        System.out.print("\nValor da transação (R$): ");
    }
    
    public static void showTransactions(List<Transaction> transactions) {
        System.out.print("""
                
        ──────────────────────────────────────────────────────────────────────────────
         ID   │ Tipo         │ Valor       │ Data       │ Descrição
        ──────────────────────────────────────────────────────────────────────────────
        """);
        for (Transaction transaction : transactions) {
            
            String tipoTraduzido = switch (transaction.getTransactionType()) {
                case INCOME -> "Receita";
                case EXPENSE -> "Despesa";
                case INVESTMENT -> "Investimento";
            };

            System.out.printf(
                " %-4d │ %-12s │ R$ %8.2f │ %s │ %s%n",
                transaction.getId(),
                tipoTraduzido,
                transaction.getValue(),
                transaction.getDate(),
                transaction.getDescription() != null ? transaction.getDescription() : "-"
            );
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
    }

    public static void showPaymentMethods() {
        System.out.print("""

        Forma de Pagamento:
        [1] ➜ PIX
        [2] ➜ Cartão de Crédito
        [3] ➜ Cartão de Débito
        [4] ➜ Dinheiro (Cash)
        [5] ➜ Transferência Bancária
        [6] ➜ Boleto

        Opção: \
        """);
    }

    public static void showBankAccounts(List<BankAccount> accounts) {
        System.out.println("\nContas Bancárias Disponíveis:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] ➜ %s (%s)%n", i + 1, accounts.get(i).getName(), accounts.get(i).getType());
        }
        System.out.printf("[%d] ➜ Criar uma nova conta na hora\nOpção: ", accounts.size() + 1);
    }

    public static void showBankAccountTypes() {
        System.out.print("""

        Tipo de Conta:
        [1] ➜ Conta Corrente (Checking)
        [2] ➜ Conta Poupança (Savings)
        [3] ➜ Carteira Física (Cash)
        [4] ➜ Outros

        Opção: \
        """);
    }

    public static void showCategories(List<Category> categories) {
        System.out.println("\nCategorias Disponíveis:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("[%d] ➜ %s%n", i + 1, categories.get(i).getName());
        }
        System.out.printf("[%d] ➜ Outros\n", categories.size() + 1);
        System.out.printf("[%d] ➜ Criar uma nova categoria\nOpção: ", categories.size() + 2);
    }

    // --- DASHBOARD METHODS ---

    public static void showDashboardCard(CardData card) {
        System.out.printf("""
                
        ──────────────────────────────────────────────────
                        📊 BALANÇO DO MÊS
        ──────────────────────────────────────────────────
         📥 Receitas      : R$ %10.2f
         📤 Despesas      : R$ %10.2f
         💎 Investimentos : R$ %10.2f
        ──────────────────────────────────────────────────
         ⚖️ SALDO TOTAL   : R$ %10.2f
        ──────────────────────────────────────────────────
        """, 
        card.getTotalIncome(), 
        card.getTotalExpense(), 
        card.getTotalInvestment(),
        card.getCashFlowBalance());
    }

    public static void showCategoryDistribution(List<CategoryChartData> distribution) {
        System.out.println("\n📈 GASTOS POR CATEGORIA:");
        System.out.println("──────────────────────────────────────────────────");
        
        if (distribution.isEmpty()) {
            System.out.println("  Nenhum gasto registrado neste mês.");
        } else {
            for (CategoryChartData data : distribution) {
                System.out.printf(" 🔸 %-20s │ R$ %8.2f%n", data.getCategoryName(), data.getTotalValue());
            }
        }
        System.out.println("──────────────────────────────────────────────────");
    }

    public static void showError(String message) {
        System.out.println("\n❌ ERRO: " + message);
    }

    public static void showSuccess(String message) {
        System.out.println("\n✅ SUCESSO: " + message);
    }
}