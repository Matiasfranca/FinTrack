package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Arrays;
import exceptions.InvalidInput;
import model.*;
import model.dto.CardData;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import repository.BankAccountRepository;
import repository.CategoryRepository;
import repository.DashboardRepository;
import repository.MonthRepository;
import repository.TransactionRepository;
import repository.sqlite.SqliteBankAccountRepository;
import repository.sqlite.SqliteCategoryRepository;
import repository.sqlite.SqliteDashboardRepository;
import repository.sqlite.SqliteMonthRepository;
import repository.sqlite.SqliteTransactionRepository;
import utils.ValidationRule;

public class FinTracker {

    private final TransactionRepository transactionRepository = new SqliteTransactionRepository();
    private final MonthRepository monthRepository = new SqliteMonthRepository();
    private final BankAccountRepository bankAccountRepository = new SqliteBankAccountRepository();
    private final CategoryRepository categoryRepository = new SqliteCategoryRepository();
    private final DashboardRepository dashboardRepository = new SqliteDashboardRepository();

    // --- TRANSACTION ---

    public void addTransaction(Transaction transaction, BankAccount account, Category category) throws InvalidInput {

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }

        List<ValidationRule<?>> rules = Arrays.asList(
                new ValidationRule<>(transaction.getValue(), v -> v.compareTo(BigDecimal.ZERO) > 0, "Invalid value."),
                new ValidationRule<>(transaction.getDate(), d -> true, "Date cannot be null."),
                new ValidationRule<>(transaction.getTransactionType(), t -> true, "Transaction type cannot be null."),
                new ValidationRule<>(transaction.getPaymentMethod(), p -> true, "Payment method cannot be null."));

        for (ValidationRule<?> rule : rules) {
            rule.validate();
        }

        int monthId = monthRepository.getOrCreate(YearMonth.from(transaction.getDate())).getId();
        int accountId = bankAccountRepository.getOrCreate(account).getId();
        Integer categoryId = (category != null && category.getName() != null && !category.getName().isBlank())
                ? categoryRepository.getOrCreate(category).getId()
                : null;

        String desc = transaction.getDescription();
        String finalDescription = (desc == null || desc.isBlank()) ? null : desc.trim();
        transaction.setDescription(finalDescription);

        transactionRepository.save(transaction, monthId, accountId, categoryId);
    }

    public void updateTransaction(Transaction transaction, BankAccount account, Category category) throws InvalidInput {
        if (transaction == null || transaction.getId() == null || transaction.getId() <= 0) {
            throw new InvalidInput("Cannot update a transaction without a valid ID.");
        }

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }

        List<ValidationRule<?>> rules = Arrays.asList(
                new ValidationRule<>(transaction.getValue(), v -> v.compareTo(BigDecimal.ZERO) > 0, "Invalid value."),
                new ValidationRule<>(transaction.getDate(), d -> true, "Date cannot be null."),
                new ValidationRule<>(transaction.getTransactionType(), t -> true, "Transaction type cannot be null."),
                new ValidationRule<>(transaction.getPaymentMethod(), p -> true, "Payment method cannot be null."));

        for (ValidationRule<?> rule : rules) {
            rule.validate();
        }

        int monthId = monthRepository.getOrCreate(YearMonth.from(transaction.getDate())).getId();
        int accountId = bankAccountRepository.getOrCreate(account).getId();
        Integer categoryId = (category != null && category.getName() != null && !category.getName().isBlank())
                ? categoryRepository.getOrCreate(category).getId()
                : null;

        String desc = transaction.getDescription();
        String finalDescription = (desc == null || desc.isBlank()) ? null : desc.trim();
        transaction.setDescription(finalDescription);

        transactionRepository.update(transaction, monthId, accountId, categoryId);
    }

    public void deleteTransaction(Transaction transaction) throws InvalidInput {
        if (transaction.getId() <= 0) {
            throw new InvalidInput("Cannot delete a transaction with an invalid ID.");
        }

        transactionRepository.delete(transaction.getId());
    }

    // --- LIST ---

    public List<Transaction> listTransactionsByMonth(YearMonth yearMonth) throws InvalidInput {
        if (yearMonth == null) {
            throw new InvalidInput("YearMonth cannot be null.");
        }

        return transactionRepository.findByMonth(yearMonth);
    }

    public List<CategoryChartData> getCategoryDistribution(YearMonth yearMonth, TransactionType type)
            throws InvalidInput {
        if (yearMonth == null || type == null) {
            throw new InvalidInput("YearMonth and TransactionType cannot be null.");
        }
        int monthId = monthRepository.getOrCreate(yearMonth).getId();
        return dashboardRepository.getCategoryDistribution(monthId, type);
    }

    public List<DailyFinancialData> getMonthlyOverview(YearMonth yearMonth) throws InvalidInput {
        if (yearMonth == null) {
            throw new InvalidInput("YearMonth cannot be null.");
        }
        int monthId = monthRepository.getOrCreate(yearMonth).getId();
        return dashboardRepository.getMonthlyOverview(monthId);
    }

    // --- CARD ---

    public CardData getMonthlyCard(YearMonth yearMonth) throws InvalidInput {
        if (yearMonth == null) {
            throw new InvalidInput("YearMonth cannot be null.");
        }
        int monthId = monthRepository.getOrCreate(yearMonth).getId();
        return dashboardRepository.getMonthlyCard(monthId);
    }

    // --- CATEGORIES ---

    public Category getOrCreateCategory(Category category) {
        return categoryRepository.getOrCreate(category);
    }

    public List<Category> listAllCategories() {
        return categoryRepository.findAll();
    }

    public void updateCategory(Category category) throws InvalidInput {
        if (category == null || category.getId() <= 0 || category.getName() == null || category.getName().isBlank()) {
            throw new InvalidInput("Cannot update category with invalid ID or name.");
        }
        categoryRepository.update(category);
    }

    public void deleteCategory(int categoryId) throws InvalidInput {
        if (categoryId <= 0) {
            throw new InvalidInput("Cannot delete a category with an invalid ID.");
        }
        categoryRepository.delete(categoryId);
    }

    // --- BANK ACCOUNTS ---

    public BankAccount getOrCreateBankAccount(BankAccount account) {
        return bankAccountRepository.getOrCreate(account);
    }

    public List<BankAccount> listAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public List<BankAccount> listActiveBankAccounts() {
        return bankAccountRepository.findAllActive();
    }

    public void updateBankAccount(BankAccount account) throws InvalidInput {
        if (account == null || account.getId() <= 0 || account.getName() == null || account.getName().isBlank()) {
            throw new InvalidInput("Cannot update bank account with invalid ID or name.");
        }
        if (account.getType() == null) {
            throw new InvalidInput("Bank account type cannot be null.");
        }
        bankAccountRepository.update(account);
    }

    public void deactivateBankAccount(int accountId) throws InvalidInput {
        if (accountId <= 0) {
            throw new InvalidInput("Cannot deactivate a bank account with an invalid ID.");
        }
        bankAccountRepository.deactivate(accountId);
    }
}