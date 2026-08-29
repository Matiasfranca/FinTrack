package repository;

import model.BankAccount;
import java.util.List;

public interface BankAccountRepository {
    BankAccount getOrCreate(BankAccount account);

    BankAccount save(BankAccount account);
    
    void update(BankAccount account);
    
    void deactivate(int accountId);
    
    List<BankAccount> findAll();
    
    List<BankAccount> findAllActive();
}