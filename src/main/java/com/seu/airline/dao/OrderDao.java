package com.seu.airline.dao;

import com.seu.airline.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderId(rs.getString("OrderID"));
            order.setCustomerId(rs.getString("CustomerID"));
            order.setBuyerId(rs.getString("BuyerID"));
            order.setFlightId(rs.getString("FlightID"));
            order.setSeatType(rs.getString("SeatType"));
            order.setOrderStatus(rs.getString("OrderStatus"));
            order.setOrderTime(rs.getTimestamp("OrderTime").toLocalDateTime());
            return order;
        }
    };

    // 修正相关方法的参数类型
    public int createOrder(String customerId, String buyerId, String flightId,
            String seatType, String orderStatus, LocalDateTime orderTime) {
        String sql = "INSERT INTO `Order` (CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, customerId, buyerId, flightId, seatType, orderStatus, orderTime);
    }

    // 修正查询方法的参数类型
    public List<Map<String, Object>> findOrderIdsByCondition(String customerId, String buyerId,
            LocalDateTime orderTime) {
        String sql = "SELECT OrderID FROM `Order` WHERE CustomerID = ? AND BuyerID = ? AND OrderTime = ?";
        return jdbcTemplate.queryForList(sql, customerId, buyerId, orderTime);
    }

    public List<Order> findByBuyerId(String buyerId) {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order` WHERE BuyerID = ?";
        return jdbcTemplate.query(sql, orderRowMapper, buyerId);
    }

    public int updateOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE `Order` SET OrderStatus = ? WHERE OrderID = ?";
        return jdbcTemplate.update(sql, newStatus, orderId);
    }

    public Optional<Order> findById(String orderId) {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order` WHERE OrderID = ?";
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, orderId);
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Map<String, Object>> findOrderWithCustomerInfo(String orderNumber, String phoneNumber) {
        String sql = """
                SELECT * FROM `Order` o
                JOIN Customer c ON o.CustomerID = c.CustomerID
                WHERE o.OrderID = ? AND c.Phone = ?
                """;
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, orderNumber, phoneNumber);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order`";
        return jdbcTemplate.query(sql, orderRowMapper);
    }
}