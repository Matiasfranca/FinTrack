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

    public void addTransaction(Transaction transaction, BankAccount account, Category category) throws InvalidInput {

        // Garante a data padrão se vier nula
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }

        // 1. Validações centralizadas usando os dados do objeto Transaction
        List<ValidationRule<?>> rules = Arrays.asList(
                new ValidationRule<>(transaction.getValue(), v -> v.compareTo(BigDecimal.ZERO) > 0, "Invalid value."),
                new ValidationRule<>(transaction.getDate(), d -> true, "Date cannot be null."),
                new ValidationRule<>(transaction.getTransactionType(), t -> true, "Transaction type cannot be null."),
                new ValidationRule<>(transaction.getPaymentMethod(), p -> true, "Payment method cannot be null."));

        for (ValidationRule<?> rule : rules) {
            rule.validate();
        }

        // 2. Resolução dos IDs
        int monthId = monthRepository.getOrCreate(YearMonth.from(transaction.getDate())).getId();
        int accountId = bankAccountRepository.getOrCreate(account).getId();
        Integer categoryId = (category != null && category.getName() != null && !category.getName().isBlank())
                ? categoryRepository.getOrCreate(category).getId()
                : null;

        // 3. Tratamento da descrição (opcional, vira null se vazia)
        String desc = transaction.getDescription();
        String finalDescription = (desc == null || desc.isBlank()) ? null : desc.trim();
        transaction.setDescription(finalDescription);

        // 4. Salva no repositório
        transactionRepository.save(transaction, monthId, accountId, categoryId);
    }

    public void updateTransaction(Transaction transaction, BankAccount account, Category category) throws InvalidInput {
        if (transaction == null || transaction.getId() == null || transaction.getId() <= 0) {
            throw new InvalidInput("Cannot update a transaction without a valid ID.");
        }

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }

        // Mesmas regras para o update
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

    public void deleteTransaction(int transactionId) throws InvalidInput {
        if (transactionId <= 0) {
            throw new InvalidInput("Cannot delete a transaction with an invalid ID.");
        }

        transactionRepository.delete(transactionId);
    }

}