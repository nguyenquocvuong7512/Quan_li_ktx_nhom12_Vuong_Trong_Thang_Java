package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.service.HopDongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/api/hop-dong", "/api/dang-ky-phong", "/api/hopdongthue"})
public class HopDongController {
    private final HopDongService service;

    public HopDongController(HopDongService service) {
        this.service = service;
    }

    @GetMapping
    public List<HopDong> getAll() {
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
}
