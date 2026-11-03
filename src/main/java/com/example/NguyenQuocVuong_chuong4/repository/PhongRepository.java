package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.Phong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Long> {

    List<Phong> findByTrangThai(String trangThai);

    List<Phong> findByTangId(Long tangId);

    @Query("SELECT p FROM Phong p WHERE (:search IS NULL OR LOWER(p.maPhong) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.loaiPhong) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:loaiPhong IS NULL OR p.loaiPhong = :loaiPhong) " +
           "AND (:trangThai IS NULL OR p.trangThai = :trangThai) " +
           "AND (:tangId IS NULL OR p.tang.id = :tangId)")
    Page<Phong> filterPhongPage(@Param("search") String search,
                                @Param("loaiPhong") String loaiPhong,
                                @Param("trangThai") String trangThai,
                                @Param("tangId") Long tangId,
                                Pageable pageable);
}
