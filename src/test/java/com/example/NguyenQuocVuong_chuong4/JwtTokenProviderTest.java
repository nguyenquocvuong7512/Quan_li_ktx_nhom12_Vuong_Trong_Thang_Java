package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.model.VaiTro;
import com.example.NguyenQuocVuong_chuong4.security.JwtTokenProvider;
import com.example.NguyenQuocVuong_chuong4.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UserPrincipal createMockPrincipal(String email, String role) {
        NguoiDung user = new NguoiDung();
        user.setId(100L);
        user.setEmail(email);
        user.setMaNguoiDung("USR100");
        user.setHoTen("Test User");
        user.setTrangThai("hoat_dong");

        VaiTro vt = new VaiTro();
        vt.setId(1L);
        vt.setTenVaiTro(role);
        user.setVaiTros(Set.of(vt));

        return UserPrincipal.create(user);
    }

    @Test
    @DisplayName("JWT 1: Tạo token hợp lệ và xác thực chữ ký số")
    void testGenerateAndValidateToken() {
        UserPrincipal principal = createMockPrincipal("admin@ktx.edu.vn", "ADMIN");
        String token = jwtTokenProvider.generateToken(principal);

        assertNotNull(token);
        assertTrue(token.length() > 20);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin@ktx.edu.vn", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("JWT 2: Trích xuất đúng quyền Authorities từ Claims")
    void testExtractRolesFromToken() {
        UserPrincipal principal = createMockPrincipal("sinhvien@ktx.edu.vn", "SINH_VIEN");
        String token = jwtTokenProvider.generateToken(principal);

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_SINH_VIEN", roles.get(0));
    }

    @Test
    @DisplayName("JWT 3: Từ chối token giả mạo hoặc sai chữ ký")
    void testRejectTamperedToken() {
        UserPrincipal principal = createMockPrincipal("user@ktx.edu.vn", "QUAN_LY");
        String token = jwtTokenProvider.generateToken(principal);

        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        assertFalse(jwtTokenProvider.validateToken(tamperedToken), "Token bị sửa đổi chữ ký phải bị từ chối!");
        assertFalse(jwtTokenProvider.validateToken("not.a.valid.jwt.token"));
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken(""));
    }
}
