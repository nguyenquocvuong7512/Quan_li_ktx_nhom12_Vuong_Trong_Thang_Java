package com.example.NguyenQuocVuong_chuong4.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SinhVienPhongDTO(
        Long hopDongId,
        Long sinhVienId,
        String maSinhVien,
        String hoTen,
        String email,
        String soDienThoai,
        String lop,
        String khoa,
        Long phongId,
        String maPhong,
        String tenToa,
        Integer soTang,
        Integer sucChua,
        Integer soNguoiHienTai,
        String loaiPhong,
        BigDecimal tienPhong,
        LocalDate ngayBatDau,
        LocalDate ngayKetThuc,
        String trangThaiHopDong
) {
}
