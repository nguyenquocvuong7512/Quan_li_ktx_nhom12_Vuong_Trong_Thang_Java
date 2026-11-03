package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Long> {
    List<HopDong> findByTrangThai(String trangThai);
    List<HopDong> findByPhongId(Long phongId);
    List<HopDong> findBySinhVienId(Long sinhVienId);
    List<HopDong> findByPhongIdAndTrangThai(Long phongId, String trangThai);
    List<HopDong> findBySinhVienIdAndTrangThai(Long sinhVienId, String trangThai);

    @Query("SELECT h FROM HopDong h WHERE (:trangThai IS NULL OR h.trangThai = :trangThai) " +
           "AND (:sinhVienId IS NULL OR h.sinhVien.id = :sinhVienId) " +
           "AND (:phongId IS NULL OR h.phong.id = :phongId)")
    Page<HopDong> filterHopDongPage(@Param("trangThai") String trangThai,
                                    @Param("sinhVienId") Long sinhVienId,
                                    @Param("phongId") Long phongId,
                                    Pageable pageable);
}
