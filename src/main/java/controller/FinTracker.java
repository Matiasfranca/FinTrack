package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Arrays;
import exceptions.InvalidInput;
import model.*;
import repository.BankAccountRepository;
import repository.CategoryRepository;
import repository.MonthRepository;
import repository.TransactionRepository;
import repository.sqlite.SqliteBankAccountRepository;
import repository.sqlite.SqliteCategoryRepository;
import repository.sqlite.SqliteMonthRepository;
import repository.sqlite.SqliteTransactionRepository;
import utils.ValidationRule;

public class FinTracker {

private final TransactionRepository transactionRepository = new SqliteTransactionRepository();
    private final MonthRepository monthRepository = new SqliteMonthRepository();
    private final BankAccountRepository bankAccountRepository = new SqliteBankAccountRepository();
    private final CategoryRepository categoryRepository = new SqliteCategoryRepository();

    public void addTransaction(String description, BigDecimal value, TransactionType type,
            PaymentMethod paymentMethod, LocalDate date, BankAccount account, Category category) throws InvalidInput {

        LocalDate transactionDate = (date != null) ? date : LocalDate.now();

        // 1. We declare all our rules in a list
        List<ValidationRule<?>> rules = Arrays.asList(
                new ValidationRule<>(value, v -> v.compareTo(BigDecimal.ZERO) > 0, "Invalid value."),
                new ValidationRule<>(transactionDate, d -> true, "Date cannot be null."), // null is checked automatically inside
                new ValidationRule<>(type, t -> true, "Transaction type cannot be null."),
                new ValidationRule<>(paymentMethod, p -> true, "Payment method cannot be null."));

        // 2. We iterate over the parameters/rules! (Exactly what you wanted)
        for (ValidationRule<?> rule : rules) {
            rule.validate(); // If any rule fails, it throws the exception and stops here
        }

        int monthId = monthRepository.getOrCreate(YearMonth.from(transactionDate)).getId();
        int accountId = bankAccountRepository.getOrCreate(account).getId();
        Integer categoryId = (category != null && category.getName() != null && !category.getName().isBlank()) ? categoryRepository.getOrCreate(category).getId() : null; 

        String finalDescription = (description == null || description.isBlank()) ? null : description.trim();

        // 3. If the loop finishes without errors, the data is 100% valid!
        Transaction transaction = new Transaction(finalDescription, value, type, paymentMethod, transactionDate, accountId, categoryId);

        transactionRepository.save(transaction, monthId, accountId, categoryId);
    }

    // public List<Transaction> listTransaction() {
    // List<Transaction> transactions = this.monthlyTransaction.getTransactions();
    // return transactions;
    // }

    // public void removeTransaction(int option) throws InvalidInput {

    // this.monthlyTransaction.del(option);

    // }

    // public double calculateTotalBalance() {
    // return this.monthlyTransaction.totalBalance();
    // }

}
