package com.seu.airline.dao;

import com.seu.airline.entity.Airport;
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
public class AirportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Airport> airportRowMapper = new RowMapper<Airport>() {
        @Override
        public Airport mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Airport airport = new Airport();
            airport.setAirportId(rs.getString("AirportID"));
            airport.setAirportName(rs.getString("AirportName"));
            return airport;
        }
    };

    // Flask中的机场模糊搜索功能 - 对应 /api/airports 路由
    public List<Airport> findByIdOrNameContaining(String query) {
        String sql = """
                SELECT AirportID, AirportName
                FROM Airport
                WHERE AirportID = ? OR AirportName LIKE ?
                LIMIT 10
                """;
        return jdbcTemplate.query(sql, airportRowMapper, query, "%" + query + "%");
    }

    // Flask中的机场名称模糊搜索功能
    public List<Airport> findByNameContaining(String name) {
        String sql = """
                SELECT AirportID, AirportName
                FROM Airport
                WHERE AirportName LIKE ?
                """;
        return jdbcTemplate.query(sql, airportRowMapper, "%" + name + "%");
    }

    // Flask中的机场ID精确查找功能
    public Optional<Airport> findByAirportId(String airportId) {
        String sql = "SELECT AirportID, AirportName FROM Airport WHERE AirportID = ?";
        try {
            Airport airport = jdbcTemplate.queryForObject(sql, airportRowMapper, airportId);
            return Optional.ofNullable(airport);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Flask中的航班搜索功能 - 支持机场ID或名称查找
    public Optional<Airport> findByIdOrName(String identifier) {
        // 先尝试按ID查找
        Optional<Airport> byId = findByAirportId(identifier);
        if (byId.isPresent()) {
            return byId;
        }

        // 如果ID查找失败，按名称模糊查找并返回第一个结果
        String sql = """
                SELECT AirportID, AirportName
                FROM Airport
                WHERE AirportName LIKE ?
                LIMIT 1
                """;
        try {
            Airport airport = jdbcTemplate.queryForObject(sql, airportRowMapper, "%" + identifier + "%");
            return Optional.ofNullable(airport);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Flask中的管理员功能 - 添加机场
    public int save(Airport airport) {
        String sql = "INSERT INTO Airport (AirportID, AirportName) VALUES (?, ?)";
        return jdbcTemplate.update(sql, airport.getAirportId(), airport.getAirportName());
    }

    // Flask中的管理员功能 - 删除机场
    public int deleteByAirportId(String airportId) {
        String sql = "DELETE FROM Airport WHERE AirportID = ?";
        return jdbcTemplate.update(sql, airportId);
    }

    // 基础查询方法
    public List<Airport> findAll() {
        String sql = "SELECT AirportID, AirportName FROM Airport";
        return jdbcTemplate.query(sql, airportRowMapper);
    }

    // 获取机场总数
    public int count() {
        String sql = "SELECT COUNT(*) FROM Airport";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}