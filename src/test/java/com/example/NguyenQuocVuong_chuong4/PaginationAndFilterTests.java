package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.dto.PageResponse;
import com.example.NguyenQuocVuong_chuong4.model.*;
import com.example.NguyenQuocVuong_chuong4.repository.*;
import com.example.NguyenQuocVuong_chuong4.service.KhoanThuService;
import com.example.NguyenQuocVuong_chuong4.service.PhongService;
import com.example.NguyenQuocVuong_chuong4.service.SinhVienService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@org.springframework.transaction.annotation.Transactional
class PaginationAndFilterTests {

    @Autowired
    private KhoanThuService khoanThuService;

    @Autowired
    private SinhVienService sinhVienService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private KhoanThuRepository khoanThuRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HopDongRepository hopDongRepository;

    @Autowired
    private KhuRepository khuRepository;

    @Autowired
    private ToaNhaRepository toaNhaRepository;

    @Autowired
    private TangRepository tangRepository;

    @Autowired
    private PhongRepository phongRepository;

    @BeforeEach
    void setupBatchData() {
        Khu khu = new Khu();
        khu.setMaKhu("K-PAGE");
        khu.setTenKhu("Khu Phân Trang");
        khu = khuRepository.save(khu);

        ToaNha toa = new ToaNha();
        toa.setKhu(khu);
        toa.setMaToa("T-PAGE");
        toa.setTenToa("Toa Phân Trang");
        toa.setSoTang(3);
        toa = toaNhaRepository.save(toa);

        Tang tang = new Tang();
        tang.setToaNha(toa);
        tang.setSoTang(1);
        tang = tangRepository.save(tang);

        // Tạo 12 phòng để kiểm tra phân trang
        java.util.List<Phong> rooms = new java.util.ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Phong p = new Phong();
            p.setTang(tang);
            p.setMaPhong("PAGE-" + (100 + i));
            p.setSucChua(4);
            p.setSoNguoiHienTai(0);
            p.setLoaiPhong("4 nguoi");
            p.setGiaPhong(new BigDecimal("1500000.00"));
            p.setTrangThai("hoat_dong");
            rooms.add(phongRepository.save(p));
        }

        // Tạo 8 sinh viên để kiểm tra phân trang
        for (int i = 1; i <= 8; i++) {
            NguoiDung u = new NguoiDung();
            u.setMaNguoiDung("PND-" + i);
            u.setHoTen("Sinh Vien Page " + i);
            u.setEmail("sv_page" + i + "@ktx.edu.vn");
            u.setMatKhau("$2a$10$9qDKoxIst0uM8ZSIYby0beqPYLraOXp6RFwzR3bhhcy/4gN9RiQXa");
            u.setTrangThai("hoat_dong");
            u = nguoiDungRepository.save(u);

            SinhVien sv = new SinhVien();
            sv.setMaSinhVien("B22DCCN" + String.format("%03d", i));
            sv.setNguoiDung(u);
            sv = sinhVienRepository.save(sv);

            HopDong hd = new HopDong();
            hd.setSinhVien(sv);
            hd.setPhong(rooms.get(i - 1));
            hd.setNgayBatDau(LocalDate.now());
            hd.setNgayKetThuc(LocalDate.now().plusMonths(6));
            hd.setTienPhong(new BigDecimal("1500000.00"));
            hd.setTrangThai("dang_hieu_luc");
            hd = hopDongRepository.save(hd);

            KhoanThu kt = new KhoanThu();
            kt.setSinhVien(sv);
            kt.setHopDong(hd);
            kt.setPhong(rooms.get(i - 1));
            kt.setMaHoaDon("INV-PAGE-" + i);
            kt.setLoaiKhoanThu("hoa_don_thang");
            kt.setSoTien(new BigDecimal("350000.00"));
            kt.setTienPhong(new BigDecimal("350000.00"));
            kt.setConLai(new BigDecimal("350000.00"));
            kt.setHanThanhToan(LocalDate.now().plusDays(15));
            kt.setTrangThai(i % 2 == 0 ? "da_thanh_toan" : "chua_thanh_toan");
            khoanThuRepository.save(kt);
        }
    }

    @Test
    @DisplayName("Pagination 1: Phân trang Hóa đơn chính xác - Page 0, Size 5")
    void testKhoanThuPagination() {
        PageResponse<KhoanThu> page0 = khoanThuService.getPaginated(
                null, null, null, null, null,
                PageRequest.of(0, 5, Sort.by("id").ascending())
        );

        assertNotNull(page0);
        assertEquals(0, page0.currentPage());
        assertEquals(5, page0.pageSize());
        assertEquals(5, page0.content().size());
        assertTrue(page0.totalElements() >= 8);
        assertTrue(page0.totalPages() >= 2);
        assertTrue(page0.first());
        assertFalse(page0.last());

        PageResponse<KhoanThu> page1 = khoanThuService.getPaginated(
                null, null, null, null, null,
                PageRequest.of(1, 5, Sort.by("id").ascending())
        );
        assertNotNull(page1);
        assertEquals(1, page1.currentPage());
        assertTrue(page1.content().size() >= 3);
        assertFalse(page1.first());
    }

    @Test
    @DisplayName("Pagination 2: Phân trang kết hợp Bộ lọc trạng thái Hóa đơn")
    void testKhoanThuFilterByStatus() {
        PageResponse<KhoanThu> unpaidPage = khoanThuService.getPaginated(
                null, "chua_thanh_toan", null, null, null,
                PageRequest.of(0, 10, Sort.by("id").descending())
        );

        assertNotNull(unpaidPage);
        assertFalse(unpaidPage.content().isEmpty());
        for (KhoanThu kt : unpaidPage.content()) {
            assertEquals("chua_thanh_toan", kt.getTrangThai(), "Bộ lọc phải chỉ trả về hóa đơn chưa thanh toán");
        }
    }

    @Test
    @DisplayName("Pagination 3: Phân trang Phòng với Page size 6")
    void testPhongPagination() {
        PageResponse<Phong> page = phongService.getPaginated(
                null, null, null, null,
                PageRequest.of(0, 6, Sort.by("maPhong").ascending())
        );

        assertNotNull(page);
        assertEquals(6, page.content().size());
        assertTrue(page.totalElements() >= 12);
        assertTrue(page.totalPages() >= 2);
    }

    @Test
    @DisplayName("Pagination 4: Phân trang Sinh viên kết hợp Tìm kiếm từ khóa")
    void testSinhVienSearchAndPagination() {
        PageResponse<SinhVien> result = sinhVienService.getPaginated(
                "B22DCCN00",
                PageRequest.of(0, 5, Sort.by("id").ascending())
        );

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        for (SinhVien sv : result.content()) {
            assertTrue(sv.getMaSinhVien().contains("B22DCCN00"));
        }
    }
}
