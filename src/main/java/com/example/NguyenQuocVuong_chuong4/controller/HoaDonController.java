package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.BaoCaoCongNoDTO;
import com.example.NguyenQuocVuong_chuong4.dto.GhiNhanThanhToanDTO;
import com.example.NguyenQuocVuong_chuong4.dto.HoaDonRequestDTO;
import com.example.NguyenQuocVuong_chuong4.dto.LapHoaDonHangLoatDTO;
import com.example.NguyenQuocVuong_chuong4.dto.PhieuThuDTO;
import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.model.ThanhToan;
import com.example.NguyenQuocVuong_chuong4.service.KhoanThuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final KhoanThuService service;

    public HoaDonController(KhoanThuService service) {
        this.service = service;
    }

    @GetMapping
    public List<KhoanThu> getAll(
            @RequestParam(required = false) String thang,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long sinhVienId,
            @RequestParam(required = false) Long phongId,
            @RequestParam(required = false) String search) {
        return service.getAll(thang, trangThai, sinhVienId, phongId, search);
    }

    @GetMapping("/{id}")
    public KhoanThu getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KhoanThu create(@Valid @RequestBody HoaDonRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public KhoanThu update(@PathVariable Long id, @Valid @RequestBody HoaDonRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/lap-hang-loat")
    public Map<String, Object> lapHoaDonHangLoat(@Valid @RequestBody LapHoaDonHangLoatDTO dto) {
        return service.lapHoaDonHangLoat(dto);
    }

    @PostMapping("/thanh-toan")
    public ThanhToan ghiNhanThanhToan(@Valid @RequestBody GhiNhanThanhToanDTO dto) {
        return service.ghiNhanThanhToan(dto);
    }

    @GetMapping("/{id}/lich-su-thanh-toan")
    public List<ThanhToan> getLichSuThanhToan(@PathVariable Long id) {
        return service.getLichSuThanhToan(id);
    }

    @GetMapping("/bao-cao")
    public BaoCaoCongNoDTO getBaoCao(
            @RequestParam(required = false) String thang,
            @RequestParam(required = false) Long toaNhaId) {
        return service.getBaoCaoCongNo(thang, toaNhaId);
    }

    @GetMapping("/{id}/phieu-thu")
    public PhieuThuDTO getPhieuThu(
            @PathVariable Long id,
            @RequestParam(required = false) Long thanhToanId) {
        return service.getChiTietPhieuThu(id, thanhToanId);
    }
}
