# FormCatch

> 极简表单收集器 · 自己部署版的 Google Forms / 金数据 / 腾讯问卷

一个单文件 H2 数据库驱动的 Spring Boot 应用，0 依赖外部中间件，下载即跑。

## ✨ 功能

### 核心
- 创建表单：标题、说明、字段（9 种类型）
- 发布表单：自动生成 `/f/{slug}` 公开链接
- 填写表单：必填校验、邮箱/数字/日期格式校验、选项白名单
- 提交保存：动态内容 JSON 存进 H2
- 后台管理：表单列表、搜索、编辑、关闭、删除
- 提交管理：列表、分页、详情、删除单条
- 一键导出 CSV（UTF-8 BOM，Excel 友好）

### 进阶
- 截止时间（`datetime-local` 调度）
- 名额限制（超限即关闭）
- 防重复提交：Cookie / IP / 邮箱
- 访问密码
- 开关状态

### 字段类型
单行文本 / 多行文本 / 数字 / 日期 / 邮箱 / 单选 / 多选 / 下拉框 / 文件上传（预留）

## 🛠 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.3.x |
| Spring Data JPA | Hibernate 6.5 |
| H2 Database | 文件模式 |
| Thymeleaf + Bootstrap 5 | CDN |
| Java | 17 |
| Maven | 3.9 |

## 🚀 快速开始

### 要求
- JDK 17+
- Maven 3.9+（或直接 `java -jar`）

### 运行

```bash
# 编译打包
mvn package -DskipTests

# 启动（默认端口 8080）
java -jar target/formcatch-0.1.0.jar
```

### 访问
| 路径 | 说明 |
|------|------|
| `http://localhost:8080/admin` | 管理后台 |
| `http://localhost:8080/admin/forms/new` | 创建表单 |
| `http://localhost:8080/f/{slug}` | 公开填写页面 |
| `http://localhost:8080/h2-console` | H2 数据库控制台（JDBC URL: `jdbc:h2:file:./data/formcatch`，用户 `sa`，密码空）|

## 📡 API

### 保存字段（整体替换）
```
POST /admin/forms/{id}/fields
Content-Type: application/json

[
  {"name":"name","label":"姓名","type":"TEXT","required":true,"sortOrder":0},
  {"name":"email","label":"邮箱","type":"EMAIL","required":true,"sortOrder":1}
]
```

### JSON 方式提交表单
```
POST /f/{slug}/api
Content-Type: application/json

{"name":"张三","email":"zhangsan@test.com"}
```

### 导出 CSV
```
GET /f/{slug}/export
```

## 🗂 项目结构

```
formcatch/
├── pom.xml
├── src/main/java/com/formcatch/
│   ├── FormCatchApplication.java
│   ├── config/WebConfig.java
│   ├── controller/
│   │   ├── AdminController.java       # 管理后台页面
│   │   └── PublicFormController.java  # 公开填写 + 提交
│   ├── entity/
│   │   ├── Form.java
│   │   ├── FormField.java
│   │   ├── FieldType.java
│   │   └── Submission.java
│   ├── repository/
│   ├── service/
│   │   ├── FormService.java
│   │   ├── SubmissionService.java
│   │   └── CsvExportService.java
│   └── util/SlugGenerator.java
└── src/main/resources/
    ├── application.yml
    ├── static/css/style.css
    └── templates/
        ├── layout.html
        ├── admin/        # 管理后台模板
        ├── public/       # 公开填写模板
        └── error/        # 错误页
```

## 🗄 数据模型

```
Form        ── 1:N ──> FormField
 │
 └── 1:N ──> Submission.dataJson  (动态字段值，JSON 格式)
```

## ⚙ 配置

全部配置在 `src/main/resources/application.yml` 中，关键项：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `server.port` | 端口 | 8080 |
| `spring.datasource.url` | H2 文件路径 | `./data/formcatch` |
| `formcatch.dedup-strategies` | 防重复策略 | `cookie,ip` |
| `formcatch.max-submissions-per-ip-per-day` | 单 IP 每日上限 | 50 |

## 🧪 测试

```bash
mvn test
```

包含表单 CRUD、字段添加、提交校验、防重复、开关状态的集成测试。

## 🐳 Docker（可选）

```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/formcatch-0.1.0.jar /app.jar
VOLUME /app/data
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t formcatch .
docker run -p 8080:8080 -v $(pwd)/data:/app/data formcatch
```

## 📝 License

MIT
