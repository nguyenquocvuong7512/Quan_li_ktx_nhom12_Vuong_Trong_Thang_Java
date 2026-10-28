package com.example.NguyenQuocVuong_chuong4.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class HoaDonRequestDTO {
    private Long id;

    @NotNull(message = "Vui lòng chọn sinh viên")
    private Long sinhVienId;

    private Long hopDongId;
    private Long phongId;

    private String thang;
    private String loaiKhoanThu;
    private LocalDate hanThanhToan;

    private BigDecimal tienPhong = BigDecimal.ZERO;
    private BigDecimal tienDien = BigDecimal.ZERO;
    private Integer chiSoDienCu = 0;
    private Integer chiSoDienMoi = 0;

    private BigDecimal tienNuoc = BigDecimal.ZERO;
    private Integer chiSoNuocCu = 0;
    private Integer chiSoNuocMoi = 0;

    private BigDecimal tienInternet = BigDecimal.ZERO;
    private BigDecimal tienDichVu = BigDecimal.ZERO;

    private String ghiChu;
}
