//package com.finance.hub.jdbcRepo;
//
//import com.finance.hub.model.Contact;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.sql.*;
//import java.util.ArrayList;
//
//public class ContactJdbcRepository {
//    //Pool of connections
//    private final DataSource dataSource;
//
//
//    public ContactJdbcRepository(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    //Find All Contacts by lastName
//    public List<Contact> findByLastName(String lastName) {
//        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE last_name = ?";
//        List<Contact> contacts = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, lastName);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    contacts.add(mapRowToContact(rs));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return contacts;
//    }
//
//    //Find all contacts by RelationshipId
//    public List<Contact> findByRelationshipId(Long relationshipId) {
//        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE relationship_id = ?";
//        List<Contact> contacts = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setLong(1, relationshipId);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while(rs.next()) {
//                    contacts.add(mapRowToContact(rs));
//                }
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//
//        return contacts;
//    }
//
//    //Find all Contacts by Job Title
//    public List<Contact> findByJobTitle(String jobTitle) {
//        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE job_title = ?";
//        List<Contact> contacts = new ArrayList<>();
//
//        try(Connection conn = dataSource.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, jobTitle);
//
//            try(ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    contacts.add(mapRowToContact(rs));
//                }
//            }
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
//
//        return contacts;
//    }
//
//
//
//
//    private Contact mapRowToContact(ResultSet rs) throws SQLException {
//        Contact contact = new Contact();
//        contact.setFirstname(rs.getString("first_name"));
//        contact.setLastName(rs.getString("last_name"));
//        contact.setEmail(rs.getString("email"));
//        contact.setJobTitle(rs.getString("job_title"));
//        return contact;
//    }
//}
