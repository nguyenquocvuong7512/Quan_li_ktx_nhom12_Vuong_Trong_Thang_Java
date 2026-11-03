package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.security.SecurityUtils;
import com.example.NguyenQuocVuong_chuong4.service.SinhVienService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/api/sinhvien", "/api/students"})
public class SinhVienController {
    private final SinhVienService service;

    public SinhVienController(SinhVienService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (SecurityUtils.isStudent()) {
            Long mySvId = SecurityUtils.getCurrentSinhVienId();
            if (mySvId != null) {
                return ResponseEntity.ok(List.of(service.getById(mySvId)));
            }
            return ResponseEntity.ok(List.of());
        }

        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 10;
            Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(dir, sortBy));
            return ResponseEntity.ok(service.getPaginated(search, pageable));
        }

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public SinhVien getById(@PathVariable Long id) {
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(id, "Bạn không có quyền xem thông tin của sinh viên khác!");
        }
        return service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public ResponseEntity<SinhVien> create(@Valid @RequestBody SinhVien student) {
        SinhVien saved = service.save(student);
        return ResponseEntity.created(URI.create("/api/sinhvien/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public SinhVien update(@PathVariable Long id, @Valid @RequestBody SinhVien student) {
        return service.update(id, student);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
