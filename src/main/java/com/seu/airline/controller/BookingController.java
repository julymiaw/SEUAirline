package com.seu.airline.controller;

import com.seu.airline.dao.*;
import com.seu.airline.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class BookingController {

    @Autowired
    private PassengerDao passengerDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private FlightDao flightDao;

    // 对应 Flask: @app.route("/book_flight", methods=["POST", "GET"])
    @GetMapping("/book_flight")
    public String bookFlightPage(@RequestParam("flight_id") String flightId,
            HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 复用PassengerDao查询当前用户所有的乘机人信息
            List<Map<String, Object>> passengers = passengerDao.findPassengerInfoByHostId(userId);

            model.addAttribute("flight_id", flightId);
            model.addAttribute("passengers", passengers);
            return "book_flight";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/choose_flight";
        }
    }

    @PostMapping("/book_flight")
    public String bookFlightProcess(@RequestParam("flight_id") String flightId,
            @RequestParam("SeatType") String seatType,
            @RequestParam("selected_passengers") List<String> selectedPassengers,
            HttpSession session) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 插入订单信息 - 复用OrderDao
            LocalDateTime currentTime = LocalDateTime.now();

            for (String guestId : selectedPassengers) {
                // 复用OrderDao创建订单
                orderDao.createOrder(guestId, userId, flightId, seatType, "Established", currentTime);
            }

            // 构建支付页面的参数
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String orderTimeStr = currentTime.format(formatter);

            return "redirect:/pay?order_time=" + orderTimeStr +
                    "&flight_id=" + flightId +
                    "&seat_type=" + seatType +
                    "&passengers=" + String.join(",", selectedPassengers);

        } catch (Exception e) {
            return "redirect:/book_flight?flight_id=" + flightId + "&error=" + e.getMessage();
        }
    }

    // 对应 Flask: @app.route("/pay", methods=["GET"])
    @GetMapping("/pay")
    public String payPage(@RequestParam("order_time") String orderTimeStr,
            @RequestParam("passengers") String passengersStr,
            @RequestParam("flight_id") String flightId,
            @RequestParam("seat_type") String seatType,
            HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 解析时间和乘客列表
            LocalDateTime orderTime = LocalDateTime.parse(orderTimeStr,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String[] passengers = passengersStr.split(",");

            List<Map<String, Object>> orderIds = new ArrayList<>();

            // 复用OrderDao获取订单信息
            for (String passenger : passengers) {
                List<Map<String, Object>> orderInfo = orderDao.findOrderIdsByCondition(
                        passenger, userId, orderTime);
                orderIds.addAll(orderInfo);
            }

            // 复用CustomerDao获取客户信息
            Optional<Customer> customer = customerDao.findById(userId);
            if (!customer.isPresent()) {
                return "redirect:/";
            }

            // 复用FlightDao获取航班信息
            Optional<Flight> flight = flightDao.findByFlightId(flightId);
            if (!flight.isPresent()) {
                return "redirect:/choose_flight";
            }

            // 计算价格和折扣 - 完全对应Flask逻辑
            Integer rank = customer.get().getRank();
            double discount = Math.min(rank / 100.0, 0.2); // 最多打8折

            BigDecimal originalAmount;
            if ("Economy".equals(seatType)) {
                originalAmount = flight.get().getEconomyPrice().multiply(BigDecimal.valueOf(passengers.length));
            } else {
                originalAmount = flight.get().getBusinessPrice().multiply(BigDecimal.valueOf(passengers.length));
            }

            BigDecimal discountedAmount = originalAmount.multiply(BigDecimal.valueOf(1 - discount));

            // 设置模板变量 - 完全对应Flask的模板参数
            model.addAttribute("buyerid", userId);
            model.addAttribute("orderids", orderIds);
            model.addAttribute("cname", customer.get().getName());
            model.addAttribute("cphone", customer.get().getPhone());
            model.addAttribute("original_amount", originalAmount);
            model.addAttribute("discount", discount * 100);
            model.addAttribute("discounted_amount", discountedAmount);
            model.addAttribute("account_balance", customer.get().getAccountBalance());

            return "pay";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/view_orders";
        }
    }

    // 对应 Flask: @app.route("/pay_order", methods=["POST"])
    @PostMapping("/pay_order")
    @ResponseBody
    public Map<String, Object> payOrder(@RequestParam("buyerid") String buyerId,
            @RequestParam("orderids") String orderIdsStr,
            @RequestParam("discounted_amount") String discountedAmountStr,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal discountedAmount = new BigDecimal(discountedAmountStr);

            // 解析订单ID列表 - 对应Flask的ast.literal_eval逻辑
            // 简化处理：假设orderIdsStr是逗号分隔的OrderID列表
            String[] orderIdArray = orderIdsStr.replaceAll("[\\[\\]'\"\\s]", "").split(",");
            List<String> orderIds = Arrays.asList(orderIdArray);

            // 复用CustomerDao获取客户信息
            Optional<Customer> customer = customerDao.findById(buyerId);
            if (!customer.isPresent()) {
                response.put("error", "Customer not found");
                return response;
            }

            // 检查余额是否足够 - 对应Flask逻辑
            Integer currentBalance = customer.get().getAccountBalance();
            if (currentBalance < discountedAmount.intValue()) {
                response.put("error", "Insufficient balance");
                return response;
            }

            // 更新订单状态 - 复用OrderDao
            for (String orderId : orderIds) {
                if (!orderId.trim().isEmpty()) {
                    orderDao.updateOrderStatus(orderId.trim(), "paid");
                }
            }

            // 扣除余额 - 复用CustomerDao
            Integer newBalance = currentBalance - discountedAmount.intValue();
            customerDao.updateAccountBalance(buyerId, newBalance);

            // 更新等级 - 复用CustomerDao
            customerDao.incrementRank(buyerId);

            response.put("message", "Payment successful");
            return response;

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    // 对应 Flask: @app.route("/search_order", methods=["POST"])
    @PostMapping("/search_order")
    @ResponseBody
    public Map<String, Object> searchOrder(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String orderNumber = data.get("order_number");
            String phoneNumber = data.get("phone_number");

            if (orderNumber == null || phoneNumber == null ||
                    orderNumber.trim().isEmpty() || phoneNumber.trim().isEmpty()) {
                response.put("message", "订单号和手机号不能为空");
                response.put("status", "error");
                return response;
            }

            // 复用OrderDao查询订单
            Optional<Map<String, Object>> order = orderDao.findOrderWithCustomerInfo(orderNumber, phoneNumber);

            if (order.isPresent()) {
                response.put("message", "Search order successfully");
                response.put("status", "success");
                response.put("redirect_url", "/view_orderID?order_number=" + orderNumber);
            } else {
                response.put("message", "未找到匹配的订单信息");
                response.put("status", "error");
            }

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // 对应 Flask: @app.route("/view_orderID", methods=["GET"])
    @GetMapping("/view_orderID")
    public String viewOrderById(@RequestParam("order_number") String orderNumber, Model model) {
        try {
            // 复用OrderDao查询订单详情
            Optional<Order> order = orderDao.findByOrderId(orderNumber);

            if (order.isPresent()) {
                model.addAttribute("order", order.get());
                return "view_orderID";
            } else {
                model.addAttribute("error", "订单不存在");
                return "redirect:/";
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    // 对应 Flask: @app.route("/view_orders", methods=["GET"])
    @GetMapping("/view_orders")
    public String viewOrders(HttpSession session, Model model) {
        String customerId = (String) session.getAttribute("user_id");
        if (customerId == null) {
            return "redirect:/";
        }

        try {
            // 复用OrderDao查询用户的所有订单
            List<Order> orders = orderDao.findByBuyerId(customerId);
            model.addAttribute("orders", orders);
            return "view_orders";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "view_orders";
        }
    }
}