package ui.javafx.components;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;

public class values {

    public static final int DAYS = 31;

    private static final double[] DAILY_INCOME = new double[DAYS];
    private static final double[] DAILY_EXPENSE = new double[DAYS];
    private static final double[] DAILY_BALANCE = new double[DAYS];
    private static double TOTAL_INCOME = 0;
    private static double TOTAL_EXPENSE = 0;
    private static double TOTAL_BALANCE = 0;

    @SuppressWarnings("deprecation")
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

    private static final List<Transaction> SAMPLE_TRANSACTIONS = List.of(

            new Transaction(
                    "Supermercado",
                    new BigDecimal("310.00"),
                    TransactionType.EXPENSE,
                    PaymentMethod.PIX,
                    LocalDate.now()),

            new Transaction(
                    42,
                    "Salário",
                    new BigDecimal("3200.00"),
                    TransactionType.INCOME,
                    PaymentMethod.BANK_TRANSFER,
                    LocalDate.of(2026, 8, 5),
                    1, // bankAccountId
                    3 // categoryId
            ));

    public static double[] dailyBalanceFor(int year, int month) {

        Random random = new Random();
        double[] balance = new double[DAYS];

        for (int i = 0; i < DAYS; i++) {
            double income = random.nextDouble() * 500;
            double expense = random.nextDouble() * 400;
            balance[i] = income - expense;
        }

        return balance;
    }

    static {
        Random random = new Random(42);
        for (int i = 0; i < DAYS; i++) {
            double income = random.nextDouble() * 500; // 0 a 500
            double expense = random.nextDouble() * 400; // 0 a 400

            DAILY_INCOME[i] = income;
            DAILY_EXPENSE[i] = expense;
            DAILY_BALANCE[i] = income - expense;
            TOTAL_INCOME += income;
            TOTAL_EXPENSE += expense;
            TOTAL_BALANCE += income - expense;
        }

        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
    }

    public static double[] dailyIncome() {
        return DAILY_INCOME;
    }

    public static double[] dailyExpense() {
        return DAILY_EXPENSE;
    }

    public static double[] dailyBalance() {
        return DAILY_BALANCE;
    }

    public static double totalBalance() {
        return TOTAL_BALANCE;
    }

    public static double totalExpense() {
        return TOTAL_EXPENSE;
    }

    public static double totalIncome() {
        return TOTAL_INCOME;
    }

    public static List<Transaction> sampleTransactions() {
    return SAMPLE_TRANSACTIONS;
    }

    public static String formatCurrency(double value) {
        return CURRENCY_FORMAT.format(value);
    }
}
