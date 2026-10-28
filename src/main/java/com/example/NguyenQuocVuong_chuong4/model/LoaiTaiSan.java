package com.example.NguyenQuocVuong_chuong4.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loai_tai_san")
@Getter
@Setter
@NoArgsConstructor
public class LoaiTaiSan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ten_loai", nullable = false, unique = true, length = 100)
    private String tenLoai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    public LoaiTaiSan(Long id, String tenLoai, String moTa) {
        this.id = id;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
    }
}
