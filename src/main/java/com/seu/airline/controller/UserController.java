package com.seu.airline.controller;

import com.seu.airline.dao.CustomerDao;
import com.seu.airline.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private CustomerDao customerDao;

    // 对应 Flask: @app.route("/", methods=["POST", "GET"])
    @GetMapping("/")
    public String home() {
        return "logreg";
    }

    // 对应 Flask: @app.route("/register", methods=["POST", "GET"])
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 复用CustomerDao，构建Customer对象
            Customer customer = new Customer();
            customer.setName((String) data.get("rname"));
            customer.setPassword((String) data.get("password"));
            customer.setAccountBalance((Integer) data.getOrDefault("AccountBalance", 0));
            customer.setPhone((String) data.get("phone"));
            customer.setEmail((String) data.getOrDefault("email", ""));
            customer.setIdentity((String) data.get("id"));
            customer.setRank((Integer) data.getOrDefault("rank", 0));

            // 使用DAO层方法
            int result = customerDao.register(customer);
            if (result > 0) {
                response.put("message", "Registration successful");
                return response;
            } else {
                response.put("error", "Registration failed");
                return response;
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    // 对应 Flask: @app.route("/login", methods=["POST"])
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> data, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = data.get("username");
            String password = data.get("password");

            // 复用DAO层的三种登录方式，完全对应Flask逻辑
            Optional<Customer> user = Optional.empty();

            // 尝试邮箱登录
            user = customerDao.findByEmailAndPassword(username, password);

            // 如果邮箱登录失败，尝试手机登录
            if (!user.isPresent()) {
                user = customerDao.findByPhoneAndPassword(username, password);
            }

            // 如果手机登录失败，尝试身份证登录
            if (!user.isPresent()) {
                user = customerDao.findByIdentityAndPassword(username, password);
            }

            if (user.isPresent()) {
                session.setAttribute("user_id", user.get().getCustomerId());
                response.put("status", "success");
                response.put("message", "Login successful");
            } else {
                response.put("status", "error");
                response.put("message", "Invalid credentials");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // 对应 Flask: @app.route("/dashboard", methods=["GET"])
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 复用DAO层方法
            Optional<Customer> user = customerDao.findById(userId);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
                return "user";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    // 对应 Flask: @app.route("/homepage", methods=["GET"])
    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }

    // 对应 Flask: @app.route("/forget_password", methods=["POST"])
    @PostMapping("/forget_password")
    @ResponseBody
    public Map<String, Object> forgetPassword(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = data.get("email");
            String password = data.get("password");

            // 复用DAO层方法
            int result = customerDao.updatePasswordByEmail(email, password);
            if (result > 0) {
                response.put("status", "success");
                response.put("message", "Forget password successful");
            } else {
                response.put("status", "error");
                response.put("message", "Email not found");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // 对应 Flask: @app.route("/logout")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user_id");
        return "redirect:/";
    }

    // 对应 Flask: @app.route("/chargepage", methods=["GET"])
    @GetMapping("/chargepage")
    public String chargePage() {
        return "charge";
    }

    // 对应 Flask: @app.route("/charge", methods=["POST"])
    @PostMapping("/charge")
    @ResponseBody
    public Map<String, Object> charge(@RequestBody Map<String, Object> data, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = (String) session.getAttribute("user_id");
            if (userId == null) {
                response.put("error", "Please login first");
                response.put("state", "error");
                return response;
            }

            Integer money = (Integer) data.get("money");

            // 复用DAO层方法，先查询当前余额
            Optional<Customer> customer = customerDao.findById(userId);

            if (customer.isPresent()) {
                Integer currentBalance = customer.get().getAccountBalance();
                Integer newBalance = currentBalance + money;

                // 复用DAO层方法更新余额
                int result = customerDao.updateAccountBalance(userId, newBalance);
                if (result > 0) {
                    response.put("message", "Charge successful");
                    response.put("state", "successful");
                } else {
                    response.put("error", "Charge failed");
                    response.put("state", "error");
                }
            } else {
                response.put("error", "User not found");
                response.put("state", "error");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("state", "error");
        }
        return response;
    }
}