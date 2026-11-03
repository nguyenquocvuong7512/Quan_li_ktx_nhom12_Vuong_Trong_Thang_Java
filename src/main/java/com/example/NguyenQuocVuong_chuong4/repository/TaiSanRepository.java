package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.TaiSan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, Long> {
    Optional<TaiSan> findByMaTaiSan(String maTaiSan);
    boolean existsByMaTaiSan(String maTaiSan);
    List<TaiSan> findByLoaiTaiSanId(Long loaiTaiSanId);
    List<TaiSan> findByTrangThai(String trangThai);

    @Query("SELECT t FROM TaiSan t WHERE (:search IS NULL OR LOWER(t.maTaiSan) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.tenTaiSan) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:loaiTaiSanId IS NULL OR t.loaiTaiSan.id = :loaiTaiSanId) " +
           "AND (:trangThai IS NULL OR t.trangThai = :trangThai)")
    Page<TaiSan> filterTaiSanPage(@Param("search") String search,
                                  @Param("loaiTaiSanId") Long loaiTaiSanId,
                                  @Param("trangThai") String trangThai,
                                  Pageable pageable);
}
