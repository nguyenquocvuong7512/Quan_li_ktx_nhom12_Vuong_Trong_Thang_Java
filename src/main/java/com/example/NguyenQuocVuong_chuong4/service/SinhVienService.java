package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SinhVienService {
    private final SinhVienRepository repository;

    public SinhVienService(SinhVienRepository repository) {
        this.repository = repository;
    }

    public List<SinhVien> getAll() {
        return repository.findAll();
    }

    public PageResponse<SinhVien> getPaginated(String search, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<SinhVien> page = repository.searchPage(cleanSearch, pageable);
        return PageResponse.from(page);
    }

    public SinhVien getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));
    }

    public SinhVien save(SinhVien student) {
        return repository.save(student);
    }

    public SinhVien update(Long id, SinhVien student) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
        }
        student.setId(id);
        return repository.save(student);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
        }
        repository.deleteById(id);
    }
}
