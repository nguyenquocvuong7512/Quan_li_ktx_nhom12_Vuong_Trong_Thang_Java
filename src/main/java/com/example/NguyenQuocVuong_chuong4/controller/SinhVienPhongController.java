package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.service.HopDongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/sinh-vien-phong", "/api/quan-he-sinh-vien-phong"})
public class SinhVienPhongController {

    private final HopDongRepository hopDongRepository;
    private final HopDongService hopDongService;

    public SinhVienPhongController(HopDongRepository hopDongRepository, HopDongService hopDongService) {
        this.hopDongRepository = hopDongRepository;
        this.hopDongService = hopDongService;
    }

    @GetMapping
    public List<SinhVienPhongDTO> getAllAllocations(@RequestParam(required = false, defaultValue = "dang_hieu_luc") String trangThai) {
        List<HopDong> list = "all".equalsIgnoreCase(trangThai)
                ? hopDongRepository.findAll()
                : hopDongRepository.findByTrangThai(trangThai);

        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/phong/{phongId}")
    public List<SinhVienPhongDTO> getStudentsByRoom(@PathVariable Long phongId) {
        return hopDongRepository.findByPhongIdAndTrangThai(phongId, "dang_hieu_luc")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/sinh-vien/{sinhVienId}")
    public List<SinhVienPhongDTO> getRoomsByStudent(@PathVariable Long sinhVienId) {
        return hopDongRepository.findBySinhVienIdAndTrangThai(sinhVienId, "dang_hieu_luc")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PostMapping("/chuyen-phong")
    public SinhVienPhongDTO transferRoom(@RequestBody Map<String, Long> payload) {
        Long hopDongId = payload.get("hopDongId");
        Long phongMoiId = payload.get("phongMoiId");
        HopDong updated = hopDongService.chuyenPhong(hopDongId, phongMoiId);
        return toDTO(updated);
    }

    @PostMapping("/tra-phong")
    public SinhVienPhongDTO checkOut(@RequestBody Map<String, Long> payload) {
        Long hopDongId = payload.get("hopDongId");
        HopDong updated = hopDongService.thanhLyHopDong(hopDongId);
        return toDTO(updated);
    }

    private SinhVienPhongDTO toDTO(HopDong h) {
        SinhVien sv = h.getSinhVien();
        Phong p = h.getPhong();

        String hoTen = null;
        String email = null;
        String soDienThoai = null;
        if (sv != null && sv.getNguoiDung() != null) {
            hoTen = sv.getNguoiDung().getHoTen();
            email = sv.getNguoiDung().getEmail();
            soDienThoai = sv.getNguoiDung().getSoDienThoai();
        }

        String tenToa = null;
        Integer soTang = null;
        if (p != null && p.getTang() != null) {
            soTang = p.getTang().getSoTang();
            if (p.getTang().getToaNha() != null) {
                tenToa = p.getTang().getToaNha().getTenToa();
            }
        }

        return new SinhVienPhongDTO(
                h.getId(),
                sv != null ? sv.getId() : null,
                sv != null ? sv.getMaSinhVien() : null,
                hoTen,
                email,
                soDienThoai,
                sv != null ? sv.getLop() : null,
                sv != null ? sv.getKhoa() : null,
                p != null ? p.getId() : null,
                p != null ? p.getMaPhong() : null,
                tenToa,
                soTang,
                p != null ? p.getSucChua() : null,
                p != null ? p.getSoNguoiHienTai() : null,
                p != null ? p.getLoaiPhong() : null,
                h.getTienPhong(),
                h.getNgayBatDau(),
                h.getNgayKetThuc(),
                h.getTrangThai()
        );
    }
}
