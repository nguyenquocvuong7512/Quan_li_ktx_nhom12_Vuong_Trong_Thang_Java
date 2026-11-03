package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.DashboardStatsDTO;
import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.KhoanThuRepository;
import com.example.NguyenQuocVuong_chuong4.repository.NguoiDungRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ToaNhaRepository;
import com.example.NguyenQuocVuong_chuong4.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final KhoanThuRepository khoanThuRepository;

    public DashboardController(SinhVienRepository sinhVienRepository,
                               ToaNhaRepository toaNhaRepository,
                               PhongRepository phongRepository,
                               NguoiDungRepository nguoiDungRepository,
                               HopDongRepository hopDongRepository,
                               KhoanThuRepository khoanThuRepository) {
        this.sinhVienRepository = sinhVienRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.phongRepository = phongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.hopDongRepository = hopDongRepository;
        this.khoanThuRepository = khoanThuRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsDTO getStats() {
        // Nếu là tài khoản Sinh viên: chỉ trả về số liệu cá nhân của chính mình để bảo mật
        if (SecurityUtils.isStudent()) {
            Long mySvId = SecurityUtils.getCurrentSinhVienId();
            List<HopDong> myContracts = (mySvId != null) ? hopDongRepository.findBySinhVienId(mySvId) : List.of();
            long totalContracts = myContracts.size();
            long activeContracts = myContracts.stream().filter(h -> "dang_hieu_luc".equalsIgnoreCase(h.getTrangThai())).count();

            List<KhoanThu> myInvoices = (mySvId != null) ? khoanThuRepository.findBySinhVienId(mySvId) : List.of();
            long totalInvoices = myInvoices.size();
            long unpaidInvoices = myInvoices.stream().filter(k -> !"da_thanh_toan".equalsIgnoreCase(k.getTrangThai())).count();
            long overdueInvoices = myInvoices.stream()
                    .filter(k -> "qua_han".equalsIgnoreCase(k.getTrangThai())
                            || (!"da_thanh_toan".equalsIgnoreCase(k.getTrangThai())
                            && k.getHanThanhToan() != null && LocalDate.now().isAfter(k.getHanThanhToan())))
                    .count();

            BigDecimal totalRevenue = myInvoices.stream()
                    .map(k -> k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDebt = myInvoices.stream()
                    .map(k -> k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalRooms = phongRepository.count();
            long availableRooms = phongRepository.findByTrangThai("con_cho").size();

            return new DashboardStatsDTO(
                    1,
                    1,
                    totalRooms,
                    1,
                    availableRooms,
                    totalRooms - availableRooms,
                    1,
                    totalContracts,
                    activeContracts,
                    totalInvoices,
                    unpaidInvoices,
                    overdueInvoices,
                    totalRevenue,
                    totalDebt
            );
        }

        // Với ADMIN và QUAN_LY: trả về số liệu toàn trường
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

        List<KhoanThu> invoices = khoanThuRepository.findAll();
        long totalInvoices = invoices.size();
        long unpaidInvoices = invoices.stream()
                .filter(k -> !"da_thanh_toan".equalsIgnoreCase(k.getTrangThai()))
                .count();
        long overdueInvoices = invoices.stream()
                .filter(k -> "qua_han".equalsIgnoreCase(k.getTrangThai())
                        || (!"da_thanh_toan".equalsIgnoreCase(k.getTrangThai())
                        && k.getHanThanhToan() != null && LocalDate.now().isAfter(k.getHanThanhToan())))
                .count();

        BigDecimal totalRevenue = invoices.stream()
                .map(k -> k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebt = invoices.stream()
                .map(k -> k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardStatsDTO(
                totalStudents,
                totalBuildings,
                totalRooms,
                totalRoomTypes,
                availableRooms,
                occupiedRooms,
                totalAccounts,
                totalContracts,
                activeContracts,
                totalInvoices,
                unpaidInvoices,
                overdueInvoices,
                totalRevenue,
                totalDebt
        );
    }
}
