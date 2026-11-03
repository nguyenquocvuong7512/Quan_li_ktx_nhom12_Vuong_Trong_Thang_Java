package com.example.NguyenQuocVuong_chuong4.controller;

import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.repository.KhoanThuRepository;
import com.example.NguyenQuocVuong_chuong4.security.SecurityUtils;
import com.example.NguyenQuocVuong_chuong4.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final VnPayService vnPayService;
    private final KhoanThuRepository khoanThuRepository;

    public PaymentController(VnPayService vnPayService, KhoanThuRepository khoanThuRepository) {
        this.vnPayService = vnPayService;
        this.khoanThuRepository = khoanThuRepository;
    }

    @PostMapping("/vnpay/create")
    public ResponseEntity<Map<String, Object>> createVnPayUrl(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!body.containsKey("khoanThuId")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu tham số khoanThuId");
        }
        Long khoanThuId = Long.valueOf(body.get("khoanThuId").toString());

        KhoanThu kt = khoanThuRepository.findById(khoanThuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn ID: " + khoanThuId));

        if (SecurityUtils.isStudent()) {
            SecurityUtils.validateOwnershipOrStaff(
                    kt.getSinhVien() != null ? kt.getSinhVien().getId() : null,
                    "Bạn không có quyền thanh toán hóa đơn của sinh viên khác!"
            );
        }

        BigDecimal customAmount = null;
        if (body.containsKey("soTien") && body.get("soTien") != null) {
            customAmount = new BigDecimal(body.get("soTien").toString());
        }

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }

        String paymentUrl = vnPayService.createPaymentUrl(khoanThuId, customAmount, clientIp);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("paymentUrl", paymentUrl);
        response.put("khoanThuId", khoanThuId);
        response.put("amount", customAmount != null ? customAmount : kt.getConLai());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public RedirectView handleVnPayReturn(@RequestParam Map<String, String> params) {
        boolean validSignature = vnPayService.verifySignature(params);
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String vnpAmount = params.get("vnp_Amount");

        String redirectUrl = "/hoa-don.html";
        if (validSignature && "00".equals(responseCode)) {
            // Cũng gọi xử lý IPN trực tiếp phòng trường hợp môi trường sandbox local không có public IPN
            vnPayService.processIpn(params);
            redirectUrl += "?vnpay_status=success&txnRef=" + txnRef + "&amount=" + vnpAmount;
        } else {
            redirectUrl += "?vnpay_status=failed&code=" + responseCode;
        }
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, Object>> handleVnPayIpn(@RequestParam Map<String, String> params) {
        Map<String, Object> ipnResult = vnPayService.processIpn(params);
        return ResponseEntity.ok(ipnResult);
    }
}

