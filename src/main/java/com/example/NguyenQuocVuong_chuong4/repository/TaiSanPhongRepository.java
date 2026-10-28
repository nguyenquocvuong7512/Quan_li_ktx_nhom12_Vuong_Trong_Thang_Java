package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.TaiSanPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiSanPhongRepository extends JpaRepository<TaiSanPhong, Long> {

    List<TaiSanPhong> findByPhongId(Long phongId);

    List<TaiSanPhong> findByPhongIdAndTrangThai(Long phongId, String trangThai);

    List<TaiSanPhong> findByTaiSanId(Long taiSanId);

    List<TaiSanPhong> findByTrangThai(String trangThai);

    Optional<TaiSanPhong> findFirstByPhongIdAndTaiSanIdAndTrangThai(Long phongId, Long taiSanId, String trangThai);

    @Query("SELECT tsp FROM TaiSanPhong tsp WHERE (:phongId IS NULL OR tsp.phong.id = :phongId) " +
           "AND (:trangThai IS NULL OR tsp.trangThai = :trangThai) " +
           "AND (:toaNhaId IS NULL OR tsp.phong.tang.toaNha.id = :toaNhaId) " +
           "ORDER BY tsp.id DESC")
    List<TaiSanPhong> filterTaiSanPhong(@Param("phongId") Long phongId,
                                       @Param("trangThai") String trangThai,
                                       @Param("toaNhaId") Long toaNhaId);

    @Query("SELECT COUNT(tsp) FROM TaiSanPhong tsp WHERE tsp.trangThai = :trangThai")
    long countByTrangThai(@Param("trangThai") String trangThai);
}
