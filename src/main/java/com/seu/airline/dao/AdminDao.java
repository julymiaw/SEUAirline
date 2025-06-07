package com.seu.airline.dao;

import com.seu.airline.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Admin> adminRowMapper = new RowMapper<Admin>() {
        @Override
        public Admin mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Admin admin = new Admin();
            admin.setAdminId(rs.getString("AdminID"));
            admin.setUsername(rs.getString("Username"));
            admin.setPassword(rs.getString("Password"));
            admin.setName(rs.getString("Name"));
            return admin;
        }
    };

    // 对应Flask的管理员登录验证
    public Optional<Admin> findByUsernameAndPassword(String username, String password) {
        // 支持用户名或AdminID登录
        String sql = """
                SELECT AdminID, Username, Password, Name
                FROM Admin
                WHERE (Username = ? OR AdminID = ?) AND Password = ?
                """;
        try {
            Admin admin = jdbcTemplate.queryForObject(sql, adminRowMapper, username, username, password);
            return Optional.ofNullable(admin);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 根据AdminID查找管理员
    public Optional<Admin> findByAdminId(String adminId) {
        String sql = "SELECT AdminID, Username, Password, Name FROM Admin WHERE AdminID = ?";
        try {
            Admin admin = jdbcTemplate.queryForObject(sql, adminRowMapper, adminId);
            return Optional.ofNullable(admin);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 查询所有管理员
    public List<Admin> findAll() {
        String sql = "SELECT AdminID, Username, Password, Name FROM Admin";
        return jdbcTemplate.query(sql, adminRowMapper);
    }

    // 管理员数量统计
    public int count() {
        String sql = "SELECT COUNT(*) FROM Admin";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }
}