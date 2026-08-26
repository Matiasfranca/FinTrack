package repository;

import model.Category;
import java.util.List;

public interface CategoryRepository {
    Category save(Category category);
    
    void update(Category category);
    
    void delete(int categoryId);
    
    List<Category> findAll();
}