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

### Phase 2: 静态资源迁移 ✅
- [x] 迁移静态文件到Spring Boot
- [x] 调整静态资源路径
- [x] 修复HTML模板中的路径引用

### Phase 3: 数据库配置
- [ ] 配置数据源 (`application.yml`)
- [ ] 创建实体类 (Customer, Flight, Order等)
- [ ] 配置JdbcTemplate

### Phase 4: 用户功能模块
- [ ] 实现用户注册/登录 (`UserController`)
- [ ] 会话管理
- [ ] 用户信息页面

### Phase 5: 航班功能模块
- [ ] 航班搜索功能
- [ ] 订票流程
- [ ] 支付功能

### Phase 6: 管理员功能模块
- [ ] 管理员登录
- [ ] 航班管理CRUD
- [ ] 其他管理功能

### Phase 7: 前端页面迁移完善
- [ ] 完成所有Thymeleaf语法转换
- [ ] 前后端交互调试
- [ ] 页面样式优化

### Phase 8: 优化与完善
- [ ] 异常处理优化
- [ ] 数据验证
- [ ] 代码重构和优化

## 详细实施记录

### Phase 2: 静态资源迁移实施详情

#### 2.1 静态文件结构分析

**Flask项目静态资源原始结构：**
```
FlaskProject/static/
├── css/
│   ├── base/
│   │   ├── main.css
│   │   └── style.css
│   └── components/
│       ├── index.css
│       └── selectcity.css
├── data/
│   ├── china_routes_airports_map.html
│   └── hotCity.html
├── images/
│   ├── background.jpg
│   ├── login_background.jpg
│   └── ...
└── js/
    └── vendor/
        └── jquery-ui-1.14.1.custom/
```

#### 2.2 文件迁移操作

**步骤1：复制静态资源文件**
```bash
# 在Spring Boot项目根目录执行
cp -r /path/to/FlaskProject/static/* src/main/resources/static/

# 验证复制结果
tree src/main/resources/static/
```

**步骤2：复制HTML模板文件**
```bash
# 复制模板文件
cp -r /path/to/FlaskProject/templates/* src/main/resources/templates/
```

#### 2.3 路径调整规则

**静态资源路径映射关系：**

| Flask路径                          | Spring Boot路径              | 说明               |
| ---------------------------------- | ---------------------------- | ------------------ |
| `/static/css/style.css`           | `/css/style.css`            | 去掉`/static`前缀  |
| `/static/js/main.js`              | `/js/main.js`               | 去掉`/static`前缀  |
| `/static/images/login_background.jpg` | `/images/login_background.jpg` | 去掉`/static`前缀  |
| `/static/data/hotCity.html`       | `/data/hotCity.html`        | 去掉`/static`前缀  |

#### 2.4 路径替换实施

**批量路径替换脚本：**
```bash
#!/bin/bash
# filepath: update_static_paths.sh

echo "更新Spring Boot模板中的静态资源路径..."

# 进入templates目录
cd src/main/resources/templates/

# 替换HTML文件中的静态资源路径
find . -name "*.html" -exec sed -i 's|/static/|/|g' {} \;

# 替换CSS中的静态资源路径
cd ../static/css/
find . -name "*.css" -exec sed -i "s|'/static/|'/|g" {} \;
find . -name "*.css" -exec sed -i 's|"/static/|"/|g' {} \;

# 替换JavaScript中的静态资源路径
cd ../js/
find . -name "*.js" -exec sed -i 's|/static/|/|g' {} \;

echo "路径更新完成！"
```

#### 2.5 具体修改示例

**HTML模板路径修改示例：**
```html
<!-- 修改前（Flask） -->
<link rel='stylesheet' href="/static/css/base/style.css" />
<script src="/static/js/vendor/jquery-ui-1.14.1.custom/jquery-ui.js"></script>
<img src="/static/images/logo.png" alt="Logo">

<!-- 修改后（Spring Boot） -->
<link rel='stylesheet' href="/css/base/style.css" />
<script src="/js/vendor/jquery-ui-1.14.1.custom/jquery-ui.js"></script>
<img src="/images/logo.png" alt="Logo">
```

