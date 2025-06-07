package com.seu.airline.dao;

import com.seu.airline.entity.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FlightDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Flight> flightRowMapper = new RowMapper<Flight>() {
        @Override
        public Flight mapRow(ResultSet rs, int rowNum) throws SQLException {
            Flight flight = new Flight();
            flight.setFlightId(rs.getString("FlightID"));
            flight.setRouteId(rs.getString("RouteID"));
            flight.setAircraftId(rs.getString("AircraftID"));
            flight.setDepartureTime(rs.getTimestamp("DepartureTime").toLocalDateTime());
            flight.setArrivalTime(rs.getTimestamp("ArrivalTime").toLocalDateTime());
            flight.setEconomyPrice(rs.getBigDecimal("EconomyPrice"));
            flight.setBusinessPrice(rs.getBigDecimal("BusinessPrice"));
            return flight;
        }
    };

    // Flask中的航班号搜索
    public Optional<Flight> findByFlightId(String flightId) {
        String sql = "SELECT FlightID, RouteID, AircraftID, DepartureTime, ArrivalTime, EconomyPrice, BusinessPrice FROM Flight WHERE FlightID = ?";
        try {
            Flight flight = jdbcTemplate.queryForObject(sql, flightRowMapper, flightId);
            return Optional.ofNullable(flight);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Flask中的复杂航班搜索逻辑
    public List<Flight> searchFlightsByRoute(String departureAirportId, String arrivalAirportId, LocalDate date) {
        String sql = """
                SELECT f.FlightID, f.RouteID, f.AircraftID, f.DepartureTime, f.ArrivalTime, f.EconomyPrice, f.BusinessPrice
                FROM Flight f
                JOIN Route r ON f.RouteID = r.RouteID
                WHERE r.DepartureAirportID = ? AND r.ArrivalAirportID = ? AND DATE(f.DepartureTime) = ?
                ORDER BY f.DepartureTime
                """;
        return jdbcTemplate.query(sql, flightRowMapper, departureAirportId, arrivalAirportId, date);
    }

    // Flask中的管理员功能 - 添加航班
    public int save(Flight flight) {
        String sql = "INSERT INTO Flight (FlightID, RouteID, AircraftID, DepartureTime, ArrivalTime, EconomyPrice, BusinessPrice) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                flight.getFlightId(),
                flight.getRouteId(),
                flight.getAircraftId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getEconomyPrice(),
                flight.getBusinessPrice());
    }

    // Flask中的管理员功能 - 删除航班
    public int deleteByFlightId(String flightId) {
        String sql = "DELETE FROM Flight WHERE FlightID = ?";
        return jdbcTemplate.update(sql, flightId);
    }

    public List<Flight> findAll() {
        String sql = "SELECT FlightID, RouteID, AircraftID, DepartureTime, ArrivalTime, EconomyPrice, BusinessPrice FROM Flight";
        return jdbcTemplate.query(sql, flightRowMapper);
    }
}