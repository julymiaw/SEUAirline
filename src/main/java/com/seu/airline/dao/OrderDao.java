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

    // ✅ 核心功能1: 创建订单（对应Flask book_flight）
    public int createOrder(String customerId, String buyerId, String flightId,
            String seatType, String orderStatus, LocalDateTime orderTime) {
        String sql = """
                INSERT INTO `Order` (CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql, customerId, buyerId, flightId, seatType, orderStatus, orderTime);
    }

    // ✅ 核心功能2: 更新订单状态（对应Flask pay_order）
    public int updateOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE `Order` SET OrderStatus = ? WHERE OrderID = ?";
        return jdbcTemplate.update(sql, newStatus, orderId);
    }

    // ✅ 核心功能3: 按订单号+手机号查询（对应Flask search_order）
    public Optional<Map<String, Object>> findOrderWithCustomerInfo(String orderNumber, String phoneNumber) {
        String sql = """
                SELECT o.*, c.Name, c.Phone, c.Email
                FROM `Order` o
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

    // ✅ 核心功能4: 按订单号查询（对应Flask view_orderID）
    public Optional<Order> findByOrderId(String orderId) {
        String sql = """
                SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime
                FROM `Order` WHERE OrderID = ?
                """;
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, orderId);
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ✅ 核心功能5: 查看我的订单（对应Flask view_orders）
    public List<Order> findByBuyerId(String buyerId) {
        String sql = """
                SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime
                FROM `Order` WHERE BuyerID = ?
                """;
        return jdbcTemplate.query(sql, orderRowMapper, buyerId);
    }

    // ✅ 辅助功能: 查询最新订单（用于支付页面获取OrderID）
    public List<Order> findLatestOrdersByCustomers(List<String> customerIds, String buyerId) {
        if (customerIds.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", customerIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = String.format("""
                SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime
                FROM `Order`
                WHERE CustomerID IN (%s) AND BuyerID = ?
                ORDER BY OrderTime DESC
                """, placeholders);

        Object[] params = new Object[customerIds.size() + 1];
        for (int i = 0; i < customerIds.size(); i++) {
            params[i] = customerIds.get(i);
        }
        params[customerIds.size()] = buyerId;

        return jdbcTemplate.query(sql, orderRowMapper, params);
    }

    // ✅ 测试辅助: 统计订单数量（仅用于测试验证）
    public Integer countOrdersByCustomerAndBuyer(String customerId, String buyerId) {
        String sql = "SELECT COUNT(*) FROM `Order` WHERE CustomerID = ? AND BuyerID = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, customerId, buyerId);
        return result != null ? result : 0;
    }

    // ✅ 基础查询: 查询所有订单（仅用于测试）
    public List<Order> findAll() {
        String sql = """
                SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime
                FROM `Order`
                """;
        return jdbcTemplate.query(sql, orderRowMapper);
    }
}