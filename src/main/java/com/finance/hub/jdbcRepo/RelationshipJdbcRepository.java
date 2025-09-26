//package com.finance.hub.jdbcRepo;
//
//import com.finance.hub.model.FinancialAccount;
//import com.finance.hub.model.Relationship;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RelationshipJdbcRepository {
//
//    private final DataSource dataSource;
//
//
//    public RelationshipJdbcRepository(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    //Find Relationship by email
//    public Relationship findByEmail(String email){
//        String sql = "SELECT id, name, email, website, phone FROM Relationship WHERE email = ?";
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)){
//
//            ps.setString(1, email);
//
//            try(ResultSet rs = ps.executeQuery()){
//                if(rs.next()){
//                    return mapRowToRelationship(rs);
//                }
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    //find by phone
//    public Relationship findByPhone(String phone){
//        String sql = "SELECT id, name, email, website, phone FROM Relationship WHERE phone = ?";
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)){
//
//            ps.setString(1, phone);
//
//            try(ResultSet rs = ps.executeQuery()){
//                if(rs.next()){
//                    return mapRowToRelationship(rs);
//                }
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    //find relationship containing part of a name
//    public List<Relationship> findByNameContainingIgnoreCase(String name){
//        String sql = "SELECT id, name, email, website, phone FROM Relationship WHERE LOWER(name) LIKE LOWER(?)";
//        List<Relationship> relationships = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)){
//
//            ps.setString(1, "%" + name + "%");
//
//            try(ResultSet rs = ps.executeQuery()){
//                while(rs.next()){
//                    relationships.add(mapRowToRelationship(rs));
//                }
//            }
//        } catch(SQLException e){
//            e.printStackTrace();
//        }
//
//        return relationships;
//    }
//
//
//    private Relationship mapRowToRelationship(ResultSet rs) throws SQLException {
//        Relationship relationship = new Relationship();
//        relationship.setName(rs.getString("name"));
//        relationship.setEmail(rs.getString("email"));
//        relationship.setPhone(rs.getString("phone"));
//        relationship.setWebsite(rs.getString("website"));
//
//        return relationship;
//    }
//
//}
