package com.example.NguyenQuocVuong_chuong4.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CapPhatThietBiDTO {
    @NotNull(message = "Vui lòng chọn phòng cần cấp phát thiết bị")
    private Long phongId;

    @NotNull(message = "Vui lòng chọn thiết bị / tài sản")
    private Long taiSanId;

    @NotNull(message = "Số lượng cấp phát không được để trống")
    @Positive(message = "Số lượng cấp phát phải lớn hơn 0")
    private Integer soLuong = 1;

    private LocalDate ngayCap = LocalDate.now();
    private String ghiChu;
    private Long nguoiThucHienId;
}
