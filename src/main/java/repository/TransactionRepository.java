package repository;

import java.time.YearMonth;
import java.util.List;
import model.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction, int monthId, int bankAccountId, Integer categoryId);
    List<Transaction> findByMonth(YearMonth month);
    void delete(int transactionId);
    void update(Transaction transaction, int monthId, int bankAccountId, Integer categoryId);
}