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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminDao adminDao;
    @Autowired
    private AirportDao airportDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private AircraftDao aircraftDao;
    @Autowired
    private FlightDao flightDao;

    // 对应 Flask: @app.route("/admin/login", methods=["POST", "GET"])
    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> adminLogin(@RequestBody Map<String, String> data, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = data.get("username");
            String password = data.get("password");

            // 🔧 修复：使用AdminDao进行真正的数据库验证
            Optional<Admin> admin = adminDao.findByUsernameAndPassword(username, password);

            if (admin.isPresent()) {
                session.setAttribute("admin_logged_in", true);
                session.setAttribute("admin_id", admin.get().getAdminId());
                session.setAttribute("admin_name", admin.get().getName());

                response.put("status", "success");
                response.put("message", "管理员登录成功");
                response.put("admin_name", admin.get().getName());
            } else {
                response.put("status", "error");
                response.put("message", "管理员账号或密码错误");
            }
        } catch (Exception e) {
            response.put("error", "登录过程中发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/admin/dashboard")
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session) {
        Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }
        return "admin_dashboard";
    }

    // =============== 机场管理模块 ===============

    // 对应 Flask: @app.route("/admin/manage_airports")
    @GetMapping("/manage_airports")
    public String manageAirports(HttpSession session, Model model) {
        Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }

        try {
            // 使用AirportDao查询所有机场
            List<Airport> airports = airportDao.findAll();
            model.addAttribute("airports", airports);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "manage_airports";
    }

    // 对应 Flask: @app.route("/admin/insert_airport", methods=["POST"])
    @PostMapping("/insert_airport")
    @ResponseBody
    public Map<String, Object> insertAirport(@RequestParam("AirportID") String airportId,
            @RequestParam("AirportName") String airportName,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用AirportDao添加机场
            Airport airport = new Airport();
            airport.setAirportId(airportId);
            airport.setAirportName(airportName);

            int result = airportDao.save(airport);
            if (result > 0) {
                response.put("message", "机场添加成功");
                response.put("status", "success");
            } else {
                response.put("error", "机场添加失败");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "添加机场时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/admin/delete_airport", methods=["POST"])
    @PostMapping("/delete_airport")
    @ResponseBody
    public Map<String, Object> deleteAirport(@RequestParam("AirportID") String airportId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用AirportDao删除机场
            int result = airportDao.deleteByAirportId(airportId);
            if (result > 0) {
                response.put("message", "机场删除成功");
                response.put("status", "success");
            } else {
                response.put("error", "机场删除失败，可能不存在该机场");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "删除机场时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // =============== 航线管理模块 ===============

    // 对应 Flask: @app.route("/admin/manage_routes")
    @GetMapping("/manage_routes")
    public String manageRoutes(HttpSession session, Model model) {
        Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }

        try {
            // 使用RouteDao查询所有航线
            List<Route> routes = routeDao.findAll();
            model.addAttribute("routes", routes);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "manage_routes";
    }

    // 对应 Flask: @app.route("/admin/insert_route", methods=["POST"])
    @PostMapping("/insert_route")
    @ResponseBody
    public Map<String, Object> insertRoute(@RequestParam("DepartureAirportID") String departureAirportId,
            @RequestParam("ArrivalAirportID") String arrivalAirportId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用RouteDao添加航线
            int result = routeDao.save(departureAirportId, arrivalAirportId);
            if (result > 0) {
                response.put("message", "航线添加成功");
                response.put("status", "success");
            } else {
                response.put("error", "航线添加失败");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "添加航线时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/admin/delete_route", methods=["POST"])
    @PostMapping("/delete_route")
    @ResponseBody
    public Map<String, Object> deleteRoute(@RequestParam("RouteID") String routeId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用RouteDao删除航线
            int result = routeDao.deleteByRouteId(routeId);
            if (result > 0) {
                response.put("message", "航线删除成功");
                response.put("status", "success");
            } else {
                response.put("error", "航线删除失败，可能不存在该航线");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "删除航线时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // =============== 飞机管理模块 ===============

    // 对应 Flask: @app.route("/admin/manage_aircraft")
    @GetMapping("/manage_aircraft")
    public String manageAircraft(HttpSession session, Model model) {
        Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }

        try {
            // 使用AircraftDao查询所有飞机
            List<Aircraft> aircraftList = aircraftDao.findAll();
            model.addAttribute("aircraftList", aircraftList);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "manage_aircraft";
    }

    // 对应 Flask: @app.route("/admin/insert_aircraft", methods=["POST"])
    @PostMapping("/insert_aircraft")
    @ResponseBody
    public Map<String, Object> insertAircraft(@RequestParam("AircraftID") String aircraftId,
            @RequestParam("AircraftType") String aircraftType,
            @RequestParam("EconomySeats") Integer economySeats,
            @RequestParam("BusinessSeats") Integer businessSeats,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用AircraftDao添加飞机
            Aircraft aircraft = new Aircraft();
            aircraft.setAircraftId(aircraftId);
            aircraft.setAircraftType(aircraftType);
            aircraft.setEconomySeats(economySeats);
            aircraft.setBusinessSeats(businessSeats);

            int result = aircraftDao.save(aircraft);
            if (result > 0) {
                response.put("message", "飞机添加成功");
                response.put("status", "success");
            } else {
                response.put("error", "飞机添加失败");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "添加飞机时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/admin/delete_aircraft", methods=["POST"])
    @PostMapping("/delete_aircraft")
    @ResponseBody
    public Map<String, Object> deleteAircraft(@RequestParam("AircraftID") String aircraftId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用AircraftDao删除飞机
            int result = aircraftDao.deleteByAircraftId(aircraftId);
            if (result > 0) {
                response.put("message", "飞机删除成功");
                response.put("status", "success");
            } else {
                response.put("error", "飞机删除失败，可能不存在该飞机");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "删除飞机时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // =============== 航班管理模块 ===============

    // 对应 Flask: @app.route("/admin/manage_flights")
    @GetMapping("/manage_flights")
    public String manageFlights(HttpSession session, Model model) {
        Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }

        try {
            // 使用FlightDao查询所有航班
            List<Flight> flights = flightDao.findAll();
            model.addAttribute("flights", flights);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "manage_flights";
    }

    // 对应 Flask: @app.route("/admin/insert_flight", methods=["POST"])
    @PostMapping("/insert_flight")
    @ResponseBody
    public Map<String, Object> insertFlight(@RequestParam("FlightID") String flightId,
            @RequestParam("DepartureAirportID") String departureAirportId,
            @RequestParam("ArrivalAirportID") String arrivalAirportId,
            @RequestParam("AircraftID") String aircraftId,
            @RequestParam("DepartureTime") String departureTimeStr,
            @RequestParam("ArrivalTime") String arrivalTimeStr,
            @RequestParam("EconomyPrice") BigDecimal economyPrice,
            @RequestParam("BusinessPrice") BigDecimal businessPrice,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 1. 使用RouteDao查找或创建航线
            Optional<Route> existingRoute = routeDao.findByAirports(departureAirportId, arrivalAirportId);
            String routeId;

            if (existingRoute.isPresent()) {
                routeId = existingRoute.get().getRouteId();
            } else {
                // 如果航线不存在，先创建航线
                int routeResult = routeDao.save(departureAirportId, arrivalAirportId);
                if (routeResult <= 0) {
                    response.put("error", "创建航线失败");
                    response.put("status", "error");
                    return response;
                }

                // 查询新创建的航线ID
                Optional<Route> newRoute = routeDao.findByAirports(departureAirportId, arrivalAirportId);
                if (!newRoute.isPresent()) {
                    response.put("error", "无法获取新创建的航线ID");
                    response.put("status", "error");
                    return response;
                }
                routeId = newRoute.get().getRouteId();
            }

            // 2. 解析时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);

            // 3. 使用FlightDao添加航班
            Flight flight = new Flight();
            flight.setFlightId(flightId);
            flight.setRouteId(routeId);
            flight.setAircraftId(aircraftId);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            flight.setEconomyPrice(economyPrice);
            flight.setBusinessPrice(businessPrice);

            int result = flightDao.save(flight);
            if (result > 0) {
                response.put("message", "航班添加成功");
                response.put("status", "success");
            } else {
                response.put("error", "航班添加失败");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "添加航班时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/admin/delete_flight", methods=["POST"])
    @PostMapping("/delete_flight")
    @ResponseBody
    public Map<String, Object> deleteFlight(@RequestParam("FlightID") String flightId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("admin_logged_in");
            if (adminLoggedIn == null || !adminLoggedIn) {
                response.put("error", "请先登录管理员账号");
                response.put("status", "error");
                return response;
            }

            // 使用FlightDao删除航班
            int result = flightDao.deleteByFlightId(flightId);
            if (result > 0) {
                response.put("message", "航班删除成功");
                response.put("status", "success");
            } else {
                response.put("error", "航班删除失败，可能不存在该航班");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", "删除航班时发生错误: " + e.getMessage());
            response.put("status", "error");
        }
        return response;
    }
}