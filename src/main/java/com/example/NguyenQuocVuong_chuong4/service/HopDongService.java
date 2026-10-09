package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HopDongService {
    private final HopDongRepository repository;

    public HopDongService(HopDongRepository repository) {
        this.repository = repository;
    }

    public List<HopDong> getAll() {
        return repository.findAll();
    }

    public HopDong getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng"));
    }

    public HopDong save(HopDong hopDong) {
        validateDates(hopDong);
        return repository.save(hopDong);
    }

    public HopDong update(Long id, HopDong hopDong) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng");
        }
        hopDong.setId(id);
        validateDates(hopDong);
        return repository.save(hopDong);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng");
        }
        repository.deleteById(id);
    }

    private void validateDates(HopDong hopDong) {
        if (hopDong.getNgayBatDau() != null && hopDong.getNgayKetThuc() != null
                && hopDong.getNgayKetThuc().isBefore(hopDong.getNgayBatDau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }
    }
}
