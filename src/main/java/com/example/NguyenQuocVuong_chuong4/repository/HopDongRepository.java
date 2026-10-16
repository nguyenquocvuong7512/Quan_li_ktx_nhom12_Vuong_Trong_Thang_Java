package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HopDongRepository extends JpaRepository<HopDong, Long> {
    List<HopDong> findByTrangThai(String trangThai);
    List<HopDong> findByPhongId(Long phongId);
    List<HopDong> findBySinhVienId(Long sinhVienId);
    List<HopDong> findByPhongIdAndTrangThai(Long phongId, String trangThai);
    List<HopDong> findBySinhVienIdAndTrangThai(Long sinhVienId, String trangThai);
}

