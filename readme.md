# 航空管理系统 Flask → Spring Boot 重构项目

## 项目概述

本项目是将基于 Flask 的航空管理系统重构为 Spring Boot 项目的学习实践。通过这个重构过程，深入学习 Spring Boot 框架的核心概念和最佳实践。

### 原始项目信息
- **框架**: Flask (Python)
- **数据库**: MySQL
- **功能**: 航空票务管理系统，包含用户管理、航班查询、订票、支付等功能

### 目标项目信息
- **框架**: Spring Boot (Java)
- **包名**: `com.seu`
- **项目名**: `airline`
- **数据库**: MySQL (保持不变)

## 技术栈对比

| 功能模块   | Flask         | Spring Boot                     |
| ---------- | ------------- | ------------------------------- |
| Web框架    | Flask         | Spring Web                      |
| 模板引擎   | Jinja2        | Thymeleaf                       |
| 数据库连接 | PyMySQL       | Spring Data JDBC + MySQL Driver |
| 会话管理   | Flask Session | Spring Session Web              |
| 开发工具   | debug=True    | Spring Boot DevTools            |
| 数据验证   | 手动验证      | Validation (可选)               |

## 项目结构对比

### Flask 项目结构
```
FlaskProject/
├── app.py                    # 主应用文件
├── static/                   # 静态资源
│   ├── css/
│   ├── js/
│   └── images/
├── templates/                # HTML模板
│   ├── logreg.html
│   ├── user.html
│   ├── admin_dashboard.html
│   └── ...
└── requirements.txt          # 依赖配置
```

### Spring Boot 目标结构
```
airline/
├── src/main/java/com/seu/
│   ├── AirlineApplication.java      # 主启动类
│   ├── controller/                  # 控制器层
│   │   ├── UserController.java
│   │   ├── FlightController.java
│   │   ├── AdminController.java
│   │   └── ...
│   ├── service/                     # 业务逻辑层
│   │   ├── UserService.java
│   │   ├── FlightService.java
│   │   └── ...
│   ├── entity/                      # 实体类
│   │   ├── Customer.java
│   │   ├── Flight.java
│   │   └── ...
│   └── config/                      # 配置类
│       └── DatabaseConfig.java
├── src/main/resources/
│   ├── static/                      # 静态资源
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   ├── templates/                   # Thymeleaf模板
│   │   ├── logreg.html
│   │   ├── user.html
│   │   └── ...
│   └── application.yml              # 配置文件
└── pom.xml                          # Maven依赖
```

## 核心功能模块分析

### 1. 用户认证模块
**Flask实现** (`/login`, `/register`):
- 使用 `request.json` 获取数据
- 手动SQL查询验证
- `session["user_id"]` 存储用户状态

**Spring Boot重构计划**:
- `@PostMapping("/login")` 注解路由
- `@RequestBody` 接收JSON数据
- `HttpSession` 管理会话
- `@Service` 层处理业务逻辑

### 2. 航班管理模块
**Flask实现** (`/search_flights`, `/book_flight`):
- 复杂的SQL查询逻辑
- 手动数据转换和格式化
- 直接在路由中处理业务逻辑

**Spring Boot重构计划**:
- 分离 Controller 和 Service 层
- 使用实体类封装数据
- `JdbcTemplate` 简化数据库操作

### 3. 管理员模块
**Flask实现** (`/admin/*`):
- 简单的CRUD操作
- 模板渲染管理页面

**Spring Boot重构计划**:
- 独立的 `AdminController`
- 统一的异常处理
- RESTful API设计

## 重构进度规划

### Phase 1: 项目基础搭建 ✅
- [x] 创建Spring Boot项目
- [x] 配置必要依赖
- [x] 建立项目结构

### Phase 2: 数据库配置
- [ ] 配置数据源 (`application.yml`)
- [ ] 创建实体类 (Customer, Flight, Order等)
- [ ] 配置JdbcTemplate

### Phase 3: 用户功能模块
- [ ] 实现用户注册/登录 (`UserController`)
- [ ] 会话管理
- [ ] 用户信息页面

### Phase 4: 航班功能模块
- [ ] 航班搜索功能
- [ ] 订票流程
- [ ] 支付功能

### Phase 5: 管理员功能模块
- [ ] 管理员登录
- [ ] 航班管理CRUD
- [ ] 其他管理功能

### Phase 6: 前端页面迁移
- [ ] 迁移HTML模板到Thymeleaf
- [ ] 适配静态资源
- [ ] 前后端交互调试

### Phase 7: 优化与完善
- [ ] 异常处理优化
- [ ] 数据验证
- [ ] 代码重构和优化

## 学习重点

### Spring Boot核心概念
1. **依赖注入** (Dependency Injection)
2. **自动配置** (Auto Configuration)  
3. **分层架构** (Controller-Service-Repository)
4. **注解驱动开发**

### 关键注解学习
- `@SpringBootApplication`
- `@Controller` / `@RestController`
- `@Service` / `@Repository`
- `@Autowired`
- `@RequestMapping` 系列
- `@RequestBody` / `@ResponseBody`

### 配置管理
- `application.yml` 配置
- 数据库连接池配置
- 会话管理配置

## 数据库配置

```yaml
# application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Airline
    username: DbUser
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    cache: false
```

## 开发环境

- **JDK版本**: 17+
- **Spring Boot版本**: 3.2.x
- **数据库**: MySQL 8.0
- **IDE**: IntelliJ IDEA / Visual Studio Code
- **构建工具**: Maven

## 学习资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Thymeleaf官方文档](https://www.thymeleaf.org/)
- [Spring Data JDBC文档](https://spring.io/projects/spring-data-jdbc)

---

**项目目标**: 通过实际重构项目，掌握Spring Boot开发的核心技能，理解企业级Java Web开发的最佳实践。

**开始时间**: 2025年6月6日  
**预期完成**: 根据学习进度调整

> 💡 **学习建议**: 每完成一个模块后，对比Flask和Spring Boot的实现方式，总结差异和优势，加深理解。