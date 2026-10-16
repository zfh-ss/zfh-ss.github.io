# ========== Build Stage ==========
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========== Runtime Stage ==========
FROM eclipse-temurin:17-jre-alpine
LABEL app="FormCatch"
LABEL description="极简表单收集器"

WORKDIR /app

# H2 数据库持久化目录
RUN mkdir -p /app/data /app/uploads

# 时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# 复制 jar
COPY --from=builder /workspace/target/formcatch-0.1.0.jar /app/app.jar

EXPOSE 8080

# 数据卷（H2 文件 + 上传文件）
VOLUME ["/app/data", "/app/uploads"]

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.datasource.url=jdbc:h2:file:/app/data/formcatch;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE", \
    "-jar", "/app/app.jar"]
