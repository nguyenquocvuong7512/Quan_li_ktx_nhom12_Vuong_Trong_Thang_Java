package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.dto.*;
import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.model.ThanhToan;
import com.example.NguyenQuocVuong_chuong4.security.SecurityUtils;
import com.example.NguyenQuocVuong_chuong4.service.KhoanThuService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final KhoanThuService service;

    public HoaDonController(KhoanThuService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String thang,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long sinhVienId,
            @RequestParam(required = false) Long phongId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        // Nếu người dùng là sinh viên, chỉ cho phép xem hóa đơn của chính mình
        if (SecurityUtils.isStudent()) {
            Long mySvId = SecurityUtils.getCurrentSinhVienId();
            sinhVienId = (mySvId != null) ? mySvId : -1L;
        }

        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 10;
            Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(dir, sortBy));
            PageResponse<KhoanThu> pageResponse = service.getPaginated(thang, trangThai, sinhVienId, phongId, search, pageable);
            return ResponseEntity.ok(pageResponse);
        }

        List<KhoanThu> list = service.getAll(thang, trangThai, sinhVienId, phongId, search);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public KhoanThu getById(@PathVariable Long id) {
        KhoanThu kt = service.getById(id);
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    kt.getSinhVien() != null ? kt.getSinhVien().getId() : null,
                    "Bạn không có quyền xem hóa đơn của sinh viên khác!"
            );
        }
        return kt;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public KhoanThu create(@Valid @RequestBody HoaDonRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public KhoanThu update(@PathVariable Long id, @Valid @RequestBody HoaDonRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/lap-hang-loat")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public Map<String, Object> lapHoaDonHangLoat(@Valid @RequestBody LapHoaDonHangLoatDTO dto) {
        return service.lapHoaDonHangLoat(dto);
    }

    @PostMapping("/thanh-toan")
    public ThanhToan ghiNhanThanhToan(@Valid @RequestBody GhiNhanThanhToanDTO dto) {
        KhoanThu kt = service.getById(dto.getKhoanThuId());
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    kt.getSinhVien() != null ? kt.getSinhVien().getId() : null,
                    "Bạn không có quyền thanh toán hóa đơn của sinh viên khác!"
            );
        }
        return service.ghiNhanThanhToan(dto);
    }

    @GetMapping("/{id}/lich-su-thanh-toan")
    public List<ThanhToan> getLichSuThanhToan(@PathVariable Long id) {
        KhoanThu kt = service.getById(id);
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    kt.getSinhVien() != null ? kt.getSinhVien().getId() : null,
                    "Bạn không có quyền xem lịch sử thanh toán của hóa đơn này!"
            );
        }
        return service.getLichSuThanhToan(id);
    }

    @GetMapping("/bao-cao")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUAN_LY')")
    public BaoCaoCongNoDTO getBaoCao(
            @RequestParam(required = false) String thang,
            @RequestParam(required = false) Long toaNhaId) {
        return service.getBaoCaoCongNo(thang, toaNhaId);
    }

    @GetMapping("/{id}/phieu-thu")
    public PhieuThuDTO getPhieuThu(
            @PathVariable Long id,
            @RequestParam(required = false) Long thanhToanId) {
        KhoanThu kt = service.getById(id);
        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    kt.getSinhVien() != null ? kt.getSinhVien().getId() : null,
                    "Bạn không có quyền in phiếu thu của sinh viên khác!"
            );
        }
        return service.getChiTietPhieuThu(id, thanhToanId);
    }
}
