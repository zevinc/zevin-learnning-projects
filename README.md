# zevin-learnning-projects

Java / Spring Boot 技术栈学习项目集，每个子项目聚焦一个独立的技术主题。

## 项目结构

| 子项目 | 技术主题 | 构建工具 | Java 版本 |
|--------|----------|----------|-----------|
| `zevin-jdk/` | JDK 基础 API 与特性 | Gradle | - |
| `zevin-spring-mvc/` | Spring MVC WebMvcConfigurer 扩展点 | Gradle (Spring Boot 3.5.14) | 21 |
| `zevin-spring-ai/` | Spring AI 教程：ChatClient / Tool Calling / RAG / Agent | Gradle (Spring Boot 3.5.14 / Spring AI 1.1.6) | 21 |

## 使用方式

每个子项目是独立的 Gradle 项目，可单独用 IntelliJ IDEA 打开其目录。

```bash
# 进入某个子项目
cd zevin-spring-mvc

# 编译
./gradlew build

# 运行测试
./gradlew test
```

## 学习笔记

详见各子项目内的 `src/` 目录及 `~/ai-workspace/docs/ai/` 知识库。
