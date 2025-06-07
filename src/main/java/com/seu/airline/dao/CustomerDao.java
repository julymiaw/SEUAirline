package com.seu.airline.dao;

import com.seu.airline.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
            customer.setCustomerId(rs.getInt("CustomerID"));
            customer.setName(rs.getString("Name"));
            customer.setPassword(rs.getString("Password"));
            customer.setAccountBalance(rs.getBigDecimal("AccountBalance"));
            customer.setPhone(rs.getString("Phone"));
            customer.setEmail(rs.getString("Email"));
            customer.setIdentity(rs.getString("Identity"));
            customer.setRank(rs.getInt("Rank"));
            return customer;
        }
    };

    // Flask中的注册功能
    public int register(Customer customer) {
        String sql = "INSERT INTO Customer (Name, Password, AccountBalance, Phone, Email, Identity, `Rank`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                customer.getName(),
                customer.getPassword(),
                customer.getAccountBalance(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getIdentity(),
                customer.getRank());
    }

    // Flask中的多种登录方式：邮箱、身份证、手机号
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

    // Flask中的忘记密码功能
    public int updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE Customer SET Password = ? WHERE Email = ?";
        return jdbcTemplate.update(sql, newPassword, email);
    }

    // Flask中的充值功能
    public int updateAccountBalance(Integer customerId, BigDecimal newBalance) {
        String sql = "UPDATE Customer SET AccountBalance = ? WHERE CustomerID = ?";
        return jdbcTemplate.update(sql, newBalance, customerId);
    }

    // Flask中的等级更新功能
    public int incrementRank(Integer customerId) {
        String sql = "UPDATE Customer SET `Rank` = `Rank` + 1 WHERE CustomerID = ?";
        return jdbcTemplate.update(sql, customerId);
    }

    // 基础查询方法
    public Optional<Customer> findById(Integer customerId) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE CustomerID = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, customerId);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Customer> findAll() {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    // Flask中的添加乘客功能 - 检查客户是否存在
    public Optional<Customer> findByPhoneAndIdentityAndName(String phone, String identity, String name) {
        String sql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer WHERE Phone = ? AND Identity = ? AND Name = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, phone, identity, name);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 插入简化的客户信息（乘客）
    public int insertBasicCustomer(String phone, String identity, String name) {
        String sql = "INSERT INTO Customer (Phone, Identity, Name) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, phone, identity, name);
    }
}