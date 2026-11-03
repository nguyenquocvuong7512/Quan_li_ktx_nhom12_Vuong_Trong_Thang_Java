package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByEmailIgnoreCaseOrMaNguoiDung(String email, String maNguoiDung);

    @Query("SELECT n FROM NguoiDung n WHERE (:search IS NULL OR LOWER(n.maNguoiDung) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(n.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(n.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<NguoiDung> searchPage(@Param("search") String search, Pageable pageable);
}
