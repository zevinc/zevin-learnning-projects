# zevin-spring-mvc

Spring MVC 学习子项目，用于系统练习 `WebMvcConfigurer` 的各类扩展点。

## 技术栈

- Java 21
- Gradle Wrapper
- Spring Boot 3.5.9
- Spring MVC
- MockMvc

## 运行

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew bootRun
```

默认端口：`8080`

示例接口：

```bash
curl "http://localhost:8080/?name=Spring%20MVC"
```

## 测试

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
```

当前系统默认 Java 26 会导致 Gradle Kotlin DSL 解析失败，建议显式使用 Java 21。

## 目录结构

```text
src/main/java/com/zevin/springmvc
  ZevinSpringMvcApplication.java
  web/
    GreetingController.java
    GreetingResponse.java

src/test/java/com/zevin/springmvc
  web/
    GreetingControllerTest.java
```
