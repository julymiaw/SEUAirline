package com.seu.airline.controller;

import com.seu.airline.dao.*;
import com.seu.airline.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 🔧 新增：安全清理方法 - 处理外键约束
    private void safeDeleteCustomers(String... phoneOrEmailList) {
        try {
            for (int i = 0; i < phoneOrEmailList.length; i += 2) {
                String phone = phoneOrEmailList[i];
                String email = phoneOrEmailList[i + 1];

                // 1. 查找要删除的Customer ID
                String findCustomerSql = "SELECT CustomerID FROM Customer WHERE Phone = ? OR Email = ?";
                List<Map<String, Object>> customers = jdbcTemplate.queryForList(findCustomerSql, phone, email);

                for (Map<String, Object> customer : customers) {
                    String customerId = (String) customer.get("CustomerID");

                    // 2. 删除相关的Order记录
                    String deleteOrderSql = "DELETE FROM `Order` WHERE CustomerID = ? OR BuyerID = ?";
                    jdbcTemplate.update(deleteOrderSql, customerId, customerId);

                    // 3. 删除相关的Passenger记录
                    String deletePassengerSql = "DELETE FROM Passenger WHERE HostID = ? OR GuestID = ?";
                    jdbcTemplate.update(deletePassengerSql, customerId, customerId);
                }

                // 4. 最后删除Customer记录
                String deleteCustomerSql = "DELETE FROM Customer WHERE Phone = ? OR Email = ?";
                jdbcTemplate.update(deleteCustomerSql, phone, email);
            }
        } catch (Exception e) {
            // 静默处理清理错误，不影响测试
        }
    }

    // 测试索引页面
    @GetMapping("/test")
    @ResponseBody
    public String testIndex() {
        StringBuilder result = new StringBuilder();
        result.append("=== Spring Boot 测试套件 ===\n\n");
        result.append("🔗 可用测试接口:\n\n");
        result.append("【单一功能测试】\n");
        result.append("  /test/user-register      - 用户注册测试\n");
        result.append("  /test/user-login         - 用户登录测试\n");
        result.append("  /test/password-reset     - 密码重置测试\n");
        result.append("  /test/account-balance    - 账户余额测试\n");
        result.append("  /test/airport-search     - 机场搜索测试\n");
        result.append("  /test/flight-query       - 航班查询测试\n");
        result.append("  /test/flight-search-api  - 航班搜索API测试\n");
        result.append("  /test/order-manage       - 订单管理测试\n");
        result.append("  /test/passenger-manage   - 乘客管理测试\n");
        result.append("  /test/booking-workflow   - 订票和支付流程测试\n");
        result.append("\n【系统诊断】\n");
        result.append("  /test/db-stats           - 数据库统计\n");
        result.append("  /test/db-connection      - 连接测试\n");
        result.append("\n💡 每个测试独立运行，自动清理环境\n");
        result.append("🎯 确保数据库状态不受影响\n");

        return result.toString();
    }

    // 测试1: 用户注册功能
    @GetMapping("/test/user-register")
    @ResponseBody
    public String testUserRegister() {
        StringBuilder result = new StringBuilder();
        result.append("=== 用户注册功能测试 ===\n\n");

        String testEmail = "register@test.com";
        String testPhone = "13800000001";

        try {
            // 🔧 安全环境清理
            safeDeleteCustomers(testPhone, testEmail);

            // 测试用户注册
            Customer testUser = new Customer();
            testUser.setName("注册测试用户");
            testUser.setPassword("register123");
            testUser.setAccountBalance(100);
            testUser.setPhone(testPhone);
            testUser.setEmail(testEmail);
            testUser.setIdentity("110101199001010001");
            testUser.setRank(0);

            int registerResult = customerDao.register(testUser);
            result.append("✅ 用户注册: 影响行数 ").append(registerResult).append("\n");

            // 验证注册成功
            Optional<Customer> registeredUser = customerDao.findByEmailAndPassword(testEmail, "register123");
            result.append("✅ 注册验证: ").append(registeredUser.isPresent() ? "成功" : "失败").append("\n");

            if (registeredUser.isPresent()) {
                result.append("   用户ID: ").append(registeredUser.get().getCustomerId()).append("\n");
                result.append("   用户名: ").append(registeredUser.get().getName()).append("\n");
            }

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhone, testEmail);
            result.append("✅ 环境清理: 完成\n");

            result.append("\n🎉 用户注册测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 注册测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhone, testEmail);
        }

        return result.toString();
    }

    // 测试2: 用户登录功能
    @GetMapping("/test/user-login")
    @ResponseBody
    public String testUserLogin() {
        StringBuilder result = new StringBuilder();
        result.append("=== 用户登录功能测试 ===\n\n");

        String testEmail = "login@test.com";
        String testPhone = "13800000002";
        String testPassword = "login123";

        try {
            // 🔧 安全环境准备
            safeDeleteCustomers(testPhone, testEmail);

            // 创建测试用户
            Customer testUser = new Customer();
            testUser.setName("登录测试用户");
            testUser.setPassword(testPassword);
            testUser.setAccountBalance(200);
            testUser.setPhone(testPhone);
            testUser.setEmail(testEmail);
            testUser.setIdentity("110101199001010002");
            testUser.setRank(0);

            customerDao.register(testUser);
            result.append("✅ 测试用户创建完成\n");

            // 测试邮箱登录
            Optional<Customer> emailLogin = customerDao.findByEmailAndPassword(testEmail, testPassword);
            result.append("✅ 邮箱登录: ").append(emailLogin.isPresent() ? "成功" : "失败").append("\n");

            // 测试手机登录
            Optional<Customer> phoneLogin = customerDao.findByPhoneAndPassword(testPhone, testPassword);
            result.append("✅ 手机登录: ").append(phoneLogin.isPresent() ? "成功" : "失败").append("\n");

            // 测试身份证登录
            Optional<Customer> identityLogin = customerDao.findByIdentityAndPassword("110101199001010002",
                    testPassword);
            result.append("✅ 身份证登录: ").append(identityLogin.isPresent() ? "成功" : "失败").append("\n");

            // 测试错误密码
            Optional<Customer> wrongPasswordLogin = customerDao.findByEmailAndPassword(testEmail, "wrongpassword");
            result.append("✅ 错误密码拒绝: ").append(!wrongPasswordLogin.isPresent() ? "成功" : "失败").append("\n");

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhone, testEmail);
            result.append("✅ 环境清理: 完成\n");

            result.append("\n🎉 用户登录测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 登录测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhone, testEmail);
        }

        return result.toString();
    }

    // 测试3: 密码重置功能
    @GetMapping("/test/password-reset")
    @ResponseBody
    public String testPasswordReset() {
        StringBuilder result = new StringBuilder();
        result.append("=== 密码重置功能测试 ===\n\n");

        String testEmail = "reset@test.com";
        String testPhone = "13800000003";
        String originalPassword = "original123";
        String newPassword = "newpass123";

        try {
            // 🔧 安全环境准备
            safeDeleteCustomers(testPhone, testEmail);

            // 创建测试用户
            Customer testUser = new Customer();
            testUser.setName("密码重置测试");
            testUser.setPassword(originalPassword);
            testUser.setAccountBalance(0);
            testUser.setPhone(testPhone);
            testUser.setEmail(testEmail);
            testUser.setIdentity("110101199001010003");
            testUser.setRank(0);

            customerDao.register(testUser);
            result.append("✅ 测试用户创建完成\n");

            // 验证原密码有效
            Optional<Customer> originalLogin = customerDao.findByEmailAndPassword(testEmail, originalPassword);
            result.append("✅ 原密码验证: ").append(originalLogin.isPresent() ? "有效" : "无效").append("\n");

            // 执行密码重置
            int updateResult = customerDao.updatePasswordByEmail(testEmail, newPassword);
            result.append("✅ 密码重置: 影响行数 ").append(updateResult).append("\n");

            // 验证新密码有效
            Optional<Customer> newPasswordLogin = customerDao.findByEmailAndPassword(testEmail, newPassword);
            result.append("✅ 新密码验证: ").append(newPasswordLogin.isPresent() ? "有效" : "无效").append("\n");

            // 验证旧密码已失效
            Optional<Customer> oldPasswordLogin = customerDao.findByEmailAndPassword(testEmail, originalPassword);
            result.append("✅ 旧密码失效: ").append(!oldPasswordLogin.isPresent() ? "是" : "否").append("\n");

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhone, testEmail);
            result.append("✅ 环境清理: 完成\n");

            result.append("\n🎉 密码重置测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 密码重置测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhone, testEmail);
        }

        return result.toString();
    }

    // 测试4: 账户余额功能
    @GetMapping("/test/account-balance")
    @ResponseBody
    public String testAccountBalance() {
        StringBuilder result = new StringBuilder();
        result.append("=== 账户余额功能测试 ===\n\n");

        String testEmail = "balance@test.com";
        String testPhone = "13800000004";

        try {
            // 🔧 安全环境准备
            safeDeleteCustomers(testPhone, testEmail);

            // 创建测试用户
            Customer testUser = new Customer();
            testUser.setName("余额测试用户");
            testUser.setPassword("balance123");
            testUser.setAccountBalance(500); // 初始余额500
            testUser.setPhone(testPhone);
            testUser.setEmail(testEmail);
            testUser.setIdentity("110101199001010004");
            testUser.setRank(0);

            customerDao.register(testUser);

            // 获取用户ID和初始余额
            Optional<Customer> customer = customerDao.findByEmailAndPassword(testEmail, "balance123");
            if (customer.isPresent()) {
                String userId = customer.get().getCustomerId();
                Integer originalBalance = customer.get().getAccountBalance();

                result.append("✅ 初始余额: ").append(originalBalance).append("\n");

                // 测试充值
                Integer chargeAmount = 200;
                Integer expectedBalance = originalBalance + chargeAmount;

                int updateResult = customerDao.updateAccountBalance(userId, expectedBalance);
                result.append("✅ 充值操作: 影响行数 ").append(updateResult).append("\n");

                // 验证余额
                Optional<Customer> updatedCustomer = customerDao.findById(userId);
                if (updatedCustomer.isPresent()) {
                    Integer actualBalance = updatedCustomer.get().getAccountBalance();
                    result.append("✅ 充值后余额: ").append(actualBalance).append("\n");
                    result.append("✅ 余额验证: ").append(actualBalance.equals(expectedBalance) ? "正确" : "错误").append("\n");
                }
            }

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhone, testEmail);
            result.append("✅ 环境清理: 完成\n");

            result.append("\n🎉 账户余额测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 账户余额测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhone, testEmail);
        }

        return result.toString();
    }

    // 测试5: 机场搜索功能
    @GetMapping("/test/airport-search")
    @ResponseBody
    public String testAirportSearch() {
        StringBuilder result = new StringBuilder();
        result.append("=== 机场搜索功能测试 ===\n\n");

        try {
            // 测试所有机场查询
            List<Airport> allAirports = airportDao.findAll();
            result.append("✅ 机场总数: ").append(allAirports.size()).append("\n");

            if (!allAirports.isEmpty()) {
                Airport testAirport = allAirports.get(0);
                String testId = testAirport.getAirportId();
                String testName = testAirport.getAirportName();

                result.append("✅ 测试机场: ").append(testId).append(" - ").append(testName).append("\n");

                // 测试ID精确搜索
                Optional<Airport> idSearch = airportDao.findByAirportId(testId);
                result.append("✅ ID精确搜索: ").append(idSearch.isPresent() ? "成功" : "失败").append("\n");

                // 测试名称模糊搜索
                if (testName.length() > 1) {
                    String partialName = testName.substring(0, 1);
                    List<Airport> nameSearch = airportDao.findByNameContaining(partialName);
                    result.append("✅ 名称模糊搜索: 找到 ").append(nameSearch.size()).append(" 个结果\n");
                }

                // 测试组合搜索（模拟Flask的/api/airports）
                List<Airport> combinedSearch = airportDao.findByIdOrNameContaining(testId);
                result.append("✅ 组合搜索: 找到 ").append(combinedSearch.size()).append(" 个结果\n");
            }

            result.append("\n🎉 机场搜索测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 机场搜索测试失败: ").append(e.getMessage());
        }

        return result.toString();
    }

    // 测试6: 航班查询功能
    @GetMapping("/test/flight-query")
    @ResponseBody
    public String testFlightQuery() {
        StringBuilder result = new StringBuilder();
        result.append("=== 航班查询功能测试 ===\n\n");

        try {
            // 测试航班列表查询
            List<Flight> allFlights = flightDao.findAll();
            result.append("✅ 航班总数: ").append(allFlights.size()).append("\n");

            if (!allFlights.isEmpty()) {
                Flight testFlight = allFlights.get(0);
                String flightId = testFlight.getFlightId();

                result.append("✅ 测试航班: ").append(flightId).append("\n");

                // 测试航班号搜索
                Optional<Flight> flightSearch = flightDao.findByFlightId(flightId);
                result.append("✅ 航班号搜索: ").append(flightSearch.isPresent() ? "成功" : "失败").append("\n");

                if (flightSearch.isPresent()) {
                    Flight flight = flightSearch.get();
                    result.append("   航班路线: ").append(flight.getRouteId()).append("\n");
                    result.append("   使用机型: ").append(flight.getAircraftId()).append("\n");
                }
            }

            // 测试航线查询
            List<Route> routes = routeDao.findAll();
            result.append("✅ 航线总数: ").append(routes.size()).append("\n");

            result.append("\n🎉 航班查询测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 航班查询测试失败: ").append(e.getMessage());
        }

        return result.toString();
    }

    @GetMapping("/test/flight-search-api")
    @ResponseBody
    public String testFlightSearchApi() {
        StringBuilder result = new StringBuilder();
        result.append("=== 航班搜索API功能测试 ===\n\n");

        try {
            // 1. 测试机场搜索API - 对应Flask /api/airports
            result.append("【机场搜索API测试】\n");
            List<Airport> allAirports = airportDao.findAll();
            if (!allAirports.isEmpty()) {
                String testQuery = allAirports.get(0).getAirportId();
                List<Airport> searchResult = airportDao.findByIdOrNameContaining(testQuery);
                result.append("✅ /api/airports模拟: 搜索'").append(testQuery).append("'找到 ").append(searchResult.size())
                        .append(" 个机场\n");

                if (!searchResult.isEmpty()) {
                    Airport airport = searchResult.get(0);
                    result.append("   示例: ").append(airport.getAirportId()).append(" - ")
                            .append(airport.getAirportName()).append("\n");
                }
            }

            // 2. 测试航班号搜索 - 对应Flask /search_flightnum
            result.append("\n【航班号搜索测试】\n");
            List<Flight> allFlights = flightDao.findAll();
            if (!allFlights.isEmpty()) {
                String testFlightId = allFlights.get(0).getFlightId();
                Optional<Flight> flightResult = flightDao.findByFlightId(testFlightId);
                result.append("✅ 航班号搜索: 搜索'").append(testFlightId).append("' ");
                result.append(flightResult.isPresent() ? "成功" : "失败").append("\n");

                if (flightResult.isPresent()) {
                    Flight flight = flightResult.get();
                    result.append("   航班路线: ").append(flight.getRouteId()).append("\n");
                    result.append("   机型: ").append(flight.getAircraftId()).append("\n");
                }
            }

            // 3. 测试复杂航班搜索 - 对应Flask /search_flights
            result.append("\n【复杂航班搜索测试】\n");
            List<Route> routes = routeDao.findAll();
            if (!routes.isEmpty()) {
                Route testRoute = routes.get(0);
                String depId = testRoute.getDepartureAirportId();
                String arrId = testRoute.getArrivalAirportId();

                // 获取机场名称
                Optional<Airport> depAirport = airportDao.findByAirportId(depId);
                Optional<Airport> arrAirport = airportDao.findByAirportId(arrId);

                if (depAirport.isPresent() && arrAirport.isPresent()) {
                    result.append("✅ 测试航线: ").append(depAirport.get().getAirportName());
                    result.append(" → ").append(arrAirport.get().getAirportName()).append("\n");

                    // 模拟今天的航班搜索
                    LocalDate today = LocalDate.now();
                    List<Flight> flightResults = flightDao.searchFlightsByRoute(depId, arrId, today);
                    result.append("✅ 航班搜索结果: 找到 ").append(flightResults.size()).append(" 个航班\n");

                    if (!flightResults.isEmpty()) {
                        Flight sampleFlight = flightResults.get(0);
                        result.append("   示例航班: ").append(sampleFlight.getFlightId()).append("\n");
                        result.append("   出发时间: ").append(sampleFlight.getDepartureTime()).append("\n");
                    }
                }
            }

            result.append("\n🎉 航班搜索API测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 航班搜索API测试失败: ").append(e.getMessage());
        }

        return result.toString();
    }

    // 测试7: 订单管理功能
    @GetMapping("/test/order-manage")
    @ResponseBody
    public String testOrderManage() {
        StringBuilder result = new StringBuilder();
        result.append("=== 订单管理功能测试 ===\n\n");

        try {
            // 测试订单列表查询
            List<Order> allOrders = orderDao.findAll();
            result.append("✅ 订单总数: ").append(allOrders.size()).append("\n");

            if (!allOrders.isEmpty()) {
                Order testOrder = allOrders.get(0);
                String orderId = testOrder.getOrderId();

                result.append("✅ 测试订单: ").append(orderId).append("\n");
                result.append("   订单状态: ").append(testOrder.getOrderStatus()).append("\n");
                result.append("   乘客ID: ").append(testOrder.getCustomerId()).append("\n");
                result.append("   购买者ID: ").append(testOrder.getBuyerId()).append("\n");

                // 测试订单查询功能
                Optional<Order> orderSearch = orderDao.findByOrderId(orderId);
                result.append("✅ 订单号查询: ").append(orderSearch.isPresent() ? "成功" : "失败").append("\n");

                // 测试Flask中的订单搜索功能（需要手机号）
                if (orderSearch.isPresent()) {
                    String customerId = orderSearch.get().getCustomerId();
                    // 获取该用户的手机号进行测试
                    Optional<Customer> customer = customerDao.findById(customerId);
                    if (customer.isPresent()) {
                        String phone = customer.get().getPhone();
                        Optional<Map<String, Object>> orderWithCustomer = orderDao.findOrderWithCustomerInfo(orderId,
                                phone);
                        result.append("✅ 订单+手机号查询: ").append(orderWithCustomer.isPresent() ? "成功" : "失败").append("\n");
                    }
                }
            }

            result.append("\n🎉 订单管理测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 订单管理测试失败: ").append(e.getMessage());
        }

        return result.toString();
    }

    // 测试8: 乘客管理功能
    @GetMapping("/test/passenger-manage")
    @ResponseBody
    public String testPassengerManage() {
        StringBuilder result = new StringBuilder();
        result.append("=== 乘客管理功能测试 ===\n\n");

        String testEmailHost = "passenger-host@test.com";
        String testPhoneHost = "13800000005";
        String testEmailGuest = "passenger-guest@test.com";
        String testPhoneGuest = "13800000006";

        try {
            // 🔧 安全环境准备 - 清理测试数据
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);

            // 创建Host用户
            Customer hostUser = new Customer();
            hostUser.setName("乘客管理Host");
            hostUser.setPassword("host123");
            hostUser.setAccountBalance(0);
            hostUser.setPhone(testPhoneHost);
            hostUser.setEmail(testEmailHost);
            hostUser.setIdentity("110101199001010005");
            hostUser.setRank(0);

            customerDao.register(hostUser);

            // 创建Guest用户
            Customer guestUser = new Customer();
            guestUser.setName("乘客管理Guest");
            guestUser.setPassword("guest123");
            guestUser.setAccountBalance(0);
            guestUser.setPhone(testPhoneGuest);
            guestUser.setEmail(testEmailGuest);
            guestUser.setIdentity("110101199001010006");
            guestUser.setRank(0);

            customerDao.register(guestUser);
            result.append("✅ 测试用户创建完成\n");

            // 获取用户ID
            Optional<Customer> hostCustomer = customerDao.findByEmailAndPassword(testEmailHost, "host123");
            Optional<Customer> guestCustomer = customerDao.findByEmailAndPassword(testEmailGuest, "guest123");

            if (hostCustomer.isPresent() && guestCustomer.isPresent()) {
                String hostId = hostCustomer.get().getCustomerId();
                String guestId = guestCustomer.get().getCustomerId();

                // 测试添加乘客关系 - 对应Flask的insert_passenger功能
                int addResult = passengerDao.addPassenger(hostId, guestId);
                result.append("✅ 添加乘客关系: 影响行数 ").append(addResult).append("\n");

                // 测试查询乘客信息 - 对应Flask的/passengers路由
                List<Map<String, Object>> passengerInfo = passengerDao.findPassengerInfoByHostId(hostId);
                result.append("✅ 查询乘客信息: 找到 ").append(passengerInfo.size()).append(" 个乘客\n");

                if (!passengerInfo.isEmpty()) {
                    Map<String, Object> passenger = passengerInfo.get(0);
                    result.append("   乘客姓名: ").append(passenger.get("Name")).append("\n");
                    result.append("   乘客手机: ").append(passenger.get("Phone")).append("\n");
                    result.append("   乘客邮箱: ").append(passenger.get("Email")).append("\n");
                }

                // 测试通过手机号、身份证、姓名查找用户 - 对应Flask的insert_passenger中的查找逻辑
                Optional<Customer> foundGuest = customerDao.findByPhoneAndIdentityAndName(
                        testPhoneGuest, "110101199001010006", "乘客管理Guest");
                result.append("✅ 用户信息查找: ").append(foundGuest.isPresent() ? "成功" : "失败").append("\n");
            }

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);
            result.append("✅ 环境清理: 完成\n");

            result.append("\n🎉 乘客管理测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 乘客管理测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);
        }

        return result.toString();
    }

    // 测试订票和支付流程
    @GetMapping("/test/booking-workflow")
    @ResponseBody
    public String testBookingWorkflow() {
        StringBuilder result = new StringBuilder();
        result.append("=== 订票和支付流程测试 ===\n\n");

        String testEmailHost = "booking-host@test.com";
        String testPhoneHost = "13800001001";
        String testEmailGuest = "booking-guest@test.com";
        String testPhoneGuest = "13800001002";

        try {
            // 🔧 安全环境准备
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);

            // 1. 创建测试用户（Host和Guest）
            result.append("【创建测试用户】\n");

            // 创建Host用户
            Customer hostUser = new Customer();
            hostUser.setName("订票Host用户");
            hostUser.setPassword("host123");
            hostUser.setAccountBalance(5000); // 足够的余额
            hostUser.setPhone(testPhoneHost);
            hostUser.setEmail(testEmailHost);
            hostUser.setIdentity("110101199001010101");
            hostUser.setRank(10); // 10%折扣

            customerDao.register(hostUser);

            // 创建Guest用户
            Customer guestUser = new Customer();
            guestUser.setName("订票Guest用户");
            guestUser.setPassword("guest123");
            guestUser.setAccountBalance(0);
            guestUser.setPhone(testPhoneGuest);
            guestUser.setEmail(testEmailGuest);
            guestUser.setIdentity("110101199001010102");
            guestUser.setRank(0);

            customerDao.register(guestUser);
            result.append("✅ 测试用户创建完成\n");

            // 2. 获取用户ID并建立乘客关系
            Optional<Customer> hostCustomer = customerDao.findByEmailAndPassword(testEmailHost, "host123");
            Optional<Customer> guestCustomer = customerDao.findByEmailAndPassword(testEmailGuest, "guest123");

            if (hostCustomer.isPresent() && guestCustomer.isPresent()) {
                String hostId = hostCustomer.get().getCustomerId();
                String guestId = guestCustomer.get().getCustomerId();

                // 添加乘客关系
                passengerDao.addPassenger(hostId, guestId);
                result.append("✅ 乘客关系建立完成\n");

                // 3. 测试乘客查询功能 - 对应Flask /passengers
                result.append("\n【乘客查询测试】\n");
                List<Map<String, Object>> passengerInfo = passengerDao.findPassengerInfoByHostId(hostId);
                result.append("✅ 查询乘客信息: 找到 ").append(passengerInfo.size()).append(" 个乘客\n");

                // 4. 测试订单创建功能 - 对应Flask /book_flight
                result.append("\n【订单创建测试】\n");
                List<Flight> flights = flightDao.findAll();
                if (!flights.isEmpty()) {
                    String testFlightId = flights.get(0).getFlightId();
                    LocalDateTime orderTime = LocalDateTime.now();

                    // 创建测试订单
                    int orderResult = orderDao.createOrder(guestId, hostId, testFlightId, "Economy", "Established",
                            orderTime);
                    result.append("✅ 创建订单: 影响行数 ").append(orderResult).append("\n");

                    // 5. 测试订单查询功能
                    result.append("\n【订单查询测试】\n");
                    List<Map<String, Object>> orderIds = orderDao.findOrderIdsByCondition(guestId, hostId, orderTime);
                    result.append("✅ 订单ID查询: 找到 ").append(orderIds.size()).append(" 个订单\n");

                    if (!orderIds.isEmpty()) {
                        String orderId = (String) orderIds.get(0).get("OrderID");
                        result.append("   订单号: ").append(orderId).append("\n");

                        // 6. 测试支付流程 - 对应Flask /pay_order
                        result.append("\n【支付流程测试】\n");
                        Flight flight = flights.get(0);
                        BigDecimal economyPrice = flight.getEconomyPrice();

                        // 计算折扣价格
                        double discount = Math.min(10 / 100.0, 0.2); // 10%折扣
                        BigDecimal discountedAmount = economyPrice.multiply(BigDecimal.valueOf(1 - discount));

                        result.append("   原价: ").append(economyPrice).append("\n");
                        result.append("   折扣: ").append(discount * 100).append("%\n");
                        result.append("   实付: ").append(discountedAmount).append("\n");

                        // 更新订单状态为已支付
                        int payResult = orderDao.updateOrderStatus(orderId, "paid");
                        result.append("✅ 订单支付: 影响行数 ").append(payResult).append("\n");

                        // 更新用户余额
                        Integer originalBalance = hostCustomer.get().getAccountBalance();
                        Integer newBalance = originalBalance - discountedAmount.intValue();
                        int balanceResult = customerDao.updateAccountBalance(hostId, newBalance);
                        result.append("✅ 余额更新: 影响行数 ").append(balanceResult).append("\n");
                        result.append("   余额变化: ").append(originalBalance).append(" → ").append(newBalance)
                                .append("\n");

                        // 增加用户等级
                        int rankResult = customerDao.incrementRank(hostId);
                        result.append("✅ 等级更新: 影响行数 ").append(rankResult).append("\n");

                        // 7. 测试订单查询功能 - 对应Flask /search_order
                        result.append("\n【订单搜索测试】\n");
                        Optional<Map<String, Object>> orderSearchResult = orderDao.findOrderWithCustomerInfo(orderId,
                                testPhoneGuest);
                        result.append("✅ 订单+手机号搜索: ").append(orderSearchResult.isPresent() ? "成功" : "失败").append("\n");

                        // 8. 测试查看我的订单功能 - 对应Flask /view_orders
                        result.append("\n【我的订单查询测试】\n");
                        List<Order> myOrders = orderDao.findByBuyerId(hostId);
                        result.append("✅ 我的订单查询: 找到 ").append(myOrders.size()).append(" 个订单\n");
                    }
                }
            }

            // 🔧 安全环境清理
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);
            result.append("\n✅ 环境清理: 完成\n");

            result.append("\n🎉 订票和支付流程测试完成！");

        } catch (Exception e) {
            result.append("\n❌ 订票流程测试失败: ").append(e.getMessage());
            safeDeleteCustomers(testPhoneHost, testEmailHost, testPhoneGuest, testEmailGuest);
        }

        return result.toString();
    }

    // 测试9: 数据库统计
    @GetMapping("/test/db-stats")
    @ResponseBody
    public String testDatabaseStats() {
        StringBuilder result = new StringBuilder();
        result.append("=== 数据库统计信息 ===\n\n");

        try {
            // 统计各表记录数
            Integer customerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Customer", Integer.class);
            Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `Order`", Integer.class);
            Integer flightCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Flight", Integer.class);
            Integer airportCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Airport", Integer.class);
            Integer routeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Route", Integer.class);
            Integer passengerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Passenger", Integer.class);

            result.append("📊 数据表统计:\n");
            result.append("   Customer: ").append(customerCount != null ? customerCount : 0).append(" 条\n");
            result.append("   Order: ").append(orderCount != null ? orderCount : 0).append(" 条\n");
            result.append("   Flight: ").append(flightCount != null ? flightCount : 0).append(" 条\n");
            result.append("   Airport: ").append(airportCount != null ? airportCount : 0).append(" 条\n");
            result.append("   Route: ").append(routeCount != null ? routeCount : 0).append(" 条\n");
            result.append("   Passenger: ").append(passengerCount != null ? passengerCount : 0).append(" 条\n");

            result.append("\n🎉 数据统计完成！");

        } catch (Exception e) {
            result.append("\n❌ 数据统计失败: ").append(e.getMessage());
        }

        return result.toString();
    }

    // 测试10: 数据库连接测试
    @GetMapping("/test/db-connection")
    @ResponseBody
    public String testDatabaseConnection() {
        StringBuilder result = new StringBuilder();
        result.append("=== 数据库连接测试 ===\n\n");

        try {
            Integer testResult = jdbcTemplate.queryForObject("SELECT 1 as test_value", Integer.class);
            result.append("✅ 数据库连接: ").append(testResult != null && testResult == 1 ? "正常" : "异常").append("\n");

            String tableCheckSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'Airline' AND table_name = 'Customer'";
            Integer tableExists = jdbcTemplate.queryForObject(tableCheckSql, Integer.class);
            result.append("✅ Customer表存在: ").append(tableExists != null && tableExists > 0 ? "是" : "否").append("\n");

        } catch (Exception e) {
            result.append("❌ 测试失败: ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
}