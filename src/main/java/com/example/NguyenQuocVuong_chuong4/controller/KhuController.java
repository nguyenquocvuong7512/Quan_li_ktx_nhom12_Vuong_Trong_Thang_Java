package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.Khu;
import com.example.NguyenQuocVuong_chuong4.service.KhuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/khu")
public class KhuController {
    private final KhuService service;

    public KhuController(KhuService service) {
        this.service = service;
    }

    @GetMapping
    public List<Khu> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Khu getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Khu> create(@Valid @RequestBody Khu khu) {
        Khu saved = service.save(khu);
        return ResponseEntity.created(URI.create("/api/khu/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Khu update(@PathVariable Long id, @Valid @RequestBody Khu khu) {
        return service.update(id, khu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
