package repository;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import model.BankAccount;
import model.BankAccountType;
import repository.sqlite.SqliteBankAccountRepository;

import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqliteBankAccountRepositoryTest {

    private static SqliteBankAccountRepository bankAccountRepo;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        bankAccountRepo = new SqliteBankAccountRepository();
    }

    @Test
    @Order(1)
    void shouldSaveBankAccountSuccessfully() {
        BankAccount newAccount = new BankAccount("Nubank", BankAccountType.CHECKING);

        BankAccount savedAccount = assertDoesNotThrow(() -> {
            return bankAccountRepo.save(newAccount);
        });

        assertNotNull(savedAccount, "Saved bank account should not be null");
        assertTrue(savedAccount.getId() > 0, "Generated ID should be greater than zero");
        assertEquals("Nubank", savedAccount.getName());
        assertEquals(BankAccountType.CHECKING, savedAccount.getType());
        assertTrue(savedAccount.isActive());
    }

    @Test
    @Order(2)
    void shouldFindAllBankAccounts() {
        List<BankAccount> accounts = bankAccountRepo.findAll();

        assertNotNull(accounts, "Bank accounts list should not be null");
        assertFalse(accounts.isEmpty(), "Bank accounts list should not be empty");
        assertEquals("Nubank", accounts.get(0).getName());
    }

    @Test
    @Order(3)
    void shouldUpdateBankAccountSuccessfully() {
        List<BankAccount> accounts = bankAccountRepo.findAll();
        Assumptions.assumeTrue(!accounts.isEmpty(), "Bank accounts list must not be empty to perform update");

        BankAccount toUpdate = accounts.get(0);
        BankAccount updatedAccount = new BankAccount(toUpdate.getId(), "Inter", BankAccountType.CASH,
                toUpdate.isActive());

        assertDoesNotThrow(() -> {
            bankAccountRepo.update(updatedAccount);
        });

        List<BankAccount> verification = bankAccountRepo.findAll();
        assertEquals("Inter", verification.get(0).getName());
        assertEquals(BankAccountType.CASH, verification.get(0).getType());
    }

    @Test
    @Order(4)
    void shouldDeactivateBankAccountSuccessfully() {
        List<BankAccount> accounts = bankAccountRepo.findAllActive();
        Assumptions.assumeTrue(!accounts.isEmpty(),
                "Active bank accounts list must not be empty to perform deactivation");

        int accountId = accounts.get(0).getId();

        assertDoesNotThrow(() -> {
            bankAccountRepo.deactivate(accountId);
        });

        List<BankAccount> activeAccounts = bankAccountRepo.findAllActive();
        boolean isStillActive = activeAccounts.stream().anyMatch(acc -> acc.getId() == accountId);
        assertFalse(isStillActive, "Deactivated bank account should not appear in active accounts list");
    }
}