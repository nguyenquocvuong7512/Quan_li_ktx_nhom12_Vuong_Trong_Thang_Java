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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_tai_san")
@Getter
@Setter
@NoArgsConstructor
public class LichSuTaiSan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tai_san_id", nullable = false)
    private TaiSan taiSan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_id")
    @JsonIgnoreProperties({"tang"})
    private Phong phong;

    @NotBlank
    @Size(max = 50)
    @Column(name = "loai_giao_dich", nullable = false, length = 50)
    private String loaiGiaoDich;

    @NotNull
    @Positive
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nguoi_thuc_hien_id")
    @JsonIgnoreProperties({"matKhau"})
    private NguoiDung nguoiThucHien;

    @PrePersist
    public void prePersist() {
        if (this.thoiGian == null) {
            this.thoiGian = LocalDateTime.now();
        }
    }
}
