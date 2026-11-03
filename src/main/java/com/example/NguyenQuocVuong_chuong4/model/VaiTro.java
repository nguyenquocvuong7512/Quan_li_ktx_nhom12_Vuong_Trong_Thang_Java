package com.example.NguyenQuocVuong_chuong4.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@NoArgsConstructor
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "ten_vai_tro", nullable = false, unique = true, length = 50)
    private String tenVaiTro;

    @Size(max = 255)
    @Column(name = "mo_ta", length = 255)
    private String moTa;

    public VaiTro(String tenVaiTro, String moTa) {
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
    }
}

