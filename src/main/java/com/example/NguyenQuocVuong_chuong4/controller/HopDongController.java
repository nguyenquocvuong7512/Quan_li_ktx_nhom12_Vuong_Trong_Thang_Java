package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.security.SecurityUtils;
import com.example.NguyenQuocVuong_chuong4.service.HopDongService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long sinhVienId,
            @RequestParam(required = false) Long phongId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        if (SecurityUtils.isStudent()) {
            sinhVienId = SecurityUtils.getCurrentSinhVienId();
        }

        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 10;
            Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(dir, sortBy));
            PageResponse<HopDong> pageResponse = service.getPaginated(trangThai, sinhVienId, phongId, pageable);
            return ResponseEntity.ok(pageResponse);
        }

        if (sinhVienId != null) {
            final Long targetSvId = sinhVienId;
            List<HopDong> list = service.getAll().stream()
                    .filter(h -> h.getSinhVien() != null && targetSvId.equals(h.getSinhVien().getId()))
                    .filter(h -> trangThai == null || trangThai.isBlank() || trangThai.equalsIgnoreCase(h.getTrangThai()))
                    .toList();
            return ResponseEntity.ok(list);
        }

        if (trangThai != null && !trangThai.isBlank()) {
            return ResponseEntity.ok(service.getByTrangThai(trangThai));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public HopDong getById(@PathVariable Long id) {
        HopDong hd = service.getById(id);
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    hd.getSinhVien() != null ? hd.getSinhVien().getId() : null,
                    "Bạn không có quyền xem hợp đồng của sinh viên khác!"
            );
        }
        return hd;
    }

    @PostMapping
    public ResponseEntity<HopDong> create(@Valid @RequestBody HopDong hopDong) {
        if (SecurityUtils.isStudent()) {
            // Student registering room: ensure it's created for themselves
            Long mySvId = SecurityUtils.getCurrentSinhVienId();
            if (mySvId != null && hopDong.getSinhVien() != null) {
                hopDong.getSinhVien().setId(mySvId);
            }
            hopDong.setTrangThai("cho_duyet");
        }
        HopDong saved = service.save(hopDong);
        return ResponseEntity.created(URI.create("/api/hop-dong/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public HopDong update(@PathVariable Long id, @Valid @RequestBody HopDong hopDong) {
        return service.update(id, hopDong);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duyet")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public HopDong duyetHopDong(@PathVariable Long id) {
        return service.duyetHopDong(id);
    }

    @PostMapping("/{id}/tu-choi")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public HopDong tuChoiHopDong(@PathVariable Long id) {
        return service.tuChoiHopDong(id);
    }

    @PostMapping("/{id}/gia-han")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public HopDong thanhLyHopDong(@PathVariable Long id) {
        return service.thanhLyHopDong(id);
    }

    @PostMapping("/{id}/chuyen-phong")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
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
