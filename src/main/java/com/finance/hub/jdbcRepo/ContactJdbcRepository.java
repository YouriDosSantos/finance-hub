package com.finance.hub.jdbcRepo;

import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.DatabaseException;
import com.finance.hub.model.Contact;
import com.finance.hub.model.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class ContactJdbcRepository {

    private static final Logger log = LoggerFactory.getLogger(ContactJdbcRepository.class);
    //Pool of connections
    private final DataSource dataSource;


    public ContactJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Contact save(Contact contact) {
        String insertSql = "INSERT INTO contact (first_name, last_name, email, phone, job_title, relationship_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE contact SET first_name = ?, last_name = ?, email = ?, phone = ?, job_title = ?, relationship_id = ? " +
                "WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            if (contact.getId() == null) {
                // INSERT new contact
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, contact.getFirstName());
                    ps.setString(2, contact.getLastName());
                    ps.setString(3, contact.getEmail());
                    ps.setString(4, contact.getPhone());
                    ps.setString(5, contact.getJobTitle());
                    ps.setObject(6, contact.getRelationship() != null ? contact.getRelationship().getId() : null);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            contact.setId(keys.getLong(1));
                        }
                    }
                }
            } else {
                // UPDATE existing contact
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, contact.getFirstName());
                    ps.setString(2, contact.getLastName());
                    ps.setString(3, contact.getEmail());
                    ps.setString(4, contact.getPhone());
                    ps.setString(5, contact.getJobTitle());
                    ps.setObject(6, contact.getRelationship() != null ? contact.getRelationship().getId() : null);
                    ps.setLong(7, contact.getId());

                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error("Error saving contact {}", contact, e);
            throw new DatabaseException("Database error while saving contact: " + e.getMessage(), e);
        }
        return contact;
    }

    public Optional<Contact> findById(Long id) {
        String sql = "SELECT id, first_name, last_name, email, phone, job_title, relationship_id FROM contact WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            log.error("error finding contact by id {}", id, e);
            throw new DatabaseException("Database error while finding contact" + id, e);
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM contact WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting contact with id {}", id, e);
            throw new DatabaseException("Database error while deleting contact: " + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM contact WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking existence of contact with id {}", id, e);
            throw new DatabaseException("Database error while checking contact existence: " + id, e);
        }
        return false;
    }

    public List<Contact> findAll(int limit, int offset, String sortBy, String direction) {
        // Validate sortBy to avoid SQL injection (only allow known columns)
        List<String> validColumns = List.of("id", "first_name", "last_name", "email", "phone", "job_title", "relationship_id");
        if (!validColumns.contains(sortBy)) {
            sortBy = "id"; // default fallback
        }

        // Validate direction
        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        String sql = "SELECT id, first_name, last_name, email, phone, job_title, relationship_id " +
                " FROM contact ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";

        List<Contact> contacts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error loading Contact records (limit={}, offset={}", limit, offset, e);
            throw new DatabaseException("Failed to load contacts" + e.getMessage(), e);
        }
        return contacts;
    }

    public long countContacts(String search) {
        String sql;
        boolean hasSearch = search != null && !search.isBlank();
        if (hasSearch) {
            sql = "SELECT COUNT(*) FROM contact " +
                    "WHERE LOWER(first_name) LIKE LOWER(?) " +
                    "   OR LOWER(last_name) LIKE LOWER(?) " +
                    "   OR LOWER(email) LIKE LOWER(?) " +
                    "   OR LOWER(job_title) LIKE LOWER(?)";
        } else {
            sql = "SELECT COUNT(*) FROM contact";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + search + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting contacts", e);
            throw new DatabaseException("Database error while counting contacts: " + e);
        }
        return 0;
    }



    //Find All Contacts by lastName
    public List<Contact> findByLastName(String lastName) {
        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE last_name = ?";
        List<Contact> contacts = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error checking contacts by Last Name {}", lastName, e);
            throw new DatabaseException("Database error while checking contact by last name: " + lastName, e);
        }

        return contacts;
    }

    //Find all contacts by RelationshipId
    public List<Contact> findByRelationshipId(Long relationshipId) {
        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE relationship_id = ?";
        List<Contact> contacts = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, relationshipId);

            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        }catch (SQLException e){
            log.error("Error checking existence of a relationship with id {}", relationshipId, e);
            throw new DatabaseException("Database error while checking relationship with contact " + relationshipId, e);
        }

        return contacts;
    }

    //Find all Contacts by Job Title
    public List<Contact> findByJobTitle(String jobTitle) {
        String sql = "SELECT id, first_name, last_name, email, job_title, relationship_id FROM contact WHERE job_title = ?";
        List<Contact> contacts = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jobTitle);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch(SQLException e) {
            log.error("Error checking existence of contact with a job title {}", jobTitle, e);
            throw new DatabaseException("Database error while checking contact with a job title: " + jobTitle, e);
        }

        return contacts;
    }


    public List<Contact> searchContacts(String search, int limit, int offset, String sortBy, String direction) {
        List<String> validColumns = List.of("id", "first_name", "last_name", "email", "phone", "job_title", "relationship_id");
        if (!validColumns.contains(sortBy)) {
            sortBy = "id";
        }
        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        String sql = "SELECT id, first_name, last_name, email, phone, job_title, relationship_id " +
                "FROM contact " +
                "WHERE LOWER(first_name) LIKE LOWER(?) " +
                "   OR LOWER(last_name) LIKE LOWER(?) " +
                "   OR LOWER(email) LIKE LOWER(?) " +
                "   OR LOWER(job_title) LIKE LOWER(?) " +
                "ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";

        List<Contact> contacts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + search + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setInt(5, limit);
            ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error checking a contact via search {}", search, e);
            throw new DatabaseException("Database error while checking contact existence via search: " + search, e);
        }
        return contacts;
    }


    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.setId(rs.getLong("id"));
        contact.setFirstName(rs.getString("first_name"));
        contact.setLastName(rs.getString("last_name"));
        contact.setEmail(rs.getString("email"));
        contact.setPhone(rs.getString("phone"));
        contact.setJobTitle(rs.getString("job_title"));
        // relationship_id mapping if needed
        Long relationshipId = rs.getLong("relationship_id");
        if (!rs.wasNull()) {
            Relationship relationship = new Relationship();
            relationship.setId(relationshipId);
            contact.setRelationship(relationship);
        }
        return contact;
    }
}
