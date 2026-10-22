package com.example.NguyenQuocVuong_chuong4.controller;

import java.math.BigDecimal;
import java.util.List;

public record LoaiPhongDTO(
        String tenLoai,
        Integer sucChua,
        BigDecimal giaPhong,
        long tongSoPhong,
        long soPhongConCho,
        long soPhongDaDay,
        List<String> danhSachMaPhong
) {
}
