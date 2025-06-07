package com.seu.airline.dao;

import com.seu.airline.entity.Customer;
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
public class CustomerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Customer> customerRowMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getString("CustomerID")); // ✅ getString
            customer.setName(rs.getString("Name"));
            customer.setPassword(rs.getString("Password"));
            customer.setAccountBalance(rs.getInt("AccountBalance")); // ✅ getInt (不是getBigDecimal!)
            customer.setPhone(rs.getString("Phone"));
            customer.setEmail(rs.getString("Email"));
            customer.setIdentity(rs.getString("Identity"));
            customer.setRank(rs.getInt("Rank"));
            return customer;
        }
    };

    // 修正注册方法的参数类型
    public int register(Customer customer) {
        String sql = "INSERT INTO Customer (Name, Password, AccountBalance, Phone, Email, Identity, `Rank`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                customer.getName(),
                customer.getPassword(),
                customer.getAccountBalance(), // ✅ Integer
                customer.getPhone(),
                customer.getEmail(),
                customer.getIdentity(),
                customer.getRank());
    }

    // 修正充值方法的参数类型
    public int updateAccountBalance(String customerId, Integer newBalance) { // ✅ 参数都改为正确类型
        String sql = "UPDATE Customer SET AccountBalance = ? WHERE CustomerID = ?";
        return jdbcTemplate.update(sql, newBalance, customerId);
    }

    // 修正等级更新方法的参数类型
    public int incrementRank(String customerId) { // ✅ 参数改为String
        String sql = "UPDATE Customer SET `Rank` = `Rank` + 1 WHERE CustomerID = ?";
        return jdbcTemplate.update(sql, customerId);
    }

    // 修正其他方法的参数类型
    public Optional<Customer> findById(String customerId) { // ✅ 参数改为String
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE CustomerID = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, customerId);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 其他登录方法保持不变（参数已经是String）
    public Optional<Customer> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE Email = ? AND Password = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, email, password);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByPhoneAndPassword(String phone, String password) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE Phone = ? AND Password = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, phone, password);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByIdentityAndPassword(String identity, String password) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE Identity = ? AND Password = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, identity, password);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE Customer SET Password = ? WHERE Email = ?";
        return jdbcTemplate.update(sql, newPassword, email);
    }

    public List<Customer> findAll() {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    public Optional<Customer> findByPhoneAndIdentityAndName(String phone, String identity, String name) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE Phone = ? AND Identity = ? AND Name = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, phone, identity, name);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int insertBasicCustomer(String phone, String identity, String name) {
        String sql = "INSERT INTO Customer (Phone, Identity, Name) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, phone, identity, name);
    }
}