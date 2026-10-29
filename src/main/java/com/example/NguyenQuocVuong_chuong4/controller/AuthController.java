package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.LoginRequest;
import com.example.NguyenQuocVuong_chuong4.dto.LoginResponse;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.security.LoginAttemptService;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final NguoiDungService service;
    private final LoginAttemptService loginAttemptService;

    public AuthController(NguoiDungService service, LoginAttemptService loginAttemptService) {
        this.service = service;
        this.loginAttemptService = loginAttemptService;
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

    private LoginResponse toResponse(NguoiDung account) {
        return new LoginResponse(account.getId(), account.getMaNguoiDung(),
                account.getHoTen(), account.getEmail());
    }
}
