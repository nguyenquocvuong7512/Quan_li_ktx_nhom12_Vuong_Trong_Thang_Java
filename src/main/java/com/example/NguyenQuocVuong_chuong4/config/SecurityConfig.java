package com.example.NguyenQuocVuong_chuong4.config;

import com.example.NguyenQuocVuong_chuong4.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Bạn cần đăng nhập (Token JWT không hợp lệ hoặc đã hết hạn) để thực hiện thao tác này!\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Bạn không có quyền truy cập chức năng này (Quyền không đủ)!\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Static resources and html pages
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/landing.html",
                    "/*.html",
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                // Auth APIs
                .requestMatchers("/api/auth/**").permitAll()
                // Payment callbacks & IPN
                .requestMatchers("/api/payment/vnpay-return", "/api/payment/vnpay-ipn", "/api/payment/**").permitAll()
                // Actuator & H2
                .requestMatchers("/actuator/**", "/h2-console/**").permitAll()

                // RBAC Protected APIs
                // 1. Quản lý người dùng: Chỉ ADMIN
                .requestMatchers("/api/nguoi-dung/**", "/api/users/**").hasRole("ADMIN")

                // 2. Các thao tác quản trị hợp đồng, hóa đơn, tài sản: ADMIN & QUAN_LY
                .requestMatchers(HttpMethod.POST, "/api/hoa-don/lap-hang-loat").hasAnyRole("ADMIN", "QUAN_LY")
                .requestMatchers("/api/hoa-don/bao-cao/**").hasAnyRole("ADMIN", "QUAN_LY")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN", "QUAN_LY")

                // 3. Toàn bộ API còn lại yêu cầu xác thực
                .requestMatchers("/api/**").authenticated()

                // Cho phép các request khác (như trang web)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

