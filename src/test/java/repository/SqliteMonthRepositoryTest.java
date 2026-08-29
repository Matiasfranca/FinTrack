package repository;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import model.Month;
import repository.sqlite.SqliteMonthRepository;

import java.time.YearMonth;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqliteMonthRepositoryTest {

    private static SqliteMonthRepository monthRepo;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        monthRepo = new SqliteMonthRepository();
    }

    @Test
    @Order(1)
    void getOrCreateTest() {
        YearMonth targetMonth = YearMonth.of(2021, 5);

        Month month = monthRepo.getOrCreate(targetMonth);
        assertNotNull(month, "Returned Month object should not be null");
        assertTrue(month.getId() > 0, "Returned month ID must be greater than zero");

        // The "trick": calling it again should return an object with the SAME ID
        Month secondMonth = monthRepo.getOrCreate(targetMonth);

        assertEquals(month.getId(), secondMonth.getId(), "Subsequent calls for the same month must return the same ID");
    }

}