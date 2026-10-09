package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.Tang;
import com.example.NguyenQuocVuong_chuong4.repository.TangRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TangService {
    private final TangRepository repository;

    public TangService(TangRepository repository) {
        this.repository = repository;
    }

    public List<Tang> getAll() {
        return repository.findAll();
    }

    public Tang getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tầng"));
    }

    public Tang save(Tang tang) {
        validateFloorNumber(tang);
        return repository.save(tang);
    }

    public Tang update(Long id, Tang tang) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tầng");
        }
        tang.setId(id);
        validateFloorNumber(tang);
        return repository.save(tang);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tầng");
        }
        repository.deleteById(id);
    }

    private void validateFloorNumber(Tang tang) {
        if (tang.getToaNha() != null && tang.getSoTang() != null
                && tang.getSoTang() > tang.getToaNha().getSoTang()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số tầng vượt quá số tầng đã khai báo cho tòa nhà");
        }
    }
}
