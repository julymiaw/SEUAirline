package com.seu.airline.dao;

import com.seu.airline.entity.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class PassengerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Passenger> passengerRowMapper = new RowMapper<Passenger>() {
        @Override
        public Passenger mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Passenger passenger = new Passenger();
            passenger.setHostId(rs.getString("HostID")); // ✅ 改为getString
            passenger.setGuestId(rs.getString("GuestID")); // ✅ 改为getString
            return passenger;
        }
    };

    // 修正方法参数类型
    public List<Map<String, Object>> findPassengerInfoByHostId(String hostId) { // ✅ 参数改为String
        String sql = """
                SELECT p.HostID, p.GuestID, c.Name, c.Phone, c.Email
                FROM Passenger p
                JOIN Customer c ON p.GuestID = c.CustomerID
                WHERE p.HostID = ?
                """;
        return jdbcTemplate.queryForList(sql, hostId);
    }

    public int addPassenger(String hostId, String guestId) { // ✅ 参数改为String
        String sql = "INSERT INTO Passenger (HostID, GuestID) VALUES (?, ?)";
        return jdbcTemplate.update(sql, hostId, guestId);
    }

    public List<Passenger> findAll() {
        String sql = "SELECT HostID, GuestID FROM Passenger";
        return jdbcTemplate.query(sql, passengerRowMapper);
    }
}