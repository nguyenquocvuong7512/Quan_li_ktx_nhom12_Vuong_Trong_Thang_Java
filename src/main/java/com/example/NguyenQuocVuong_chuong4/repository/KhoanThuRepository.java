package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhoanThuRepository extends JpaRepository<KhoanThu, Long> {

    Optional<KhoanThu> findByMaHoaDon(String maHoaDon);

    boolean existsByMaHoaDon(String maHoaDon);

    List<KhoanThu> findByThang(String thang);

    List<KhoanThu> findByTrangThai(String trangThai);

    List<KhoanThu> findBySinhVienId(Long sinhVienId);

    List<KhoanThu> findByPhongId(Long phongId);

    @Query("SELECT k FROM KhoanThu k WHERE (:thang IS NULL OR k.thang = :thang) " +
           "AND (:trangThai IS NULL OR k.trangThai = :trangThai) " +
           "AND (:sinhVienId IS NULL OR k.sinhVien.id = :sinhVienId) " +
           "AND (:phongId IS NULL OR k.phong.id = :phongId) " +
           "ORDER BY k.id DESC")
    List<KhoanThu> filterKhoanThu(@Param("thang") String thang,
                                 @Param("trangThai") String trangThai,
                                 @Param("sinhVienId") Long sinhVienId,
                                 @Param("phongId") Long phongId);

    @Query("SELECT k FROM KhoanThu k WHERE k.trangThai != 'da_thanh_toan' AND k.hanThanhToan < :today")
    List<KhoanThu> findOverdueInvoices(@Param("today") LocalDate today);

    boolean existsBySinhVienIdAndThangAndLoaiKhoanThu(Long sinhVienId, String thang, String loaiKhoanThu);
}
