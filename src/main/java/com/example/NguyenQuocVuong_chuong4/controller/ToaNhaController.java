package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.ToaNha;
import com.example.NguyenQuocVuong_chuong4.service.ToaNhaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/toa-nha")
public class ToaNhaController {
    private final ToaNhaService service;

    public ToaNhaController(ToaNhaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ToaNha> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ToaNha getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ToaNha> create(@Valid @RequestBody ToaNha toaNha) {
        ToaNha saved = service.save(toaNha);
        return ResponseEntity.created(URI.create("/api/toa-nha/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ToaNha update(@PathVariable Long id, @Valid @RequestBody ToaNha toaNha) {
        return service.update(id, toaNha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
