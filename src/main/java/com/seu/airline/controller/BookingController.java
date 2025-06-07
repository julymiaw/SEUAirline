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

    // 对应 Flask: @app.route("/book_flight", methods=["GET", "POST"])
    @GetMapping("/book_flight")
    public String bookFlightPage(@RequestParam("flight_id") String flightId,
            HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 使用PassengerDao查询乘机人信息
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
            LocalDateTime currentTime = LocalDateTime.now();

            // 使用OrderDao创建订单
            for (String guestId : selectedPassengers) {
                orderDao.createOrder(guestId, userId, flightId, seatType, "Established", currentTime);
            }

            // 直接跳转到支付页面
            return "redirect:/pay?flight_id=" + flightId +
                    "&seat_type=" + seatType +
                    "&passengers=" + String.join(",", selectedPassengers);

        } catch (Exception e) {
            return "redirect:/book_flight?flight_id=" + flightId + "&error=" + e.getMessage();
        }
    }

    // 对应 Flask: @app.route("/pay", methods=["GET"])
    @GetMapping("/pay")
    public String payPage(@RequestParam("passengers") String passengersStr,
            @RequestParam("flight_id") String flightId,
            @RequestParam("seat_type") String seatType,
            HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            String[] passengers = passengersStr.split(",");
            List<String> passengerList = Arrays.asList(passengers);

            // 使用OrderDao获取最新订单信息
            List<Order> recentOrders = orderDao.findLatestOrdersByCustomers(passengerList, userId);

            List<Map<String, Object>> orderIds = new ArrayList<>();
            for (Order order : recentOrders) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("OrderID", order.getOrderId());
                orderIds.add(orderInfo);
            }

            // 使用CustomerDao和FlightDao获取信息
            Optional<Customer> customer = customerDao.findById(userId);
            Optional<Flight> flight = flightDao.findByFlightId(flightId);

            if (!customer.isPresent() || !flight.isPresent()) {
                return "redirect:/";
            }

            // 计算价格和折扣
            Integer rank = customer.get().getRank();
            double discount = Math.min(rank / 100.0, 0.2);

            BigDecimal originalAmount;
            if ("Economy".equals(seatType)) {
                originalAmount = flight.get().getEconomyPrice().multiply(BigDecimal.valueOf(passengers.length));
            } else {
                originalAmount = flight.get().getBusinessPrice().multiply(BigDecimal.valueOf(passengers.length));
            }

            BigDecimal discountedAmount = originalAmount.multiply(BigDecimal.valueOf(1 - discount));

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
            String[] orderIdArray = orderIdsStr.replaceAll("[\\[\\]'\"\\s]", "").split(",");
            List<String> orderIds = Arrays.asList(orderIdArray);

            // 使用CustomerDao检查余额
            Optional<Customer> customer = customerDao.findById(buyerId);
            if (!customer.isPresent()) {
                response.put("error", "Customer not found");
                return response;
            }

            Integer currentBalance = customer.get().getAccountBalance();
            if (currentBalance < discountedAmount.intValue()) {
                response.put("error", "Insufficient balance");
                return response;
            }

            // 使用OrderDao更新订单状态
            for (String orderId : orderIds) {
                if (!orderId.trim().isEmpty()) {
                    orderDao.updateOrderStatus(orderId.trim(), "Paid");
                }
            }

            // 使用CustomerDao更新余额和等级
            Integer newBalance = currentBalance - discountedAmount.intValue();
            customerDao.updateAccountBalance(buyerId, newBalance);
            customerDao.incrementRank(buyerId);

            response.put("message", "Payment successful");
            return response;

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    // ✅ 核心功能: 订单号+手机号查询（实际使用的唯一查询方式）
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

            // 使用OrderDao查询订单（唯一的订单查询方式）
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

    @GetMapping("/view_orderID")
    public String viewOrderById(@RequestParam("order_number") String orderNumber, Model model) {
        try {
            // 使用OrderDao查询订单详情
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

    @GetMapping("/view_orders")
    public String viewOrders(HttpSession session, Model model) {
        String customerId = (String) session.getAttribute("user_id");
        if (customerId == null) {
            return "redirect:/";
        }

        try {
            // 使用OrderDao查询用户的所有订单
            List<Order> orders = orderDao.findByBuyerId(customerId);
            model.addAttribute("orders", orders);
            return "view_orders";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "view_orders";
        }
    }
}