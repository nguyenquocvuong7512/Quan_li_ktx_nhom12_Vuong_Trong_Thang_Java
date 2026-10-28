package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

    List<ThanhToan> findByKhoanThuIdOrderByThoiGianDesc(Long khoanThuId);

    List<ThanhToan> findAllByOrderByThoiGianDesc();
}
