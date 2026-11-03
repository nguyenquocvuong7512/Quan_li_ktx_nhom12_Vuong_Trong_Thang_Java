# BÁO CÁO KẾT QUẢ KIỂM THỬ HỆ THỐNG (TEST REPORT)
> **Dự án**: Hệ thống Quản lý Ký túc xá - Nhóm 12 (Vương Trọng Thắng)  
> **Thời gian thực hiện**: Ngày 03/11/2026  
> **Môi trường thực thi**: Java 25.0.4.1 LTS, Spring Boot 4.1.1, H2 In-Memory & MySQL 8  
> **Kết quả chung**: **25/25 Tests PASSED (Tỷ lệ thành công: 100%)**

---

## 1. TỔNG QUAN KẾT QUẢ KIỂM THỬ

| Nhóm kiểm thử (Test Suite) | Số lượng Test Cases | Trạng thái | Thời gian thực thi |
|---|:---:|:---:|:---:|
| **JwtTokenProviderTest** | 3 | ✅ PASSED (100%) | ~0.08 s |
| **SecurityAttackProtectionTests** | 4 | ✅ PASSED (100%) | ~0.04 s |
| **RbacSecurityAndOwnershipTests** | 6 | ✅ PASSED (100%) | ~0.63 s |
| **PaginationAndFilterTests** | 4 | ✅ PASSED (100%) | ~0.15 s |
| **VnPayIntegrationTests** | 3 | ✅ PASSED (100%) | ~0.08 s |
| **NguyenQuocVuongChuong4ApplicationTests** | 5 | ✅ PASSED (100%) | ~1.20 s |
| **TỔNG CỘNG** | **25** | **100% THÀNH CÔNG** | **~2.18 s** |

---

## 2. CHI TIẾT CÁC BÀI KIỂM THỬ THEO TỪNG HẠNG MỤC

### 2.1. Hạng mục Security & RBAC Phân quyền

#### Test Suite: `JwtTokenProviderTest`
1. `testGenerateAndValidateToken`: Tạo JWT Access Token chứa các trường định danh (`userId`, `email`, `roles`), tiến hành kiểm tra tính toàn vẹn chữ ký HMAC-SHA256.  
   👉 **Kết quả**: `PASSED` - Token hợp lệ, trích xuất chính xác username.
2. `testExtractRolesFromToken`: Trích xuất danh sách vai trò (`ROLE_SINH_VIEN`) từ claims của token.  
   👉 **Kết quả**: `PASSED` - Trích xuất đúng quyền từ payload.
3. `testRejectTamperedToken`: Kiểm tra khả năng từ chối khi token bị kẻ tấn công sửa đổi chữ ký hoặc gửi chuỗi rác.  
   👉 **Kết quả**: `PASSED` - `validateToken` trả về `false`, từ chối truy cập.

#### Test Suite: `SecurityAttackProtectionTests`
1. `testBruteForceAttackDefense`: Mô phỏng kẻ tấn công thử mật khẩu sai liên tiếp. Lần 1-4 trả về `401 Unauthorized`. Lần thứ 5 và 6 bị kích hoạt khóa với mã lỗi HTTP `429 TOO_MANY_REQUESTS`.  
   👉 **Kết quả**: `PASSED` - Chặn đứng tấn công Brute-force.
2. `testSqlInjectionDefense`: Gửi chuỗi tấn công SQLi kinh điển `' OR '1'='1' --` vào repository. JPA sử dụng Parameterized Query an toàn.  
   👉 **Kết quả**: `PASSED` - Truy vấn trả về empty, không thể bypass.
3. `testXssProtection`: Gửi chuỗi chứa mã độc JavaScript `<script>alert('hack')</script>` và các sự kiện inline `onerror`.  
   👉 **Kết quả**: `PASSED` - XssProtectionUtil bóc tách hoàn toàn script độc hại.
4. `testSecurityHeadersFilter`: Kiểm tra bộ header an ninh trình duyệt chống Clickjacking và MIME sniffing (`X-Frame-Options: SAMEORIGIN`, `X-Content-Type-Options: nosniff`).  
   👉 **Kết quả**: `PASSED` - Đầy đủ 4 headers an ninh.

#### Test Suite: `RbacSecurityAndOwnershipTests`
1. `testAdminCanAccessUserManagement`: Admin gửi Bearer Token truy cập endpoint nhạy cảm `/api/nguoi-dung`.  
   👉 **Kết quả**: `PASSED` - Trả về `200 OK`.
2. `testStudentForbiddenFromUserManagement`: Sinh viên gửi Bearer Token truy cập `/api/nguoi-dung`.  
   👉 **Kết quả**: `PASSED` - Trả về `403 FORBIDDEN`.
3. `testUnauthenticatedAccessDenied`: Người dùng chưa đăng nhập truy cập API nghiệp vụ.  
   👉 **Kết quả**: `PASSED` - Trả về `401 UNAUTHORIZED`.
4. `testStudentCanAccessOwnInvoice`: Sinh viên A truy cập hóa đơn phòng của chính mình.  
   👉 **Kết quả**: `PASSED` - Trả về `200 OK`.
