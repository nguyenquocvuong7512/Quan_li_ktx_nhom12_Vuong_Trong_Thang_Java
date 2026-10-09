package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.Tang;
import com.example.NguyenQuocVuong_chuong4.service.TangService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tang")
public class TangController {
    private final TangService service;

    public TangController(TangService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tang> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Tang getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Tang> create(@Valid @RequestBody Tang tang) {
        Tang saved = service.save(tang);
        return ResponseEntity.created(URI.create("/api/tang/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Tang update(@PathVariable Long id, @Valid @RequestBody Tang tang) {
        return service.update(id, tang);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
