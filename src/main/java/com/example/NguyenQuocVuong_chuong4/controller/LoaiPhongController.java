package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.LoaiPhongDTO;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loai-phong")
public class LoaiPhongController {

    private final PhongRepository phongRepository;

    public LoaiPhongController(PhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    @GetMapping
    public List<LoaiPhongDTO> getAllLoaiPhong() {
        List<Phong> rooms = phongRepository.findAll();

        Map<String, List<Phong>> grouped = rooms.stream()
                .filter(p -> p.getLoaiPhong() != null && !p.getLoaiPhong().trim().isEmpty())
                .collect(Collectors.groupingBy(p -> p.getLoaiPhong().trim(), LinkedHashMap::new, Collectors.toList()));

        List<LoaiPhongDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<Phong>> entry : grouped.entrySet()) {
            String loai = entry.getKey();
            List<Phong> roomList = entry.getValue();

            int sucChua = roomList.stream()
                    .map(Phong::getSucChua)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(4);

            BigDecimal giaPhong = roomList.stream()
                    .map(Phong::getGiaPhong)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(BigDecimal.ZERO);

            long tongSoPhong = roomList.size();
            long soPhongConCho = roomList.stream()
                    .filter(p -> "con_cho".equalsIgnoreCase(p.getTrangThai()))
                    .count();
            long soPhongDaDay = roomList.stream()
                    .filter(p -> "da_day".equalsIgnoreCase(p.getTrangThai())
                            || (p.getSucChua() != null && p.getSoNguoiHienTai() != null
                            && p.getSoNguoiHienTai() >= p.getSucChua()))
                    .count();

            List<String> maPhongList = roomList.stream()
                    .map(Phong::getMaPhong)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            result.add(new LoaiPhongDTO(
                    loai,
                    sucChua,
                    giaPhong,
                    tongSoPhong,
                    soPhongConCho,
                    soPhongDaDay,
                    maPhongList
            ));
        }

        // Standard catalogs if empty
        if (result.isEmpty()) {
            result.add(new LoaiPhongDTO("Phòng 2 người", 2, new BigDecimal("1500000"), 0, 0, 0, List.of()));
            result.add(new LoaiPhongDTO("Phòng 4 người", 4, new BigDecimal("1200000"), 0, 0, 0, List.of()));
            result.add(new LoaiPhongDTO("Phòng 6 người", 6, new BigDecimal("900000"), 0, 0, 0, List.of()));
            result.add(new LoaiPhongDTO("Phòng 8 người", 8, new BigDecimal("650000"), 0, 0, 0, List.of()));
        }

        return result;
    }
}
