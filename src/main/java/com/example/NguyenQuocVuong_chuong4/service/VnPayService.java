package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.config.VnPayConfig;
import com.example.NguyenQuocVuong_chuong4.model.KhoanThu;
import com.example.NguyenQuocVuong_chuong4.model.ThanhToan;
import com.example.NguyenQuocVuong_chuong4.repository.KhoanThuRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ThanhToanRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VnPayService {

    private final VnPayConfig vnPayConfig;
    private final KhoanThuRepository khoanThuRepository;
    private final ThanhToanRepository thanhToanRepository;

    public VnPayService(VnPayConfig vnPayConfig,
                         KhoanThuRepository khoanThuRepository,
                         ThanhToanRepository thanhToanRepository) {
        this.vnPayConfig = vnPayConfig;
        this.khoanThuRepository = khoanThuRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    public String createPaymentUrl(Long khoanThuId, BigDecimal customAmount, String clientIp) {
        KhoanThu kt = khoanThuRepository.findById(khoanThuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn ID: " + khoanThuId));

        if ("da_thanh_toan".equalsIgnoreCase(kt.getTrangThai()) && kt.getConLai().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hóa đơn này đã được thanh toán đầy đủ!");
        }

        BigDecimal payAmount = (customAmount != null && customAmount.compareTo(BigDecimal.ZERO) > 0)
                ? customAmount
                : kt.getConLai();

        if (payAmount.compareTo(kt.getConLai()) > 0) {
            payAmount = kt.getConLai();
        }

        long amountInVndCents = payAmount.multiply(new BigDecimal(100)).longValue();

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = kt.getId() + "_" + System.currentTimeMillis();
        String vnp_OrderInfo = "Thanh toan tien phong KTX hoa don " + (kt.getMaHoaDon() != null ? kt.getMaHoaDon() : kt.getId());
        String orderType = "other";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVndCents));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", (clientIp != null && !clientIp.isBlank()) ? clientIp : "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    hashData.append(fieldValue);
                    query.append(fieldName).append('=').append(fieldValue);
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = VnPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnPayConfig.getPaymentUrl() + "?" + query.toString();
    }

    public boolean verifySignature(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isBlank()) {
            return false;
        }

        Map<String, String> fields = new HashMap<>(params);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String calculatedHash = VnPayConfig.hashAllFields(fields, vnPayConfig.getHashSecret());
        return vnp_SecureHash.equalsIgnoreCase(calculatedHash);
    }

    public boolean verifyChecksum(Map<String, String> params) {
        return verifySignature(params);
    }

    @Transactional
    public Map<String, Object> processIpn(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();

        // 1. Kiểm tra chữ ký bảo mật
        if (!verifySignature(params)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum (Sai chữ ký bảo mật VNPAY)");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || !txnRef.contains("_")) {
            response.put("RspCode", "01");
            response.put("Message", "Order Not Found (Mã giao dịch không hợp lệ)");
            return response;
        }

        Long khoanThuId = Long.parseLong(txnRef.split("_")[0]);
        Optional<KhoanThu> ktOpt = khoanThuRepository.findById(khoanThuId);
        if (ktOpt.isEmpty()) {
            response.put("RspCode", "01");
            response.put("Message", "Order Not Found (Không tìm thấy hóa đơn)");
            return response;
        }

        KhoanThu kt = ktOpt.get();

        // 2. Kiểm tra số tiền
        long vnpAmount = Long.parseLong(params.getOrDefault("vnp_Amount", "0")) / 100;
        BigDecimal paidAmount = new BigDecimal(vnpAmount);

        // 3. Kiểm tra xem giao dịch đã xác nhận trước đó chưa (Chống IPN lặp)
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        if (vnp_TransactionNo != null && thanhToanRepository.existsByMaGiaoDich(vnp_TransactionNo)) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed (Giao dịch này đã được ghi nhận trước đó)");
            return response;
        }

        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            // Thanh toán thành công -> Ghi nhận thanh toán và trừ công nợ
            ThanhToan tt = new ThanhToan();
            tt.setKhoanThu(kt);
            tt.setSoTien(paidAmount);
            tt.setPhuongThuc("vnpay");
            tt.setMaGiaoDich(vnp_TransactionNo != null ? vnp_TransactionNo : "VNPAY-" + txnRef);
            tt.setThoiGian(LocalDateTime.now());
            tt.setTrangThai("thanh_cong");
            tt.setGhiChu("Thanh toán trực tuyến VNPAY Sandbox thành công (Ngân hàng: " + params.getOrDefault("vnp_BankCode", "VNPAY") + ")");
            tt.setNguoiThu("Cổng VNPAY");
            thanhToanRepository.save(tt);

            BigDecimal currentDaTra = kt.getDaThanhToan() != null ? kt.getDaThanhToan() : BigDecimal.ZERO;
            kt.setDaThanhToan(currentDaTra.add(paidAmount));
            kt.calculateTotals();
            khoanThuRepository.save(kt);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success (Cập nhật hóa đơn thành công)");
        } else {
            // Giao dịch không thành công hoặc người dùng hủy
            response.put("RspCode", "00");
            response.put("Message", "Giao dịch không thành công từ phía khách hàng (Mã: " + responseCode + ")");
        }

        return response;
    }
}
