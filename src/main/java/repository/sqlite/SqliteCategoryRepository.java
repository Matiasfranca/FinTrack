package repository.sqlite;

import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.Category;
import repository.CategoryRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteCategoryRepository implements CategoryRepository {

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO CATEGORY (name, color) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getColor());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Category(
                        generatedKeys.getInt(1), 
                        category.getName(), 
                        category.getColor()
                    );
                } else {
                    throw new SQLException("Failed to create category, no ID returned.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save category: " + category.getName(), e);
        }
    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE CATEGORY SET name = ?, color = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getColor());
            stmt.setInt(3, category.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update category: " + category.getName(), e);
        }
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM CATEGORY WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete category with ID: " + categoryId, e);
        }
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT id, name, color FROM CATEGORY ORDER BY name ASC";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("color")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve categories", e);
        }
        
        return categories;
    }
}