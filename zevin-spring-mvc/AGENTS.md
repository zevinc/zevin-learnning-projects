# Spring MVC WebMvcConfigurer Learning Rules

## Goal

Help me systematically learn all extension points of Spring MVC `WebMvcConfigurer`.

Use Spring Boot defaults unless the current lesson explicitly requires overriding them.

Do not use `@EnableWebMvc` unless explaining full manual MVC takeover.

## Learning Scope

Cover all `WebMvcConfigurer` methods:

1. configurePathMatch
2. configureContentNegotiation
3. configureApiVersioning
4. configureAsyncSupport
5. configureDefaultServletHandling
6. addFormatters
7. addInterceptors
8. addResourceHandlers
9. addCorsMappings
10. addViewControllers
11. configureViewResolvers
12. addArgumentResolvers
13. addReturnValueHandlers
14. configureMessageConverters
15. extendMessageConverters
16. configureHandlerExceptionResolvers
17. extendHandlerExceptionResolvers
18. addErrorResponseInterceptors
19. getValidator
20. getMessageCodesResolver

For Spring Framework 7+, also explain deprecated methods and replacement APIs.

## Answer Format

For each method, always explain:

1. What problem it solves
2. When to use it
3. When not to use it
4. Spring Boot default behavior
5. Minimal runnable example
6. Test case using MockMvc
7. Common pitfalls
8. Relation to DispatcherServlet / HandlerMapping / HandlerAdapter / HandlerExceptionResolver

## Code Rules

Use Java, Spring Boot, Gradle, Lombok only when useful.

Prefer small independent examples.

Every example must be runnable.

Every feature must include:
- config class
- controller or test endpoint
- MockMvc test
- notes in - `~/ai-workspace/docs/ai/`

## Knowledge Capture Rule

After each completed topic, create or update:

docs/ai/spring-mvc/<method-name>.md

Each note must include:
- summary
- key concepts
- source code path
- test path
- common mistakes
- interview-level explanation

Also update:
`~/ai-workspace/docs/ai/INDEX.md`