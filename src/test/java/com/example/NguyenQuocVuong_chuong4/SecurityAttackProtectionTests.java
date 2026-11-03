package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.controller.AuthController;
import com.example.NguyenQuocVuong_chuong4.dto.LoginRequest;
import com.example.NguyenQuocVuong_chuong4.dto.LoginResponse;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.repository.NguoiDungRepository;
import com.example.NguyenQuocVuong_chuong4.security.LoginAttemptService;
import com.example.NguyenQuocVuong_chuong4.security.SecurityHeadersFilter;
import com.example.NguyenQuocVuong_chuong4.security.XssProtectionUtil;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử chứng minh khả năng phòng chống các cuộc tấn công mạng:
 * 1. Chống Brute-force / Dictionary Attack (Khóa tài khoản sau 5 lần sai).
 * 2. Chống SQL Injection (Tham số hóa truy vấn Spring Data JPA).
 * 3. Chống Cross-Site Scripting (XSS Sanitizer & HTML Escaping).
 * 4. Chống Clickjacking & MIME Sniffing qua Security Headers.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SecurityAttackProtectionTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;

    @BeforeEach
    void setUp() {
        loginAttemptService.resetAll();
    }

    @Test
    @DisplayName("Security 1: Chặn tấn công Brute-force - Khóa tài khoản sau 5 lần đăng nhập sai liên tiếp")
    void testBruteForceAttackDefense() {
        String testUser = "victim_account@ktx.edu.vn";
        String wrongPassword = "wrong_password_attempt";

        // Thử sai 5 lần liên tiếp -> Mỗi lần bị từ chối với 401 UNAUTHORIZED
        for (int i = 1; i <= 5; i++) {
            final int attempt = i;
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                authController.login(new LoginRequest(testUser, wrongPassword));
            }, "Lần thứ " + attempt + " phải ném ResponseStatusException");

            if (attempt < 5) {
                assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
            } else {
                // Lần thứ 5 vừa đạt ngưỡng -> kích hoạt khóa
                assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getStatusCode());
            }
        }

        // Lần thứ 6: Bị chặn ngay lập tức với mã HTTP 429 TOO_MANY_REQUESTS
        ResponseStatusException blockedEx = assertThrows(ResponseStatusException.class, () -> {
            authController.login(new LoginRequest(testUser, wrongPassword));
        });
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, blockedEx.getStatusCode());
        assertTrue(blockedEx.getReason().contains("chống Brute-force"));
        assertTrue(loginAttemptService.isBlocked(testUser));

        // Kiểm tra sau khi reset (hoặc đăng nhập thành công) -> Hết bị khóa
        loginAttemptService.loginSucceeded(testUser);
        assertFalse(loginAttemptService.isBlocked(testUser));
    }

    @Test
    @DisplayName("Security 2: Chặn tấn công SQL Injection qua Spring Data JPA Parameterized Query")
    void testSqlInjectionDefense() {
        // Payload tấn công SQL Injection kinh điển nhằm bypass authentication hoặc trích xuất toàn bộ dữ liệu
        String sqlInjectionPayload = "' OR '1'='1' --";

        Optional<NguoiDung> result = nguoiDungRepository.findByEmailIgnoreCaseOrMaNguoiDung(sqlInjectionPayload, sqlInjectionPayload);
        // Do JPA sử dụng PreparedStatements tham số hóa, payload được xem là literal string -> Trả về empty an toàn
        assertFalse(result.isPresent(), "SQL Injection payload không được phép bypass truy vấn CSDL!");
    }

    @Test
    @DisplayName("Security 3: Chặn tấn công Cross-Site Scripting (XSS) qua Sanitizer và HTML Escaping")
    void testXssProtection() {
        // Payload chứa mã độc JavaScript
        String maliciousScript = "<script>alert('Stealing Cookies!')</script>Nguyễn Văn Thắng";
        String sanitized = XssProtectionUtil.sanitize(maliciousScript);
        assertEquals("Nguyễn Văn Thắng", sanitized, "Thẻ script độc hại phải bị loại bỏ hoàn toàn!");

        // Payload chứa sự kiện inline onload/onerror
        String inlineEventPayload = "<img src='invalid.jpg' onerror='maliciousFunction()'>";
        String sanitizedImg = XssProtectionUtil.sanitize(inlineEventPayload);
        assertFalse(sanitizedImg.contains("onerror"), "Sự kiện onerror độc hại phải bị vô hiệu hóa!");

        // HTML escaping
        String unescaped = "<div class=\"test\">&'</div>";
        String escaped = XssProtectionUtil.escapeHtml(unescaped);
        assertTrue(escaped.contains("&lt;div"), "Ký tự < phải được chuyển thành &lt;");
        assertTrue(escaped.contains("&amp;"), "Ký tự & phải được chuyển thành &amp;");
    }

    @Test
    @DisplayName("Security 4: Kiểm tra các Security Headers bảo vệ Clickjacking & MIME-type Sniffing")
    void testSecurityHeadersFilter() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        securityHeadersFilter.doFilter(request, response, chain);

        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
        assertEquals("SAMEORIGIN", response.getHeader("X-Frame-Options"));
        assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
    }
}

