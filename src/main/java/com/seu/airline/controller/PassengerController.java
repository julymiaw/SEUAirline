package com.seu.airline.controller;

import com.seu.airline.dao.CustomerDao;
import com.seu.airline.dao.PassengerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class PassengerController {

    @Autowired
    private PassengerDao passengerDao;
    @Autowired
    private CustomerDao customerDao;

    // 对应 Flask: @app.route("/passengers", methods=["GET"])
    @GetMapping("/passengers")
    public String passengersPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/";
        }

        try {
            // 使用PassengerDao查询乘客信息
            List<Map<String, Object>> guestInfo = passengerDao.findPassengerInfoByHostId(userId);
            model.addAttribute("guests", guestInfo);
            return "passengers";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "passengers";
        }
    }

    // 对应 Flask: @app.route("/admin/insert_passenger", methods=["POST"])
    @PostMapping("/admin/insert_passenger")
    @ResponseBody
    public Map<String, Object> insertPassenger(@RequestParam("Phone") String guestPhone,
            @RequestParam("Identity") String guestIdentity,
            @RequestParam("Name") String guestName,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String hostId = (String) session.getAttribute("user_id");
            if (hostId == null) {
                response.put("error", "请先登录");
                response.put("status", "error");
                return response;
            }

            // 使用CustomerDao查找或创建Guest用户
            Optional<com.seu.airline.entity.Customer> existingGuest = customerDao
                    .findByPhoneAndIdentityAndName(guestPhone, guestIdentity, guestName);

            String guestId;
            if (existingGuest.isPresent()) {
                // 如果Guest已存在，直接使用其ID
                guestId = existingGuest.get().getCustomerId();
            } else {
                // 如果Guest不存在，创建新的Customer记录
                int insertResult = customerDao.insertBasicCustomer(guestPhone, guestIdentity, guestName);
                if (insertResult <= 0) {
                    response.put("error", "创建乘客信息失败");
                    response.put("status", "error");
                    return response;
                }

                // 查询新创建的Guest的ID
                Optional<com.seu.airline.entity.Customer> newGuest = customerDao
                        .findByPhoneAndIdentityAndName(guestPhone, guestIdentity, guestName);

                if (!newGuest.isPresent()) {
                    response.put("error", "无法获取新创建的乘客ID");
                    response.put("status", "error");
                    return response;
                }
                guestId = newGuest.get().getCustomerId();
            }

            // 使用PassengerDao建立Host-Guest关系
            int relationResult = passengerDao.addPassenger(hostId, guestId);
            if (relationResult > 0) {
                response.put("message", "乘客添加成功");
                response.put("status", "success");
            } else {
                response.put("error", "建立乘客关系失败");
                response.put("status", "error");
            }

        } catch (Exception e) {
            response.put("error", "添加乘客时发生错误: " + e.getMessage());
            response.put("status", "error");
        }

        return response;
    }
}