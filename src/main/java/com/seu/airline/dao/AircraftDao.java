package com.seu.airline.dao;

import com.seu.airline.entity.Aircraft;
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
public class AircraftDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Aircraft> aircraftRowMapper = new RowMapper<Aircraft>() {
        @Override
        public Aircraft mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Aircraft aircraft = new Aircraft();
            aircraft.setAircraftId(rs.getString("AircraftID"));
            aircraft.setAircraftType(rs.getString("AircraftType"));
            aircraft.setEconomySeats(rs.getInt("EconomySeats"));
            aircraft.setBusinessSeats(rs.getInt("BusinessSeats"));
            return aircraft;
        }
    };

    // Flask中的管理员功能 - 添加飞机
    public int save(Aircraft aircraft) {
        String sql = "INSERT INTO Aircraft (AircraftID, AircraftType, EconomySeats, BusinessSeats) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                aircraft.getAircraftId(),
                aircraft.getAircraftType(),
                aircraft.getEconomySeats(),
                aircraft.getBusinessSeats());
    }

    // Flask中的管理员功能 - 删除飞机
    public int deleteByAircraftId(String aircraftId) {
        String sql = "DELETE FROM Aircraft WHERE AircraftID = ?";
        return jdbcTemplate.update(sql, aircraftId);
    }

    // Flask中用于查询飞机类型的功能
    public Optional<Aircraft> findByAircraftId(String aircraftId) {
        String sql = "SELECT AircraftID, AircraftType, EconomySeats, BusinessSeats FROM Aircraft WHERE AircraftID = ?";
        try {
            Aircraft aircraft = jdbcTemplate.queryForObject(sql, aircraftRowMapper, aircraftId);
            return Optional.ofNullable(aircraft);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Aircraft> findAll() {
        String sql = "SELECT AircraftID, AircraftType, EconomySeats, BusinessSeats FROM Aircraft";
        return jdbcTemplate.query(sql, aircraftRowMapper);
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM Aircraft";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }
}