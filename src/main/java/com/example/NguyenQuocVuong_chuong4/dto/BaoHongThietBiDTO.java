package com.example.NguyenQuocVuong_chuong4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaoHongThietBiDTO {
    @NotNull(message = "ID trang bị phòng không được để trống")
    private Long taiSanPhongId;

    @NotBlank(message = "Vui lòng nhập lý do / mô tả tình trạng hư hỏng")
    private String lyDo;

    private String trangThaiMoi = "hong_hoc"; // hong_hoc, can_bao_tri, dang_sua_chua, da_sua_xong
    private Long nguoiThucHienId;
}
