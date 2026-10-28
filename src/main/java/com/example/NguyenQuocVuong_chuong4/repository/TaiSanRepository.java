package com.example.NguyenQuocVuong_chuong4.repository;

import com.example.NguyenQuocVuong_chuong4.model.TaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, Long> {
    Optional<TaiSan> findByMaTaiSan(String maTaiSan);
    boolean existsByMaTaiSan(String maTaiSan);
    List<TaiSan> findByLoaiTaiSanId(Long loaiTaiSanId);
    List<TaiSan> findByTrangThai(String trangThai);
}
