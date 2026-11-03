package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "ma_nguoi_dung", nullable = false, unique = true, length = 30)
    private String maNguoiDung;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Size(min = 6, max = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    @Size(max = 20)
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Size(max = 20)
    @Column(name = "gioi_tinh", length = 20)
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @NotBlank
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "hoat_dong";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "nguoi_dung_vai_tro",
        joinColumns = @JoinColumn(name = "nguoi_dung_id"),
        inverseJoinColumns = @JoinColumn(name = "vai_tro_id")
    )
    private Set<VaiTro> vaiTros = new HashSet<>();
}
