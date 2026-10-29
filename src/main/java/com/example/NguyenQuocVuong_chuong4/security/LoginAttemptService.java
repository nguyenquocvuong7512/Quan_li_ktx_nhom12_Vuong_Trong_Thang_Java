package com.example.NguyenQuocVuong_chuong4.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Dịch vụ giám sát và ngăn chặn tấn công dò mật khẩu (Brute-Force Attack Protection).
 * Tự động khóa tạm thời tài khoản/IP nếu nhập sai mật khẩu quá 5 lần liên tiếp.
 */
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final long BLOCK_DURATION_SECONDS = 15 * 60; // 15 phút

    private static class AttemptInfo {
        int attempts;
        Instant firstFailedTime;
        Instant blockedUntil;

        AttemptInfo(int attempts, Instant time) {
            this.attempts = attempts;
            this.firstFailedTime = time;
            this.blockedUntil = null;
        }
    }

    private final ConcurrentMap<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    /**
     * Ghi nhận 1 lần đăng nhập thất bại.
     */
    public void loginFailed(String key) {
        String normalizedKey = normalize(key);
        Instant now = Instant.now();

        attemptsCache.compute(normalizedKey, (k, info) -> {
            if (info == null) {
                return new AttemptInfo(1, now);
            }

            // Nếu block đã hết hạn thì reset lại
            if (info.blockedUntil != null && now.isAfter(info.blockedUntil)) {
                return new AttemptInfo(1, now);
            }

            info.attempts++;
            if (info.attempts >= MAX_ATTEMPTS) {
                info.blockedUntil = now.plusSeconds(BLOCK_DURATION_SECONDS);
            }
            return info;
        });
    }

    /**
     * Ghi nhận đăng nhập thành công -> xóa bỏ bộ đếm thất bại.
     */
    public void loginSucceeded(String key) {
        attemptsCache.remove(normalize(key));
    }

    /**
     * Kiểm tra tài khoản hoặc IP hiện có đang bị khóa (chặn tấn công Brute-force) hay không.
     */
    public boolean isBlocked(String key) {
        String normalizedKey = normalize(key);
        AttemptInfo info = attemptsCache.get(normalizedKey);
        if (info == null || info.blockedUntil == null) {
            return false;
        }

        if (Instant.now().isAfter(info.blockedUntil)) {
            // Hết thời gian khóa, tự động gỡ block
            attemptsCache.remove(normalizedKey);
            return false;
        }
        return true;
    }

    /**
     * Lấy số giây còn lại cần chờ trước khi được thử lại.
     */
    public long getRemainingBlockSeconds(String key) {
        String normalizedKey = normalize(key);
        AttemptInfo info = attemptsCache.get(normalizedKey);
        if (info == null || info.blockedUntil == null) {
            return 0;
        }
        long remaining = info.blockedUntil.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(0, remaining);
    }

    /**
     * Lấy số lần đã nhập sai hiện tại.
     */
    public int getFailedAttempts(String key) {
        AttemptInfo info = attemptsCache.get(normalize(key));
        return info != null ? info.attempts : 0;
    }

    /**
     * Xóa cache (hỗ trợ kiểm thử).
     */
    public void resetAll() {
        attemptsCache.clear();
    }

    private String normalize(String key) {
        return key != null ? key.trim().toLowerCase() : "anonymous";
    }
}
