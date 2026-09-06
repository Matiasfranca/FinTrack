package ui.console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

import controller.FinTracker;
import exceptions.InvalidInput;
import model.BankAccount;
import model.BankAccountType;
import model.Category;
import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;
import model.dto.CardData;
import model.dto.CategoryChartData;

public class ConsoleUI {

    private final FinTracker finTracker;

    public ConsoleUI() {
        this.finTracker = new FinTracker();
    }

    public void start(Scanner sc) {

        int option = 0;

        do {
            option = 0;
            ConsoleFormatter.clearScreen();
            ConsoleFormatter.showMenu();
            option = ConsoleInput.readInt(sc, 5);

            switch (option) {

                case 1 -> this.addTransaction(sc);
                case 2 -> this.listTransaction(sc);
                case 3 -> this.removeTransaction(sc);
                case 4 -> this.showDashboard(sc);
                case 5 -> System.out.println("\nSaindo do FinTrack. Até logo! 👋");

            }
        } while (option != 5);

        sc.close();
    }

    private void addTransaction(Scanner sc) {

        ConsoleFormatter.clearScreen();
        ConsoleFormatter.showHeader("➕ NOVA TRANSAÇÃO");

        ConsoleFormatter.showInputDescription();
        String description = ConsoleInput.readString(sc);

        ConsoleFormatter.showInputValue();
        double val = ConsoleInput.readDouble(sc);
        BigDecimal value = BigDecimal.valueOf(val);

        ConsoleFormatter.showInputType();
        int typeOpt = ConsoleInput.readInt(sc, 4);
        TransactionType type = switch (typeOpt) {
            case 1 -> TransactionType.INCOME;
            case 3 -> TransactionType.INVESTMENT;
            case 4 -> TransactionType.REDEMPTION;
            default -> TransactionType.EXPENSE;
        };

        List<BankAccount> activeAccounts = finTracker.listActiveBankAccounts();
        BankAccount account = null;

        if (activeAccounts.isEmpty() && type != TransactionType.REDEMPTION) {
            System.out.println("\nNenhuma conta cadastrada. Vamos criar uma nova.");
            System.out.print("Nome da Conta (ex: Nubank): ");
            String bankName = ConsoleInput.readString(sc);

            ConsoleFormatter.showBankAccountTypes();
            int typeAccOpt = ConsoleInput.readInt(sc, 4);
            BankAccountType accType = mapBankAccountType(typeAccOpt);

            account = new BankAccount(bankName, accType);
        } else if (activeAccounts.isEmpty()) {
            ConsoleFormatter.showError("Você precisa ter contas cadastradas para realizar um resgate.");
            ConsoleFormatter.pause(sc);
            return;
        } else {
            ConsoleFormatter.showBankAccounts(activeAccounts);
            int maxOpt = (type == TransactionType.REDEMPTION) ? activeAccounts.size() : activeAccounts.size() + 1;
            int accOpt = ConsoleInput.readInt(sc, maxOpt);

            if (accOpt == activeAccounts.size() + 1) {
                System.out.print("Nome da Nova Conta: ");
                String bankName = ConsoleInput.readString(sc);

                ConsoleFormatter.showBankAccountTypes();
                int typeAccOpt = ConsoleInput.readInt(sc, 4);
                BankAccountType accType = mapBankAccountType(typeAccOpt);
                account = new BankAccount(bankName, accType);
            } else if (accOpt > 0 && accOpt <= activeAccounts.size()) {
                account = activeAccounts.get(accOpt - 1);
            } else {
                account = activeAccounts.get(0);
            }
        }

        ConsoleFormatter.showPaymentMethods();
        int payOpt = ConsoleInput.readInt(sc, 6);
        PaymentMethod paymentMethod = switch (payOpt) {
            case 1 -> PaymentMethod.PIX;
            case 2 -> PaymentMethod.CREDIT_CARD;
            case 3 -> PaymentMethod.DEBIT_CARD;
            case 4 -> PaymentMethod.CASH;
            case 5 -> PaymentMethod.BANK_TRANSFER;
            default -> PaymentMethod.BOLETO;
        };

        List<Category> categories = finTracker.listAllCategories();
        Category category = null;

        if (!categories.isEmpty()) {
            ConsoleFormatter.showCategories(categories);

            int maxCatOpt = (type == TransactionType.REDEMPTION) ? categories.size() + 1 : categories.size() + 2;
            int catOpt = ConsoleInput.readInt(sc, maxCatOpt);

            if (catOpt == categories.size() + 2 && type != TransactionType.REDEMPTION) {
                System.out.print("Nome da Nova Categoria: ");
                String catName = ConsoleInput.readString(sc);
                category = new Category(catName, "#CCCCCC");
            } else if (catOpt == categories.size() + 1 && type != TransactionType.REDEMPTION) {
                category = null;
            } else if (catOpt > 0 && catOpt <= categories.size()) {
                category = categories.get(catOpt - 1);
            }
        } else {
            ConsoleFormatter.showCategories(categories);

            int catOpt = ConsoleInput.readInt(sc, categories.size() + 2);

            if (catOpt == categories.size() + 2) {
                System.out.print("Nome da Nova Categoria: ");
                String catName = ConsoleInput.readString(sc);
                category = new Category(catName, "#CCCCCC");
            } else if (catOpt == categories.size() + 1) {
                category = null;
            } else {
                category = categories.get(catOpt - 1);
            }
        }

        Transaction transaction = new Transaction(description, value, type, paymentMethod, LocalDate.now());

        try {
            finTracker.addTransaction(transaction, account, category);
            ConsoleFormatter.showSuccess("\n✅ Transação adicionada com sucesso!");
        } catch (InvalidInput e) {
            ConsoleFormatter.showError(e.getMessage());
        }

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

    private void listTransaction(Scanner sc) {
        ConsoleFormatter.clearScreen();
        ConsoleFormatter.showHeader("📋 LISTA DE TRANSAÇÕES (MÊS ATUAL)");

        try {
            List<Transaction> transactions = finTracker.listTransactionsByMonth(YearMonth.now());
            if (transactions.isEmpty()) {
                System.out.println("\nNenhuma transação encontrada para este mês.");
            } else {
                ConsoleFormatter.showTransactions(transactions);
            }
        } catch (InvalidInput e) {
            ConsoleFormatter.showError(e.getMessage());
        }

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

    private void removeTransaction(Scanner sc) {
        ConsoleFormatter.clearScreen();
        ConsoleFormatter.showHeader("🗑️ REMOVER TRANSAÇÃO");

        try {
            List<Transaction> transactions = finTracker.listTransactionsByMonth(YearMonth.now());
            if (transactions.isEmpty()) {
                System.out.println("\nNão há transações para remover neste mês.");
                ConsoleFormatter.pause(sc);
                return;
            }

            ConsoleFormatter.showTransactions(transactions);
            System.out.print("\nDigite o ID da transação a ser apagada: ");
            int id = ConsoleInput.readInt(sc, transactions.size());

            try {
                finTracker.deleteTransaction(transactions.get(id - 1));
                ConsoleFormatter.showSuccess("Transação removida com sucesso!");
                return;
            } catch (InvalidInput e) {
                ConsoleFormatter.showError(e.getMessage());
            }

        } catch (InvalidInput e) {
            ConsoleFormatter.showError(e.getMessage());
        }

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();
    }

    private void showDashboard(Scanner sc) {
        ConsoleFormatter.clearScreen();
        YearMonth currentMonth = YearMonth.now();

        try {
            CardData card = finTracker.getMonthlyCard(currentMonth);
            ConsoleFormatter.showDashboardCard(card);

            List<CategoryChartData> expensesByCategory = finTracker.getCategoryDistribution(currentMonth,
                    TransactionType.EXPENSE);
            ConsoleFormatter.showCategoryDistribution(expensesByCategory);

        } catch (InvalidInput e) {
            ConsoleFormatter.showError(e.getMessage());
        }

        ConsoleFormatter.pause(sc);
        ConsoleFormatter.clearScreen();

    }

    private BankAccountType mapBankAccountType(int option) {
        return switch (option) {
            case 1 -> BankAccountType.CHECKING;
            case 2 -> BankAccountType.SAVINGS;
            case 3 -> BankAccountType.CASH;
            default -> BankAccountType.OTHER;
        };
    }

}