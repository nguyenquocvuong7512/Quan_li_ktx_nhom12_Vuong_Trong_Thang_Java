package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.config.VnPayConfig;
import com.example.NguyenQuocVuong_chuong4.model.*;
import com.example.NguyenQuocVuong_chuong4.repository.*;
import com.example.NguyenQuocVuong_chuong4.service.VnPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class VnPayIntegrationTests {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private KhoanThuRepository khoanThuRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;

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

    private KhoanThu testInvoice;

    @BeforeEach
    void setupInvoice() {
        NguoiDung u = new NguoiDung();
        u.setMaNguoiDung("VNP-USER");
        u.setHoTen("VNPAY Tester");
        u.setEmail("vnpay_tester@ktx.edu.vn");
        u.setMatKhau("$2a$10$9qDKoxIst0uM8ZSIYby0beqPYLraOXp6RFwzR3bhhcy/4gN9RiQXa");
        u.setTrangThai("hoat_dong");
        u = nguoiDungRepository.save(u);

        SinhVien sv = new SinhVien();
        sv.setMaSinhVien("VNP-SV01");
        sv.setNguoiDung(u);
        sv = sinhVienRepository.save(sv);

        Khu khu = new Khu();
        khu.setMaKhu("K-VNP");
        khu.setTenKhu("Khu VNPAY");
        khu = khuRepository.save(khu);

        ToaNha toa = new ToaNha();
        toa.setKhu(khu);
        toa.setMaToa("T-VNP");
        toa.setTenToa("Toa VNPAY");
        toa.setSoTang(1);
        toa = toaNhaRepository.save(toa);

        Tang tang = new Tang();
        tang.setToaNha(toa);
        tang.setSoTang(1);
        tang = tangRepository.save(tang);

        Phong p = new Phong();
        p.setTang(tang);
        p.setMaPhong("VNP-101");
        p.setSucChua(2);
        p.setSoNguoiHienTai(1);
        p.setLoaiPhong("2 nguoi");
        p.setGiaPhong(new BigDecimal("2000000.00"));
        p.setTrangThai("hoat_dong");
        p = phongRepository.save(p);

        HopDong hd = new HopDong();
        hd.setSinhVien(sv);
        hd.setPhong(p);
        hd.setNgayBatDau(LocalDate.now());
        hd.setNgayKetThuc(LocalDate.now().plusMonths(6));
        hd.setTienPhong(new BigDecimal("2000000.00"));
        hd.setTrangThai("dang_hieu_luc");
        hd = hopDongRepository.save(hd);

        testInvoice = new KhoanThu();
        testInvoice.setSinhVien(sv);
        testInvoice.setHopDong(hd);
        testInvoice.setPhong(p);
        testInvoice.setMaHoaDon("INV-VNP-001");
        testInvoice.setLoaiKhoanThu("hoa_don_thang");
        testInvoice.setSoTien(new BigDecimal("2000000.00"));
        testInvoice.setTienPhong(new BigDecimal("2000000.00"));
        testInvoice.setConLai(new BigDecimal("2000000.00"));
        testInvoice.setHanThanhToan(LocalDate.now().plusDays(10));
        testInvoice.setTrangThai("chua_thanh_toan");
        testInvoice = khoanThuRepository.save(testInvoice);
    }

    @Test
    @DisplayName("VNPAY 1: Tạo Payment URL chứa chữ ký HMAC-SHA512 hợp lệ")
    void testCreatePaymentUrl() {
        String paymentUrl = vnPayService.createPaymentUrl(testInvoice.getId(), new BigDecimal("2000000.00"), "127.0.0.1");

        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.startsWith(vnPayConfig.getPaymentUrl()));
        assertTrue(paymentUrl.contains("vnp_TmnCode=" + vnPayConfig.getTmnCode()));
        assertTrue(paymentUrl.contains("vnp_Amount=200000000")); // Số tiền x 100
        assertTrue(paymentUrl.contains("vnp_SecureHash="));
    }

    @Test
    @DisplayName("VNPAY 2: Kiểm tra xác thực chữ ký số Checksum và chống giả mạo")
    void testVerifyChecksum() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", "200000000");
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_CardType", "ATM");
        params.put("vnp_OrderInfo", "Thanh toan hoa don");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_TransactionNo", "14285700");
        params.put("vnp_TxnRef", "INV-VNP-001");

        // Ký HMAC-SHA512
        String signValue = vnPayConfig.hashAllFields(params);
        params.put("vnp_SecureHash", signValue);

        assertTrue(vnPayService.verifyChecksum(params), "Checksum hợp lệ phải được xác nhận thành công!");

        // Giả mạo số tiền (Tampered data)
        params.put("vnp_Amount", "100000000");
        assertFalse(vnPayService.verifyChecksum(params), "Dữ liệu bị sửa đổi phải bị từ chối Checksum!");
    }

    @Test
    @DisplayName("VNPAY 3: Xử lý IPN thành công và cơ chế chống xử lý lặp Idempotency")
    void testProcessIpnSuccessAndIdempotency() {
        String txnRef = testInvoice.getId() + "_1700000000";

        Map<String, String> ipnParams = new HashMap<>();
        ipnParams.put("vnp_Amount", "200000000");
        ipnParams.put("vnp_ResponseCode", "00");
        ipnParams.put("vnp_TransactionNo", "VNP_TXN_9999");
        ipnParams.put("vnp_TxnRef", txnRef);
        ipnParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        String sign = vnPayConfig.hashAllFields(ipnParams);
        ipnParams.put("vnp_SecureHash", sign);

        // Lần 1: Xử lý thành công
        Map<String, Object> response1 = vnPayService.processIpn(ipnParams);
        assertEquals("00", response1.get("RspCode"));
        assertTrue(response1.get("Message").toString().contains("Success"));

        KhoanThu updatedInvoice = khoanThuRepository.findById(testInvoice.getId()).orElseThrow();
        assertEquals("da_thanh_toan", updatedInvoice.getTrangThai(), "Hóa đơn phải chuyển sang trạng thái đã thanh toán!");

        assertTrue(thanhToanRepository.existsByMaGiaoDich("VNP_TXN_9999"), "Bản ghi ThanhToan phải được tạo!");

        // Lần 2: Gọi lại IPN với cùng mã giao dịch (Idempotency check)
        Map<String, Object> response2 = vnPayService.processIpn(ipnParams);
        assertEquals("02", response2.get("RspCode"), "Lần 2 phải trả về mã 02 - Order already confirmed!");
        assertTrue(response2.get("Message").toString().contains("Order already confirmed"));
    }
}
