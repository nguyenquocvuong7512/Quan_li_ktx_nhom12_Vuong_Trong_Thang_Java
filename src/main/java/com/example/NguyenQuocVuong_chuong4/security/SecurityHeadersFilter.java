package com.example.NguyenQuocVuong_chuong4.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter bảo vệ ứng dụng Web chống các cuộc tấn công phổ biến:
 * - Clickjacking (X-Frame-Options)
 * - MIME Sniffing (X-Content-Type-Options)
 * - Cross-Site Scripting (X-XSS-Protection & Content-Security-Policy)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResponse.setHeader("Content-Security-Policy",
                    "default-src 'self' 'unsafe-inline' 'unsafe-eval' https: data: blob:; " +
                    "img-src 'self' https: data: blob: https://img.vietqr.io; " +
                    "connect-src 'self' https:;");
        }
        chain.doFilter(request, response);
    }
}

