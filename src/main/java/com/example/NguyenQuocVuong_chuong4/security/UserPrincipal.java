package com.example.NguyenQuocVuong_chuong4.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username; // email or maNguoiDung
    private final String maNguoiDung;
    private final String email;
    private final Long sinhVienId;
    private final String maSinhVien;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String maNguoiDung, String email, Long sinhVienId, String maSinhVien,
            List<String> roles) {
        this.id = id;
        this.username = username;
        this.maNguoiDung = maNguoiDung;
        this.email = email;
        this.sinhVienId = sinhVienId;
        this.maSinhVien = maSinhVien;
        this.authorities = roles.stream()
                .map(role -> {
                    String cleanRole = role.trim().toUpperCase();
                    if (!cleanRole.startsWith("ROLE_")) {
                        cleanRole = "ROLE_" + cleanRole;
                    }
                    return new SimpleGrantedAuthority(cleanRole);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(String role) {
        String targetRole = role.toUpperCase();
        if (!targetRole.startsWith("ROLE_")) {
            targetRole = "ROLE_" + targetRole;
        }
        for (GrantedAuthority auth : authorities) {
            if (auth.getAuthority().equalsIgnoreCase(targetRole)) {
                return true;
            }
        }
        return false;
    }

    public static UserPrincipal create(com.example.NguyenQuocVuong_chuong4.model.NguoiDung user) {
        return create(user, null, null);
    }

    public static UserPrincipal create(com.example.NguyenQuocVuong_chuong4.model.NguoiDung user, Long sinhVienId,
            String maSinhVien) {
        List<String> roleNames = user.getVaiTros() != null && !user.getVaiTros().isEmpty()
                ? user.getVaiTros().stream().map(com.example.NguyenQuocVuong_chuong4.model.VaiTro::getTenVaiTro)
                        .collect(Collectors.toList())
                : List.of("SINH_VIEN");
        return new UserPrincipal(
                user.getId(),
                user.getEmail() != null ? user.getEmail() : user.getMaNguoiDung(),
                user.getMaNguoiDung(),
                user.getEmail(),
                sinhVienId,
                maSinhVien,
                roleNames);
    }
}
