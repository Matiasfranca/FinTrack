package repository;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import model.Category;
import repository.sqlite.SqliteCategoryRepository;

import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqliteCategoryRepositoryTest {

    private static SqliteCategoryRepository categoryRepo;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        categoryRepo = new SqliteCategoryRepository();
    }

    @Test
    @Order(1)
    void shouldSaveCategorySuccessfully() {
        Category newCategory = new Category("Food", "#FF5733");

        Category savedCategory = assertDoesNotThrow(() -> {
            return categoryRepo.save(newCategory);
        });

        assertNotNull(savedCategory, "Saved category should not be null");
        assertTrue(savedCategory.getId() > 0, "Generated ID should be greater than zero");
        assertEquals("Food", savedCategory.getName());
        assertEquals("#FF5733", savedCategory.getColor());
    }

    @Test
    @Order(2)
    void shouldFindAllCategories() {
        List<Category> categories = categoryRepo.findAll();

        assertNotNull(categories, "Categories list should not be null");
        assertFalse(categories.isEmpty(), "Categories list should not be empty");
        assertEquals("Food", categories.get(0).getName());
    }

    @Test
    @Order(3)
    void shouldUpdateCategorySuccessfully() {
        List<Category> categories = categoryRepo.findAll();
        Assumptions.assumeTrue(!categories.isEmpty(), "Categories list must not be empty to perform update");

        Category toUpdate = categories.get(0);
        Category updatedCategory = new Category(toUpdate.getId(), "Supermarket", "#33FF57");

        assertDoesNotThrow(() -> {
            categoryRepo.update(updatedCategory);
        });

        List<Category> verification = categoryRepo.findAll();
        assertEquals("Supermarket", verification.get(0).getName());
        assertEquals("#33FF57", verification.get(0).getColor());
    }

    @Test
    @Order(4)
    void shouldDeleteCategorySuccessfully() {
        List<Category> categories = categoryRepo.findAll();

        if (!categories.isEmpty()) {
            int categoryId = categories.get(0).getId();

            assertDoesNotThrow(() -> {
                categoryRepo.delete(categoryId);
            });
        }
    }
}