package com.seu.airline.dao;

import com.seu.airline.entity.Route;
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
public class RouteDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Route> routeRowMapper = new RowMapper<Route>() {
        @Override
        public Route mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Route route = new Route();
            route.setRouteId(rs.getString("RouteID"));
            route.setDepartureAirportId(rs.getString("DepartureAirportID"));
            route.setArrivalAirportId(rs.getString("ArrivalAirportID"));
            return route;
        }
    };

    // Flask中用于查找航线ID的功能
    public Optional<Route> findByAirports(String departureAirportId, String arrivalAirportId) {
        String sql = "SELECT RouteID, DepartureAirportID, ArrivalAirportID FROM Route WHERE DepartureAirportID = ? AND ArrivalAirportID = ?";
        try {
            Route route = jdbcTemplate.queryForObject(sql, routeRowMapper, departureAirportId, arrivalAirportId);
            return Optional.ofNullable(route);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Flask中的管理员功能
    public int save(String departureAirportId, String arrivalAirportId) {
        String sql = "INSERT INTO Route (DepartureAirportID, ArrivalAirportID) VALUES (?, ?)";
        return jdbcTemplate.update(sql, departureAirportId, arrivalAirportId);
    }

    public int deleteByRouteId(String routeId) {
        String sql = "DELETE FROM Route WHERE RouteID = ?";
        return jdbcTemplate.update(sql, routeId);
    }

    public List<Route> findAll() {
        String sql = "SELECT RouteID, DepartureAirportID, ArrivalAirportID FROM Route";
        return jdbcTemplate.query(sql, routeRowMapper);
    }
}