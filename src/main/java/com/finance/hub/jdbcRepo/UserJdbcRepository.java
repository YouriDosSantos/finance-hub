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

    //Loads Authenticated User with Roles
    public Optional<User> findUserWithRolesByEmail(String email) {
        String sql = """
        SELECT u.id, u.name, u.email, u.password,
               r.id AS role_id, r.authority
        FROM tb_user u
        LEFT JOIN tb_user_role ur ON u.id = ur.user_id
        LEFT JOIN tb_role r ON r.id = ur.role_id
        WHERE u.email = ?
    """;

        User user = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (user == null) {
                        user = new User();
                        user.setId(rs.getLong("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                    }

                    Long roleId = rs.getLong("role_id");
                    String authority = rs.getString("authority");

                    if (roleId != 0 && authority != null) {
                        Role role = new Role();
                        role.setId(roleId);
                        role.setAuthority(authority);
                        user.addRole(role);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading user with roles " + email, e);
        }

        return Optional.ofNullable(user);
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

    public List<User> findAllUsers() {
        String sql = """
        SELECT u.id, u.name, u.email, u.password,
               r.id AS role_id, r.authority
        FROM tb_user u
        LEFT JOIN tb_user_role ur ON u.id = ur.user_id
        LEFT JOIN tb_role r ON r.id = ur.role_id
        ORDER BY u.id
        """;

        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            User currentUser = null;
            Long currentUserId = null;

            while (rs.next()) {
                Long userId = rs.getLong("id");

                if (currentUser == null || !userId.equals(currentUserId)) {
                    currentUser = new User();
                    currentUser.setId(userId);
                    currentUser.setName(rs.getString("name"));
                    currentUser.setEmail(rs.getString("email"));
                    currentUser.setPassword(rs.getString("password"));
                    users.add(currentUser);
                    currentUserId = userId;
                }

                Long roleId = rs.getLong("role_id");
                String authority = rs.getString("authority");
                if (roleId != 0 && authority != null) {
                    Role role = new Role();
                    role.setId(roleId);
                    role.setAuthority(authority);
                    currentUser.addRole(role);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading all users", e);
        }

        return users;
    }

    public void updateUserName(Long id, String name) {
        String sql = "UPDATE tb_user SET name = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user name for id " + id, e);
        }
    }

    public void updateUserEmail(Long id, String email) {
        String sql = "UPDATE tb_user SET email = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user email for id " + id, e);
        }
    }

    public void updateUserRoles(Long id, List<Role> roles) {
        String deleteSql = "DELETE FROM tb_user_role WHERE user_id = ?";
        String insertSql = "INSERT INTO tb_user_role (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setLong(1, id);
                deletePs.executeUpdate();
            }

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                for (Role role : roles) {
                    insertPs.setLong(1, id);
                    insertPs.setLong(2, role.getId());
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user roles for id " + id, e);
        }
    }

}
