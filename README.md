# spring-boot-rest-api

> 一个基于 Spring Boot 3.x 的 REST API 示例项目，展示规范的 API 设计、全局异常处理、参数数据验证以及单元测试。

## 项目简介

本项目以"商品(Product)"为业务模型，实现了一套完整的 RESTful CRUD 接口，涵盖：

- 规范化的 RESTful API 设计（资源路径、HTTP 方法语义、状态码）
- 基于 `@Valid` + `jakarta.validation` 的参数校验
- 统一的全局异常处理（`@RestControllerAdvice`）
- 分页查询与统一分页响应结构
- JPA + H2 内存数据库持久化
- Lombok 减少样板代码
- 控制器层 / 服务层单元测试

## 技术栈

| 分类       | 技术                                   |
| ---------- | -------------------------------------- |
| 语言       | Java 17                                |
| 框架       | Spring Boot 3.2.5                      |
| Web        | spring-boot-starter-web                |
| 校验       | spring-boot-starter-validation         |
| 持久层     | spring-boot-starter-data-jpa + H2      |
| 工具       | Lombok                                 |
| 构建       | Maven                                  |
| 测试       | spring-boot-starter-test (JUnit5 + MockMvc) |

## API 接口文档

基础路径：`/api/products`

| 方法   | 路径                | 说明         | 请求体                  | 响应                          |
| ------ | ------------------- | ------------ | ----------------------- | ----------------------------- |
| GET    | /api/products       | 分页查询列表 | - (query: page, size)   | `PageResponse<ProductDTO>`    |
| GET    | /api/products/{id}  | 查询详情     | -                       | `ProductDTO`                  |
| POST   | /api/products       | 创建商品     | `ProductDTO`            | `ProductDTO` (201)            |
| PUT    | /api/products/{id}  | 更新商品     | `ProductDTO`            | `ProductDTO`                  |
| DELETE | /api/products/{id}  | 删除商品     | -                       | 204 No Content                |

### ProductDTO 字段

| 字段        | 类型       | 校验                       | 说明     |
| ----------- | ---------- | -------------------------- | -------- |
| id          | Long       | -                          | 主键     |
| name        | String     | @NotBlank, @Size(max=100)  | 名称     |
| description | String     | @Size(max=500)             | 描述     |
| price       | BigDecimal | @NotNull, @DecimalMin("0") | 价格     |
| stock       | Integer    | @NotNull, @Min(0)          | 库存     |

### 错误响应格式

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 99",
  "path": "/api/products/99"
}
```

## 运行方式

### 环境要求

- JDK 17+
- Maven 3.8+

### 启动

```bash
mvn clean spring-boot:run
```

启动后访问：

- API 根路径：http://localhost:8080/api/products
- H2 控制台：http://localhost:8080/h2-console （JDBC URL: `jdbc:h2:mem:testdb`）

### 打包

```bash
mvn clean package
java -jar target/spring-boot-rest-api-0.0.1-SNAPSHOT.jar
```

## 测试方式

```bash
# 运行全部测试
mvn test

# 运行指定测试类
mvn test -Dtest=ProductControllerTest
mvn test -Dtest=ProductServiceTest
```

## 项目结构

```
spring-boot-rest-api
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
└── src
    ├── main
    │   ├── java
    │   │   └── com/wjhsky/demo
    │   │       ├── SpringBootRestApiApplication.java   # 启动类
    │   │       ├── controller
    │   │       │   └── ProductController.java          # REST 控制器
    │   │       ├── service
    │   │       │   └── ProductService.java             # 业务逻辑层
    │   │       ├── repository
    │   │       │   └── ProductRepository.java          # JPA Repository
    │   │       ├── model
    │   │       │   └── Product.java                    # 实体类
    │   │       ├── dto
    │   │       │   ├── ProductDTO.java                 # 数据传输对象
    │   │       │   └── PageResponse.java               # 分页响应 DTO
    │   │       └── exception
    │   │           ├── GlobalExceptionHandler.java     # 全局异常处理
    │   │           ├── ResourceNotFoundException.java  # 自定义异常
    │   │           └── ErrorResponse.java              # 错误响应 DTO
    │   └── resources
    │       └── application.yml                         # 配置文件
    └── test
        └── java
            └── com/wjhsky/demo
                ├── controller
                │   └── ProductControllerTest.java     # 控制器测试
                └── service
                    └── ProductServiceTest.java        # 服务层测试
```

## License

[MIT License](./LICENSE) © wjhsky666
