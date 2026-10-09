package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.service.PhongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public List<Phong> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Phong getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Phong> create(@Valid @RequestBody Phong phong) {
        Phong saved = service.save(phong);
        return ResponseEntity.created(URI.create("/api/phong/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Phong update(@PathVariable Long id, @Valid @RequestBody Phong phong) {
        return service.update(id, phong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
