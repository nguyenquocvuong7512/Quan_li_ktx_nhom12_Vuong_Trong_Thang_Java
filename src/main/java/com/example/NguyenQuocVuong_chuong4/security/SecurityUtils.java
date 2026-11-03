package com.example.NguyenQuocVuong_chuong4.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId).orElse(null);
    }

    public static Long getCurrentSinhVienId() {
        return getCurrentUser().map(UserPrincipal::getSinhVienId).orElse(null);
    }

    public static boolean hasRole(String role) {
        return getCurrentUser().map(u -> u.hasRole(role)).orElse(false);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("ROLE_ADMIN");
    }

    public static boolean isManager() {
        return hasRole("QUAN_LY") || hasRole("ROLE_QUAN_LY");
    }

    public static boolean isStaff() {
        return isAdmin() || isManager();
    }

    public static boolean isStudent() {
        return hasRole("SINH_VIEN") || hasRole("ROLE_SINH_VIEN");
    }

    public static void validateOwnershipOrStaff(Long targetSinhVienId, String errorMessage) {
        if (isStaff()) {
            return; // Admins and managers have full access
        }
        Long currentSvId = getCurrentSinhVienId();
        if (currentSvId == null || !currentSvId.equals(targetSinhVienId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage != null ? errorMessage : "Bạn không có quyền truy cập dữ liệu của sinh viên khác!");
        }
    }
}

