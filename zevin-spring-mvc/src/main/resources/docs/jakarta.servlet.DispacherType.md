DispatcherType 是 Jakarta Servlet 规范中控制过滤器（Filter）在哪些请求转发类型下生效的枚举。

    枚举值一览（共 6 个）
    
    java
    package jakarta.servlet;
    
    public enum DispatcherType {
        REQUEST,      // 普通客户端请求
        FORWARD,      // 服务端内部转发 (request.getRequestDispatcher().forward())
        INCLUDE,      // 服务端包含 (request.getRequestDispatcher().include())
        ASYNC,        // 异步请求分发 (AsyncContext.dispatch())
        ERROR,        // 错误页面转发 (web.xml <error-page>)
        WEBSOCKET     // WebSocket 升级请求 (since Servlet 6.1)
    }
    
    
    
    
    含义与触发场景
    
    | 枚举值 | 触发条件 | 典型场景 |
    |--------|----------|----------|
    | REQUEST | 客户端直接发起的请求（GET/POST/...） | 默认值，绝大多数请求 |
    | FORWARD | request.getRequestDispatcher("/target").forward() | Controller 内部转发到另一个 URL |
    | INCLUDE | request.getRequestDispatcher("/fragment").include() | JSP <jsp:include> / 页面片段拼装 |
    | ASYNC | AsyncContext.dispatch("/result") | 异步 Servlet 完成后分发结果 |
    | ERROR | 容器根据 <error-page> 或 web.xml 错误映射转发 | 404/500 错误页面 |
    | WEBSOCKET | HTTP Upgrade 请求升级到 WebSocket | WebSocket 握手阶段（Servlet 6.1 新增） |
    
    
    
    为什么需要它？
    
    核心问题：一个 Filter 默认只在 REQUEST 类型下触发。如果请求是内部 forward 过来的，Filter 不会再次执行，可能导致安全检查被绕过。
    
    例子 — 安全漏洞：
    
    
    客户端                  Filter                 Controller A          Controller B
      │                      │                        │                    │
      │──── GET /public ────►│ @/public/*             │                    │
      │                      │ 放行 ✅                 │                    │
      │                      │────────────────────────►                    │
      │                      │                        │ forward("/admin")  │
      │                      │                        │───────────────────►│
      │                      │                        │        Filter 不会再执行！
      │                      │                        │        绕过 /admin 的权限检查 🔴
    
    
    修复：让 Filter 也作用于 FORWARD：
    
    java
    @WebFilter(
        urlPatterns = "/admin/*",
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
    )
    public class AdminFilter implements Filter {
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
            // 检查权限
            chain.doFilter(req, res);
        }
    }
    
    
    或者通过 Spring Boot 的 FilterRegistrationBean 配置：
    
    java
    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilterReg() {
        FilterRegistrationBean<AdminFilter> reg = new FilterRegistrationBean<>(new AdminFilter());
        reg.addUrlPatterns("/admin/*");
        reg.setDispatcherTypes(
            DispatcherType.REQUEST,
            DispatcherType.FORWARD,
            DispatcherType.ASYNC
        );
        return reg;
    }
    
    
    
    
    实际工作流程
    
    当请求到达 Servlet 容器时，容器会：
    
    
    1. 识别本次请求的 DispatcherType 是什么
    2. 遍历所有 Filter，检查其 dispatcherTypes 配置
    3. 只有 dispatcherTypes 包含当前类型的 Filter 才会执行
    
    
    源码层面，Tomcat 的 ApplicationFilterChain 在遍历 Filter 时会这样判断：
    
    java
    // Tomcat 简化伪代码
    for (FilterMap filterMap : filterMaps) {
        if (filterMap.getDispatcherTypes().contains(request.getDispatcherType())) {
            // 执行这个 Filter
            filter.doFilter(request, response, chain);
        }
    }
    
    
    
    
    实战案例
    
    案例 1：Spring Security 的 Filter 配置
    
    Spring Security 默认将所有 Filter 注册为 REQUEST + ASYNC + ERROR + FORWARD + INCLUDE：
    
    java
    // Spring Security 内部源码简化
    registerFilter(springSecurityFilterChain, dispatcherTypes);
    // dispatcherTypes = [REQUEST, ASYNC, ERROR, FORWARD, INCLUDE]
    
    
    这样设计是为了安全最大化：无论请求以何种方式到达，安全过滤器链都会执行。
    
    案例 2：自定义日志 Filter — 避免重复记录
    
    如果你只关心客户端原始请求，不希望 forward/include 触发重复日志：
    
    java
    @Bean
    public FilterRegistrationBean<AccessLogFilter> logFilter() {
        FilterRegistrationBean<AccessLogFilter> reg = new FilterRegistrationBean<>(new AccessLogFilter());
        reg.addUrlPatterns("/*");
        // 只在 REQUEST 下执行，forward/include 不触发（避免重复日志）
        reg.setDispatcherTypes(DispatcherType.REQUEST);
        return reg;
    }
    
    
    案例 3：自定义 Filter 拦截错误页面
    
    java
    // 让 Filter 也能作用于错误页面转发
    reg.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
    
    
    
    
    对比总结
    
    
    DispatcherType               场景                                默认是否生效
    ══════════════════════════════════════════════════════════════════════════════
    REQUEST          浏览器地址栏输入 URL、AJAX 请求                    ✅ 是
    FORWARD          Controller 内部 forward("/other")                  ❌ 否
    INCLUDE          页面碎片 include（JSP <jsp:include>）               ❌ 否
    ASYNC            AsyncContext.dispatch() 分发结果                   ❌ 否
    ERROR            404/500 错误页面转发                               ❌ 否
    WEBSOCKET        WebSocket 升级握手                                 ❌ 否
    
    
    核心规则：Filter 只对 REQUEST 生效是默认行为，同时对 FORWARD / INCLUDE / ASYNC / ERROR 的转发"视而不见"。如果业务要求所有路径都必须经过 Filter 检查，需要显式声明对应的 DispatcherType。
