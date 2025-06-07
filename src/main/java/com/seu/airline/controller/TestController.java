package com.seu.airline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String testStaticResources() {
        return "admin_login"; // 测试加载admin_login.html模板
    }

    @GetMapping("/test/db")
    @ResponseBody
    public String testDatabaseConnection() {
        try {
            // 测试数据库连接
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");

            // 测试查询Airport表
            List<Map<String, Object>> airports = jdbcTemplate.queryForList(
                    "SELECT COUNT(*) as count FROM Airport LIMIT 5");

            return "✅ 数据库连接成功！\n" +
                    "测试查询结果: " + result + "\n" +
                    "机场表记录数: " + airports;

        } catch (Exception e) {
            return "❌ 数据库连接失败: " + e.getMessage();
        }
    }

    @GetMapping("/test/customer")
    @ResponseBody
    public String testCustomerQuery() {
        try {
            List<Map<String, Object>> customers = jdbcTemplate.queryForList(
                    "SELECT CustomerID, Name, Phone FROM Customer LIMIT 3");
            return "✅ Customer表查询成功: " + customers;
        } catch (Exception e) {
            return "❌ Customer表查询失败: " + e.getMessage();
        }
    }
}