package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.model.*;
import com.example.NguyenQuocVuong_chuong4.repository.*;
import com.example.NguyenQuocVuong_chuong4.security.JwtTokenProvider;
import com.example.NguyenQuocVuong_chuong4.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@org.springframework.transaction.annotation.Transactional
class RbacSecurityAndOwnershipTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private KhuRepository khuRepository;

    @Autowired
    private ToaNhaRepository toaNhaRepository;

    @Autowired
    private TangRepository tangRepository;

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private HopDongRepository hopDongRepository;

    @Autowired
    private KhoanThuRepository khoanThuRepository;

    private String adminToken;
    private String quanLyToken;
    private String studentAToken;
    private String studentBToken;

    private KhoanThu invoiceStudentA;
    private KhoanThu invoiceStudentB;

    @BeforeEach
    void setupData() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Tạo vai trò
        VaiTro roleAdmin = vaiTroRepository.findByTenVaiTroIgnoreCase("ADMIN")
                .orElseGet(() -> vaiTroRepository.save(new VaiTro("ADMIN", "Admin")));
        VaiTro roleQuanLy = vaiTroRepository.findByTenVaiTroIgnoreCase("QUAN_LY")
                .orElseGet(() -> vaiTroRepository.save(new VaiTro("QUAN_LY", "Quản lý")));
        VaiTro roleSinhVien = vaiTroRepository.findByTenVaiTroIgnoreCase("SINH_VIEN")
                .orElseGet(() -> vaiTroRepository.save(new VaiTro("SINH_VIEN", "Sinh viên")));

        // Tạo người dùng Admin
        NguoiDung admin = new NguoiDung();
        admin.setMaNguoiDung("ADM01");
        admin.setHoTen("Quan Tri Vien");
        admin.setEmail("rbac_admin@ktx.edu.vn");
        admin.setMatKhau("$2a$10$PrKwSK8SErl9pdYuKOgltOr3nwweCmaZrpZCX5LiBv3LZpK4khizS");
        admin.setTrangThai("hoat_dong");
        admin.setVaiTros(Set.of(roleAdmin));
        admin = nguoiDungRepository.save(admin);
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        // Tạo người dùng Quản lý
        NguoiDung quanLy = new NguoiDung();
        quanLy.setMaNguoiDung("QL01");
        quanLy.setHoTen("Nhan Vien Quan Ly");
        quanLy.setEmail("rbac_quanly@ktx.edu.vn");
        quanLy.setMatKhau("$2a$10$KeDGKK43mVLIAYNQh0Kihe7aWkX6Yl7XqskG2gnomn5fSDu7maO.a");
        quanLy.setTrangThai("hoat_dong");
        quanLy.setVaiTros(Set.of(roleQuanLy));
        quanLy = nguoiDungRepository.save(quanLy);
        quanLyToken = jwtTokenProvider.generateToken(UserPrincipal.create(quanLy));

        // Tạo người dùng Sinh viên A
        NguoiDung userA = new NguoiDung();
        userA.setMaNguoiDung("SVA01");
        userA.setHoTen("Sinh Vien A");
        userA.setEmail("rbac_sva@ktx.edu.vn");
        userA.setMatKhau("$2a$10$9qDKoxIst0uM8ZSIYby0beqPYLraOXp6RFwzR3bhhcy/4gN9RiQXa");
        userA.setTrangThai("hoat_dong");
        userA.setVaiTros(Set.of(roleSinhVien));
        userA = nguoiDungRepository.save(userA);

        SinhVien svA = new SinhVien();
        svA.setMaSinhVien("B21DCCN001");
        svA.setNguoiDung(userA);
        svA = sinhVienRepository.save(svA);
        studentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA, svA.getId(), svA.getMaSinhVien()));

        // Tạo người dùng Sinh viên B
        NguoiDung userB = new NguoiDung();
        userB.setMaNguoiDung("SVB02");
        userB.setHoTen("Sinh Vien B");
        userB.setEmail("rbac_svb@ktx.edu.vn");
        userB.setMatKhau("$2a$10$9qDKoxIst0uM8ZSIYby0beqPYLraOXp6RFwzR3bhhcy/4gN9RiQXa");
        userB.setTrangThai("hoat_dong");
        userB.setVaiTros(Set.of(roleSinhVien));
        userB = nguoiDungRepository.save(userB);

        SinhVien svB = new SinhVien();
        svB.setMaSinhVien("B21DCCN002");
        svB.setNguoiDung(userB);
        svB = sinhVienRepository.save(svB);
        studentBToken = jwtTokenProvider.generateToken(UserPrincipal.create(userB, svB.getId(), svB.getMaSinhVien()));

        // Tạo Phòng & Hợp đồng cho A và B
        Khu khu = new Khu();
        khu.setMaKhu("K-RBAC");
        khu.setTenKhu("Khu RBAC");
        khu = khuRepository.save(khu);

        ToaNha toa = new ToaNha();
        toa.setKhu(khu);
        toa.setMaToa("T-RBAC");
        toa.setTenToa("Toa RBAC");
        toa.setSoTang(1);
        toa = toaNhaRepository.save(toa);

        Tang tang = new Tang();
        tang.setToaNha(toa);
        tang.setSoTang(1);
        tang = tangRepository.save(tang);

        Phong pA = new Phong();
        pA.setTang(tang);
        pA.setMaPhong("RBAC-101");
        pA.setSucChua(2);
        pA.setSoNguoiHienTai(1);
        pA.setLoaiPhong("2 nguoi");
        pA.setGiaPhong(new BigDecimal("1000000.00"));
        pA.setTrangThai("hoat_dong");
        pA = phongRepository.save(pA);

        Phong pB = new Phong();
        pB.setTang(tang);
        pB.setMaPhong("RBAC-102");
        pB.setSucChua(2);
        pB.setSoNguoiHienTai(1);
        pB.setLoaiPhong("2 nguoi");
        pB.setGiaPhong(new BigDecimal("1000000.00"));
        pB.setTrangThai("hoat_dong");
        pB = phongRepository.save(pB);

        HopDong hdA = new HopDong();
        hdA.setSinhVien(svA);
        hdA.setPhong(pA);
        hdA.setNgayBatDau(LocalDate.now());
        hdA.setNgayKetThuc(LocalDate.now().plusMonths(6));
        hdA.setTienPhong(new BigDecimal("1000000.00"));
        hdA.setTrangThai("dang_hieu_luc");
        hdA = hopDongRepository.save(hdA);

        HopDong hdB = new HopDong();
        hdB.setSinhVien(svB);
        hdB.setPhong(pB);
        hdB.setNgayBatDau(LocalDate.now());
        hdB.setNgayKetThuc(LocalDate.now().plusMonths(6));
        hdB.setTienPhong(new BigDecimal("1000000.00"));
        hdB.setTrangThai("dang_hieu_luc");
        hdB = hopDongRepository.save(hdB);

        // Tạo Hóa đơn
        invoiceStudentA = new KhoanThu();
        invoiceStudentA.setSinhVien(svA);
        invoiceStudentA.setHopDong(hdA);
        invoiceStudentA.setPhong(pA);
        invoiceStudentA.setMaHoaDon("INV-SVA-01");
        invoiceStudentA.setLoaiKhoanThu("hoa_don_thang");
        invoiceStudentA.setSoTien(new BigDecimal("1000000.00"));
        invoiceStudentA.setTienPhong(new BigDecimal("1000000.00"));
        invoiceStudentA.setConLai(new BigDecimal("1000000.00"));
        invoiceStudentA.setHanThanhToan(LocalDate.now().plusDays(10));
        invoiceStudentA.setTrangThai("chua_thanh_toan");
        invoiceStudentA = khoanThuRepository.save(invoiceStudentA);

        invoiceStudentB = new KhoanThu();
        invoiceStudentB.setSinhVien(svB);
        invoiceStudentB.setHopDong(hdB);
        invoiceStudentB.setPhong(pB);
        invoiceStudentB.setMaHoaDon("INV-SVB-02");
        invoiceStudentB.setLoaiKhoanThu("hoa_don_thang");
        invoiceStudentB.setSoTien(new BigDecimal("1000000.00"));
        invoiceStudentB.setTienPhong(new BigDecimal("1000000.00"));
        invoiceStudentB.setConLai(new BigDecimal("1000000.00"));
        invoiceStudentB.setHanThanhToan(LocalDate.now().plusDays(10));
        invoiceStudentB.setTrangThai("chua_thanh_toan");
        invoiceStudentB = khoanThuRepository.save(invoiceStudentB);
    }

    @Test
    @DisplayName("RBAC 1: ADMIN truy cập được API Quản lý người dùng")
    void testAdminCanAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/nguoi-dung")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RBAC 2: SINH_VIEN bị cấm truy cập API Quản lý người dùng (403 Forbidden)")
    void testStudentForbiddenFromUserManagement() throws Exception {
        mockMvc.perform(get("/api/nguoi-dung")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC 3: Yêu cầu chưa đăng nhập bị từ chối (401 Unauthorized)")
    void testUnauthenticatedAccessDenied() throws Exception {
        mockMvc.perform(get("/api/nguoi-dung")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RBAC 4: Sinh viên A xem được hóa đơn của chính mình")
    void testStudentCanAccessOwnInvoice() throws Exception {
        mockMvc.perform(get("/api/hoa-don/" + invoiceStudentA.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RBAC 5: Backend Data Ownership - Sinh viên A KHÔNG THỂ xem hóa đơn của Sinh viên B (403 Forbidden)")
    void testStudentCannotAccessOtherStudentInvoice() throws Exception {
        mockMvc.perform(get("/api/hoa-don/" + invoiceStudentB.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC 6: Quản lý hoặc Admin có quyền xem hóa đơn của mọi sinh viên")
    void testManagerCanAccessAnyInvoice() throws Exception {
        mockMvc.perform(get("/api/hoa-don/" + invoiceStudentB.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + quanLyToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
