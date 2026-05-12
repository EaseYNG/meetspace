# Meetspace 项目回顾



## Spring Security 认证流程

- 抽象层主要类：

  - SecurityContextHolder - 存储用户上下文，与线程绑定

  - SecurityContext - 上下文本身，只存储Authentication对象

  - Authentication - 核心认证接口

    - principal - 主体（UserDetails 实例 - 可实现并自定义）
    - credentials - 凭证（密码，认证后通常清空）
    - authorities - 权限
    - isAuthenticated - 是否已认证

  - AuthenticationManager - 认证管理接口，默认实现ProviderManager（包含一个provider列表，处理登录token）

    - Authentication authenticate(Authentication authentication) throws AuthenticationException 调用时**遍历寻找合适的provider**

  - AuthenticationProvider - 处理不同登录token类型

    - DaoAuthenticationProvider - 查数据库
    - JwtAuthenticationProvider - JWT认证
    - LdapAuthenticationProvider - LDAP认证
    - RememberMeAuthenticationProvider - "记住我"功能

    **负责加载用户、校验密码、返回新的Authentication**

    1. 加载用户

       retrieveUser(username, password) 调用UserDetailsService的loadUserByUsername来获取用户信息

    2. 校验密码

       additionalAuthenticationChecks(UserDetails, token) 用PasswordEncoder.matches()校验密码

    3. 生成新Authentication

       返回isAuthencicated的authentication

  - UserDetailsService - 单一方法 UserDetails loadUserByUsername(username) 加载用户数据，**由AuthenticationProvider调用**

### AuthService 认证流程

```java
// AuthServiceImpl.class
@Autowired
private final AuthenticationManager authenticationManager;

@Override
public CustomUserDetails login(LoginCmd cmd, HttpServletRequest request, HttpServletResponse response) {
    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(cmd.getUsername(), cmd.getPassword());

    Authentication authentication = authenticationManager.authenticate(authToken);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    securityContextRepository.saveContext(context, request, response);

    log.info("User logged in: {}", cmd.getUsername());
    return (CustomUserDetails) authentication.getPrincipal();
}
```

1. 创建认证token

```java
UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(cmd.getUsername(), cmd.getPassword());
```

将前端提供的cmd中的username和password传入，生成token

2. 创建Authentication对象并认证

```java
Authentication authentication = authenticationManager.authenticate(authToken);
// username -> authentication.principle
// password -> authentication.credentials
```

authenticationManager调用authenticationProvider进行认证处理

3. 初始化上下文容器

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
```

4. 将登录上下文存入ContextRepo

```java
securityContextRepository.saveContext(context, request, response);
```

5. 创建SecurityUtil用于获取上下文中的用户信息

```java
public final class SecurityUtil {

