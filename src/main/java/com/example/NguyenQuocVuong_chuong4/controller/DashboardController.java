package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.NguoiDungRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ToaNhaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final SinhVienRepository sinhVienRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final PhongRepository phongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HopDongRepository hopDongRepository;

    public DashboardController(SinhVienRepository sinhVienRepository,
                               ToaNhaRepository toaNhaRepository,
                               PhongRepository phongRepository,
                               NguoiDungRepository nguoiDungRepository,
                               HopDongRepository hopDongRepository) {
        this.sinhVienRepository = sinhVienRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.phongRepository = phongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.hopDongRepository = hopDongRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsDTO getStats() {
        long totalStudents = sinhVienRepository.count();
        long totalBuildings = toaNhaRepository.count();
        List<Phong> rooms = phongRepository.findAll();
        long totalRooms = rooms.size();

        long totalRoomTypes = rooms.stream()
                .map(Phong::getLoaiPhong)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .count();

        long availableRooms = rooms.stream()
                .filter(p -> "con_cho".equalsIgnoreCase(p.getTrangThai()))
                .count();

        long occupiedRooms = rooms.stream()
                .filter(p -> "da_day".equalsIgnoreCase(p.getTrangThai())
                        || (p.getSucChua() != null && p.getSoNguoiHienTai() != null
                        && p.getSoNguoiHienTai() >= p.getSucChua()))
                .count();

        long totalAccounts = nguoiDungRepository.count();

        List<HopDong> contracts = hopDongRepository.findAll();
        long totalContracts = contracts.size();
        long activeContracts = contracts.stream()
                .filter(h -> "dang_hieu_luc".equalsIgnoreCase(h.getTrangThai()))
                .count();

        return new DashboardStatsDTO(
                totalStudents,
                totalBuildings,
                totalRooms,
                totalRoomTypes,
                availableRooms,
                occupiedRooms,
                totalAccounts,
                totalContracts,
                activeContracts
        );
    }
}
