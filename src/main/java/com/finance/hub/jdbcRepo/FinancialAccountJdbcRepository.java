//package com.finance.hub.jdbcRepo;
//
//import com.finance.hub.model.FinancialAccount;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class FinancialAccountJdbcRepository {
//
//    private final DataSource dataSource;
//
//    public FinancialAccountJdbcRepository(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    //Find Accounts by relationship id
//    public List<FinancialAccount> findAccountsByRelationshipId(Long relationshipId){
//
//        String sql = "SELECT id, account_name, account_number, account_type, balance, relationship_id from financial_account where relationship_id = ?";
//        List<FinancialAccount> accounts = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setLong(1, relationshipId);
//
//            try(ResultSet rs = ps.executeQuery()) {
//                while(rs.next()) {
//                    accounts.add(mapRowToFinancialAccount(rs));
//                }
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
//
//        return accounts;
//    }
//
//    //Find account by account number
//    public FinancialAccount findByAccountNumber(int accountNumber) {
//        String sql = "SELECT id, account_name, account_number, account_type, balance, relationship_id WHERE account_number = ?";
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, accountNumber);
//
//            try(ResultSet rs = ps.executeQuery()){
//                if(rs.next()) {
//                  return mapRowToFinancialAccount(rs);
//                }
//            }
//        } catch(SQLException e){
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//    //Find by Account Type
//    public List<FinancialAccount> findByAccountType(String accountType) {
//        String sql = "SELECT id, account_name, account_number, account_type, balance, relationship_id WHERE account_type = ? ";
//        List<FinancialAccount> accounts = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, accountType);
//
//            try(ResultSet rs = ps.executeQuery()){
//                while(rs.next()){
//                    accounts.add(mapRowToFinancialAccount(rs));
//                }
//            }
//        }catch(SQLException e) {
//            e.printStackTrace();
//        }
//        return accounts;
//    }
//
//
//    private FinancialAccount mapRowToFinancialAccount(ResultSet rs) throws SQLException {
//        FinancialAccount account = new FinancialAccount();
//        account.setAccountName(rs.getString("account_name"));
//        account.setAccountNumber(rs.getInt("account_number"));
//        account.setAccountType(rs.getString("account_type"));
//        account.setBalance(rs.getBigDecimal("balance"));
//
//        return account;
//    }
//
//}
