package com.finance.hub.jdbcRepo;

import com.finance.hub.exception.DatabaseException;
import com.finance.hub.model.Role;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RoleJdbcRepository {

    private final DataSource dataSource;

    public RoleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Role findByAuthority(String authority) {
        String sql = "SELECT id, authority FROM tb_role WHERE authority = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authority);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setAuthority(rs.getString("authority"));
                    return role;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading role " + authority, e);
        }

        return null;
    }
}
