package com.finance.hub.jdbcRepo;

import com.finance.hub.exception.DatabaseException;
import com.finance.hub.model.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RelationshipJdbcRepository {

    private static final Logger log = LoggerFactory.getLogger(RelationshipJdbcRepository.class);
    //Pool of connections
    private final DataSource dataSource;


    public RelationshipJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Relationship save(Relationship relationship) {
        String insertSql = "INSERT INTO relationship (name, website, email) " +
                "VALUES (?, ?, ?)";
        String updateSql = "UPDATE relationship SET name = ?, website = ?, email = ? " +
                "WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            if (relationship.getId() == null) {
                //INSERT NEW RELATIONSHIP
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, relationship.getName());
                    ps.setString(2, relationship.getWebsite());
                    ps.setString(3, relationship.getEmail());

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            relationship.setId(keys.getLong(1));
                        }
                    }
                }
            } else {
                //UPDATE EXISTING RELATIONSHIP
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, relationship.getName());
                    ps.setString(2, relationship.getWebsite());
                    ps.setString(3, relationship.getEmail());
                    ps.setLong(4, relationship.getId());

                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error("Error saving relationship {}", relationship, e);
            throw new DatabaseException("Database error while saving relationship: " + e.getMessage(), e);
        }

        return relationship;
    }

    public Optional<Relationship> findById(Long id) {
        String sql = "SELECT id, name, website, email FROM relationship WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return Optional.of(mapRowToRelationship(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding relationship by id {}", id, e);
            throw new DatabaseException("Database error while finding relationship" + id, e);
        }

        return Optional.empty();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM relationship WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting relationship with id {}", id, e);
            throw new DatabaseException("Database error while deleting relationship: " + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM relationship WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking existence of relationship with id {}", id, e);
            throw new DatabaseException("Database error while checking relationship existence" + id, e);
        }

        return false;
    }

    public List<Relationship> findAll(int limit, int offset, String sortBy, String direction) {
        //validate sortBy to avoid SQL injection (Only allow known columns)
        List<String> validColumns = List.of("id", "name", "website", "email");

        if (!validColumns.contains(sortBy)){
            sortBy = "id"; //default fallback
        }

        //validate direction
        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";


        String sql = "SELECT id, name, website, email" +
                " FROM relationship ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";

        List<Relationship> relationships = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    relationships.add(mapRowToRelationship(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error loading relationship records (limit={}, offset={}", limit, offset, e);
            throw new DatabaseException("Failed to load relationships" + e.getMessage(), e);
        }

        return relationships;
    }


    public long countRelationships(String search) {
        String sql;
        boolean hasSearch = search != null && !search.isBlank();
        if (hasSearch) {
            sql = "SELECT COUNT(*) FROM relationship " +
                    "WHERE LOWER(name) LIKE LOWER(?) " +
                    "OR LOWER(website) LIKE LOWER(?) " +
                    "OR LOWER(email) LIKE LOWER(?)";
        } else {
            sql = "SELECT COUNT(*) FROM relationship";
        }

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + search + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting relationships", e);
            throw new DatabaseException("Database error while counting relationships" + e);
        }

        return 0;
    }


    //Find Relationship by email
    public Relationship findByEmail(String email){
        String sql = "SELECT id, name, email, website FROM relationship WHERE email = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapRowToRelationship(rs);
                }
            }
        }catch (SQLException e){
            log.error("Error checking the existence of relationship with an email {}", email, e);
            throw new DatabaseException("Database error while checking relationship with an email: " + email, e);
        }

        return null;
    }

    //find relationship containing part of a name
    public List<Relationship> findByNameContainingIgnoreCase(String name){
        String sql = "SELECT id, name, email, website FROM relationship WHERE LOWER(name) LIKE LOWER(?)";
        List<Relationship> relationships = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, "%" + name + "%");

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    relationships.add(mapRowToRelationship(rs));
                }
            }
        } catch(SQLException e){
            log.error("Error finding relationship by name {}", name, e);
            throw new DatabaseException("Database error while checking relationship name" + name, e);
        }

        return relationships;
    }

    public List<Relationship> searchRelationships(String search, int limit, int offset, String sortBy, String direction) {
        List<String> validColumns = List.of("id", "name", "website", "email");
        if (!validColumns.contains(sortBy)) {
            sortBy = "id"; //default fallback
        }

        //validate direction
        String order = direction.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        String sql = "SELECT id, name, website, email"
                + " FROM relationship "
                + " WHERE LOWER(name) LIKE LOWER(?) "
                + " OR LOWER(website) LIKE LOWER(?) "
                + " OR LOWER(email) LIKE LOWER(?) "
                + "ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";


        List<Relationship> relationships = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + search + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            ps.setInt(5, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    relationships.add(mapRowToRelationship(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error loading relationship records (limit={}, offset={}", limit, offset, e);
            throw new DatabaseException("failed to load relationships" + e.getMessage(), e);
        }

        return relationships;

    }


    private Relationship mapRowToRelationship(ResultSet rs) throws SQLException {
        Relationship relationship = new Relationship();
        relationship.setId(rs.getLong("id"));
        relationship.setName(rs.getString("name"));
        relationship.setEmail(rs.getString("email"));
        relationship.setWebsite(rs.getString("website"));

        return relationship;
    }

}
