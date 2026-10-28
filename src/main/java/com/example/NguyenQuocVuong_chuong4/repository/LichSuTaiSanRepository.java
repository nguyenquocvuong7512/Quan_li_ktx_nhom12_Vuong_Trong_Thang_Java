package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.LichSuTaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuTaiSanRepository extends JpaRepository<LichSuTaiSan, Long> {
    List<LichSuTaiSan> findByPhongIdOrderByThoiGianDesc(Long phongId);
    List<LichSuTaiSan> findByTaiSanIdOrderByThoiGianDesc(Long taiSanId);
    List<LichSuTaiSan> findAllByOrderByThoiGianDesc();
}