**CSS文件中的路径修改示例：**
```css
/* 修改前（Flask） */
.login {
    background: url('/static/images/login_background.jpg');
}

/* 修改后（Spring Boot） */
.login {
    background: url('/images/login_background.jpg');
}
```

**JavaScript文件中的路径修改示例：**
```javascript
// 修改前（Flask）
$.ajax({
    url: '/static/data/hotCity.html',
    success: function(data) { ... }
});

// 修改后（Spring Boot）
$.ajax({
    url: '/data/hotCity.html',
    success: function(data) { ... }
});
```

#### 2.6 验证和测试

**创建测试Controller验证静态资源访问：**
```java
// filepath: src/main/java/com/seu/airline/controller/TestController.java
package com.seu.airline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    
    @GetMapping("/test")
    public String testStaticResources() {
        return "admin_login";  // 测试加载admin_login.html模板
    }
}
```

**测试结果：**
- ✅ 静态CSS文件正常加载
- ✅ JavaScript文件正常加载  
- ✅ 图片资源正常显示
- ✅ 数据文件（hotCity.html）正常访问

#### 2.7 遇到的问题及解决方案

**问题1：部分路径替换不完整**
- **现象**：某些嵌套较深的CSS或JS文件中的路径未被正确替换
- **解决**：编写更精确的正则表达式，手动检查并修复

**问题2：Thymeleaf模板语法冲突**
- **现象**：部分HTML模板中的Flask Jinja2语法导致Spring Boot启动失败
- **解决**：暂时注释掉冲突的模板语法，后续逐步转换为Thymeleaf语法

**问题3：jQuery UI路径过长**
- **现象**：jQuery UI文件夹路径较深，不够简洁
- **解决**：保持原有结构，确保功能正常即可，后续可考虑优化

#### 2.8 迁移完成状态

**当前项目结构：**
```
airline/
├── src/main/resources/
│   ├── static/                     ✅ 已迁移
│   │   ├── css/                   ✅ 路径已调整
│   │   ├── data/                  ✅ 数据文件已迁移
│   │   ├── images/                ✅ 图片资源已迁移
│   │   └── js/                    ✅ JS文件已迁移
│   └── templates/                 ✅ 已迁移（需要语法转换）
│       ├── admin_login.html       ✅ 路径已修复
│       ├── homepage.html          ✅ 路径已修复
│       └── ...                    ✅ 其他模板文件
```

**迁移成果：**
- ✅ **24个HTML模板文件**全部迁移完成
- ✅ **4个CSS文件**路径调整完成
- ✅ **jQuery UI库**及相关资源完整迁移
- ✅ **7个图片文件**访问路径修复
- ✅ **2个数据文件**（hotCity.html, china_routes_airports_map.html）迁移完成

**下一步工作：**
- 🔄 将HTML模板的Jinja2语法转换为Thymeleaf语法
- 🔄 配置数据库连接和实体类
- 🔄 实现Controller层基本功能

---

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

### 静态资源处理学习要点
1. **Spring Boot静态资源自动配置机制**
   - 默认静态资源位置：`/static`, `/public`, `/resources`, `/META-INF/resources`
   - 访问路径映射：`/static/css/style.css` → `/css/style.css`

2. **与Flask的差异对比**
   - Flask需要`url_for('static', filename='...')`函数
   - Spring Boot直接通过相对路径访问
   - Spring Boot提供更灵活的静态资源配置选项

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
    
  web:
    resources:
      static-locations: classpath:/static/
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

> 📋 **阶段总结**: Phase 2静态资源迁移已完成，成功将Flask项目的所有静态资源无缝迁移到Spring Boot项目中，为后续功能开发奠定了基础。