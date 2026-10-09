package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "phong")
@Getter
@Setter
@NoArgsConstructor
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tang_id", nullable = false)
    @JsonIgnoreProperties({"toaNha"})
    private Tang tang;

    @NotBlank
    @Size(max = 30)
    @Column(name = "ma_phong", nullable = false, unique = true, length = 30)
    private String maPhong;

    @NotNull
    @Positive
    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @NotNull
    @PositiveOrZero
    @Column(name = "so_nguoi_hien_tai", nullable = false)
    private Integer soNguoiHienTai = 0;

    @NotBlank
    @Size(max = 50)
    @Column(name = "loai_phong", nullable = false, length = 50)
    private String loaiPhong;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "gia_phong", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaPhong = BigDecimal.ZERO;

    @NotBlank
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "con_cho";
}
