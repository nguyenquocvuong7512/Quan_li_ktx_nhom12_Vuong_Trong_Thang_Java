package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tai-khoan")
public class NguoiDungController {
    private final NguoiDungService service;

    public NguoiDungController(NguoiDungService service) {
        this.service = service;
    }

    @GetMapping
    public List<NguoiDung> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NguoiDung getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<NguoiDung> create(@Valid @RequestBody NguoiDung account) {
        NguoiDung saved = service.save(account);
        return ResponseEntity.created(URI.create("/api/tai-khoan/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public NguoiDung update(@PathVariable Long id, @Valid @RequestBody NguoiDung account) {
        return service.update(id, account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
