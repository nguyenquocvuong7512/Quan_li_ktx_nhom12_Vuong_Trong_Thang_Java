package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.service.SinhVienService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sinhvien")
public class SinhVienController {
    private final SinhVienService service;

    public SinhVienController(SinhVienService service) {
        this.service = service;
    }

    @GetMapping
    public List<SinhVien> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SinhVien getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<SinhVien> create(@Valid @RequestBody SinhVien student) {
        SinhVien saved = service.save(student);
        return ResponseEntity.created(URI.create("/api/sinhvien/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public SinhVien update(@PathVariable Long id, @Valid @RequestBody SinhVien student) {
        return service.update(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
