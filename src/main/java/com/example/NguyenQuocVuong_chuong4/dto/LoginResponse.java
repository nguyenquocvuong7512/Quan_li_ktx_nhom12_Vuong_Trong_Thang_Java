package com.example.NguyenQuocVuong_chuong4.dto;

import java.util.List;

public record LoginResponse(
    Long id,
    String maNguoiDung,
    String hoTen,
    String email,
    List<String> roles,
    String token,
    Long sinhVienId,
    String maSinhVien
) {
    public LoginResponse(Long id, String maNguoiDung, String hoTen, String email) {
        this(id, maNguoiDung, hoTen, email, List.of("ADMIN"), null, null, null);
    }
}
