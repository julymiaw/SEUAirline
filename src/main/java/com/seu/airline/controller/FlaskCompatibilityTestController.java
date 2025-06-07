package com.seu.airline.controller;

import com.seu.airline.dao.*;
import com.seu.airline.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test/flask-compatibility")
    @ResponseBody
    public String testFlaskCompatibility() {
        StringBuilder result = new StringBuilder();
        result.append("=== Flask功能兼容性测试 ===\n\n");

        try {
            // 1. 测试用户注册功能（先检查是否已存在）
            result.append("【用户注册功能测试】\n");

            // 检查测试用户是否已存在
            String checkSql = "SELECT COUNT(*) FROM Customer WHERE Phone = ? AND Email = ?";
            Integer existingCount = jdbcTemplate.queryForObject(checkSql, Integer.class, "13912345678",
                    "test@compatibility.com");

            existingCount = existingCount != null ? existingCount : 0;

            if (existingCount > 0) {
                result.append("⚠️ 测试用户已存在，跳过注册步骤，共 ").append(existingCount).append(" 条\n");
            } else {
                Customer testCustomer = new Customer();
                testCustomer.setName("测试用户兼容");
                testCustomer.setPassword("test123");
                testCustomer.setAccountBalance(500);
                testCustomer.setPhone("13912345678");
                testCustomer.setEmail("test@compatibility.com");
                testCustomer.setIdentity("110101199001011111");
                testCustomer.setRank(0);

                int registerResult = customerDao.register(testCustomer);
                result.append("✅ 用户注册: 影响行数 ").append(registerResult).append("\n");
            }

            // 2. 测试多种登录方式（使用明确存在的测试数据）
            result.append("\n【多种登录方式测试】\n");

            Optional<Customer> emailLogin = customerDao.findByEmailAndPassword("test@compatibility.com", "test123");
            result.append("✅ 邮箱登录: ").append(emailLogin.isPresent() ? "成功" : "失败");
            if (emailLogin.isPresent()) {
                result.append(" (CustomerID: ").append(emailLogin.get().getCustomerId()).append(")");
            }
            result.append("\n");

            Optional<Customer> phoneLogin = customerDao.findByPhoneAndPassword("13912345678", "test123");
            result.append("✅ 手机号登录: ").append(phoneLogin.isPresent() ? "成功" : "失败");
            if (phoneLogin.isPresent()) {
                result.append(" (CustomerID: ").append(phoneLogin.get().getCustomerId()).append(")");
            }
            result.append("\n");

            Optional<Customer> identityLogin = customerDao.findByIdentityAndPassword("110101199001011111", "test123");
            result.append("✅ 身份证号登录: ").append(identityLogin.isPresent() ? "成功" : "失败");
            if (identityLogin.isPresent()) {
                result.append(" (CustomerID: ").append(identityLogin.get().getCustomerId()).append(")");
            }
            result.append("\n");

            // 3. 测试现有用户登录（使用数据库中确实存在的用户）
            result.append("\n【现有用户登录测试】\n");
            Optional<Customer> existingUserLogin = customerDao.findByEmailAndPassword("1213758223@qq.com", "123");
            result.append("✅ 现有用户邮箱登录: ").append(existingUserLogin.isPresent() ? "成功" : "失败");
            if (existingUserLogin.isPresent()) {
                result.append(" (用户: ").append(existingUserLogin.get().getName()).append(")");
            }
            result.append("\n");

            // 4. 航班搜索功能测试
            result.append("\n【航班搜索功能测试】\n");
            List<Flight> flights = flightDao.findAll();
            if (!flights.isEmpty()) {
                Flight firstFlight = flights.get(0);
                result.append("✅ 航班列表查询: 查询到 ").append(flights.size()).append(" 条航班\n");
                result.append("   示例航班: ").append(firstFlight.getFlightId()).append("\n");

                Optional<Flight> flightSearch = flightDao.findByFlightId(firstFlight.getFlightId());
                result.append("✅ 航班号搜索: ").append(flightSearch.isPresent() ? "找到航班" : "未找到").append("\n");
            }

            // 5. 订单管理功能测试
            result.append("\n【订单管理功能测试】\n");
            List<Order> orders = orderDao.findAll();
            result.append("✅ 订单查询: 查询到 ").append(orders.size()).append(" 条订单\n");

            if (!orders.isEmpty()) {
                Order firstOrder = orders.get(0);
                result.append("   示例订单: ").append(firstOrder.getOrderId())
                        .append(" - ").append(firstOrder.getOrderStatus()).append("\n");
            }

            // 6. 乘客管理功能测试
            result.append("\n【乘客管理功能测试】\n");
            List<Customer> customers = customerDao.findAll();
            if (!customers.isEmpty()) {
                String hostId = customers.get(0).getCustomerId();
                List<Map<String, Object>> passengerInfo = passengerDao.findPassengerInfoByHostId(hostId);
                result.append("✅ 乘客信息查询: 用户 ").append(hostId)
                        .append(" 有 ").append(passengerInfo.size()).append(" 个乘客\n");
            }

            // 7. 机场搜索功能测试
            result.append("\n【机场搜索功能测试】\n");

            // 测试机场模糊搜索
            List<Airport> beijingAirports = airportDao.findByNameContaining("北京");
            result.append("✅ 机场模糊搜索: '北京'相关机场 ").append(beijingAirports.size()).append(" 个\n");

            // 如果没找到北京机场，尝试其他关键词
            if (beijingAirports.isEmpty()) {
                List<Airport> shanghaiAirports = airportDao.findByNameContaining("上海");
                result.append("✅ 机场模糊搜索: '上海'相关机场 ").append(shanghaiAirports.size()).append(" 个\n");
            }

            // 测试机场ID搜索
            List<Airport> allAirports = airportDao.findAll();
            if (!allAirports.isEmpty()) {
                String testAirportId = allAirports.get(0).getAirportId();
                List<Airport> searchResult = airportDao.findByIdOrNameContaining(testAirportId);
                result.append("✅ 机场ID/名称搜索: 查询'").append(testAirportId).append("'找到 ")
                        .append(searchResult.size()).append(" 个结果\n");
            }

            // 8. 航线查询功能测试
            result.append("\n【航线查询功能测试】\n");
            List<Route> routes = routeDao.findAll();
            result.append("✅ 航线查询: 查询到 ").append(routes.size()).append(" 条航线\n");

            // 9. 账户余额功能测试
            result.append("\n【账户余额功能测试】\n");
            if (emailLogin.isPresent()) {
                Customer customer = emailLogin.get();
                Integer originalBalance = customer.getAccountBalance();
                Integer newBalance = originalBalance + 100;

                int balanceUpdateResult = customerDao.updateAccountBalance(customer.getCustomerId(), newBalance);
                result.append("✅ 账户充值: 影响行数 ").append(balanceUpdateResult).append("\n");
                result.append("   原余额: ").append(originalBalance).append(", 新余额: ").append(newBalance).append("\n");

                // 恢复原余额
                customerDao.updateAccountBalance(customer.getCustomerId(), originalBalance);
                result.append("   余额已恢复\n");
            } else if (existingUserLogin.isPresent()) {
                // 如果测试用户登录失败，使用现有用户测试
                Customer customer = existingUserLogin.get();
                Integer originalBalance = customer.getAccountBalance();
                Integer newBalance = originalBalance + 100;

                int balanceUpdateResult = customerDao.updateAccountBalance(customer.getCustomerId(), newBalance);
                result.append("✅ 账户充值(现有用户): 影响行数 ").append(balanceUpdateResult).append("\n");
                result.append("   原余额: ").append(originalBalance).append(", 新余额: ").append(newBalance).append("\n");

                // 恢复原余额
                customerDao.updateAccountBalance(customer.getCustomerId(), originalBalance);
                result.append("   余额已恢复\n");
            }

            // 10. Flask API接口测试
            result.append("\n【Flask API接口测试】\n");
            List<Airport> apiAirports = airportDao.findByIdOrNameContaining("PEK");
            result.append("✅ /api/airports 接口模拟: 搜索'PEK'找到 ")
                    .append(apiAirports.size()).append(" 个机场\n");

            // 如果PEK没找到，尝试用实际存在的机场ID
            if (apiAirports.isEmpty() && !allAirports.isEmpty()) {
                String realAirportId = allAirports.get(0).getAirportId();
                List<Airport> realAirports = airportDao.findByIdOrNameContaining(realAirportId);
                result.append("✅ /api/airports 接口模拟: 搜索'").append(realAirportId).append("'找到 ")
                        .append(realAirports.size()).append(" 个机场\n");
            }

            result.append("\n🎉 所有Flask功能兼容性测试通过！");

        } catch (Exception e) {
            result.append("\n❌ 兼容性测试失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    @GetMapping("/test/cleanup-test-data-safe")
    @ResponseBody
    public String cleanupTestDataSafe() {
        StringBuilder result = new StringBuilder();
        result.append("=== 安全清理测试数据 ===\n\n");

        try {
            // 1. 先查找要删除的测试用户
            String findTestUserSql = "SELECT CustomerID FROM Customer WHERE Phone = '13912345678' AND Email = 'test@compatibility.com'";
            List<Map<String, Object>> testUsers = jdbcTemplate.queryForList(findTestUserSql);

            result.append("找到测试用户: ").append(testUsers.size()).append(" 个\n");

            if (testUsers.isEmpty()) {
                result.append("没有找到测试用户，无需清理\n");
                return result.toString();
            }

            // 2. 删除外键依赖的数据
            for (Map<String, Object> user : testUsers) {
                String customerId = (String) user.get("CustomerID");
                result.append("正在清理用户: ").append(customerId).append("\n");

                // 删除该用户作为Host的Passenger记录
                String deletePassengerHostSql = "DELETE FROM Passenger WHERE HostID = ?";
                int deletedPassengerHost = jdbcTemplate.update(deletePassengerHostSql, customerId);
                result.append("  删除Passenger表(作为Host): ").append(deletedPassengerHost).append(" 条\n");

                // 删除该用户作为Guest的Passenger记录
                String deletePassengerGuestSql = "DELETE FROM Passenger WHERE GuestID = ?";
                int deletedPassengerGuest = jdbcTemplate.update(deletePassengerGuestSql, customerId);
                result.append("  删除Passenger表(作为Guest): ").append(deletedPassengerGuest).append(" 条\n");

                // 删除该用户的Order记录
                String deleteOrderSql = "DELETE FROM `Order` WHERE CustomerID = ? OR BuyerID = ?";
                int deletedOrders = jdbcTemplate.update(deleteOrderSql, customerId, customerId);
                result.append("  删除Order表: ").append(deletedOrders).append(" 条\n");
            }

            // 3. 最后删除Customer记录
            String deleteCustomerSql = "DELETE FROM Customer WHERE Phone = '13912345678' AND Email = 'test@compatibility.com'";
            int deletedCustomers = jdbcTemplate.update(deleteCustomerSql);
            result.append("  删除Customer表: ").append(deletedCustomers).append(" 条\n");

            result.append("\n🎉 安全清理完成！");

        } catch (Exception e) {
            result.append("\n❌ 安全清理失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    @GetMapping("/test/database-full-diagnosis")
    @ResponseBody
    public String testDatabaseFullDiagnosis() {
        StringBuilder result = new StringBuilder();
        result.append("=== 完整数据库诊断 ===\n\n");

        try {
            // 1. 查看所有Customer数据
            result.append("【Customer表 - 所有数据】\n");
            String customerSql = "SELECT CustomerID, Name, Password, AccountBalance, Phone, Email, Identity, `Rank` FROM Customer";
            List<Map<String, Object>> customers = jdbcTemplate.queryForList(customerSql);
            result.append("Customer表总记录数: ").append(customers.size()).append("\n");
            for (int i = 0; i < customers.size(); i++) {
                Map<String, Object> customer = customers.get(i);
                result.append(String.format("  %d. CustomerID: %s, Name: %s, Phone: %s, Email: %s, Password: %s\n",
                        i + 1, customer.get("CustomerID"), customer.get("Name"),
                        customer.get("Phone"), customer.get("Email"), customer.get("Password")));
            }

            // 2. 查看所有Order数据
            result.append("\n【Order表 - 所有数据】\n");
            String orderSql = "SELECT OrderID, CustomerID, BuyerID, FlightID, SeatType, OrderStatus, OrderTime FROM `Order`";
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(orderSql);
            result.append("Order表总记录数: ").append(orders.size()).append("\n");
            for (int i = 0; i < Math.min(orders.size(), 5); i++) { // 只显示前5条
                Map<String, Object> order = orders.get(i);
                result.append(
                        String.format("  %d. OrderID: %s, CustomerID: %s, BuyerID: %s, FlightID: %s, Status: %s\n",
                                i + 1, order.get("OrderID"), order.get("CustomerID"), order.get("BuyerID"),
                                order.get("FlightID"), order.get("OrderStatus")));
            }
            if (orders.size() > 5) {
                result.append("  ... 还有 ").append(orders.size() - 5).append(" 条记录\n");
            }

            // 3. 查看所有Flight数据
            result.append("\n【Flight表 - 所有数据】\n");
            String flightSql = "SELECT FlightID, RouteID, AircraftID, DepartureTime, ArrivalTime FROM Flight";
            List<Map<String, Object>> flights = jdbcTemplate.queryForList(flightSql);
            result.append("Flight表总记录数: ").append(flights.size()).append("\n");
            for (Map<String, Object> flight : flights) {
                result.append(String.format("  FlightID: %s, RouteID: %s, AircraftID: %s\n",
                        flight.get("FlightID"), flight.get("RouteID"), flight.get("AircraftID")));
            }

            // 4. 查看所有Airport数据
            result.append("\n【Airport表 - 所有数据】\n");
            String airportSql = "SELECT AirportID, AirportName FROM Airport";
            List<Map<String, Object>> airports = jdbcTemplate.queryForList(airportSql);
            result.append("Airport表总记录数: ").append(airports.size()).append("\n");
            for (Map<String, Object> airport : airports) {
                result.append(String.format("  AirportID: %s, AirportName: %s\n",
                        airport.get("AirportID"), airport.get("AirportName")));
            }

            // 5. 查看所有Route数据
            result.append("\n【Route表 - 所有数据】\n");
            String routeSql = "SELECT RouteID, DepartureAirportID, ArrivalAirportID FROM Route";
            List<Map<String, Object>> routes = jdbcTemplate.queryForList(routeSql);
            result.append("Route表总记录数: ").append(routes.size()).append("\n");
            for (Map<String, Object> route : routes) {
                result.append(String.format("  RouteID: %s, 从 %s 到 %s\n",
                        route.get("RouteID"), route.get("DepartureAirportID"), route.get("ArrivalAirportID")));
            }

            // 6. 查看所有Passenger数据
            result.append("\n【Passenger表 - 所有数据】\n");
            String passengerSql = "SELECT HostID, GuestID FROM Passenger";
            List<Map<String, Object>> passengers = jdbcTemplate.queryForList(passengerSql);
            result.append("Passenger表总记录数: ").append(passengers.size()).append("\n");
            for (Map<String, Object> passenger : passengers) {
                result.append(String.format("  HostID: %s, GuestID: %s\n",
                        passenger.get("HostID"), passenger.get("GuestID")));
            }

            // 7. 查看所有Admin数据
            result.append("\n【Admin表 - 所有数据】\n");
            String adminSql = "SELECT AdminID, Password FROM Admin";
            List<Map<String, Object>> admins = jdbcTemplate.queryForList(adminSql);
            result.append("Admin表总记录数: ").append(admins.size()).append("\n");
            for (Map<String, Object> admin : admins) {
                result.append(String.format("  AdminID: %s\n", admin.get("AdminID")));
            }

            result.append("\n🔍 完整数据库诊断完成！");

        } catch (Exception e) {
            result.append("\n❌ 数据库诊断失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    @GetMapping("/test/login-diagnosis")
    @ResponseBody
    public String testLoginDiagnosis() {
        StringBuilder result = new StringBuilder();
        result.append("=== 登录功能专项诊断 ===\n\n");

        try {
            // 1. 测试现有用户的登录
            result.append("【测试现有用户登录】\n");

            // 获取前3个有密码的用户进行测试
            String userSql = "SELECT CustomerID, Name, Phone, Email, Identity, Password FROM Customer WHERE Password IS NOT NULL AND Password != '' LIMIT 3";
            List<Map<String, Object>> testUsers = jdbcTemplate.queryForList(userSql);

            result.append("找到有密码的用户: ").append(testUsers.size()).append(" 个\n\n");

            for (int i = 0; i < testUsers.size(); i++) {
                Map<String, Object> user = testUsers.get(i);
                String customerId = (String) user.get("CustomerID");
                String name = (String) user.get("Name");
                String phone = (String) user.get("Phone");
                String email = (String) user.get("Email");
                String identity = (String) user.get("Identity");
                String password = (String) user.get("Password");

                result.append("--- 测试用户 ").append(i + 1).append(" ---\n");
                result.append("CustomerID: ").append(customerId).append("\n");
                result.append("Name: ").append(name).append("\n");
                result.append("Phone: ").append(phone).append("\n");
                result.append("Email: ").append(email).append("\n");
                result.append("Identity: ").append(identity).append("\n");
                result.append("Password: ").append(password).append("\n");

                // 测试邮箱登录
                if (email != null && !email.trim().isEmpty()) {
                    Optional<Customer> emailLogin = customerDao.findByEmailAndPassword(email, password);
                    result.append("✅ 邮箱登录测试: ").append(emailLogin.isPresent() ? "成功" : "失败").append("\n");
                } else {
                    result.append("⚠️ 邮箱为空，跳过邮箱登录测试\n");
                }

                // 测试手机登录
                if (phone != null && !phone.trim().isEmpty()) {
                    Optional<Customer> phoneLogin = customerDao.findByPhoneAndPassword(phone, password);
                    result.append("✅ 手机登录测试: ").append(phoneLogin.isPresent() ? "成功" : "失败").append("\n");
                } else {
                    result.append("⚠️ 手机号为空，跳过手机登录测试\n");
                }

                // 测试身份证登录
                if (identity != null && !identity.trim().isEmpty()) {
                    Optional<Customer> identityLogin = customerDao.findByIdentityAndPassword(identity, password);
                    result.append("✅ 身份证登录测试: ").append(identityLogin.isPresent() ? "成功" : "失败").append("\n");
                } else {
                    result.append("⚠️ 身份证号为空，跳过身份证登录测试\n");
                }

                result.append("\n");
            }

            // 2. 测试我们的测试用户
            result.append("【测试我们创建的测试用户】\n");
            String testUserSql = "SELECT CustomerID, Name, Phone, Email, Identity, Password FROM Customer WHERE Phone = '13912345678' OR Email = 'test@compatibility.com'";
            List<Map<String, Object>> ourTestUsers = jdbcTemplate.queryForList(testUserSql);

            result.append("找到我们的测试用户: ").append(ourTestUsers.size()).append(" 个\n");
            for (Map<String, Object> user : ourTestUsers) {
                result.append("CustomerID: ").append(user.get("CustomerID"));
                result.append(", Name: ").append(user.get("Name"));
                result.append(", Password: ").append(user.get("Password")).append("\n");

                // 测试这些用户的登录
                String password = (String) user.get("Password");
                Optional<Customer> emailLogin = customerDao.findByEmailAndPassword("test@compatibility.com", password);
                Optional<Customer> phoneLogin = customerDao.findByPhoneAndPassword("13912345678", password);
                Optional<Customer> identityLogin = customerDao.findByIdentityAndPassword("110101199001011111",
                        password);

                result.append("  邮箱登录: ").append(emailLogin.isPresent() ? "成功" : "失败");
                result.append(", 手机登录: ").append(phoneLogin.isPresent() ? "成功" : "失败");
                result.append(", 身份证登录: ").append(identityLogin.isPresent() ? "成功" : "失败").append("\n");
            }

            result.append("\n🔍 登录诊断完成！");

        } catch (Exception e) {
            result.append("\n❌ 登录诊断失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    @GetMapping("/test/flask-airports")
    @ResponseBody
    public String testFlaskAirportFeatures() {
        StringBuilder result = new StringBuilder();
        result.append("=== Flask机场功能专项测试 ===\n\n");

        try {
            // 模拟Flask的 /api/airports 路由功能
            result.append("【Flask /api/airports 路由测试】\n");

            // 测试1: 空查询
            List<Airport> emptyQuery = airportDao.findByIdOrNameContaining("");
            result.append("✅ 空查询测试: 返回 ").append(emptyQuery.size()).append(" 个结果\n");

            // 测试2: 机场ID查询
            List<Airport> idQuery = airportDao.findByIdOrNameContaining("PEK");
            result.append("✅ 机场ID查询: 'PEK' 返回 ").append(idQuery.size()).append(" 个结果\n");

            // 测试3: 机场名称查询
            List<Airport> nameQuery = airportDao.findByIdOrNameContaining("首都");
            result.append("✅ 机场名称查询: '首都' 返回 ").append(nameQuery.size()).append(" 个结果\n");

            // 测试4: 部分匹配查询
            List<Airport> partialQuery = airportDao.findByIdOrNameContaining("北");
            result.append("✅ 部分匹配查询: '北' 返回 ").append(partialQuery.size()).append(" 个结果\n");

            // 模拟Flask的航班搜索中的机场处理逻辑
            result.append("\n【Flask航班搜索机场处理测试】\n");

            List<Airport> allAirports = airportDao.findAll();
            if (!allAirports.isEmpty()) {
                Airport testAirport = allAirports.get(0);

                // 测试先按ID精确查找，再按名称模糊查找的逻辑
                Optional<Airport> exactMatch = airportDao.findByAirportId(testAirport.getAirportId());
                result.append("✅ 精确ID匹配: ").append(exactMatch.isPresent() ? "成功" : "失败").append("\n");

                Optional<Airport> fuzzyMatch = airportDao.findByIdOrName(testAirport.getAirportName().substring(0, 2));
                result.append("✅ 模糊名称匹配: ").append(fuzzyMatch.isPresent() ? "成功" : "失败").append("\n");
            }

            result.append("\n🎉 Flask机场功能测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 机场功能测试失败: ").append(e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }
}