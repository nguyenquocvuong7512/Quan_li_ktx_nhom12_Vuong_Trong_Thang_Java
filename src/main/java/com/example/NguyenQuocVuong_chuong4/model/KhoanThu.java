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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "khoan_thu")
@Getter
@Setter
@NoArgsConstructor
public class KhoanThu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    @Column(name = "ma_hoa_don", length = 50, unique = true)
    private String maHoaDon;

    @Size(max = 20)
    @Column(name = "thang", length = 20)
    private String thang;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sinh_vien_id", nullable = false)
    @JsonIgnoreProperties({"nguoiDung"})
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hop_dong_id")
    @JsonIgnoreProperties({"sinhVien", "phong"})
    private HopDong hopDong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_id")
    @JsonIgnoreProperties({"tang"})
    private Phong phong;

    @NotNull
    @Size(max = 50)
    @Column(name = "loai_khoan_thu", nullable = false, length = 50)
    private String loaiKhoanThu = "hoa_don_thang";

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tien_phong", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienPhong = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tien_dien", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienDien = BigDecimal.ZERO;

    @Column(name = "chi_so_dien_cu")
    private Integer chiSoDienCu = 0;

    @Column(name = "chi_so_dien_moi")
    private Integer chiSoDienMoi = 0;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tien_nuoc", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienNuoc = BigDecimal.ZERO;

    @Column(name = "chi_so_nuoc_cu")
    private Integer chiSoNuocCu = 0;

    @Column(name = "chi_so_nuoc_moi")
    private Integer chiSoNuocMoi = 0;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tien_internet", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienInternet = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tien_dich_vu", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienDichVu = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "da_thanh_toan", nullable = false, precision = 12, scale = 2)
    private BigDecimal daThanhToan = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "con_lai", nullable = false, precision = 12, scale = 2)
    private BigDecimal conLai = BigDecimal.ZERO;

    @Column(name = "han_thanh_toan")
    private LocalDate hanThanhToan;

    @NotNull
    @Size(max = 30)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "chua_thanh_toan";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        if (tienPhong == null) tienPhong = BigDecimal.ZERO;
        if (tienDien == null) tienDien = BigDecimal.ZERO;
        if (tienNuoc == null) tienNuoc = BigDecimal.ZERO;
        if (tienInternet == null) tienInternet = BigDecimal.ZERO;
        if (tienDichVu == null) tienDichVu = BigDecimal.ZERO;
        if (daThanhToan == null) daThanhToan = BigDecimal.ZERO;

        this.soTien = tienPhong.add(tienDien).add(tienNuoc).add(tienInternet).add(tienDichVu);
        this.conLai = this.soTien.subtract(this.daThanhToan);
        if (this.conLai.compareTo(BigDecimal.ZERO) < 0) {
            this.conLai = BigDecimal.ZERO;
        }

        // Tự động cập nhật trạng thái nếu không cưỡng chế
        if (this.daThanhToan.compareTo(BigDecimal.ZERO) > 0 && this.conLai.compareTo(BigDecimal.ZERO) <= 0) {
            this.trangThai = "da_thanh_toan";
        } else if (this.daThanhToan.compareTo(BigDecimal.ZERO) > 0 && this.conLai.compareTo(BigDecimal.ZERO) > 0) {
            if (this.hanThanhToan != null && LocalDate.now().isAfter(this.hanThanhToan)) {
                this.trangThai = "qua_han";
            } else {
                this.trangThai = "thanh_toan_mot_phan";
            }
        } else {
            if (this.hanThanhToan != null && LocalDate.now().isAfter(this.hanThanhToan)) {
                this.trangThai = "qua_han";
            } else {
                this.trangThai = "chua_thanh_toan";
            }
        }
    }
}
