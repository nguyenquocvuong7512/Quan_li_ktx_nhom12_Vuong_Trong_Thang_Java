package com.example.NguyenQuocVuong_chuong4.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LapHoaDonHangLoatDTO {
    @NotBlank(message = "Vui lòng chọn kỳ tháng/năm (vd: 2026-10)")
    private String thang;

    private LocalDate hanThanhToan;

    private BigDecimal tienInternet = new BigDecimal("50000");
    private BigDecimal tienDichVu = new BigDecimal("30000");
    private BigDecimal tienDienMacDinh = BigDecimal.ZERO;
    private BigDecimal tienNuocMacDinh = BigDecimal.ZERO;

    private String ghiChu;
}
