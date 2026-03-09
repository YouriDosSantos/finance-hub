package com.finance.hub.jdbcRepo;

import com.finance.hub.exception.DatabaseException;
import com.finance.hub.model.Role;
import com.finance.hub.model.User;
import com.finance.hub.projection.UserDetailsProjection;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.processing.SQL;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM tb_user WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading user " + email, e);
        }

        return Optional.empty();
    }

    public List<UserDetailsProjection> searchUserAndRolesByEmail(String email) {
        String sql = """
                SELECT u.email AS username, u.password, r.id AS roleId, r.authority
                FROM tb_user u
                JOIN tb_user_role ur ON u.id = ur.user_id
                JOIN tb_role r ON r.id = ur.role_id
                WHERE u.email = ?
                """;

        List<UserDetailsProjection> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    Long roleId = rs.getLong("roleId");
                    String authority = rs.getString("authority");

                    result.add(new UserDetailsProjection() {
                        public String getUsername() { return username;}
                        public String getPassword() { return password;}
                        public Long getRoleId() { return roleId;}
                        public String getAuthority() { return authority;}
                    });
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading user roles for " + email, e);
        }

        return result;
    }

    @Transactional
    public User save(User user) {
        String insertUser = "INSERT INTO tb_user (name, email, password) VALUES (?, ?, ?)";
        String insertUserRole = "INSERT INTO tb_user_role (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            // Insert user
            try (PreparedStatement ps = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getLong(1));
                    }
                }
            }
            // Insert roles
            for (Role role : user.getAuthorities().stream().map(r -> (Role) r).toList()) {
                try (PreparedStatement ps = conn.prepareStatement(insertUserRole)) {
                    ps.setLong(1, user.getId());
                    ps.setLong(2, role.getId());
                    ps.executeUpdate(); } } }
        catch (SQLException e) {
            throw new DatabaseException("Error saving user", e);
        }

        return user;
    }
}
