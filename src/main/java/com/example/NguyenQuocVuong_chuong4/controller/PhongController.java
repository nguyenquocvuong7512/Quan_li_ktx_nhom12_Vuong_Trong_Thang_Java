package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.service.PhongService;
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
@RequestMapping({"/api/phong", "/api/rooms"})
public class PhongController {
    private final PhongService service;

    public PhongController(PhongService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String loaiPhong,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long tangId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 10;
            Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(dir, sortBy));
            PageResponse<Phong> pageResponse = service.getPaginated(search, loaiPhong, trangThai, tangId, pageable);
            return ResponseEntity.ok(pageResponse);
        }

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public Phong getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public ResponseEntity<Phong> create(@Valid @RequestBody Phong phong) {
        Phong saved = service.save(phong);
        return ResponseEntity.created(URI.create("/api/phong/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public Phong update(@PathVariable Long id, @Valid @RequestBody Phong phong) {
        return service.update(id, phong);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
