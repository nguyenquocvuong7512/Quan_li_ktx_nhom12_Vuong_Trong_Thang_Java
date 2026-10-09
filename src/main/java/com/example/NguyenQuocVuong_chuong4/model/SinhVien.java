package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sinh_vien")
@Getter
@Setter
@NoArgsConstructor
public class SinhVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nguoi_dung_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"matKhau"})
    private NguoiDung nguoiDung;

    @NotBlank
    @Size(max = 30)
    @Column(name = "ma_sinh_vien", nullable = false, unique = true, length = 30)
    private String maSinhVien;

    @Size(max = 50)
    @Column(length = 50)
    private String lop;

    @Size(max = 100)
    @Column(length = 100)
    private String khoa;

    @Size(max = 100)
    @Column(length = 100)
    private String nganh;

    @Positive
    @Column(name = "khoa_hoc")
    private Integer khoaHoc;

    @Size(max = 150)
    @Column(name = "que_quan", length = 150)
    private String queQuan;
}
