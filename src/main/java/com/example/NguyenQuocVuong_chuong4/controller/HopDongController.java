package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.service.HopDongService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/hop-dong", "/api/dang-ky-phong", "/api/hopdongthue"})
public class HopDongController {
    private final HopDongService service;

    public HopDongController(HopDongService service) {
        this.service = service;
    }

    @GetMapping
    public List<HopDong> getAll(@RequestParam(required = false) String trangThai) {
        if (trangThai != null && !trangThai.isBlank()) {
            return service.getByTrangThai(trangThai);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HopDong getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<HopDong> create(@Valid @RequestBody HopDong hopDong) {
        HopDong saved = service.save(hopDong);
        return ResponseEntity.created(URI.create("/api/hop-dong/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public HopDong update(@PathVariable Long id, @Valid @RequestBody HopDong hopDong) {
        return service.update(id, hopDong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duyet")
    public HopDong duyetHopDong(@PathVariable Long id) {
        return service.duyetHopDong(id);
    }

    @PostMapping("/{id}/tu-choi")
    public HopDong tuChoiHopDong(@PathVariable Long id) {
        return service.tuChoiHopDong(id);
    }

    @PostMapping("/{id}/gia-han")
    public HopDong giaHanHopDong(@PathVariable Long id,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayKetThuc,
                                 @RequestBody(required = false) Map<String, String> body) {
        LocalDate newDate = ngayKetThuc;
        if (newDate == null && body != null && body.containsKey("ngayKetThuc")) {
            newDate = LocalDate.parse(body.get("ngayKetThuc"));
        }
        return service.giaHanHopDong(id, newDate);
    }

    @PostMapping("/{id}/thanh-ly")
    public HopDong thanhLyHopDong(@PathVariable Long id) {
        return service.thanhLyHopDong(id);
    }

    @PostMapping("/{id}/chuyen-phong")
    public HopDong chuyenPhong(@PathVariable Long id,
                               @RequestParam(required = false) Long phongMoiId,
                               @RequestBody(required = false) Map<String, Long> body) {
        Long targetRoomId = phongMoiId;
        if (targetRoomId == null && body != null && body.containsKey("phongMoiId")) {
            targetRoomId = body.get("phongMoiId");
        }
        return service.chuyenPhong(id, targetRoomId);
    }
}
