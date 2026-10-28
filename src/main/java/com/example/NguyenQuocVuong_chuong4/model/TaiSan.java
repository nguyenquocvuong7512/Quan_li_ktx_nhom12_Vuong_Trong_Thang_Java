package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tai_san")
@Getter
@Setter
@NoArgsConstructor
public class TaiSan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "loai_tai_san_id", nullable = false)
    private LoaiTaiSan loaiTaiSan;

    @NotBlank
    @Size(max = 30)
    @Column(name = "ma_tai_san", nullable = false, unique = true, length = 30)
    private String maTaiSan;

    @NotBlank
    @Size(max = 150)
    @Column(name = "ten_tai_san", nullable = false, length = 150)
    private String tenTaiSan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_nhap")
    private LocalDate ngayNhap;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "gia_tri", nullable = false, precision = 14, scale = 2)
    private BigDecimal giaTri = BigDecimal.ZERO;

    @NotBlank
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "dang_su_dung";

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
}
