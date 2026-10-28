package com.example.NguyenQuocVuong_chuong4.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PhieuThuDTO {
    private String soPhieu;
    private String maHoaDon;
    private String thang;
    private LocalDateTime ngayThu;

    private String maSinhVien;
    private String hoTenSinhVien;
    private String lop;
    private String soDienThoai;

    private String maPhong;
    private String tenToa;

    private BigDecimal tienPhong = BigDecimal.ZERO;
    private BigDecimal tienDien = BigDecimal.ZERO;
    private Integer chiSoDienCu;
    private Integer chiSoDienMoi;

    private BigDecimal tienNuoc = BigDecimal.ZERO;
    private Integer chiSoNuocCu;
    private Integer chiSoNuocMoi;

    private BigDecimal tienInternet = BigDecimal.ZERO;
    private BigDecimal tienDichVu = BigDecimal.ZERO;

    private BigDecimal tongTienHoaDon = BigDecimal.ZERO;
    private BigDecimal soTienThanhToan = BigDecimal.ZERO;
    private BigDecimal daThanhToanLuyKe = BigDecimal.ZERO;
    private BigDecimal conLai = BigDecimal.ZERO;

    private String phuongThuc;
    private String maGiaoDich;
    private String nguoiThu;
    private String ghiChu;
    private String tongTienChu;
}
