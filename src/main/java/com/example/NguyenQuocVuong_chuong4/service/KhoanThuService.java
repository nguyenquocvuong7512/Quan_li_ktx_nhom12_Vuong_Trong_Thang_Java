package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.dto.BaoCaoCongNoDTO;
import com.example.NguyenQuocVuong_chuong4.dto.GhiNhanThanhToanDTO;
import com.example.NguyenQuocVuong_chuong4.dto.HoaDonRequestDTO;
import com.example.NguyenQuocVuong_chuong4.dto.LapHoaDonHangLoatDTO;
import com.example.NguyenQuocVuong_chuong4.dto.PhieuThuDTO;
import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.model.ThanhToan;
import com.example.NguyenQuocVuong_chuong4.model.ToaNha;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.KhoanThuRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ThanhToanRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ToaNhaRepository;
import com.example.NguyenQuocVuong_chuong4.util.MoneyToWordsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KhoanThuService {

    private final KhoanThuRepository repository;
    private final ThanhToanRepository thanhToanRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HopDongRepository hopDongRepository;
    private final PhongRepository phongRepository;
    private final ToaNhaRepository toaNhaRepository;

    public KhoanThuService(KhoanThuRepository repository,
                           ThanhToanRepository thanhToanRepository,
                           SinhVienRepository sinhVienRepository,
                           HopDongRepository hopDongRepository,
                           PhongRepository phongRepository,
                           ToaNhaRepository toaNhaRepository) {
        this.repository = repository;
        this.thanhToanRepository = thanhToanRepository;
        this.sinhVienRepository = sinhVienRepository;
        this.hopDongRepository = hopDongRepository;
        this.phongRepository = phongRepository;
        this.toaNhaRepository = toaNhaRepository;
    }

    @Transactional
    public void capNhatTrangThaiQuaHan() {
        LocalDate today = LocalDate.now();
        List<KhoanThu> overdueList = repository.findOverdueInvoices(today);
        for (KhoanThu kt : overdueList) {
            if (!"qua_han".equals(kt.getTrangThai()) && !"da_thanh_toan".equals(kt.getTrangThai())) {
                kt.setTrangThai("qua_han");
                repository.save(kt);
            }
        }
    }

    public List<KhoanThu> getAll(String thang, String trangThai, Long sinhVienId, Long phongId, String search) {
        capNhatTrangThaiQuaHan();

        List<KhoanThu> list = repository.filterKhoanThu(
                (thang != null && !thang.isBlank()) ? thang : null,
                (trangThai != null && !trangThai.isBlank()) ? trangThai : null,
                sinhVienId,
                phongId
        );

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(k -> {
                boolean matchMaHD = k.getMaHoaDon() != null && k.getMaHoaDon().toLowerCase().contains(q);
                boolean matchSVName = k.getSinhVien() != null && k.getSinhVien().getNguoiDung() != null
                        && k.getSinhVien().getNguoiDung().getHoTen() != null
                        && k.getSinhVien().getNguoiDung().getHoTen().toLowerCase().contains(q);
                boolean matchMaSV = k.getSinhVien() != null && k.getSinhVien().getMaSinhVien() != null
                        && k.getSinhVien().getMaSinhVien().toLowerCase().contains(q);
                boolean matchPhong = k.getPhong() != null && k.getPhong().getMaPhong() != null
                        && k.getPhong().getMaPhong().toLowerCase().contains(q);
                return matchMaHD || matchSVName || matchMaSV || matchPhong;
            }).collect(Collectors.toList());
        }

        return list;
    }

    public KhoanThu getById(Long id) {
        capNhatTrangThaiQuaHan();
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn có ID: " + id));
    }

    @Transactional
    public KhoanThu create(HoaDonRequestDTO dto) {
        SinhVien sv = sinhVienRepository.findById(dto.getSinhVienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));

        HopDong hopDong = null;
        if (dto.getHopDongId() != null) {
            hopDong = hopDongRepository.findById(dto.getHopDongId()).orElse(null);
        } else {
            // Thử tìm hợp đồng hiệu lực gần nhất của sinh viên
            List<HopDong> contracts = hopDongRepository.findAll().stream()
                    .filter(h -> h.getSinhVien() != null && h.getSinhVien().getId().equals(sv.getId())
                            && "dang_hieu_luc".equalsIgnoreCase(h.getTrangThai()))
                    .toList();
            if (!contracts.isEmpty()) {
                hopDong = contracts.get(0);
            }
        }

        Phong phong = null;
        if (dto.getPhongId() != null) {
            phong = phongRepository.findById(dto.getPhongId()).orElse(null);
        } else if (hopDong != null && hopDong.getPhong() != null) {
            phong = hopDong.getPhong();
        }

        KhoanThu kt = new KhoanThu();
        kt.setSinhVien(sv);
        kt.setHopDong(hopDong);
        kt.setPhong(phong);
        kt.setThang(dto.getThang() != null && !dto.getThang().isBlank() ? dto.getThang() : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        kt.setLoaiKhoanThu(dto.getLoaiKhoanThu() != null && !dto.getLoaiKhoanThu().isBlank() ? dto.getLoaiKhoanThu() : "hoa_don_thang");

        // Tiền phòng: nếu DTO không nhập hoặc = 0, lấy từ hợp đồng hoặc phòng
        BigDecimal tienPhong = dto.getTienPhong();
        if ((tienPhong == null || tienPhong.compareTo(BigDecimal.ZERO) == 0)) {
            if (hopDong != null && hopDong.getTienPhong() != null) {
                tienPhong = hopDong.getTienPhong();
            } else if (phong != null && phong.getGiaPhong() != null) {
                tienPhong = phong.getGiaPhong();
            }
        }
        kt.setTienPhong(tienPhong != null ? tienPhong : BigDecimal.ZERO);

        kt.setTienDien(dto.getTienDien() != null ? dto.getTienDien() : BigDecimal.ZERO);
        kt.setChiSoDienCu(dto.getChiSoDienCu() != null ? dto.getChiSoDienCu() : 0);
        kt.setChiSoDienMoi(dto.getChiSoDienMoi() != null ? dto.getChiSoDienMoi() : 0);

        kt.setTienNuoc(dto.getTienNuoc() != null ? dto.getTienNuoc() : BigDecimal.ZERO);
        kt.setChiSoNuocCu(dto.getChiSoNuocCu() != null ? dto.getChiSoNuocCu() : 0);
        kt.setChiSoNuocMoi(dto.getChiSoNuocMoi() != null ? dto.getChiSoNuocMoi() : 0);

        kt.setTienInternet(dto.getTienInternet() != null ? dto.getTienInternet() : BigDecimal.ZERO);
        kt.setTienDichVu(dto.getTienDichVu() != null ? dto.getTienDichVu() : BigDecimal.ZERO);

        kt.setHanThanhToan(dto.getHanThanhToan() != null ? dto.getHanThanhToan() : LocalDate.now().plusDays(15));
        kt.setGhiChu(dto.getGhiChu());

        kt.setMaHoaDon(generateMaHoaDon(kt.getThang()));
        kt.calculateTotals();

        return repository.save(kt);
    }

    @Transactional
    public KhoanThu update(Long id, HoaDonRequestDTO dto) {
        KhoanThu existing = getById(id);

        if (dto.getSinhVienId() != null) {
            SinhVien sv = sinhVienRepository.findById(dto.getSinhVienId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));
            existing.setSinhVien(sv);
        }

        if (dto.getHopDongId() != null) {
            HopDong hd = hopDongRepository.findById(dto.getHopDongId()).orElse(null);
            existing.setHopDong(hd);
        }

        if (dto.getPhongId() != null) {
            Phong p = phongRepository.findById(dto.getPhongId()).orElse(null);
            existing.setPhong(p);
        }

        if (dto.getThang() != null && !dto.getThang().isBlank()) {
            existing.setThang(dto.getThang());
        }

        if (dto.getLoaiKhoanThu() != null && !dto.getLoaiKhoanThu().isBlank()) {
            existing.setLoaiKhoanThu(dto.getLoaiKhoanThu());
        }

        if (dto.getTienPhong() != null) existing.setTienPhong(dto.getTienPhong());
        if (dto.getTienDien() != null) existing.setTienDien(dto.getTienDien());
        if (dto.getChiSoDienCu() != null) existing.setChiSoDienCu(dto.getChiSoDienCu());
        if (dto.getChiSoDienMoi() != null) existing.setChiSoDienMoi(dto.getChiSoDienMoi());
        if (dto.getTienNuoc() != null) existing.setTienNuoc(dto.getTienNuoc());
        if (dto.getChiSoNuocCu() != null) existing.setChiSoNuocCu(dto.getChiSoNuocCu());
        if (dto.getChiSoNuocMoi() != null) existing.setChiSoNuocMoi(dto.getChiSoNuocMoi());
        if (dto.getTienInternet() != null) existing.setTienInternet(dto.getTienInternet());
        if (dto.getTienDichVu() != null) existing.setTienDichVu(dto.getTienDichVu());
        if (dto.getHanThanhToan() != null) existing.setHanThanhToan(dto.getHanThanhToan());
        if (dto.getGhiChu() != null) existing.setGhiChu(dto.getGhiChu());

        existing.calculateTotals();
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        KhoanThu existing = getById(id);
        List<ThanhToan> payments = thanhToanRepository.findByKhoanThuIdOrderByThoiGianDesc(id);
        if (!payments.isEmpty()) {
            thanhToanRepository.deleteAll(payments);
        }
        repository.delete(existing);
    }

    @Transactional
    public Map<String, Object> lapHoaDonHangLoat(LapHoaDonHangLoatDTO dto) {
        String thang = dto.getThang();
        LocalDate hanThanhToan = dto.getHanThanhToan() != null ? dto.getHanThanhToan() : LocalDate.now().plusDays(15);

        List<HopDong> activeContracts = hopDongRepository.findAll().stream()
                .filter(h -> "dang_hieu_luc".equalsIgnoreCase(h.getTrangThai()))
                .toList();

        int createdCount = 0;
        int skippedCount = 0;
        List<String> createdInvoices = new ArrayList<>();

        for (HopDong hd : activeContracts) {
            SinhVien sv = hd.getSinhVien();
            if (sv == null) continue;

            // Kiểm tra xem đã lập hóa đơn tháng này cho sinh viên chưa
            boolean exists = repository.existsBySinhVienIdAndThangAndLoaiKhoanThu(sv.getId(), thang, "hoa_don_thang");
            if (exists) {
                skippedCount++;
                continue;
            }

            KhoanThu kt = new KhoanThu();
            kt.setMaHoaDon(generateMaHoaDon(thang));
            kt.setThang(thang);
            kt.setSinhVien(sv);
            kt.setHopDong(hd);
            kt.setPhong(hd.getPhong());
            kt.setLoaiKhoanThu("hoa_don_thang");

            kt.setTienPhong(hd.getTienPhong() != null ? hd.getTienPhong() : (hd.getPhong() != null ? hd.getPhong().getGiaPhong() : BigDecimal.ZERO));
            kt.setTienDien(dto.getTienDienMacDinh() != null ? dto.getTienDienMacDinh() : BigDecimal.ZERO);
            kt.setTienNuoc(dto.getTienNuocMacDinh() != null ? dto.getTienNuocMacDinh() : BigDecimal.ZERO);
            kt.setTienInternet(dto.getTienInternet() != null ? dto.getTienInternet() : BigDecimal.ZERO);
            kt.setTienDichVu(dto.getTienDichVu() != null ? dto.getTienDichVu() : BigDecimal.ZERO);

            kt.setHanThanhToan(hanThanhToan);
            kt.setGhiChu(dto.getGhiChu() != null && !dto.getGhiChu().isBlank()
                    ? dto.getGhiChu()
                    : "Hóa đơn KTX tự động kỳ tháng " + thang);

            kt.calculateTotals();
            repository.save(kt);
            createdCount++;
            createdInvoices.add(kt.getMaHoaDon());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("thang", thang);
        res.put("createdCount", createdCount);
        res.put("skippedCount", skippedCount);
        res.put("createdInvoices", createdInvoices);
        return res;
    }

    @Transactional
    public ThanhToan ghiNhanThanhToan(GhiNhanThanhToanDTO dto) {
        KhoanThu kt = getById(dto.getKhoanThuId());

        if ("da_thanh_toan".equalsIgnoreCase(kt.getTrangThai()) && kt.getConLai().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hóa đơn này đã được thanh toán đầy đủ!");
        }

        BigDecimal payAmount = dto.getSoTien();
        if (payAmount.compareTo(kt.getConLai()) > 0) {
            // Cho phép thanh toán dư hoặc giới hạn theo số nợ còn lại
            // Ở KTX, số tiền thu không vượt quá số còn lại
            payAmount = kt.getConLai();
        }

        ThanhToan tt = new ThanhToan();
        tt.setKhoanThu(kt);
        tt.setSoTien(payAmount);
        tt.setPhuongThuc(dto.getPhuongThuc() != null ? dto.getPhuongThuc() : "tien_mat");

        String maGD = dto.getMaGiaoDich();
        if (maGD == null || maGD.isBlank()) {
            maGD = "GD-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                    + "-" + ((int) (Math.random() * 900) + 100);
        }
        tt.setMaGiaoDich(maGD);
        tt.setThoiGian(LocalDateTime.now());
        tt.setTrangThai("thanh_cong");
        tt.setGhiChu(dto.getGhiChu());
        tt.setNguoiThu(dto.getNguoiThu() != null && !dto.getNguoiThu().isBlank() ? dto.getNguoiThu() : "Quản trị viên KTX");

        ThanhToan savedPayment = thanhToanRepository.save(tt);

        // Cập nhật lại số tiền đã thanh toán của KhoanThu
        BigDecimal currentDaTra = kt.getDaThanhToan() != null ? kt.getDaThanhToan() : BigDecimal.ZERO;
        kt.setDaThanhToan(currentDaTra.add(payAmount));
        kt.calculateTotals();
        repository.save(kt);

        return savedPayment;
    }

    public List<ThanhToan> getLichSuThanhToan(Long khoanThuId) {
        return thanhToanRepository.findByKhoanThuIdOrderByThoiGianDesc(khoanThuId);
    }

    public BaoCaoCongNoDTO getBaoCaoCongNo(String thang, Long toaNhaId) {
        capNhatTrangThaiQuaHan();

        List<KhoanThu> all = repository.findAll();

        if (thang != null && !thang.isBlank()) {
            all = all.stream().filter(k -> thang.equalsIgnoreCase(k.getThang())).collect(Collectors.toList());
        }

        if (toaNhaId != null) {
            all = all.stream().filter(k -> k.getPhong() != null
                    && k.getPhong().getTang() != null
                    && k.getPhong().getTang().getToaNha() != null
                    && toaNhaId.equals(k.getPhong().getTang().getToaNha().getId())).collect(Collectors.toList());
        }

        BaoCaoCongNoDTO report = new BaoCaoCongNoDTO();
        BigDecimal tongTien = BigDecimal.ZERO;
        BigDecimal tongDaThu = BigDecimal.ZERO;
        BigDecimal tongConNo = BigDecimal.ZERO;

        long soHD = all.size();
        long soDaTT = 0;
        long soChuaTT = 0;
        long soQuaHan = 0;
        long soMotPhan = 0;

        // Group by SinhVien
        Map<Long, List<KhoanThu>> svMap = new HashMap<>();
        // Group by Phong
        Map<Long, List<KhoanThu>> phongMap = new HashMap<>();

        for (KhoanThu k : all) {
            BigDecimal tien = k.getSoTien() != null ? k.getSoTien() : BigDecimal.ZERO;
            BigDecimal daTra = k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO;
            BigDecimal no = k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO;

            tongTien = tongTien.add(tien);
            tongDaThu = tongDaThu.add(daTra);
            tongConNo = tongConNo.add(no);

            String status = k.getTrangThai();
            if ("da_thanh_toan".equalsIgnoreCase(status)) {
                soDaTT++;
            } else if ("qua_han".equalsIgnoreCase(status)) {
                soQuaHan++;
            } else if ("thanh_toan_mot_phan".equalsIgnoreCase(status)) {
                soMotPhan++;
            } else {
                soChuaTT++;
            }

            if (k.getSinhVien() != null) {
                svMap.computeIfAbsent(k.getSinhVien().getId(), key -> new ArrayList<>()).add(k);
            }
            if (k.getPhong() != null) {
                phongMap.computeIfAbsent(k.getPhong().getId(), key -> new ArrayList<>()).add(k);
            }
        }

        report.setTongTienHoaDon(tongTien);
        report.setTongDaThu(tongDaThu);
        report.setTongConNo(tongConNo);
        if (tongTien.compareTo(BigDecimal.ZERO) > 0) {
            double rate = tongDaThu.divide(tongTien, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            report.setTyLeThu(Math.round(rate * 100.0) / 100.0);
        } else {
            report.setTyLeThu(0.0);
        }

        report.setSoHoaDon(soHD);
        report.setSoDaThanhToan(soDaTT);
        report.setSoChuaThanhToan(soChuaTT);
        report.setSoQuaHan(soQuaHan);
        report.setSoThanhToanMotPhan(soMotPhan);

        // Sinh Vien Debt list
        List<BaoCaoCongNoDTO.CongNoSinhVienDTO> listSV = new ArrayList<>();
        for (Map.Entry<Long, List<KhoanThu>> entry : svMap.entrySet()) {
            List<KhoanThu> list = entry.getValue();
            if (list.isEmpty()) continue;
            KhoanThu first = list.get(0);
            SinhVien sv = first.getSinhVien();

            BigDecimal svTong = BigDecimal.ZERO;
            BigDecimal svDaTra = BigDecimal.ZERO;
            BigDecimal svNo = BigDecimal.ZERO;
            long svChuaTraCount = 0;
            long svQuaHanCount = 0;

            for (KhoanThu k : list) {
                svTong = svTong.add(k.getSoTien() != null ? k.getSoTien() : BigDecimal.ZERO);
                svDaTra = svDaTra.add(k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO);
                svNo = svNo.add(k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO);
                if (!"da_thanh_toan".equalsIgnoreCase(k.getTrangThai())) {
                    svChuaTraCount++;
                }
                if ("qua_han".equalsIgnoreCase(k.getTrangThai())) {
                    svQuaHanCount++;
                }
            }

            String hoTen = sv.getNguoiDung() != null ? sv.getNguoiDung().getHoTen() : "";
            String sdt = sv.getNguoiDung() != null ? sv.getNguoiDung().getSoDienThoai() : "";
            String maPhong = first.getPhong() != null ? first.getPhong().getMaPhong() : "-";
            String tenToa = (first.getPhong() != null && first.getPhong().getTang() != null && first.getPhong().getTang().getToaNha() != null)
                    ? first.getPhong().getTang().getToaNha().getTenToa() : "-";

            listSV.add(new BaoCaoCongNoDTO.CongNoSinhVienDTO(
                    sv.getId(),
                    sv.getMaSinhVien(),
                    hoTen,
                    sv.getLop(),
                    sdt,
                    maPhong,
                    tenToa,
                    svTong,
                    svDaTra,
                    svNo,
                    svChuaTraCount,
                    svQuaHanCount
            ));
        }
        // Sắp xếp sinh viên theo số nợ giảm dần
        listSV.sort(Comparator.comparing(BaoCaoCongNoDTO.CongNoSinhVienDTO::getConNo).reversed());
        report.setTheoSinhVien(listSV);

        // Phong Debt list
        List<BaoCaoCongNoDTO.CongNoPhongDTO> listPhong = new ArrayList<>();
        for (Map.Entry<Long, List<KhoanThu>> entry : phongMap.entrySet()) {
            List<KhoanThu> list = entry.getValue();
            if (list.isEmpty()) continue;
            KhoanThu first = list.get(0);
            Phong p = first.getPhong();

            BigDecimal pTong = BigDecimal.ZERO;
            BigDecimal pDaTra = BigDecimal.ZERO;
            BigDecimal pNo = BigDecimal.ZERO;
            long pNoCount = 0;

            for (KhoanThu k : list) {
                pTong = pTong.add(k.getSoTien() != null ? k.getSoTien() : BigDecimal.ZERO);
                pDaTra = pDaTra.add(k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO);
                pNo = pNo.add(k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO);
                if (!"da_thanh_toan".equalsIgnoreCase(k.getTrangThai())) {
                    pNoCount++;
                }
            }

            String tenToa = (p.getTang() != null && p.getTang().getToaNha() != null)
                    ? p.getTang().getToaNha().getTenToa() : "-";

            listPhong.add(new BaoCaoCongNoDTO.CongNoPhongDTO(
                    p.getId(),
                    p.getMaPhong(),
                    tenToa,
                    p.getLoaiPhong(),
                    p.getSoNguoiHienTai(),
                    pTong,
                    pDaTra,
                    pNo,
                    pNoCount
            ));
        }
        listPhong.sort(Comparator.comparing(BaoCaoCongNoDTO.CongNoPhongDTO::getConNo).reversed());
        report.setTheoPhong(listPhong);

        // Toa Nha Debt list
        List<ToaNha> toaNhas = toaNhaRepository.findAll();
        List<BaoCaoCongNoDTO.CongNoToaNhaDTO> listToa = new ArrayList<>();

        for (ToaNha tn : toaNhas) {
            BigDecimal tnTong = BigDecimal.ZERO;
            BigDecimal tnDaThu = BigDecimal.ZERO;
            BigDecimal tnNo = BigDecimal.ZERO;
            long tnHDCount = 0;

            for (KhoanThu k : all) {
                if (k.getPhong() != null && k.getPhong().getTang() != null
                        && k.getPhong().getTang().getToaNha() != null
                        && tn.getId().equals(k.getPhong().getTang().getToaNha().getId())) {
                    tnTong = tnTong.add(k.getSoTien() != null ? k.getSoTien() : BigDecimal.ZERO);
                    tnDaThu = tnDaThu.add(k.getDaThanhToan() != null ? k.getDaThanhToan() : BigDecimal.ZERO);
                    tnNo = tnNo.add(k.getConLai() != null ? k.getConLai() : BigDecimal.ZERO);
                    tnHDCount++;
                }
            }

            double rate = 0.0;
            if (tnTong.compareTo(BigDecimal.ZERO) > 0) {
                rate = tnDaThu.divide(tnTong, 4, RoundingMode.HALF_UP).doubleValue() * 100;
                rate = Math.round(rate * 100.0) / 100.0;
            }

            listToa.add(new BaoCaoCongNoDTO.CongNoToaNhaDTO(
                    tn.getId(),
                    tn.getMaToa(),
                    tn.getTenToa(),
                    tnTong,
                    tnDaThu,
                    tnNo,
                    rate,
                    tnHDCount
            ));
        }
        listToa.sort(Comparator.comparing(BaoCaoCongNoDTO.CongNoToaNhaDTO::getTongNo).reversed());
        report.setTheoToaNha(listToa);

        return report;
    }

    public PhieuThuDTO getChiTietPhieuThu(Long khoanThuId, Long thanhToanId) {
        KhoanThu kt = getById(khoanThuId);
        PhieuThuDTO dto = new PhieuThuDTO();

        dto.setMaHoaDon(kt.getMaHoaDon());
        dto.setThang(kt.getThang());

        if (kt.getSinhVien() != null) {
            dto.setMaSinhVien(kt.getSinhVien().getMaSinhVien());
            dto.setLop(kt.getSinhVien().getLop());
            if (kt.getSinhVien().getNguoiDung() != null) {
                dto.setHoTenSinhVien(kt.getSinhVien().getNguoiDung().getHoTen());
                dto.setSoDienThoai(kt.getSinhVien().getNguoiDung().getSoDienThoai());
            }
        }

        if (kt.getPhong() != null) {
            dto.setMaPhong(kt.getPhong().getMaPhong());
            if (kt.getPhong().getTang() != null && kt.getPhong().getTang().getToaNha() != null) {
                dto.setTenToa(kt.getPhong().getTang().getToaNha().getTenToa());
            }
        }

        dto.setTienPhong(kt.getTienPhong());
        dto.setTienDien(kt.getTienDien());
        dto.setChiSoDienCu(kt.getChiSoDienCu());
        dto.setChiSoDienMoi(kt.getChiSoDienMoi());
        dto.setTienNuoc(kt.getTienNuoc());
        dto.setChiSoNuocCu(kt.getChiSoNuocCu());
        dto.setChiSoNuocMoi(kt.getChiSoNuocMoi());
        dto.setTienInternet(kt.getTienInternet());
        dto.setTienDichVu(kt.getTienDichVu());
        dto.setTongTienHoaDon(kt.getSoTien());
        dto.setDaThanhToanLuyKe(kt.getDaThanhToan());
        dto.setConLai(kt.getConLai());

        if (thanhToanId != null) {
            ThanhToan tt = thanhToanRepository.findById(thanhToanId).orElse(null);
            if (tt != null) {
                dto.setSoPhieu("PT-" + tt.getId());
                dto.setNgayThu(tt.getThoiGian());
                dto.setSoTienThanhToan(tt.getSoTien());
                dto.setPhuongThuc(tt.getPhuongThuc());
                dto.setMaGiaoDich(tt.getMaGiaoDich());
                dto.setNguoiThu(tt.getNguoiThu());
                dto.setGhiChu(tt.getGhiChu());
                dto.setTongTienChu(MoneyToWordsUtil.convertToVietnameseWords(tt.getSoTien()));
                return dto;
            }
        }

        // Nếu không có thanhToanId cụ thể, lấy thanh toán gần nhất hoặc toàn bộ
        List<ThanhToan> list = thanhToanRepository.findByKhoanThuIdOrderByThoiGianDesc(khoanThuId);
        if (!list.isEmpty()) {
            ThanhToan tt = list.get(0);
            dto.setSoPhieu("PT-" + tt.getId());
            dto.setNgayThu(tt.getThoiGian());
            dto.setSoTienThanhToan(tt.getSoTien());
            dto.setPhuongThuc(tt.getPhuongThuc());
            dto.setMaGiaoDich(tt.getMaGiaoDich());
            dto.setNguoiThu(tt.getNguoiThu());
            dto.setGhiChu(tt.getGhiChu());
            dto.setTongTienChu(MoneyToWordsUtil.convertToVietnameseWords(tt.getSoTien()));
        } else {
            dto.setSoPhieu("PT-HD" + kt.getId());
            dto.setNgayThu(LocalDateTime.now());
            dto.setSoTienThanhToan(kt.getSoTien());
            dto.setPhuongThuc("tien_mat");
            dto.setNguoiThu("Kế toán KTX");
            dto.setGhiChu(kt.getGhiChu());
            dto.setTongTienChu(MoneyToWordsUtil.convertToVietnameseWords(kt.getSoTien()));
        }

        return dto;
    }

    private String generateMaHoaDon(String thang) {
        String cleanThang = (thang != null) ? thang.replaceAll("[^0-9]", "") : DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.now());
        long count = repository.count() + 1;
        String code = "HD" + cleanThang + String.format("-%04d", count);
        while (repository.existsByMaHoaDon(code)) {
            count++;
            code = "HD" + cleanThang + String.format("-%04d", count);
        }
        return code;
    }
}
