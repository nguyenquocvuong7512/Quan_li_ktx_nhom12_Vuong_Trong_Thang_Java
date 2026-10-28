package com.example.NguyenQuocVuong_chuong4.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class GhiNhanThanhToanDTO {
    @NotNull(message = "Mã hóa đơn không được để trống")
    private Long khoanThuId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @DecimalMin(value = "1000", message = "Số tiền thanh toán tối thiểu là 1,000 VND")
    private BigDecimal soTien;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThuc = "tien_mat";

    private String maGiaoDich;
    private String ghiChu;
    private String nguoiThu;
}