    private SecurityUtil() {}

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED, "Not logged in");
    }

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED, "Not logged in");
    }
}
```

6. 注销

清除SecurityContext并使登录会话失效

```java
@Override
public void logout(HttpServletRequest request, HttpServletResponse response) {
    SecurityContextHolder.clearContext();
    request.getSession().invalidate();
    log.info("User logged out");
}
```

## Spring Security 配置

```java
package com.venus.meetspace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .anonymous(anonymous -> anonymous.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/health/**",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    Result<Void> result = Result.fail("Not authenticated", ResultCode.UNAUTHORIZED);
                    new ObjectMapper().writeValue(response.getOutputStream(), result);
                })
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
```

### 解释（未提炼）

# SecurityConfig 逐层详解

### 类注解

- **`@Configuration`**：Spring 配置类，注册所有 `@Bean`
- **`@EnableWebSecurity`**：启用 Spring Security 的 Web 安全支持

`@EnableWebSecurity` 导入 `WebSecurityConfiguration`，它会将声明为 `@Bean` 的 `SecurityFilterChain` 注册到 Spring 的过滤器链中。

------

### @Bean 1: PasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**在认证流程中的位置：**

- **注册时**：`AuthServiceImpl.register()` 调用 `passwordEncoder.encode(明文)` 存储密文
- **登录时**：`DaoAuthenticationProvider` 内部自动调用 `passwordEncoder.matches(明文, 数据库密文)`

> **DaoAuthenticationProvider 如何知道用哪个 PasswordEncoder？**
>
> Spring Boot 自动装配发现容器中唯一的 `PasswordEncoder` bean，注入到 `DaoAuthenticationProvider`。如果你不声明，会抛 `NoSuchBeanDefinitionException`。

------

### @Bean 2: AuthenticationManager

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
}
```

通过 `AuthenticationConfiguration.getAuthenticationManager()` 获取全局的 `ProviderManager`。这个 `ProviderManager` 会自动收集容器中所有 `AuthenticationProvider` bean（包括 Spring Boot 自动注册的 `DaoAuthenticationProvider`）。

**需要显式暴露的原因**：Spring Security 的 `AuthenticationManager` 默认不公开为 bean。本项目在 `AuthServiceImpl` 中通过构造器注入了它，因此需要这个 `@Bean` 方法将其暴露。

------

### @Bean 3: SecurityFilterChain — 核心

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
```

`HttpSecurity` 是 Spring Security 基于 Builder 模式的配置入口。每次 `.xxx(...)` 调用本质上是向一个 `SecurityConfigurer` 列表注册配置器，最终 `http.build()` 遍历这些配置器组装出完整的过滤器链。

#### 3.1 .csrf(csrf -> csrf.disable())

**含义**：关掉 CSRF 保护。

Spring Security 默认启用 CSRF（对 PATCH/POST/PUT/DELETE 要求 `_csrf` token）。RESTful API 通过 Cookie 认证时，CSRF 理论上是有必要的（防止跨站请求伪造）。但项目禁用它的理由是：

- Session 认证的 API，如果前端是真的 SPA（非浏览器页面跳转），CSRF 风险较低
- 前端 Flutter 应用不是浏览器网页，不存在 CSRF 攻击面

**但需要注意**：如果以后有 Web 前端直接浏览器访问，应当启用 CSRF。

#### 3.2 .cors(cors -> cors.configurationSource(corsConfigurationSource()))

**含义**：使用自定义 CORS 配置。

Spring Security 的 CORS 处理和 Spring MVC 的 `@CrossOrigin` 是两个独立层。如果只配了 MVC 的 CORS 而忘了 Security 的 CORS，Security 过滤器链会先拒绝（返回 403）请求，MVC 根本收不到。所以 HTTP Security 层和 Controller 层两层都要配 CORS。

#### 3.3 .anonymous(anonymous -> anonymous.disable())

**含义**：禁用匿名用户。

默认情况下，未认证用户被 Spring Security 视为 `AnonymousAuthenticationToken`，可以访问 `permitAll()` 之外的路径（如果配置允许）。禁用后，未认证用户访问受保护路径直接触发 `AuthenticationEntryPoint`——行为更明确（要么有会话，要么 401），不会出现隐式匿名。

#### 3.4 .authorizeHttpRequests(...)

- `.requestMatchers("/api/v1/auth/", "/api/v1/health/", ...).permitAll()`
- `.anyRequest().authenticated()`

**过滤器链中的次序敏感**：`requestMatchers` 按声明顺序匹配，第一个匹配到的规则生效。所以放行路径必须写在 `anyRequest().authenticated()` 之前。

`permitAll()` 意味着该路径跳过所有认证过滤器，包括匿名和 Session 检查。

#### 3.5 .formLogin(...).httpBasic(...).logout(...)

三者全部 `.disable()`。Spring Security 默认启用 `formLogin`（生成 `/login` 页面端点）、`httpBasic`（Basic Auth 弹窗）、`logout`（生成 `/logout` 端点）。本项目全部 API 化，不需要这些页面端点，所以显式关闭。

#### 3.6 .sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED))

`SessionCreationPolicy` 的三个选项：

- **ALWAYS** → 即使没有认证，每次请求都创建 Session
- **NEVER** → 不主动创建 Session，但如果已有则使用
- **IF_REQUIRED** → 只有需要时才创建（如认证成功时）→ 本项目选择
- **STATELESS** → 无状态，完全不使用 Session → JWT 模式的选择

`IF_REQUIRED` 配合 `HttpSessionSecurityContextRepository.saveContext()` 实现"登录时才创建 Session"。

#### 3.7 .exceptionHandling(ex -> ex.authenticationEntryPoint(...))

覆盖默认行为。默认情况下，Spring Security 发现未认证用户访问受保护资源时：

- 如果配置了 `formLogin` → 重定向到 `/login` 页面
- 否则 → 发送 401 状态码但无 body

本项目替换为自定义 Lambda，返回统一 JSON 格式的 401：

```java
(request, response, authException) -> {
  response.setContentType("application/json;charset=UTF-8");
  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  Result<Void> result = Result.fail("Not authenticated", ResultCode.UNAUTHORIZED);
  new ObjectMapper().writeValue(response.getOutputStream(), result);
}
```

这个 `AuthenticationEntryPoint` 在以下情况触发：

- 访问需要认证的路径但未登录
- Session 已过期
- 请求的 `JSESSIONID` 无效

------

### @Bean 4: CorsConfigurationSource

- `config.setAllowedOriginPatterns(List.of("*"));` // 允许任何源
- `config.setAllowedMethods(List.of("*"));` // 允许任何 HTTP 方法
- `config.setAllowedHeaders(List.of("*"));` // 允许任何请求头
- `config.setAllowCredentials(true);` // 允许携带 Cookie

`allowCredentials(true)` 与 `allowedOriginPatterns("*")` 配合使用。如果 `allowCredentials(true)` 却用 `allowedOrigins("*")`，浏览器会报错（CORS 通配符不允许 `withCredentials`）。所以必须用 `allowedOriginPatterns("*")`。

------

### 各 @Bean 之间隐式依赖关系

- **SecurityFilterChain**
  - 内部引用 `CorsConfigurationSource` bean（通过 `.cors(cors -> ...)`）
  - build 时注册 `SecurityContextHolderFilter`
    - 内部使用 `SecurityContextRepository`
      - `HttpSessionSecurityContextRepository`（默认实现）
- **AuthServiceImpl**
  - `@Autowired AuthenticationManager`
    - `ProviderManager`
      - `@Autowired DaoAuthenticationProvider`
        - `@Autowired CustomUserDetailsService`
        - `@Autowired PasswordEncoder`
  - 创建 `HttpSessionSecurityContextRepository`（直接在代码中 new）

------

### 未显式配置但 Spring Boot 自动装配的组件

| **组件**                                | **自动配置来源**                                             | **在本项目的作用**                              |
| --------------------------------------- | ------------------------------------------------------------ | ----------------------------------------------- |
| **DaoAuthenticationProvider**           | AuthenticationManagerConfiguration                           | 处理 UsernamePasswordAuthenticationToken 的认证 |
| **SecurityContextHolderFilter**         | SecurityFilterAutoConfiguration                              | 每个请求从 Session 恢复 SecurityContext         |
| **WebSecurityConfigurerAdapter 的后继** | 自动检测 SecurityFilterChain bean                            | 替代旧版继承方式                                |
| **UserDetailsService 发现**             | DaoAuthenticationProvider 通过容器找到 CustomUserDetailsService | 自动注入                                        |



## MyBatis-Plus







## Redis



### 缓存穿透

- 缓存击穿指向缓存取一个缓存中不存在的值，从而打到DB。

- 解决方式：

  1. 简单检查：缓存中不存在就从db中查找并加载，如DB中也不存在就给Key赋空值，这样访问缓存只会拿到空值。

     pros：实现简单

     cons：缓存仍然在被访问，并没有减轻缓存压力。

  2. **布隆过滤器（BloomFilter）：**使用布隆过滤器提前添加所有合法值，自动过滤不合法的值。

     pros：防护能力最强、占用内存极小

     cons：有差错可能**（不合法->合法）**

  3. controller加校验：从请求层避免明显的参数错误。



### 缓存击穿

- 缓存穿透指大量并发请求同时向缓存取一个过期的key，此时db瞬间承受极大压力。

- 解决方式：

  1. 分布式锁：只允许一个请求查DB，同时将key重新加载到缓存中。没有获得锁的短暂休眠后重试。

     pros：一致性好

     cons：存在等待，高并发下性能损失

  2. 热点Key永不过期：对于确定的热点数据可以设置不过期

     cons：强一致性场景不适用，数据更新有延迟

  3. 逻辑过期：Key永不过期，给Value设置过期时间，一旦过期就返回旧数据，再从DB异步查新数据

     cons：同样舍弃一点一致性



### 缓存雪崩

- 缓存雪崩指大量Key于同一时间过期，从而大量请求打到DB，或者Redis服务崩溃。

- 解决方式：

  1. 过期时间加抖动：设置key过期时间时加一点抖动，避免同时过期

     pros：简单易用

     cons：Redis宕机时不适用

  2. Redis高可用集群：通过哨兵模式等保证redis不宕机

  3. 服务层限流、熔断、降级
