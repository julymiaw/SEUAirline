package com.seu.airline.controller;

import com.seu.airline.dao.*;
import com.seu.airline.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class FlightController {

    @Autowired
    private AirportDao airportDao;
    @Autowired
    private FlightDao flightDao;
    @Autowired
    private RouteDao routeDao;

    // 对应 Flask: @app.route("/api/airports", methods=["GET"])
    @GetMapping("/api/airports")
    @ResponseBody
    public List<Airport> getAirports(@RequestParam(value = "query", defaultValue = "") String query) {
        try {
            // 复用AirportDao的机场搜索功能，完全对应Flask逻辑
            return airportDao.findByIdOrNameContaining(query);
        } catch (Exception e) {
            return new ArrayList<>(); // 返回空列表而不是抛异常
        }
    }

    // 对应 Flask: @app.route("/search_flightnum", methods=["POST"])
    @PostMapping("/search_flightnum")
    @ResponseBody
    public Map<String, Object> searchFlightnum(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String flightNum = data.get("flightnumber");

            // 复用FlightDao方法
            Optional<Flight> flight = flightDao.findByFlightId(flightNum);

            if (flight.isPresent()) {
                response.put("message", "Flight search successful");
                response.put("status", "success");
                response.put("flight", flight.get());
            } else {
                response.put("message", "flight not found");
                response.put("status", "error");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // 对应 Flask: @app.route("/search_flights", methods=["POST"])
    @PostMapping("/search_flights")
    @ResponseBody
    public Map<String, Object> searchFlights(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String departure = data.get("departure_airport");
            String arrival = data.get("arrival_airport");
            String departureDateStr = data.get("departure_date");
            LocalDate departureDate = LocalDate.parse(departureDateStr);

            // 1. 复用AirportDao查找出发机场（支持ID或名称）
            Optional<Airport> departureAirport = airportDao.findByIdOrName(departure);
            if (!departureAirport.isPresent()) {
                response.put("message", "出发机场不存在");
                response.put("status", "error");
                return response;
            }

            // 2. 复用AirportDao查找到达机场（支持ID或名称）
            Optional<Airport> arrivalAirport = airportDao.findByIdOrName(arrival);
            if (!arrivalAirport.isPresent()) {
                response.put("message", "到达机场不存在");
                response.put("status", "error");
                return response;
            }

            // 3. 复用RouteDao查找航线
            Optional<Route> route = routeDao.findByAirports(
                    departureAirport.get().getAirportId(),
                    arrivalAirport.get().getAirportId());

            if (!route.isPresent()) {
                response.put("message", "暂无该航线");
                response.put("status", "error");
                return response;
            }

            // 4. 复用FlightDao搜索航班
            List<Flight> flights = flightDao.searchFlightsByRoute(
                    departureAirport.get().getAirportId(),
                    arrivalAirport.get().getAirportId(),
                    departureDate);

            if (!flights.isEmpty()) {
                // 构建Flask格式的航班信息
                List<Map<String, Object>> flightInfo = new ArrayList<>();
                int order = 1;

                for (Flight flight : flights) {
                    Map<String, Object> flightDict = new HashMap<>();

                    // 格式化时间（模拟Flask的时间处理）
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    flightDict.put("order", order++);
                    flightDict.put("startPlace", departureAirport.get().getAirportName());
                    flightDict.put("startTime", flight.getDepartureTime().format(timeFormatter));
                    flightDict.put("startDate", flight.getDepartureTime().format(dateFormatter));
                    flightDict.put("endPlace", arrivalAirport.get().getAirportName());
                    flightDict.put("endTime", flight.getArrivalTime().format(timeFormatter));
                    flightDict.put("endDate", flight.getArrivalTime().format(dateFormatter));

                    // 计算飞行时间
                    long minutes = java.time.Duration.between(
                            flight.getDepartureTime(),
                            flight.getArrivalTime()).toMinutes();
                    flightDict.put("periodTime", String.format("%dh%dm", minutes / 60, minutes % 60));

                    flightDict.put("flightCode", flight.getFlightId());
                    flightDict.put("airline", "东南航空");
                    flightDict.put("planeType", flight.getAircraftId());
                    flightDict.put("planeImg", "320.jpg");
                    flightDict.put("economyPrice", flight.getEconomyPrice());
                    flightDict.put("businessPrice", flight.getBusinessPrice());

                    flightInfo.add(flightDict);
                }

                response.put("message", "Flight search successful");
                response.put("status", "success");
                response.put("flights", flightInfo);
            } else {
                response.put("message", "flight not found");
                response.put("status", "error");
            }

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }
        return response;
    }

    // 对应 Flask: @app.route("/choose_flight", methods=["GET"])
    @GetMapping("/choose_flight")
    public String chooseFlightPage() {
        return "choose_flight";
    }
}