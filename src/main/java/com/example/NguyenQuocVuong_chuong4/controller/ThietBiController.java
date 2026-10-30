package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.BaoHongThietBiDTO;
import com.example.NguyenQuocVuong_chuong4.dto.CapPhatThietBiDTO;
import com.example.NguyenQuocVuong_chuong4.dto.ThietBiStatsDTO;
import com.example.NguyenQuocVuong_chuong4.model.LichSuTaiSan;
import com.example.NguyenQuocVuong_chuong4.model.LoaiTaiSan;
import com.example.NguyenQuocVuong_chuong4.model.TaiSan;
import com.example.NguyenQuocVuong_chuong4.model.TaiSanPhong;
import com.example.NguyenQuocVuong_chuong4.service.ThietBiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thiet-bi")
public class ThietBiController {

    private final ThietBiService thietBiService;

    public ThietBiController(ThietBiService thietBiService) {
        this.thietBiService = thietBiService;
    }

    // ================= 1. DANH MỤC THIẾT BỊ =================
    @GetMapping
    public ResponseEntity<List<TaiSan>> getAllTaiSan() {
        return ResponseEntity.ok(thietBiService.getAllTaiSan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaiSan> getTaiSanById(@PathVariable Long id) {
        return ResponseEntity.ok(thietBiService.getTaiSanById(id));
    }

    @PostMapping
    public ResponseEntity<TaiSan> createTaiSan(@RequestBody TaiSan taiSan) {
        TaiSan saved = thietBiService.saveTaiSan(taiSan);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaiSan> updateTaiSan(@PathVariable Long id, @RequestBody TaiSan taiSan) {
        return ResponseEntity.ok(thietBiService.updateTaiSan(id, taiSan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaiSan(@PathVariable Long id) {
        thietBiService.deleteTaiSan(id);
        return ResponseEntity.noContent().build();
    }

    // ================= 2. QUẢN LÝ THIẾT BỊ TRONG PHÒNG =================
    @GetMapping("/phong")
    public ResponseEntity<List<TaiSanPhong>> getTaiSanPhong(
            @RequestParam(required = false) Long phongId,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long toaNhaId) {
        return ResponseEntity.ok(thietBiService.getTaiSanPhong(phongId, trangThai, toaNhaId));
    }

    @GetMapping("/phong/{id}")
    public ResponseEntity<TaiSanPhong> getTaiSanPhongById(@PathVariable Long id) {
        return ResponseEntity.ok(thietBiService.getTaiSanPhongById(id));
    }

    @PostMapping("/cap-phat")
    public ResponseEntity<TaiSanPhong> capPhatChoPhong(@Valid @RequestBody CapPhatThietBiDTO dto) {
        TaiSanPhong created = thietBiService.capPhatChoPhong(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/bao-hong")
    public ResponseEntity<TaiSanPhong> baoHong(@Valid @RequestBody BaoHongThietBiDTO dto) {
        TaiSanPhong updated = thietBiService.baoHong(dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/sua-chua/{id}")
    public ResponseEntity<TaiSanPhong> suaChua(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        String ghiChu = payload != null && payload.containsKey("ghiChu") ? (String) payload.get("ghiChu") : null;
        Long nguoiThucHienId = null;
        if (payload != null && payload.get("nguoiThucHienId") != null) {
            nguoiThucHienId = Long.valueOf(payload.get("nguoiThucHienId").toString());
        }
        return ResponseEntity.ok(thietBiService.hoanTatSuaChua(id, ghiChu, nguoiThucHienId));
    }

    @PostMapping("/thu-hoi/{id}")
    public ResponseEntity<TaiSanPhong> thuHoi(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        String lyDo = payload != null && payload.containsKey("lyDo") ? (String) payload.get("lyDo") : null;
        Long nguoiThucHienId = null;
        if (payload != null && payload.get("nguoiThucHienId") != null) {
            nguoiThucHienId = Long.valueOf(payload.get("nguoiThucHienId").toString());
        }
        return ResponseEntity.ok(thietBiService.thuHoi(id, lyDo, nguoiThucHienId));
    }

    @DeleteMapping("/phong/{id}")
    public ResponseEntity<Void> xoaTrangBiPhong(@PathVariable Long id) {
        thietBiService.xoaTrangBiPhong(id);
        return ResponseEntity.noContent().build();
    }

    // ================= 3. LOẠI TÀI SẢN =================
    @GetMapping("/loai")
    public ResponseEntity<List<LoaiTaiSan>> getAllLoaiTaiSan() {
        return ResponseEntity.ok(thietBiService.getAllLoaiTaiSan());
    }

    @PostMapping("/loai")
    public ResponseEntity<LoaiTaiSan> createLoaiTaiSan(@RequestBody LoaiTaiSan loai) {
        return ResponseEntity.status(HttpStatus.CREATED).body(thietBiService.createLoaiTaiSan(loai));
    }

    // Cập nhật loại tài sản
    @PutMapping("/loai/{id}")
    public ResponseEntity<LoaiTaiSan> updateLoaiTaiSan(
            @PathVariable Long id,
            @RequestBody LoaiTaiSan loai) {
        return ResponseEntity.ok(thietBiService.updateLoaiTaiSan(id, loai));
    }

    // Xóa loại tài sản
    @DeleteMapping("/loai/{id}")
    public ResponseEntity<Void> deleteLoaiTaiSan(@PathVariable Long id) {
        thietBiService.deleteLoaiTaiSan(id);
        return ResponseEntity.noContent().build();
    }

    // ================= 4. LỊCH SỬ GIAO DỊCH =================
    @GetMapping("/lich-su")
    public ResponseEntity<List<LichSuTaiSan>> getLichSu(
            @RequestParam(required = false) Long phongId,
            @RequestParam(required = false) Long taiSanId) {
        return ResponseEntity.ok(thietBiService.getLichSu(phongId, taiSanId));
    }

    // ================= 5. THỐNG KÊ =================
    @GetMapping("/stats")
    public ResponseEntity<ThietBiStatsDTO> getStats() {
        return ResponseEntity.ok(thietBiService.getStats());
    }
}