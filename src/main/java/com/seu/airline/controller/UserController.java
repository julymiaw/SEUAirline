package com.seu.airline.controller;

import com.seu.airline.dao.CustomerDao;
import com.seu.airline.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

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
    public Map<String, Object> register(@Valid @RequestBody Customer customer,
            BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 检查验证错误
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                response.put("error", "数据验证失败");
                response.put("details", errors);
                response.put("status", "validation_error");
                return response;
            }

            // 设置默认值
            if (customer.getAccountBalance() == null) {
                customer.setAccountBalance(0);
            }
            if (customer.getRank() == null) {
                customer.setRank(0);
            }

            // 检查邮箱是否已存在
            Optional<Customer> existingByEmail = customerDao.findByEmailAndPassword(
                    customer.getEmail(), "");
            if (existingByEmail.isPresent()) {
                response.put("error", "该邮箱已被注册");
                response.put("status", "error");
                return response;
            }

            // 使用DAO层方法注册
            int result = customerDao.register(customer);
            if (result > 0) {
                response.put("message", "注册成功！");
                response.put("status", "success");
            } else {
                response.put("error", "注册失败，请稍后重试");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "注册过程中发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/login", methods=["POST"])
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> data, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = data.get("username");
            String password = data.get("password");

            // 基础验证
            if (username == null || username.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "用户名不能为空");
                return response;
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "密码不能为空");
                return response;
            }

            // 复用DAO层的三种登录方式
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
                response.put("message", "登录成功");
                response.put("user_name", user.get().getName());
            } else {
                response.put("status", "error");
                response.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "登录过程中发生错误: " + e.getMessage());
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

            // 基础验证
            if (email == null || email.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "邮箱不能为空");
                return response;
            }

            if (password == null || password.length() < 6) {
                response.put("status", "error");
                response.put("message", "新密码长度至少6位");
                return response;
            }

            // 验证邮箱格式
            if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                response.put("status", "error");
                response.put("message", "邮箱格式不正确");
                return response;
            }

            // 使用DAO层方法
            int result = customerDao.updatePasswordByEmail(email, password);
            if (result > 0) {
                response.put("status", "success");
                response.put("message", "密码重置成功");
            } else {
                response.put("status", "error");
                response.put("message", "邮箱不存在");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "密码重置过程中发生错误: " + e.getMessage());
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
                response.put("error", "请先登录");
                response.put("status", "error");
                return response;
            }

            // 改进的金额处理和验证
            Object moneyObj = data.get("money");
            Integer money;

            if (moneyObj instanceof Integer) {
                money = (Integer) moneyObj;
            } else if (moneyObj instanceof String) {
                try {
                    money = Integer.parseInt((String) moneyObj);
                } catch (NumberFormatException e) {
                    response.put("error", "充值金额格式不正确");
                    response.put("status", "error");
                    return response;
                }
            } else if (moneyObj instanceof Double) {
                money = ((Double) moneyObj).intValue();
            } else {
                response.put("error", "充值金额格式不正确");
                response.put("status", "error");
                return response;
            }

            // 验证充值金额
            if (money <= 0) {
                response.put("error", "充值金额必须大于0");
                response.put("status", "error");
                return response;
            }

            if (money > 50000) {
                response.put("error", "单次充值金额不能超过50000元");
                response.put("status", "error");
                return response;
            }

            // 使用CustomerDao实现充值逻辑
            Optional<Customer> customer = customerDao.findById(userId);

            if (customer.isPresent()) {
                Integer currentBalance = customer.get().getAccountBalance();
                currentBalance = currentBalance != null ? currentBalance : 0; // 防止空指针
                Integer newBalance = currentBalance + money;

                int result = customerDao.updateAccountBalance(userId, newBalance);
                if (result > 0) {
                    response.put("message", "充值成功！");
                    response.put("status", "success");
                    response.put("new_balance", newBalance);
                    response.put("charged_amount", money);
                    response.put("original_balance", currentBalance);
                } else {
                    response.put("error", "充值失败，请稍后重试");
                    response.put("status", "error");
                }
            } else {
                response.put("error", "用户不存在");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "充值过程中发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }
}