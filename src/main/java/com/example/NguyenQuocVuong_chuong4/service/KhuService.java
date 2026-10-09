package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.Khu;
import com.example.NguyenQuocVuong_chuong4.repository.KhuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KhuService {
    private final KhuRepository repository;

    public KhuService(KhuRepository repository) {
        this.repository = repository;
    }

    public List<Khu> getAll() {
        return repository.findAll();
    }

    public Khu getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khu"));
    }

    public Khu save(Khu khu) {
        return repository.save(khu);
    }

    public Khu update(Long id, Khu khu) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khu");
        }
        khu.setId(id);
        return repository.save(khu);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khu");
        }
        repository.deleteById(id);
    }
}
