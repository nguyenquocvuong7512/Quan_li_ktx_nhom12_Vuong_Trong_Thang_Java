package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.LoginRequest;
import com.example.NguyenQuocVuong_chuong4.dto.LoginResponse;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.model.VaiTro;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import com.example.NguyenQuocVuong_chuong4.security.JwtTokenProvider;
import com.example.NguyenQuocVuong_chuong4.security.LoginAttemptService;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final NguoiDungService service;
    private final LoginAttemptService loginAttemptService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SinhVienRepository sinhVienRepository;

    public AuthController(NguoiDungService service,
                          LoginAttemptService loginAttemptService,
                          JwtTokenProvider jwtTokenProvider,
                          SinhVienRepository sinhVienRepository) {
        this.service = service;
        this.loginAttemptService = loginAttemptService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sinhVienRepository = sinhVienRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String identifier = request.identifier();

        // 1. Kiểm tra cơ chế chống tấn công Brute-force
        if (loginAttemptService.isBlocked(identifier)) {
            long remainingSeconds = loginAttemptService.getRemainingBlockSeconds(identifier);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Tài khoản bị tạm khóa do nhập sai mật khẩu quá 5 lần liên tiếp (Cơ chế chống Brute-force). Vui lòng thử lại sau " + remainingSeconds + " giây!"
            );
        }

        // 2. Xác thực tài khoản
        Optional<NguoiDung> authenticated = service.authenticate(identifier, request.password());

        if (authenticated.isPresent()) {
            // Đăng nhập thành công -> Xóa bộ đếm sai
            loginAttemptService.loginSucceeded(identifier);
            return ResponseEntity.ok(toResponse(authenticated.get()));
        } else {
            // Đăng nhập thất bại -> Ghi nhận 1 lần sai
            loginAttemptService.loginFailed(identifier);
            int failed = loginAttemptService.getFailedAttempts(identifier);
            int remaining = Math.max(0, LoginAttemptService.MAX_ATTEMPTS - failed);

            if (loginAttemptService.isBlocked(identifier)) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Tài khoản vừa bị khóa do nhập sai mật khẩu quá 5 lần liên tiếp (Chống Brute-force). Vui lòng thử lại sau 15 phút!"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Tài khoản hoặc mật khẩu không chính xác! (Còn " + remaining + " lần thử trước khi bị tạm khóa)"
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa cung cấp Token JWT hợp lệ!");
        }
        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT không hợp lệ hoặc đã hết hạn!");
        }
        String username = jwtTokenProvider.getUsernameFromToken(token);
        Optional<NguoiDung> userOpt = service.getAll().stream()
                .filter(u -> username.equalsIgnoreCase(u.getEmail()) || username.equalsIgnoreCase(u.getMaNguoiDung()))
                .findFirst();
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản!");
        }
        return ResponseEntity.ok(toResponse(userOpt.get(), token));
    }

    private LoginResponse toResponse(NguoiDung account) {
        return toResponse(account, null);
    }

    private LoginResponse toResponse(NguoiDung account, String existingToken) {
        // Lấy danh sách vai trò
        List<String> roles = new ArrayList<>();
        if (account.getVaiTros() != null && !account.getVaiTros().isEmpty()) {
            for (VaiTro vt : account.getVaiTros()) {
                String roleName = vt.getTenVaiTro().toUpperCase();
                // Map aliases (quan_tri -> ADMIN, quan_ly_ky_tuc -> QUAN_LY, sinh_vien -> SINH_VIEN)
                if ("QUAN_TRI".equals(roleName) || "ADMIN".equals(roleName)) {
                    roles.add("ADMIN");
                } else if ("QUAN_LY_KY_TUC".equals(roleName) || "QUAN_LY".equals(roleName)) {
                    roles.add("QUAN_LY");
                } else if ("SINH_VIEN".equals(roleName)) {
                    roles.add("SINH_VIEN");
                } else {
                    roles.add(roleName);
                }
            }
        }

        // Kiểm tra xem người dùng có phải là sinh viên
        Long sinhVienId = null;
        String maSinhVien = null;
        Optional<SinhVien> svOpt = sinhVienRepository.findByNguoiDungId(account.getId());
        if (svOpt.isPresent()) {
            SinhVien sv = svOpt.get();
            sinhVienId = sv.getId();
            maSinhVien = sv.getMaSinhVien();
            if (!roles.contains("SINH_VIEN")) {
                roles.add("SINH_VIEN");
            }
        }

        if (roles.isEmpty()) {
            roles.add("ADMIN"); // Default fallback
        }

        String token = existingToken;
        if (token == null || token.isBlank()) {
            token = jwtTokenProvider.generateToken(
                    account.getEmail(),
                    account.getId(),
                    account.getMaNguoiDung(),
                    roles,
                    sinhVienId,
                    maSinhVien
            );
        }

        return new LoginResponse(
                account.getId(),
                account.getMaNguoiDung(),
                account.getHoTen(),
                account.getEmail(),
                roles,
                token,
                sinhVienId,
                maSinhVien
        );
    }
}
