# Campus Parking Management System

校园停车管理系统 - 一个基于 Spring Boot + Vue 3 的现代化停车管理解决方案

## 📋 项目简介

本系统是一个完整的校园停车管理平台，提供车辆进出管理、车位管理、用户管理、数据统计等功能。系统采用前后端分离架构，支持多角色权限控制。

## 🚀 主要功能

### 用户管理
- 多角色支持：管理员(ADMIN)、保安(GUARD)、教师(TEACHER)、学生(STUDENT)
- 用户登录/登出
- 个人信息管理
- 角色权限控制

### 车辆管理
- 车辆信息登记
- 车辆类型分类：内部车辆(INTERNAL)、临时车辆(TEMPORARY)
- 车牌号唯一标识
- 车主信息管理

### 停车场管理
- 停车区域管理（A区、B区、C区等）
- 车位状态管理（空闲/占用/故障）
- 车位实时更新

### 进出记录
- 车辆入场/出场记录
- 自动分配车位
- 操作人员记录
- 进出历史查询

### 数据统计
- 停车场使用率统计
- 车辆进出趋势分析
- 实时数据大屏展示

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 2.7.18
- **Java版本**: JDK 17
- **ORM**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 9.0
- **认证**: JWT (Hutool JWT)
- **密码加密**: BCrypt
- **构建工具**: Maven

### 前端技术
- **框架**: Vue 3
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **图表库**: ECharts
- **HTTP客户端**: Axios
- **路由**: Vue Router 4

## 📦 项目结构

```
campus_parking/
├── spring_boot/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/campus/parking/
│   │   │   │   ├── common/      # 公共工具类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── mapper/      # MyBatis Mapper
│   │   │   │   └── service/     # 服务层
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   │   └── test/                # 测试代码
│   └── pom.xml                  # Maven配置
│
├── vite_vue/                    # 前端项目
│   ├── src/
│   │   ├── api/                 # API接口
│   │   ├── assets/              # 静态资源
│   │   ├── router/              # 路由配置
│   │   ├── utils/               # 工具函数
│   │   └── views/               # 页面组件
│   ├── index.html               # 入口HTML
│   ├── package.json             # 依赖配置
│   └── vite.config.js           # Vite配置
│
├── campus_parking.sql           # 数据库脚本
└── README.md                    # 项目说明
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE campus_parking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入数据库脚本：
```bash
mysql -u root -p campus_parking < campus_parking.sql
```

### 后端启动

1. 进入后端目录：
```bash
cd spring_boot
```

2. 配置环境变量（可选，也可以直接修改 application.yml）：
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=campus_parking
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET_KEY=your_jwt_secret_key
```

3. 使用 Maven 启动：
```bash
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

1. 进入前端目录：
```bash
cd vite_vue
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端应用将在 http://localhost:5173 启动

### 默认账号

- 管理员：admin / 123456
- 保安：guard / 123456

## 📝 配置说明

### 后端配置

主要配置文件：`spring_boot/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:campus_parking}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:campus_parking}
    password: ${DB_PASSWORD:your_password}

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
```

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_HOST | 数据库主机 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | campus_parking |
| DB_USERNAME | 数据库用户名 | campus_parking |
| DB_PASSWORD | 数据库密码 | your_password |
| JWT_SECRET_KEY | JWT密钥 | your_jwt_secret_key_here |

## 🔐 安全说明

- 所有敏感配置均通过环境变量注入
- 密码使用 BCrypt 加密存储
- JWT Token 有效期为 24 小时
- 支持基于角色的访问控制(RBAC)

## 📄 API 接口

### 认证相关
- `POST /api/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

### 车辆管理
- `GET /api/vehicles` - 获取车辆列表
- `POST /api/vehicles` - 添加车辆
- `PUT /api/vehicles/{id}` - 更新车辆信息
- `DELETE /api/vehicles/{id}` - 删除车辆

### 停车场管理
- `GET /api/parking/areas` - 获取停车场区域
- `GET /api/parking/spots` - 获取车位列表
- `PUT /api/parking/spots/{id}` - 更新车位状态

### 进出记录
- `POST /api/access/entry` - 车辆入场
- `POST /api/access/exit` - 车辆出场
- `GET /api/access/records` - 获取进出记录

### 统计分析
- `GET /api/statistics/overview` - 获取概览统计
- `GET /api/statistics/trends` - 获取趋势数据

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 更新日志

### v1.0.0 (2026-06-08)
- 初始版本发布
- 实现用户管理、车辆管理、停车场管理功能
- 实现车辆进出记录管理
- 实现数据统计功能

## 📞 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

**注意**: 首次运行前请确保已正确配置数据库连接信息，并导入数据库脚本。
