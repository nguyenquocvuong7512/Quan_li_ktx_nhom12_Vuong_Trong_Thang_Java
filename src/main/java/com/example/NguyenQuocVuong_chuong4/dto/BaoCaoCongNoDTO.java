package com.example.NguyenQuocVuong_chuong4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BaoCaoCongNoDTO {
    private BigDecimal tongTienHoaDon = BigDecimal.ZERO;
    private BigDecimal tongDaThu = BigDecimal.ZERO;
    private BigDecimal tongConNo = BigDecimal.ZERO;
    private double tyLeThu = 0.0;

    private long soHoaDon = 0;
    private long soDaThanhToan = 0;
    private long soChuaThanhToan = 0;
    private long soQuaHan = 0;
    private long soThanhToanMotPhan = 0;

    private List<CongNoSinhVienDTO> theoSinhVien = new ArrayList<>();
    private List<CongNoPhongDTO> theoPhong = new ArrayList<>();
    private List<CongNoToaNhaDTO> theoToaNha = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CongNoSinhVienDTO {
        private Long sinhVienId;
        private String maSinhVien;
        private String hoTen;
        private String lop;
        private String soDienThoai;
        private String maPhong;
        private String tenToa;
        private BigDecimal tongTien = BigDecimal.ZERO;
        private BigDecimal daTra = BigDecimal.ZERO;
        private BigDecimal conNo = BigDecimal.ZERO;
        private long soHoaDonChuaTra = 0;
        private long soHoaDonQuaHan = 0;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CongNoPhongDTO {
        private Long phongId;
        private String maPhong;
        private String tenToa;
        private String loaiPhong;
        private Integer soNguoiHienTai;
        private BigDecimal tongTien = BigDecimal.ZERO;
        private BigDecimal daTra = BigDecimal.ZERO;
        private BigDecimal conNo = BigDecimal.ZERO;
        private long soHoaDonNo = 0;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CongNoToaNhaDTO {
        private Long toaNhaId;
        private String maToa;
        private String tenToa;
        private BigDecimal tongTien = BigDecimal.ZERO;
        private BigDecimal daThu = BigDecimal.ZERO;
        private BigDecimal tongNo = BigDecimal.ZERO;
        private double tyLeThu = 0.0;
        private long soHoaDon = 0;
    }
}
