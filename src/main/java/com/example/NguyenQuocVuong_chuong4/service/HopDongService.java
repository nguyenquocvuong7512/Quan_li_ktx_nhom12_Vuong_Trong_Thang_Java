package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class HopDongService {
    private final HopDongRepository repository;
    private final PhongRepository phongRepository;

    public HopDongService(HopDongRepository repository, PhongRepository phongRepository) {
        this.repository = repository;
        this.phongRepository = phongRepository;
    }

    public List<HopDong> getAll() {
        return repository.findAll();
    }

    public List<HopDong> getByTrangThai(String trangThai) {
        return repository.findByTrangThai(trangThai);
    }

    public HopDong getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng"));
    }

    @Transactional
    public HopDong save(HopDong hopDong) {
        validateDates(hopDong);
        if (hopDong.getTrangThai() == null || hopDong.getTrangThai().isBlank()) {
            hopDong.setTrangThai("cho_duyet");
        }
        if ("dang_hieu_luc".equalsIgnoreCase(hopDong.getTrangThai()) && hopDong.getPhong() != null) {
            Phong phong = phongRepository.findById(hopDong.getPhong().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
            if (phong.getSoNguoiHienTai() >= phong.getSucChua()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng này đã đầy, không thể thêm hợp đồng hiệu lực!");
            }
            phong.setSoNguoiHienTai(phong.getSoNguoiHienTai() + 1);
            if (phong.getSoNguoiHienTai() >= phong.getSucChua()) {
                phong.setTrangThai("da_day");
            }
            phongRepository.save(phong);
            hopDong.setPhong(phong);
        }
        return repository.save(hopDong);
    }

    @Transactional
    public HopDong update(Long id, HopDong hopDong) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng");
        }
        hopDong.setId(id);
        validateDates(hopDong);
        return repository.save(hopDong);
    }

    @Transactional
    public void delete(Long id) {
        HopDong existing = getById(id);
        if ("dang_hieu_luc".equalsIgnoreCase(existing.getTrangThai()) && existing.getPhong() != null) {
            Phong phong = existing.getPhong();
            phong.setSoNguoiHienTai(Math.max(0, phong.getSoNguoiHienTai() - 1));
            if ("da_day".equalsIgnoreCase(phong.getTrangThai())) {
                phong.setTrangThai("con_cho");
            }
            phongRepository.save(phong);
        }
        repository.deleteById(id);
    }

    @Transactional
    public HopDong duyetHopDong(Long id) {
        HopDong hopDong = getById(id);
        if ("dang_hieu_luc".equalsIgnoreCase(hopDong.getTrangThai())) {
            return hopDong;
        }
        Phong phong = phongRepository.findById(hopDong.getPhong().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));

        if (phong.getSoNguoiHienTai() >= phong.getSucChua()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng " + phong.getMaPhong() + " đã đủ người, không thể duyệt thêm!");
        }

        phong.setSoNguoiHienTai(phong.getSoNguoiHienTai() + 1);
        if (phong.getSoNguoiHienTai() >= phong.getSucChua()) {
            phong.setTrangThai("da_day");
        }
        phongRepository.save(phong);

        hopDong.setTrangThai("dang_hieu_luc");
        return repository.save(hopDong);
    }

    @Transactional
    public HopDong tuChoiHopDong(Long id) {
        HopDong hopDong = getById(id);
        if ("dang_hieu_luc".equalsIgnoreCase(hopDong.getTrangThai()) && hopDong.getPhong() != null) {
            Phong phong = hopDong.getPhong();
            phong.setSoNguoiHienTai(Math.max(0, phong.getSoNguoiHienTai() - 1));
            if ("da_day".equalsIgnoreCase(phong.getTrangThai())) {
                phong.setTrangThai("con_cho");
            }
            phongRepository.save(phong);
        }
        hopDong.setTrangThai("da_tu_choi");
        return repository.save(hopDong);
    }

    @Transactional
    public HopDong giaHanHopDong(Long id, LocalDate ngayKetThucMoi) {
        HopDong hopDong = getById(id);
        if (ngayKetThucMoi == null || ngayKetThucMoi.isBefore(hopDong.getNgayBatDau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc mới phải sau ngày bắt đầu!");
        }
        hopDong.setNgayKetThuc(ngayKetThucMoi);
        return repository.save(hopDong);
    }

    @Transactional
    public HopDong thanhLyHopDong(Long id) {
        HopDong hopDong = getById(id);
        if ("dang_hieu_luc".equalsIgnoreCase(hopDong.getTrangThai()) && hopDong.getPhong() != null) {
            Phong phong = hopDong.getPhong();
            phong.setSoNguoiHienTai(Math.max(0, phong.getSoNguoiHienTai() - 1));
            if ("da_day".equalsIgnoreCase(phong.getTrangThai())) {
                phong.setTrangThai("con_cho");
            }
            phongRepository.save(phong);
        }
        hopDong.setTrangThai("da_ket_thuc");
        return repository.save(hopDong);
    }

    @Transactional
    public HopDong chuyenPhong(Long hopDongId, Long phongMoiId) {
        HopDong hopDong = getById(hopDongId);
        Phong phongCu = hopDong.getPhong();
        if (phongCu != null && phongCu.getId().equals(phongMoiId)) {
            return hopDong;
        }

        Phong phongMoi = phongRepository.findById(phongMoiId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng mới"));

        if (phongMoi.getSoNguoiHienTai() >= phongMoi.getSucChua()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng mới (" + phongMoi.getMaPhong() + ") đã đủ sức chứa, vui lòng chọn phòng khác!");
        }

        if ("dang_hieu_luc".equalsIgnoreCase(hopDong.getTrangThai())) {
            if (phongCu != null) {
                phongCu.setSoNguoiHienTai(Math.max(0, phongCu.getSoNguoiHienTai() - 1));
                if ("da_day".equalsIgnoreCase(phongCu.getTrangThai())) {
                    phongCu.setTrangThai("con_cho");
                }
                phongRepository.save(phongCu);
            }

            phongMoi.setSoNguoiHienTai(phongMoi.getSoNguoiHienTai() + 1);
            if (phongMoi.getSoNguoiHienTai() >= phongMoi.getSucChua()) {
                phongMoi.setTrangThai("da_day");
            }
            phongRepository.save(phongMoi);
        }

        hopDong.setPhong(phongMoi);
        hopDong.setTienPhong(phongMoi.getGiaPhong());
        return repository.save(hopDong);
    }

    private void validateDates(HopDong hopDong) {
        if (hopDong.getNgayBatDau() != null && hopDong.getNgayKetThuc() != null
                && hopDong.getNgayKetThuc().isBefore(hopDong.getNgayBatDau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }
    }
}
