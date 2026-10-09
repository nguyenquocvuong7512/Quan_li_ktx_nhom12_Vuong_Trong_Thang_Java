package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.ToaNha;
import com.example.NguyenQuocVuong_chuong4.repository.ToaNhaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ToaNhaService {
    private final ToaNhaRepository repository;

    public ToaNhaService(ToaNhaRepository repository) {
        this.repository = repository;
    }

    public List<ToaNha> getAll() {
        return repository.findAll();
    }

    public ToaNha getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tòa nhà"));
    }

    public ToaNha save(ToaNha toaNha) {
        return repository.save(toaNha);
    }

    public ToaNha update(Long id, ToaNha toaNha) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tòa nhà");
        }
        toaNha.setId(id);
        return repository.save(toaNha);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tòa nhà");
        }
        repository.deleteById(id);
    }
}