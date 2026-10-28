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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Getter
@Setter
@NoArgsConstructor
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "khoan_thu_id", nullable = false)
    @JsonIgnoreProperties({"hopDong"})
    private KhoanThu khoanThu;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @NotBlank
    @Size(max = 50)
    @Column(name = "phuong_thuc", nullable = false, length = 50)
    private String phuongThuc = "tien_mat";

    @Size(max = 100)
    @Column(name = "ma_giao_dich", length = 100, unique = true)
    private String maGiaoDich;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @NotBlank
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "thanh_cong";

    @Size(max = 255)
    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Size(max = 100)
    @Column(name = "nguoi_thu", length = 100)
    private String nguoiThu;

    @PrePersist
    public void prePersist() {
        if (this.thoiGian == null) {
            this.thoiGian = LocalDateTime.now();
        }
    }
}
