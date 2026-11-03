package com.example.NguyenQuocVuong_chuong4.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:KTX_NHOM12_VUONG_TRONG_THANG_SECURE_JWT_SECRET_KEY_2026_XYZ_SUPER_SAFE}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        // Ensure secret has enough length for HMAC-SHA256
        String paddedSecret = (secret + "01234567890123456789012345678901").substring(0, 48);
        this.secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserPrincipal principal) {
        List<String> roles = principal.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());
        return generateToken(principal.getUsername(), principal.getId(), principal.getMaNguoiDung(), roles, principal.getSinhVienId(), principal.getMaSinhVien());
    }

    public String generateToken(String username, Long userId, String maNguoiDung, List<String> roles, Long sinhVienId, String maSinhVien) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("maNguoiDung", maNguoiDung)
                .claim("roles", roles)
                .claim("sinhVienId", sinhVienId)
                .claim("maSinhVien", maSinhVien)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return getClaims(token).get("roles", List.class);
    }
}
