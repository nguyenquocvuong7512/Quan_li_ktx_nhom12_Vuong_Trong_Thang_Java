package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {
    Optional<SinhVien> findByNguoiDungId(Long nguoiDungId);
    Optional<SinhVien> findByMaSinhVien(String maSinhVien);

    @Query("SELECT s FROM SinhVien s WHERE (:search IS NULL OR LOWER(s.maSinhVien) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.nguoiDung.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.lop) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.khoa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SinhVien> searchPage(@Param("search") String search, Pageable pageable);
}
