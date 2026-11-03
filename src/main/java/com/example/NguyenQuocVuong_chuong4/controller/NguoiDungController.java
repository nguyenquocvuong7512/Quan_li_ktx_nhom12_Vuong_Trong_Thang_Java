package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
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
@RequestMapping({"/api/tai-khoan", "/api/nguoi-dung", "/api/users"})
@PreAuthorize("hasRole('ADMIN')")
public class NguoiDungController {
    private final NguoiDungService service;

    public NguoiDungController(NguoiDungService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 10;
            Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(dir, sortBy));
            return ResponseEntity.ok(service.getPaginated(search, pageable));
        }

        return ResponseEntity.ok(service.getAll());
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
