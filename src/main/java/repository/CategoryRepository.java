package repository;

import model.Category;
import model.TransactionType;

import java.util.List;

public interface CategoryRepository {
    Category getOrCreate(Category category);

    Category save(Category category);
    
    void update(Category category);
    
    void delete(int categoryId);
    
    List<Category> findAll();

    List<Category> findByTransactionType(TransactionType type);
}