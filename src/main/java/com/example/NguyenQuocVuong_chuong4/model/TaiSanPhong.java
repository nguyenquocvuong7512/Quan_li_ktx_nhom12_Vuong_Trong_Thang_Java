package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tai_san_phong")
@Getter
@Setter
@NoArgsConstructor
public class TaiSanPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tai_san_id", nullable = false)
    private TaiSan taiSan;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "phong_id", nullable = false)
    @JsonIgnoreProperties({"tang"})
    private Phong phong;

    @NotNull
    @Positive
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_cap", nullable = false)
    private LocalDate ngayCap = LocalDate.now();

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_thu_hoi")
    private LocalDate ngayThuHoi;

    @NotNull
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "dang_su_dung";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
}
