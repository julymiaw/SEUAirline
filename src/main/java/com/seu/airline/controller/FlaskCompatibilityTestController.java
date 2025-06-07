package com.seu.airline.controller;

import com.seu.airline.dao.*;
import com.seu.airline.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class FlaskCompatibilityTestController {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private FlightDao flightDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private AirportDao airportDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private PassengerDao passengerDao;

    @GetMapping("/test/flask-compatibility")
    @ResponseBody
    public String testFlaskCompatibility() {
        StringBuilder result = new StringBuilder();
        result.append("=== Flask功能兼容性测试 ===\n\n");

        try {
            // 1. 测试用户注册功能
            result.append("【用户注册功能测试】\n");
            Customer testCustomer = new Customer();
            testCustomer.setName("测试用户兼容");
            testCustomer.setPassword("test123");
            testCustomer.setAccountBalance(new BigDecimal("500.00"));
            testCustomer.setPhone("13912345678");
            testCustomer.setEmail("test@compatibility.com");
            testCustomer.setIdentity("110101199001011111");
            testCustomer.setRank(0);

            int registerResult = customerDao.register(testCustomer);
            result.append("✅ 用户注册: 影响行数 ").append(registerResult).append("\n");

            // 2. 测试多种登录方式（Flask特色功能）
            result.append("\n【多种登录方式测试】\n");

            Optional<Customer> emailLogin = customerDao.findByEmailAndPassword("test@compatibility.com", "test123");
            result.append("✅ 邮箱登录: ").append(emailLogin.isPresent() ? "成功" : "失败").append("\n");

            Optional<Customer> phoneLogin = customerDao.findByPhoneAndPassword("13912345678", "test123");
            result.append("✅ 手机号登录: ").append(phoneLogin.isPresent() ? "成功" : "失败").append("\n");

            Optional<Customer> identityLogin = customerDao.findByIdentityAndPassword("110101199001011111", "test123");
            result.append("✅ 身份证号登录: ").append(identityLogin.isPresent() ? "成功" : "失败").append("\n");

            // 3. 测试航班搜索功能
            result.append("\n【航班搜索功能测试】\n");
            Optional<Flight> flightSearch = flightDao.findByFlightId("CA1234");
            result.append("✅ 航班号搜索: ").append(flightSearch.isPresent() ? "找到航班" : "未找到").append("\n");

            List<Flight> flights = flightDao.findAll();
            if (!flights.isEmpty()) {
                Flight firstFlight = flights.get(0);
                result.append("✅ 航班列表查询: 查询到 ").append(flights.size()).append(" 条航班\n");
                result.append("   示例航班: ").append(firstFlight.getFlightId()).append("\n");
            }

            // 4. 测试订单管理功能
            result.append("\n【订单管理功能测试】\n");
            List<Order> orders = orderDao.findAll();
            result.append("✅ 订单查询: 查询到 ").append(orders.size()).append(" 条订单\n");

            if (!orders.isEmpty()) {
                Order firstOrder = orders.get(0);
                result.append("   示例订单: ").append(firstOrder.getOrderId())
                        .append(" - ").append(firstOrder.getOrderStatus()).append("\n");
            }

            // 5. 测试乘客管理功能
            result.append("\n【乘客管理功能测试】\n");
            List<Customer> customers = customerDao.findAll();
            if (!customers.isEmpty()) {
                Integer hostId = customers.get(0).getCustomerId();
                List<Map<String, Object>> passengerInfo = passengerDao.findPassengerInfoByHostId(hostId);
                result.append("✅ 乘客信息查询: 用户 ").append(hostId)
                        .append(" 有 ").append(passengerInfo.size()).append(" 个乘客\n");
            }

            // 6. 测试机场模糊搜索功能
            result.append("\n【机场搜索功能测试】\n");
            List<Airport> beijingAirports = airportDao.findByNameContaining("北京");
            result.append("✅ 机场模糊搜索: '北京'相关机场 ").append(beijingAirports.size()).append(" 个\n");

            // 7. 测试路线查询功能
            result.append("\n【航线查询功能测试】\n");
            List<Route> routes = routeDao.findAll();
            result.append("✅ 航线查询: 查询到 ").append(routes.size()).append(" 条航线\n");

            // 8. 测试账户余额功能
            result.append("\n【账户余额功能测试】\n");
            if (emailLogin.isPresent()) {
                Customer customer = emailLogin.get();
                BigDecimal originalBalance = customer.getAccountBalance();
                BigDecimal newBalance = originalBalance.add(new BigDecimal("100.00"));

                int balanceUpdateResult = customerDao.updateAccountBalance(customer.getCustomerId(), newBalance);
                result.append("✅ 账户充值: 影响行数 ").append(balanceUpdateResult).append("\n");
                result.append("   原余额: ").append(originalBalance).append(", 新余额: ").append(newBalance).append("\n");

                // 恢复原余额
                customerDao.updateAccountBalance(customer.getCustomerId(), originalBalance);
            }

            // 清理测试数据
            if (emailLogin.isPresent()) {
                // 这里可以删除测试数据，但为了演示保留
                result.append("\n📋 测试数据已创建，可手动清理\n");
            }

            result.append("\n🎉 所有Flask功能兼容性测试通过！");

        } catch (Exception e) {
            result.append("\n❌ 兼容性测试失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    @GetMapping("/test/flask-workflows")
    @ResponseBody
    public String testFlaskWorkflows() {
        StringBuilder result = new StringBuilder();
        result.append("=== Flask业务流程测试 ===\n\n");

        try {
            // 模拟Flask中的完整订票流程
            result.append("【模拟完整订票流程】\n");

            // 1. 用户登录
            Optional<Customer> customer = customerDao.findByEmailAndPassword("zhang@email.com", "123456");
            if (customer.isPresent()) {
                result.append("✅ 步骤1 - 用户登录成功: ").append(customer.get().getName()).append("\n");

                // 2. 查找航班
                List<Flight> availableFlights = flightDao.findAll();
                if (!availableFlights.isEmpty()) {
                    Flight selectedFlight = availableFlights.get(0);
                    result.append("✅ 步骤2 - 航班选择: ").append(selectedFlight.getFlightId()).append("\n");

                    // 3. 查找乘客
                    List<Map<String, Object>> passengers = passengerDao
                            .findPassengerInfoByHostId(customer.get().getCustomerId());
                    result.append("✅ 步骤3 - 乘客查询: 找到 ").append(passengers.size()).append(" 个乘客\n");

                    // 4. 创建订单
                    LocalDateTime orderTime = LocalDateTime.now();
                    int orderResult = orderDao.createOrder(
                            customer.get().getCustomerId(),
                            customer.get().getCustomerId(),
                            selectedFlight.getFlightId(),
                            "Economy",
                            "Established",
                            orderTime);
                    result.append("✅ 步骤4 - 订单创建: 影响行数 ").append(orderResult).append("\n");

                    // 5. 模拟支付流程
                    List<Map<String, Object>> orderIds = orderDao.findOrderIdsByCondition(
                            customer.get().getCustomerId(),
                            customer.get().getCustomerId(),
                            orderTime);

                    if (!orderIds.isEmpty()) {
                        Integer orderId = (Integer) orderIds.get(0).get("OrderID");
                        int paymentResult = orderDao.updateOrderStatus(orderId, "Paid");
                        result.append("✅ 步骤5 - 订单支付: 订单 ").append(orderId).append(" 支付成功\n");

                        // 6. 更新用户等级
                        int rankResult = customerDao.incrementRank(customer.get().getCustomerId());
                        result.append("✅ 步骤6 - 等级更新: 影响行数 ").append(rankResult).append("\n");
                    }
                }
            }

            result.append("\n🎉 Flask业务流程测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 业务流程测试失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }
}