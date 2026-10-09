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
@Table(name = "khu")
@Getter
@Setter
@NoArgsConstructor
public class Khu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "ma_khu", nullable = false, unique = true, length = 20)
    private String maKhu;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ten_khu", nullable = false, length = 100)
    private String tenKhu;

    @Size(max = 255)
    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(columnDefinition = "TEXT")
    private String moTa;
}
