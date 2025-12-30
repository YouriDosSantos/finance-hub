package com.finance.hub.jdbcRepo;

import com.finance.hub.exception.DatabaseException;
import com.finance.hub.model.Contact;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.model.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FinancialAccountJdbcRepository {


    private static final Logger log = LoggerFactory.getLogger(FinancialAccountJdbcRepository.class);
    //Pool of connections
    private final DataSource dataSource;

    public FinancialAccountJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public FinancialAccount save(FinancialAccount financialAccount) {
        String insertSql = "INSERT INTO financial_account " + "(account_name, account_number, account_type, balance, relationship_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE financial_account SET "  + "account_name = ?, account_number = ?, account_type = ?, balance = ?, relationship_id = ? " +
                "WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            if(financialAccount.getId() == null) {
                //INSERT NEW Financial Account
                try (PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)){

                    ps.setString(1, financialAccount.getAccountName());
                    ps.setString(2, financialAccount.getAccountNumber());
                    ps.setString(3, financialAccount.getAccountType());
                    ps.setBigDecimal(4, financialAccount.getBalance());
                    ps.setObject(5, financialAccount.getRelationship() != null ? financialAccount.getRelationship().getId() : null);

                    ps.executeUpdate();

                    try(ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()){
                            financialAccount.setId(keys.getLong(1));
                        }
                    }
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {

                    ps.setString(1, financialAccount.getAccountName());
                    ps.setString(2, financialAccount.getAccountNumber());
                    ps.setString(3, financialAccount.getAccountType());
                    ps.setBigDecimal(4, financialAccount.getBalance());
                    ps.setObject(5, financialAccount.getRelationship() != null ? financialAccount.getRelationship().getId() : null);
                    ps.setLong(6, financialAccount.getId());

                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error("Error saving financial account {}", financialAccount, e);
            throw new DatabaseException("Database error while saving financial account " + e.getMessage(), e);
        }

        return financialAccount;
    }

    public Optional<FinancialAccount> findById(Long id) {
        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                WHERE fa.id = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToFinancialAccount(rs));
                }
            }
        } catch (SQLException e) {
            log.error("error finding financial account by id {}", id, e);
            throw new DatabaseException("Database error while finding financial account" + id, e);
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM financial_account WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting financial account with id {}", id, e);
            throw new DatabaseException("Database error while deleting financial account " + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM financial_account where id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking existence of financial account with id {}", id, e);
            throw new DatabaseException("Database error while checking financial account existence: " + id, e);
        }

        return false;
    }

    public List<FinancialAccount> findAll(int limit, int offset, String sortBy, String direction) {
        // Validate sortBy to avoid SQL injection (only allow known columns)
        List<String> validColumns = List.of("id", "account_name", "account_number", "account_type", "balance", "relationship_id");
        if(!validColumns.contains(sortBy)){
            sortBy = "id"; //default fallback
        }

        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                ORDER BY %s %s
                LIMIT ? OFFSET ?
                """.formatted(sortBy, order);

        List<FinancialAccount> financialAccounts = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    financialAccounts.add(mapRowToFinancialAccount(rs));
                }
            }
        } catch (SQLException e){
            log.error("Error loading FinancialAccount records (limit={}, offset={}", limit, offset, e);
            throw new DatabaseException("Failed to load financial accounts: " + e.getMessage(), e);
        }

        return financialAccounts;
    }

    public long countFinancialAccounts(String search) {
        String sql;
        boolean hasSearch = search != null && !search.isBlank();

        if(hasSearch) {
            sql = "SELECT COUNT(*) FROM financial_account " +
                    "WHERE LOWER(account_name) LIKE LOWER(?) " +
                    "OR LOWER(account_number) LIKE LOWER(?) " +
                    "OR LOWER(account_type) LIKE LOWER(?)";
        } else {
            sql = "SELECT COUNT(*) FROM financial_account";
        }

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            if(hasSearch) {
                String like = "%" + search + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting Financial Accounts ", e);
            throw new DatabaseException("Database error while counting financial account: " + e.getMessage(), e);
        }

        return 0;
    }



    //Find Accounts by relationship id
    public List<FinancialAccount> findAccountsByRelationshipId(Long relationshipId){

        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                WHERE fa.relationship_id = ?
                """;
        List<FinancialAccount> accounts = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, relationshipId);

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    accounts.add(mapRowToFinancialAccount(rs));
                }
            }
        }catch(SQLException e){
            log.error("Error checking existence of a relationship with id {}", relationshipId, e);
            throw new DatabaseException("Database error while checking relationship with Account " + relationshipId, e);
        }

        return accounts;
    }

    //Find account by account number
    public Optional<FinancialAccount> findByAccountNumber(String accountNumber) {
        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                WHERE fa.account_number = ?
                """;

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                  return Optional.of(mapRowToFinancialAccount(rs));
                }
            }
        } catch(SQLException e){
            log.error("Error finding account by accountNumber {}", accountNumber, e);
            throw new DatabaseException("Database error while finding account by Account Number " + accountNumber, e);
        }

        return Optional.empty();
    }


    //Find by Account Type
    public List<FinancialAccount> findByAccountType(String accountType) {
        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                WHERE fa.account_type = ?
                """;
        List<FinancialAccount> accounts = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountType);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    accounts.add(mapRowToFinancialAccount(rs));
                }
            }
        }catch(SQLException e) {
            log.error("Error finding accounts by accountType {}", accountType, e);
            throw new DatabaseException("Database error while finding accounts by account type" + accountType, e);
        }
        return accounts;
    }


    public List<FinancialAccount> searchFinancialAccounts(String search, int limit, int offset, String sortBy, String direction) {
        List<String> validColumns = List.of("id", "account_name", "account_number", "account_type", "balance", "relationship_id");
        if (!validColumns.contains(sortBy)) {
            sortBy = "id";
        }
        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        String sql = """
                SELECT
                    fa.id, fa.account_name, fa.account_number, fa.account_type, fa.balance, fa.relationship_id, r.name AS relationship_name
                FROM financial_account fa
                LEFT JOIN relationship r ON fa.relationship_id = r.id
                WHERE LOWER(fa.account_name) LIKE LOWER(?)
                    OR LOWER(fa.account_number) LIKE LOWER(?)
                    OR LOWER(fa.account_type) LIKE LOWER(?)
                ORDER BY %s %s
                LIMIT ? OFFSET ?
                """.formatted(sortBy, offset);

        List<FinancialAccount> financialAccounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + search + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    financialAccounts.add(mapRowToFinancialAccount(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error checking a financial account via search {}", search, e);
            throw new DatabaseException("Database error while checking financial account existence via search: " + search, e);
        }
        return financialAccounts;
    }


    private FinancialAccount mapRowToFinancialAccount(ResultSet rs) throws SQLException {
        FinancialAccount account = new FinancialAccount();
        account.setId(rs.getLong("id"));
        account.setAccountName(rs.getString("account_name"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));

        String relationshipName = rs.getString("relationship_name");

        Long relationshipId = rs.getLong("relationship_id");
        if(!rs.wasNull()) {
            Relationship relationship = new Relationship();
            relationship.setId(relationshipId);
            relationship.setName(relationshipName);
            account.setRelationship(relationship);
        }

        return account;
    }

}
