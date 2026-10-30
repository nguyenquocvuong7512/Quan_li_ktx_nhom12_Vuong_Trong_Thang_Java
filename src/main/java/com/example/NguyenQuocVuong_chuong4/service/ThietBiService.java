package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.dto.BaoHongThietBiDTO;
import com.example.NguyenQuocVuong_chuong4.dto.CapPhatThietBiDTO;
import com.example.NguyenQuocVuong_chuong4.dto.ThietBiStatsDTO;
import com.example.NguyenQuocVuong_chuong4.model.LichSuTaiSan;
import com.example.NguyenQuocVuong_chuong4.model.LoaiTaiSan;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.model.TaiSan;
import com.example.NguyenQuocVuong_chuong4.model.TaiSanPhong;
import com.example.NguyenQuocVuong_chuong4.repository.LichSuTaiSanRepository;
import com.example.NguyenQuocVuong_chuong4.repository.LoaiTaiSanRepository;
import com.example.NguyenQuocVuong_chuong4.repository.NguoiDungRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.TaiSanPhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.TaiSanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThietBiService {

    private final TaiSanRepository taiSanRepository;
    private final TaiSanPhongRepository taiSanPhongRepository;
    private final LoaiTaiSanRepository loaiTaiSanRepository;
    private final LichSuTaiSanRepository lichSuTaiSanRepository;
    private final PhongRepository phongRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public ThietBiService(TaiSanRepository taiSanRepository,
            TaiSanPhongRepository taiSanPhongRepository,
            LoaiTaiSanRepository loaiTaiSanRepository,
            LichSuTaiSanRepository lichSuTaiSanRepository,
            PhongRepository phongRepository,
            NguoiDungRepository nguoiDungRepository) {
        this.taiSanRepository = taiSanRepository;
        this.taiSanPhongRepository = taiSanPhongRepository;
        this.loaiTaiSanRepository = loaiTaiSanRepository;
        this.lichSuTaiSanRepository = lichSuTaiSanRepository;
        this.phongRepository = phongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    // ================= 1. DANH MỤC THIẾT BỊ / TÀI SẢN =================
    public List<TaiSan> getAllTaiSan() {
        return taiSanRepository.findAll();
    }

    public TaiSan getTaiSanById(Long id) {
        return taiSanRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thiết bị ID: " + id));
    }

    @Transactional
    public TaiSan saveTaiSan(TaiSan taiSan) {
        if (taiSan.getMaTaiSan() == null || taiSan.getMaTaiSan().isBlank()) {
            taiSan.setMaTaiSan("TB-" + System.currentTimeMillis() % 100000);
        }
        if (taiSan.getNgayNhap() == null) {
            taiSan.setNgayNhap(LocalDate.now());
        }
        if (taiSan.getTrangThai() == null || taiSan.getTrangThai().isBlank()) {
            taiSan.setTrangThai("dang_su_dung");
        }
        return taiSanRepository.save(taiSan);
    }

    @Transactional
    public TaiSan updateTaiSan(Long id, TaiSan taiSan) {
        TaiSan existing = getTaiSanById(id);
        existing.setTenTaiSan(taiSan.getTenTaiSan());
        existing.setLoaiTaiSan(taiSan.getLoaiTaiSan());
        existing.setGiaTri(taiSan.getGiaTri());
        existing.setTrangThai(taiSan.getTrangThai());
        existing.setMoTa(taiSan.getMoTa());
        if (taiSan.getNgayNhap() != null) {
            existing.setNgayNhap(taiSan.getNgayNhap());
        }
        return taiSanRepository.save(existing);
    }

    @Transactional
    public void deleteTaiSan(Long id) {
        TaiSan existing = getTaiSanById(id);
        List<TaiSanPhong> allocations = taiSanPhongRepository.findByTaiSanId(id);
        if (!allocations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thiết bị này đang được cấp phát trong phòng, không thể xóa!");
        }
        taiSanRepository.delete(existing);
    }

    // ================= 2. QUẢN LÝ THIẾT BỊ TRONG PHÒNG =================
    public List<TaiSanPhong> getTaiSanPhong(Long phongId, String trangThai, Long toaNhaId) {
        return taiSanPhongRepository.filterTaiSanPhong(phongId, trangThai, toaNhaId);
    }

    public TaiSanPhong getTaiSanPhongById(Long id) {
        return taiSanPhongRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy trang bị phòng ID: " + id));
    }

    @Transactional
    public TaiSanPhong capPhatChoPhong(CapPhatThietBiDTO dto) {
        Phong phong = phongRepository.findById(dto.getPhongId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
        TaiSan taiSan = taiSanRepository.findById(dto.getTaiSanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thiết bị"));

        NguoiDung nguoiThucHien = null;
        if (dto.getNguoiThucHienId() != null) {
            nguoiThucHien = nguoiDungRepository.findById(dto.getNguoiThucHienId()).orElse(null);
        }

        // Kiểm tra xem thiết bị này đã có trong phòng ở trạng thái đang sử dụng chưa
        TaiSanPhong tsp = taiSanPhongRepository
                .findFirstByPhongIdAndTaiSanIdAndTrangThai(phong.getId(), taiSan.getId(), "dang_su_dung")
                .orElse(null);

        if (tsp != null) {
            tsp.setSoLuong(tsp.getSoLuong() + dto.getSoLuong());
            if (dto.getGhiChu() != null && !dto.getGhiChu().isBlank()) {
                tsp.setGhiChu(dto.getGhiChu());
            }
        } else {
            tsp = new TaiSanPhong();
            tsp.setPhong(phong);
            tsp.setTaiSan(taiSan);
            tsp.setSoLuong(dto.getSoLuong());
            tsp.setNgayCap(dto.getNgayCap() != null ? dto.getNgayCap() : LocalDate.now());
            tsp.setTrangThai("dang_su_dung");
            tsp.setGhiChu(dto.getGhiChu());
        }

        TaiSanPhong saved = taiSanPhongRepository.save(tsp);

        // Ghi lại nhật ký lịch sử
        LichSuTaiSan ls = new LichSuTaiSan();
        ls.setTaiSan(taiSan);
        ls.setPhong(phong);
        ls.setLoaiGiaoDich("cap_phat");
        ls.setSoLuong(dto.getSoLuong());
        ls.setLyDo(dto.getGhiChu() != null && !dto.getGhiChu().isBlank() ? dto.getGhiChu()
                : "Cấp phát trang thiết bị cho phòng " + phong.getMaPhong());
        ls.setThoiGian(LocalDateTime.now());
        ls.setNguoiThucHien(nguoiThucHien);
        lichSuTaiSanRepository.save(ls);

        return saved;
    }

    @Transactional
    public TaiSanPhong baoHong(BaoHongThietBiDTO dto) {
        TaiSanPhong tsp = getTaiSanPhongById(dto.getTaiSanPhongId());
        String oldStatus = tsp.getTrangThai();
        String newStatus = dto.getTrangThaiMoi() != null ? dto.getTrangThaiMoi() : "hong_hoc";

        tsp.setTrangThai(newStatus);
        if (dto.getLyDo() != null && !dto.getLyDo().isBlank()) {
            tsp.setGhiChu(dto.getLyDo());
        }
        TaiSanPhong saved = taiSanPhongRepository.save(tsp);

        NguoiDung nguoiThucHien = null;
        if (dto.getNguoiThucHienId() != null) {
            nguoiThucHien = nguoiDungRepository.findById(dto.getNguoiThucHienId()).orElse(null);
        }

        // Ghi lại nhật ký
        LichSuTaiSan ls = new LichSuTaiSan();
        ls.setTaiSan(tsp.getTaiSan());
        ls.setPhong(tsp.getPhong());
        ls.setLoaiGiaoDich("bao_hong");
        ls.setSoLuong(tsp.getSoLuong());
        ls.setLyDo("Báo hỏng (" + oldStatus + " -> " + newStatus + "): " + dto.getLyDo());
        ls.setThoiGian(LocalDateTime.now());
        ls.setNguoiThucHien(nguoiThucHien);
        lichSuTaiSanRepository.save(ls);

        return saved;
    }

    @Transactional
    public TaiSanPhong hoanTatSuaChua(Long id, String ghiChu, Long nguoiThucHienId) {
        TaiSanPhong tsp = getTaiSanPhongById(id);
        tsp.setTrangThai("dang_su_dung");
        tsp.setGhiChu(ghiChu != null && !ghiChu.isBlank() ? ghiChu : "Đã sửa chữa xong và kiểm tra hoạt động tốt");
        TaiSanPhong saved = taiSanPhongRepository.save(tsp);

        NguoiDung nguoiThucHien = null;
        if (nguoiThucHienId != null) {
            nguoiThucHien = nguoiDungRepository.findById(nguoiThucHienId).orElse(null);
        }

        LichSuTaiSan ls = new LichSuTaiSan();
        ls.setTaiSan(tsp.getTaiSan());
        ls.setPhong(tsp.getPhong());
        ls.setLoaiGiaoDich("sua_chua");
        ls.setSoLuong(tsp.getSoLuong());
        ls.setLyDo(ghiChu != null && !ghiChu.isBlank() ? ghiChu : "Hoàn tất bảo trì, thiết bị sử dụng bình thường");
        ls.setThoiGian(LocalDateTime.now());
        ls.setNguoiThucHien(nguoiThucHien);
        lichSuTaiSanRepository.save(ls);

        return saved;
    }

    @Transactional
    public TaiSanPhong thuHoi(Long id, String lyDo, Long nguoiThucHienId) {
        TaiSanPhong tsp = getTaiSanPhongById(id);
        tsp.setTrangThai("da_thu_hoi");
        tsp.setNgayThuHoi(LocalDate.now());
        if (lyDo != null && !lyDo.isBlank()) {
            tsp.setGhiChu(lyDo);
        }
        TaiSanPhong saved = taiSanPhongRepository.save(tsp);

        NguoiDung nguoiThucHien = null;
        if (nguoiThucHienId != null) {
            nguoiThucHien = nguoiDungRepository.findById(nguoiThucHienId).orElse(null);
        }

        LichSuTaiSan ls = new LichSuTaiSan();
        ls.setTaiSan(tsp.getTaiSan());
        ls.setPhong(tsp.getPhong());
        ls.setLoaiGiaoDich("thu_hoi");
        ls.setSoLuong(tsp.getSoLuong());
        ls.setLyDo(lyDo != null && !lyDo.isBlank() ? lyDo : "Thu hồi thiết bị về kho");
        ls.setThoiGian(LocalDateTime.now());
        ls.setNguoiThucHien(nguoiThucHien);
        lichSuTaiSanRepository.save(ls);

        return saved;
    }

    @Transactional
    public void xoaTrangBiPhong(Long id) {
        TaiSanPhong tsp = getTaiSanPhongById(id);
        taiSanPhongRepository.delete(tsp);
    }

    // ================= 3. LOẠI TÀI SẢN =================

    // Lấy danh sách loại tài sản
    public List<LoaiTaiSan> getAllLoaiTaiSan() {
        return loaiTaiSanRepository.findAll();
    }

    // Thêm loại tài sản
    @Transactional
    public LoaiTaiSan createLoaiTaiSan(LoaiTaiSan loai) {
        if (loai.getTenLoai() == null || loai.getTenLoai().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên loại tài sản không được để trống");
        }

        loai.setTenLoai(loai.getTenLoai().trim());

        if (loaiTaiSanRepository.existsByTenLoai(loai.getTenLoai())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên loại tài sản đã tồn tại");
        }

        return loaiTaiSanRepository.save(loai);
    }

    // Cập nhật loại tài sản
    @Transactional
    public LoaiTaiSan updateLoaiTaiSan(Long id, LoaiTaiSan loai) {
        LoaiTaiSan existing = loaiTaiSanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại tài sản ID: " + id));

        if (loai.getTenLoai() == null || loai.getTenLoai().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên loại tài sản không được để trống");
        }

        String tenLoaiMoi = loai.getTenLoai().trim();

        // Kiểm tra tên mới có bị trùng với loại tài sản khác không
        boolean trungTen = loaiTaiSanRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getTenLoai().equalsIgnoreCase(tenLoaiMoi));

        if (trungTen) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên loại tài sản đã tồn tại");
        }

        existing.setTenLoai(tenLoaiMoi);
        existing.setMoTa(loai.getMoTa());

        return loaiTaiSanRepository.save(existing);
    }

    // Xóa loại tài sản
    @Transactional
    public void deleteLoaiTaiSan(Long id) {
        LoaiTaiSan existing = loaiTaiSanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại tài sản ID: " + id));

        // Không cho xóa loại tài sản đang được thiết bị sử dụng
        boolean dangDuocSuDung = taiSanRepository.findAll().stream()
                .anyMatch(taiSan -> taiSan.getLoaiTaiSan() != null
                        && taiSan.getLoaiTaiSan().getId().equals(id));

        if (dangDuocSuDung) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể xóa loại tài sản này vì đang có thiết bị sử dụng!");
        }

        loaiTaiSanRepository.delete(existing);
    }

    // ================= 4. LỊCH SỬ THIẾT BỊ =================
    public List<LichSuTaiSan> getLichSu(Long phongId, Long taiSanId) {
        if (phongId != null) {
            return lichSuTaiSanRepository.findByPhongIdOrderByThoiGianDesc(phongId);
        }
        if (taiSanId != null) {
            return lichSuTaiSanRepository.findByTaiSanIdOrderByThoiGianDesc(taiSanId);
        }
        return lichSuTaiSanRepository.findAllByOrderByThoiGianDesc();
    }

    // ================= 5. THỐNG KÊ TỔNG QUAN =================
    public ThietBiStatsDTO getStats() {
        long tongDanhMuc = taiSanRepository.count();
        long tongTrangBiPhong = taiSanPhongRepository.count();
        long dangSuDung = taiSanPhongRepository.countByTrangThai("dang_su_dung");
        long hongHoc = taiSanPhongRepository.countByTrangThai("hong_hoc");
        long canBaoTri = taiSanPhongRepository.countByTrangThai("can_bao_tri")
                + taiSanPhongRepository.countByTrangThai("dang_sua_chua");
        long daThuHoi = taiSanPhongRepository.countByTrangThai("da_thu_hoi");

        return new ThietBiStatsDTO(tongDanhMuc, tongTrangBiPhong, dangSuDung, hongHoc, canBaoTri, daThuHoi);
    }
}