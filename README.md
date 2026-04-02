# 税税通后端服务

基于 Spring Boot + MyBatis 的智能税务管理系统后端。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis 3.0.3
- **数据库**: MySQL 8.0+
- **安全**: JWT + BCrypt
- **构建工具**: Maven

## 项目结构

```
backend/
├── src/main/java/com/haizhou/smarttax/
│   ├── SmartTaxApplication.java       # 应用启动类
│   ├── common/                        # 公共类
│   │   └── Result.java                # 统一响应对象
│   ├── config/                        # 配置类
│   │   └── CorsConfig.java            # 跨域配置
│   ├── controller/                    # 控制器层
│   │   ├── AuthController.java        # 认证接口
│   │   ├── TaxDeclarationController.java  # 税务申报接口
│   │   ├── ExchangeRateController.java    # 汇率接口
│   │   ├── ChatController.java        # 聊天接口
│   │   └── ApiKeyController.java      # API密钥接口
│   ├── dto/                           # 数据传输对象
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── TaxDeclarationRequest.java
│   │   └── ExchangeRateRequest.java
│   ├── entity/                        # 实体类
│   │   ├── User.java
│   │   ├── TaxDeclaration.java
│   │   ├── ExchangeRate.java
│   │   ├── ChatMessage.java
│   │   └── ApiKey.java
│   ├── exception/                     # 异常处理
│   │   └── GlobalExceptionHandler.java
│   ├── mapper/                        # MyBatis Mapper接口
│   │   ├── UserMapper.java
│   │   ├── TaxDeclarationMapper.java
│   │   ├── ExchangeRateMapper.java
│   │   ├── ChatMessageMapper.java
│   │   └── ApiKeyMapper.java
│   ├── service/                       # 服务层
│   │   ├── UserService.java
│   │   ├── TaxDeclarationService.java
│   │   ├── ExchangeRateService.java
│   │   ├── ChatMessageService.java
│   │   └── ApiKeyService.java
│   └── util/                          # 工具类
│       └── JwtUtil.java               # JWT工具
├── src/main/resources/
│   ├── application.yml                # 应用配置
│   ├── mapper/                        # MyBatis XML映射文件
│   │   ├── UserMapper.xml
│   │   ├── TaxDeclarationMapper.xml
│   │   ├── ExchangeRateMapper.xml
│   │   ├── ChatMessageMapper.xml
│   │   └── ApiKeyMapper.xml
│   └── sql/
│       └── schema.sql                 # 数据库建表脚本
└── pom.xml                            # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置

```bash
# 创建数据库
mysql -u root -p
source src/main/resources/sql/schema.sql
```

或者手动执行 `schema.sql` 中的SQL语句。

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_tax_db
    username: root
    password: your_password
```

### 4. 运行项目

```bash
# 使用 Maven
mvn clean install
mvn spring-boot:run

# 或者使用 IDE 直接运行 SmartTaxApplication.java
```

服务启动后访问：http://localhost:8080/api

## API 接口文档

### 认证接口

#### 用户注册
```
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "companyName": "示例公司",
  "phone": "13800138000"
}
```

#### 用户登录
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### 税务申报接口

#### 创建申报
```
POST /api/v1/tax/declaration
Authorization: Bearer {token}
Content-Type: application/json

{
  "declarationType": "出口退税",
  "amount": 10000.00,
  "taxAmount": 1300.00,
  "currency": "CNY",
  "declarationDate": "2025-01-01",
  "remarks": "2024年第四季度退税"
}
```

#### 查询用户申报列表
```
GET /api/v1/tax/declarations
Authorization: Bearer {token}
```

#### 查询申报详情
```
GET /api/v1/tax/declaration/{id}
```

### 汇率转换接口

#### 货币转换
```
POST /api/v1/exchange/convert
Content-Type: application/json

{
  "fromCurrency": "CNY",
  "toCurrency": "USD",
  "amount": 1000.00
}
```

#### 查询汇率
```
GET /api/v1/exchange/rate?from=CNY&to=USD
```

### 聊天接口

#### 发送消息
```
POST /api/v1/chat/message
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "你好，我想咨询退税问题"
}
```

#### 获取消息历史
```
GET /api/v1/chat/messages
Authorization: Bearer {token}
```

### API密钥接口

#### 生成API密钥
```
POST /api/v1/apikey/generate
Authorization: Bearer {token}
```

#### 获取API密钥列表
```
GET /api/v1/apikey/list
Authorization: Bearer {token}
```

#### 验证API密钥
```
POST /api/v1/apikey/validate?apiKey={key}
```

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- **code**: 状态码（200-成功，500-失败）
- **message**: 响应消息
- **data**: 响应数据

## 安全说明

- 密码使用 BCrypt 加密存储
- 使用 JWT Token 进行身份认证
- Token 有效期：24小时
- 支持跨域请求（CORS）

## 开发建议

1. 在生产环境中，请修改 `application.yml` 中的 JWT secret
2. 配置正确的数据库连接信息
3. 启用 HTTPS
4. 添加请求日志和监控
5. 实现API限流

## 许可证

Copyright © 2025 税税通