5. `testStudentCannotAccessOtherStudentInvoice`: **Kiểm tra quyền sở hữu dữ liệu (Backend Data Ownership)**. Sinh viên A cố tình thay đổi ID để truy cập hóa đơn của Sinh viên B.  
   👉 **Kết quả**: `PASSED` - Backend kiểm tra IDOR và từ chối với `403 FORBIDDEN`.
6. `testManagerCanAccessAnyInvoice`: Quản lý có quyền tra cứu hóa đơn của bất kỳ sinh viên nào.  
   👉 **Kết quả**: `PASSED` - Trả về `200 OK`.

---

### 2.2. Hạng mục Phân trang & Tìm kiếm (Backend Pagination)

#### Test Suite: `PaginationAndFilterTests`
1. `testKhoanThuPagination`: Phân trang danh sách Hóa đơn với `page = 0, size = 5`.  
   👉 **Kết quả**: `PASSED` - Trả về đúng 5 bản ghi, `totalElements >= 8`, `totalPages >= 2`, cờ `first = true`, `last = false`.
2. `testKhoanThuFilterByStatus`: Phân trang kết hợp bộ lọc trạng thái `chua_thanh_toan`.  
   👉 **Kết quả**: `PASSED` - Toàn bộ dữ liệu trả về đều có trạng thái chính xác.
3. `testPhongPagination`: Phân trang danh sách Phòng với `size = 6`.  
   👉 **Kết quả**: `PASSED` - Trả về đúng 6 phòng mỗi trang.
4. `testSinhVienSearchAndPagination`: Phân trang kết hợp tìm kiếm từ khóa mã sinh viên `B22DCCN00`.  
   👉 **Kết quả**: `PASSED` - 100% bản ghi trả về khớp với từ khóa tìm kiếm.

---

### 2.3. Hạng mục Cổng thanh toán trực tuyến (VNPAY Sandbox)

#### Test Suite: `VnPayIntegrationTests`
1. `testCreatePaymentUrl`: Khởi tạo liên kết thanh toán sang cổng VNPAY. Kiểm tra các tham số bắt buộc `vnp_TmnCode`, `vnp_Amount`, `vnp_TxnRef` và chữ ký số `vnp_SecureHash`.  
   👉 **Kết quả**: `PASSED` - URL hợp lệ, đúng cấu trúc sandbox.
2. `testVerifyChecksum`: Kiểm tra hàm xác thực chữ ký HMAC-SHA512. Thử nghiệm thay đổi số tiền (giả mạo tham số) để kiểm chứng khả năng phát hiện dữ liệu bị can thiệp.  
   👉 **Kết quả**: `PASSED` - Xác nhận thành công dữ liệu chuẩn và từ chối dữ liệu bị chỉnh sửa.
3. `testProcessIpnSuccessAndIdempotency`: Mô phỏng gọi IPN callback từ VNPAY. Lần 1 xử lý ghi nhận thanh toán và cập nhật hóa đơn sang `da_thanh_toan`. Lần 2 gọi lại cùng mã giao dịch -> Trả về mã `02` (Order already confirmed) để chống xử lý lặp.  
   👉 **Kết quả**: `PASSED` - Đảm bảo tính toàn vẹn và chống trùng lặp thanh toán.

---

### 2.4. Hạng mục Nghiệp vụ Ký túc xá & Vòng đời hợp đồng

#### Test Suite: `NguyenQuocVuongChuong4ApplicationTests`
1. `savesRelatedEntitiesAndAuthenticatesWithHashedPassword`: Kiểm tra lưu trữ toàn vẹn liên kết giữa Khu -> Tòa -> Tầng -> Phòng -> Sinh viên -> Hợp đồng và mã hóa mật khẩu BCrypt.  
   👉 **Kết quả**: `PASSED`.
2. `rejectsRoomCapacityAndInvalidContractDates`: Kiểm tra tính hợp lệ của nghiệp vụ: số người không được vượt sức chứa; ngày kết thúc hợp đồng không được trước ngày bắt đầu.  
   👉 **Kết quả**: `PASSED`.
3. `dashboardAndLoaiPhongEndpointsReturnAccurateAggregations`: Thống kê số lượng sinh viên, phòng và loại phòng.  
   👉 **Kết quả**: `PASSED`.
4. `testPerson3ContractLifecycleAndRoomAllocation`: Kiểm tra toàn bộ vòng đời hợp đồng: Đăng ký phòng (`cho_duyet`) -> Phê duyệt (`dang_hieu_luc`, tăng số người) -> Chuyển phòng (giảm phòng cũ, tăng phòng mới) -> Thanh lý hợp đồng (hoàn trả số người về 0).  
   👉 **Kết quả**: `PASSED`.

---

## 3. LỆNH CHẠY KIỂM THỬ TRÊN MÁY BẢO VỆ ĐỒ ÁN

```powershell
.\mvnw.cmd test
```

Toàn bộ 25 bài kiểm thử chạy tự động trong khoảng 20 giây và hiển thị:
```
[INFO] Results:
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

