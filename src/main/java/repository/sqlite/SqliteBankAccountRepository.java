package repository.sqlite;

import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.BankAccount;
import model.BankAccountType;
import repository.BankAccountRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteBankAccountRepository implements BankAccountRepository {

    @Override
    public BankAccount save(BankAccount account) {
        String sql = "INSERT INTO BANK_ACCOUNT (name, type) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getType().name());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new BankAccount(
                        generatedKeys.getInt(1),
                        account.getName(),
                        account.getType(),
                        account.isActive()
                    );
                } else {
                    throw new SQLException("Falha ao criar conta bancária, nenhum ID retornado.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao salvar a conta bancária: " + account.getName(), e);
        }
    }

    @Override
    public void update(BankAccount account) {
        String sql = "UPDATE BANK_ACCOUNT SET name = ?, type = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getType().name());
            stmt.setInt(3, account.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao atualizar a conta bancária", e);
        }
    }

    @Override
    public void deactivate(int accountId) {
        String sql = "UPDATE BANK_ACCOUNT SET is_active = 0 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao desativar a conta bancária " + accountId, e);
        }
    }

    @Override
    public List<BankAccount> findAll() {
        return fetchAccounts("SELECT id, name, type, is_active FROM BANK_ACCOUNT ORDER BY name ASC");
    }

    @Override
    public List<BankAccount> findAllActive() {
        return fetchAccounts("SELECT id, name, type, is_active FROM BANK_ACCOUNT WHERE is_active = 1 ORDER BY name ASC");
    }

    private List<BankAccount> fetchAccounts(String sql) {
        List<BankAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(new BankAccount(
                    rs.getInt("id"),
                    rs.getString("name"),
                    BankAccountType.valueOf(rs.getString("type")),
                    rs.getInt("is_active") == 1
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao buscar contas bancárias", e);
        }
        
        return accounts;
    }
}