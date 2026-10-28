package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.LoaiTaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaiTaiSanRepository extends JpaRepository<LoaiTaiSan, Long> {
    Optional<LoaiTaiSan> findByTenLoai(String tenLoai);
    boolean existsByTenLoai(String tenLoai);
}
