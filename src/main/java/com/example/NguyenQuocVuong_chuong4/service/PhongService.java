package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PhongService {
    private final PhongRepository repository;

    public PhongService(PhongRepository repository) {
        this.repository = repository;
    }

    public List<Phong> getAll() {
        return repository.findAll();
    }

    public Phong getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
    }

    public Phong save(Phong phong) {
        validateCapacity(phong);
        return repository.save(phong);
    }

    public Phong update(Long id, Phong phong) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng");
        }
        phong.setId(id);
        validateCapacity(phong);
        return repository.save(phong);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng");
        }
        repository.deleteById(id);
    }

    private void validateCapacity(Phong phong) {
        if (phong.getSucChua() != null && phong.getSoNguoiHienTai() != null
                && phong.getSoNguoiHienTai() > phong.getSucChua()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số người hiện tại không được vượt quá sức chứa");
        }
    }
}
