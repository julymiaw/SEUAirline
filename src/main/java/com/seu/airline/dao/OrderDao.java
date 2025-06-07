package com.seu.airline.dao;

import com.seu.airline.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderId(rs.getInt("OrderID"));
            order.setCustomerId(rs.getInt("CustomerID"));
            order.setBuyerId(rs.getInt("BuyerID"));
            order.setFlightId(rs.getString("FlightID"));
            order.setSeatType(rs.getString("SeatType"));
            order.setOrderStatus(rs.getString("OrderStatus"));
            order.setOrderTime(rs.getTimestamp("OrderTime").toLocalDateTime());
            return order;
        }
    };

    // Flask中的订票功能
    public int createOrder(Integer customerId, Integer buyerId, String flightId,
            String seatType, String orderStatus, LocalDateTime orderTime) {
        String sql = "INSERT INTO `Order` (CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, customerId, buyerId, flightId, seatType, orderStatus, orderTime);
    }

    // Flask中的支付流程 - 查找特定时间的订单
    public List<Map<String, Object>> findOrderIdsByCondition(Integer customerId, Integer buyerId,
            LocalDateTime orderTime) {
        String sql = "SELECT OrderID FROM `Order` WHERE CustomerID = ? AND BuyerID = ? AND OrderTime = ?";
        return jdbcTemplate.queryForList(sql, customerId, buyerId, orderTime);
    }

    // Flask中的支付功能 - 更新订单状态
    public int updateOrderStatus(Integer orderId, String newStatus) {
        String sql = "UPDATE `Order` SET OrderStatus = ? WHERE OrderID = ?";
        return jdbcTemplate.update(sql, newStatus, orderId);
    }

    // Flask中的订单查询功能 - 按购买者查询
    public List<Order> findByBuyerId(Integer buyerId) {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order` WHERE BuyerID = ?";
        return jdbcTemplate.query(sql, orderRowMapper, buyerId);
    }

    // Flask中的订单查询功能 - 按订单号和手机号查询
    public Optional<Map<String, Object>> findOrderWithCustomerInfo(Integer orderNumber, String phoneNumber) {
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

    // 基础查询方法
    public Optional<Order> findById(Integer orderId) {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order` WHERE OrderID = ?";
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, orderId);
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order`";
        return jdbcTemplate.query(sql, orderRowMapper);
    }
}