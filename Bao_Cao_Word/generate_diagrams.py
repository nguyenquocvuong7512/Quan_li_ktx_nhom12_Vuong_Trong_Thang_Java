import os
import sys
import tempfile

# Configure matplotlib to use temp dir for config/cache
os.environ['MPLCONFIGDIR'] = tempfile.gettempdir()

import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch, BoxStyle, ArrowStyle
import numpy as np

# Set standard fonts
plt.rcParams['font.sans-serif'] = ['Segoe UI', 'Arial', 'DejaVu Sans']
plt.rcParams['font.family'] = 'sans-serif'
plt.rcParams['figure.autolayout'] = False

OUTPUT_DIR = r"D:\Quan_li_ktx_nhom12_Vuong_Trong_Thang_Java\Bao_Cao_Word\diagrams"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Common styling palette
C_BG = "#F8FAFC"
C_PRIMARY = "#1E40AF"     # Deep Blue
C_PRIMARY_LIGHT = "#EFF6FF"
C_SECONDARY = "#0D9488"   # Teal
C_SECONDARY_LIGHT = "#F0FDFA"
C_ACCENT = "#D97706"      # Amber
C_ACCENT_LIGHT = "#FFFBEB"
C_DANGER = "#DC2626"      # Red
C_DANGER_LIGHT = "#FEF2F2"
C_DARK = "#0F172A"        # Slate 900
C_TEXT = "#1E293B"        # Slate 800
C_MUTED = "#64748B"       # Slate 500
C_BORDER = "#CBD5E1"      # Slate 300
C_WHITE = "#FFFFFF"
C_SUCCESS = "#16A34A"     # Green
C_SUCCESS_LIGHT = "#F0FDF4"
C_PURPLE = "#7C3AED"
C_PURPLE_LIGHT = "#F5F3FF"

# Helper for drawing cards
def draw_card(ax, x, y, w, h, title, items=None, bg_color=C_WHITE, border_color=C_BORDER, title_color=C_PRIMARY, title_bg=None, corner=0.03):
    box = FancyBboxPatch((x, y), w, h, boxstyle=f"round,pad=0,rounding_size={corner}",
                         facecolor=bg_color, edgecolor=border_color, linewidth=1.5, zorder=2)
    ax.add_patch(box)
    
    if title_bg:
        t_box = FancyBboxPatch((x, y + h - 0.35), w, 0.35, boxstyle=f"round,pad=0,rounding_size={corner}",
                               facecolor=title_bg, edgecolor="none", zorder=3)
        ax.add_patch(t_box)
        # cover lower round corners of title
        t_rect = patches.Rectangle((x, y + h - 0.35), w, 0.15, facecolor=title_bg, edgecolor="none", zorder=3)
        ax.add_patch(t_rect)
        ax.text(x + w/2, y + h - 0.18, title, ha='center', va='center', fontsize=10, fontweight='bold', color=C_WHITE, zorder=4)
    else:
        ax.text(x + w/2, y + h - 0.22, title, ha='center', va='center', fontsize=10, fontweight='bold', color=title_color, zorder=4)
        ax.plot([x + 0.1, x + w - 0.1], [y + h - 0.4, y + h - 0.4], color=border_color, lw=1, zorder=3)
        
    if items:
        cur_y = y + h - 0.55
        for item in items:
            ax.text(x + 0.15, cur_y, item, ha='left', va='center', fontsize=8.5, color=C_TEXT, zorder=4)
            cur_y -= 0.24

# -------------------------------------------------------------
# 1. SƠ ĐỒ KIẾN TRÚC TỔNG THỂ HỆ THỐNG
# -------------------------------------------------------------
def make_system_architecture():
    fig, ax = plt.subplots(figsize=(13, 8.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 14)
    ax.set_ylim(0, 9)
    ax.axis('off')
    
    # Title
    ax.text(7, 8.6, "SƠ ĐỒ KIẾN TRÚC TỔNG THỂ HỆ THỐNG QUẢN LÝ KÝ TÚC XÁ", 
            ha='center', va='center', fontsize=14, fontweight='bold', color=C_DARK)
    ax.text(7, 8.25, "Mô hình phân tầng doanh nghiệp đa lớp trên nền tảng Java 25 & Spring Boot", 
            ha='center', va='center', fontsize=9.5, style='italic', color=C_MUTED)
    
    # Layer 1: Client Layer
    l1_box = FancyBboxPatch((0.5, 6.4), 13, 1.45, boxstyle="round,pad=0,rounding_size=0.04",
                            facecolor=C_PRIMARY_LIGHT, edgecolor=C_PRIMARY, linewidth=1.5, linestyle="--")
    ax.add_patch(l1_box)
    ax.text(0.8, 7.6, "1. TẦNG KHÁCH HÀNG (CLIENT PRESENTATION LAYER)", fontsize=10.5, fontweight='bold', color=C_PRIMARY)
    
    draw_card(ax, 1.0, 6.55, 3.5, 0.9, "Giao diện Quản trị Web (Admin)", 
              ["• Quản lý Tòa, Tầng, Phòng, Sinh viên", "• Quản lý Hợp đồng, Hóa đơn, Thiết bị"], C_WHITE, C_PRIMARY, C_PRIMARY)
    draw_card(ax, 5.0, 6.55, 3.8, 0.9, "Giao diện Sinh viên / Đăng ký", 
              ["• Đăng ký phòng online, Tra cứu công nợ", "• Thanh toán VietQR 24/7 & Ví MoMo"], C_WHITE, C_PRIMARY, C_PRIMARY)
    draw_card(ax, 9.3, 6.55, 3.8, 0.9, "Mô phỏng 3D Campus Ký túc xá", 
              ["• Thư viện Three.js WebGL Engine", "• Trực quan hóa tòa nhà & không gian KTX"], C_WHITE, C_PRIMARY, C_PRIMARY)
    
    # Down arrow 1
    ax.annotate('', xy=(7, 6.25), xytext=(7, 6.4),
                arrowprops=dict(facecolor=C_PRIMARY, edgecolor=C_PRIMARY, width=2, headwidth=8, headlength=6))
    ax.text(7.2, 6.32, "HTTPS / REST API JSON", fontsize=8.5, fontweight='bold', color=C_PRIMARY)

    # Layer 2: Security & Filter Layer
    l2_box = FancyBboxPatch((0.5, 4.8), 13, 1.35, boxstyle="round,pad=0,rounding_size=0.04",
                            facecolor=C_DANGER_LIGHT, edgecolor=C_DANGER, linewidth=1.5, linestyle="--")
    ax.add_patch(l2_box)
    ax.text(0.8, 5.95, "2. TẦNG AN NINH & BẢO VỆ MẠNG (SECURITY & FILTER LAYER)", fontsize=10.5, fontweight='bold', color=C_DANGER)
    
    draw_card(ax, 1.0, 4.95, 3.6, 0.85, "LoginAttemptService", 
              ["• Chống Brute-Force / Dictionary", "• Khóa tạm thời 15p sau 5 lần sai"], C_WHITE, C_DANGER, C_DANGER)
    draw_card(ax, 5.1, 4.95, 3.6, 0.85, "SecurityHeadersFilter", 
              ["• X-Frame-Options (Chống Clickjacking)", "• X-Content-Type-Options (nosniff)"], C_WHITE, C_DANGER, C_DANGER)
    draw_card(ax, 9.2, 4.95, 3.9, 0.85, "XssProtectionUtil & BCrypt", 
              ["• Làm sạch mã độc HTML / XSS Scripts", "• BCrypt PasswordEncoder băm mật khẩu"], C_WHITE, C_DANGER, C_DANGER)

    # Down arrow 2
    ax.annotate('', xy=(7, 4.65), xytext=(7, 4.8),
                arrowprops=dict(facecolor=C_DANGER, edgecolor=C_DANGER, width=2, headwidth=8, headlength=6))

    # Layer 3: Application & Business Service Layer
    l3_box = FancyBboxPatch((0.5, 2.5), 13, 2.05, boxstyle="round,pad=0,rounding_size=0.04",
                            facecolor=C_SECONDARY_LIGHT, edgecolor=C_SECONDARY, linewidth=1.5, linestyle="--")
    ax.add_patch(l3_box)
    ax.text(0.8, 4.35, "3. TẦNG XỬ LÝ NGHIỆP VỤ & ĐIỀU HƯỚNG (APPLICATION & BUSINESS SERVICE LAYER)", fontsize=10.5, fontweight='bold', color=C_SECONDARY)
    
    # Controllers sub-box
    draw_card(ax, 1.0, 2.65, 5.6, 1.55, "REST Controllers (Spring Web MVC)", 
              ["• AuthController, DashboardController, WebViewController",
               "• SinhVienController, PhongController, LoaiPhongController",
               "• KhuController, ToaNhaController, TangController",
               "• HopDongController, SinhVienPhongController",
               "• HoaDonController (Lập loạt, QR), ThietBiController",
               "• ApiExceptionHandler (Xử lý lỗi toàn cục @RestControllerAdvice)"], C_WHITE, C_SECONDARY, C_SECONDARY)

    # Services sub-box
    draw_card(ax, 7.2, 2.65, 5.9, 1.55, "Business Services (@Service, @Transactional)", 
              ["• NguoiDungService: Xác thực, mã hóa BCrypt, kiểm tra trạng thái",
               "• HopDongService: Vòng đời HĐ, duyệt, gia hạn, chuyển phòng",
               "• KhoanThuService: Tính toán hóa đơn, thanh toán, công nợ",
               "• ThietBiService: Cấp phát phòng, báo hỏng, sửa chữa, lịch sử",
               "• PhongService, SinhVienService, ToaNhaService, TangService",
               "• MoneyToWordsUtil: Đọc số tiền hóa đơn thành chữ tiếng Việt"], C_WHITE, C_SECONDARY, C_SECONDARY)

    # Down arrow 3
    ax.annotate('', xy=(7, 2.35), xytext=(7, 2.5),
                arrowprops=dict(facecolor=C_SECONDARY, edgecolor=C_SECONDARY, width=2, headwidth=8, headlength=6))

    # Layer 4: Data Access & Database
    l4_box = FancyBboxPatch((0.5, 0.4), 13, 1.85, boxstyle="round,pad=0,rounding_size=0.04",
                            facecolor=C_PURPLE_LIGHT, edgecolor=C_PURPLE, linewidth=1.5, linestyle="--")
    ax.add_patch(l4_box)
    ax.text(0.8, 2.05, "4. TẦNG TRUY XUẤT DỮ LIỆU & CƠ SỞ DỮ LIỆU (PERSISTENCE & DATA LAYER)", fontsize=10.5, fontweight='bold', color=C_PURPLE)

    draw_card(ax, 1.0, 0.55, 6.2, 1.35, "Spring Data JPA & Hibernate ORM 7", 
              ["• Repositories: SinhVien, Phong, HopDong, KhoanThu, ThanhToan...",
               "• Derived Queries, JPQL Custom Filter & Tham số hóa PreparedStatements",
               "• Tự động chống SQL Injection & Quản lý vòng đời Entity"], C_WHITE, C_PURPLE, C_PURPLE)

    draw_card(ax, 7.7, 0.55, 5.4, 1.35, "Cơ Sở Dữ Liệu Quan Hệ (MySQL 8 / MariaDB)", 
              ["• 15 Bảng dữ liệu quan hệ (nguoi_dung, phong, hop_dong...)",
               "• Toàn vẹn tham chiếu PK/FK, Khóa ngoại Cascade/Restrict",
               "• H2 In-Memory Database phục vụ Unit & Integration Test"], C_WHITE, C_PURPLE, C_PURPLE)

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_4_1_kien_truc_tong_the.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 2. SƠ ĐỒ USE CASE TỔNG QUÁT
# -------------------------------------------------------------
def make_usecase_general():
    fig, ax = plt.subplots(figsize=(14, 9.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 15)
    ax.set_ylim(0, 10.5)
    ax.axis('off')

    ax.text(7.5, 10.0, "SƠ ĐỒ USE CASE TỔNG QUÁT HỆ THỐNG QUẢN LÝ KÝ TÚC XÁ", 
            ha='center', va='center', fontsize=14, fontweight='bold', color=C_DARK)
    ax.text(7.5, 9.65, "Phân quyền và chức năng thực tế của các tác nhân trong hệ thống", 
            ha='center', va='center', fontsize=9.5, style='italic', color=C_MUTED)

    # Boundary Box
    system_box = FancyBboxPatch((3.2, 0.5), 8.8, 8.8, boxstyle="round,pad=0,rounding_size=0.03",
                               facecolor=C_WHITE, edgecolor=C_PRIMARY, linewidth=2)
    ax.add_patch(system_box)
    ax.text(7.6, 9.05, "HỆ THỐNG QUẢN LÝ KÝ TÚC XÁ (KTX DORMITORY SYSTEM)", 
            ha='center', va='center', fontsize=11, fontweight='bold', color=C_PRIMARY)

    # Actor drawing helper
    def draw_actor(x, y, name, role_desc):
        # Head
        circle = plt.Circle((x, y + 0.35), 0.18, facecolor=C_PRIMARY_LIGHT, edgecolor=C_PRIMARY, lw=1.5, zorder=5)
        ax.add_patch(circle)
        # Body
        ax.plot([x, x], [y + 0.17, y - 0.25], color=C_PRIMARY, lw=2, zorder=5)
        # Arms
        ax.plot([x - 0.3, x + 0.3], [y + 0.05, y + 0.05], color=C_PRIMARY, lw=2, zorder=5)
        # Legs
        ax.plot([x, x - 0.25], [y - 0.25, y - 0.65], color=C_PRIMARY, lw=2, zorder=5)
        ax.plot([x, x + 0.25], [y - 0.25, y - 0.65], color=C_PRIMARY, lw=2, zorder=5)
        # Label
        ax.text(x, y - 0.85, name, ha='center', va='center', fontsize=9.5, fontweight='bold', color=C_DARK, zorder=5)
        ax.text(x, y - 1.05, role_desc, ha='center', va='center', fontsize=8, color=C_MUTED, zorder=5)

    draw_actor(1.5, 7.8, "Quản Trị Viên", "(quan_tri / Admin)")
    draw_actor(1.5, 3.8, "Quản Lý KTX", "(quan_ly_ky_tuc)")
    draw_actor(13.5, 5.0, "Sinh Viên", "(sinh_vien / Student)")

    # Use Case Ellipse helper
    def draw_uc(x, y, w, h, text, bg=C_PRIMARY_LIGHT, border=C_PRIMARY):
        e = patches.Ellipse((x, y), w, h, facecolor=bg, edgecolor=border, lw=1.4, zorder=3)
        ax.add_patch(e)
        ax.text(x, y, text, ha='center', va='center', fontsize=8.5, fontweight='bold', color=C_TEXT, zorder=4)

    # Column 1 (Admin & Common): x = 5.2
    draw_uc(5.3, 8.4, 3.4, 0.65, "Đăng nhập hệ thống\n(Chống Brute-force)")
    draw_uc(5.3, 7.4, 3.4, 0.65, "Quản lý tài khoản & vai trò\n(nguoi_dung & vai_tro)")
    draw_uc(5.3, 6.4, 3.4, 0.65, "Quản lý cơ sở vật chất\n(Khu, Tòa nhà, Tầng, Phòng)")
    draw_uc(5.3, 5.4, 3.4, 0.65, "Quản lý hồ sơ sinh viên\n(Thêm, sửa, xóa, gắn user)")
    draw_uc(5.3, 4.4, 3.4, 0.65, "Dashboard thống kê KTX\n(Phòng trống, doanh thu, nợ)")
    draw_uc(5.3, 3.4, 3.4, 0.65, "Quản lý danh mục thiết bị\n(loai_tai_san, tai_san)")

    # Column 2 (Manager & Student): x = 9.8
    draw_uc(9.7, 8.4, 3.4, 0.65, "Đăng ký thuê phòng online\n(dang-ky-phong.html)")
    draw_uc(9.7, 7.3, 3.4, 0.7, "Duyệt / Từ chối hợp đồng\n(Cập nhật số người & trạng thái)")
    draw_uc(9.7, 6.1, 3.4, 0.7, "Gia hạn, chuyển phòng, trả phòng\n(Điều chuyển chỗ ở tự động)")
    draw_uc(9.7, 4.9, 3.4, 0.7, "Lập hóa đơn thu phí hàng loạt\n(Tiền phòng, điện, nước, net)")
    draw_uc(9.7, 3.7, 3.4, 0.7, "Thanh toán VietQR & MoMo\n(Quét mã QR 24/7, in phiếu thu)")
    draw_uc(9.7, 2.5, 3.4, 0.7, "Cấp phát, báo hỏng, sửa chữa\n(Theo dõi lịch sử tài sản phòng)")
    draw_uc(9.7, 1.3, 3.4, 0.65, "Xem bản đồ 3D Campus KTX\n(Mô phỏng Three.js)")

    # Lines from Admin (1.5, 7.8)
    admin_pos = (1.8, 7.8)
    for target in [(3.6, 8.4), (3.6, 7.4), (3.6, 6.4), (3.6, 5.4), (3.6, 4.4), (3.6, 3.4)]:
        ax.plot([admin_pos[0], target[0]], [admin_pos[1], target[1]], color=C_PRIMARY, lw=1.2, zorder=2)

    # Lines from Manager (1.5, 3.8)
    mgr_pos = (1.8, 3.8)
    for target in [(3.6, 8.4), (3.6, 6.4), (3.6, 5.4), (3.6, 4.4), (8.0, 7.3), (8.0, 6.1), (8.0, 4.9), (8.0, 3.7), (8.0, 2.5)]:
        ax.plot([mgr_pos[0], target[0]], [mgr_pos[1], target[1]], color=C_SECONDARY, lw=1.2, linestyle="-", zorder=2)

    # Lines from Student (13.5, 5.0)
    stu_pos = (13.2, 5.0)
    for target in [(7.0, 8.4), (11.4, 8.4), (11.4, 6.1), (11.4, 3.7), (11.4, 1.3)]:
        ax.plot([stu_pos[0], target[0]], [stu_pos[1], target[1]], color=C_ACCENT, lw=1.2, linestyle="-", zorder=2)

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_1_usecase_tong_quat.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 3. SƠ ĐỒ HOẠT ĐỘNG: ĐĂNG NHẬP & CHỐNG BRUTE-FORCE
# -------------------------------------------------------------
def make_activity_login():
    fig, ax = plt.subplots(figsize=(11, 8.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 11)
    ax.set_ylim(0, 9)
    ax.axis('off')

    ax.text(5.5, 8.6, "SƠ ĐỒ HOẠT ĐỘNG (ACTIVITY DIAGRAM): QUY TRÌNH ĐĂNG NHẬP HỆ THỐNG", 
            ha='center', va='center', fontsize=12.5, fontweight='bold', color=C_DARK)
    ax.text(5.5, 8.25, "Cơ chế xác thực tài khoản và phòng chống tấn công dò mật khẩu (Brute-force Defense)", 
            ha='center', va='center', fontsize=9, style='italic', color=C_MUTED)

    # Swimlanes
    ax.plot([3.8, 3.8], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.plot([7.4, 7.4], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.text(1.9, 7.8, "Người Dùng (Client)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_PRIMARY)
    ax.text(5.6, 7.8, "AuthController & Security Filter", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_DANGER)
    ax.text(9.2, 7.8, "NguoiDungService & Database", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_SECONDARY)

    # Start Node
    start = plt.Circle((1.9, 7.2), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(start)

    def draw_act(x, y, w, h, text, bg=C_WHITE, border=C_PRIMARY):
        box = FancyBboxPatch((x - w/2, y - h/2), w, h, boxstyle="round,pad=0,rounding_size=0.08",
                             facecolor=bg, edgecolor=border, lw=1.3, zorder=3)
        ax.add_patch(box)
        ax.text(x, y, text, ha='center', va='center', fontsize=8.5, fontweight='bold', color=C_TEXT, zorder=4)

    def draw_decision(x, y, size, text):
        diamond = patches.RegularPolygon((x, y), numVertices=4, radius=size, orientation=0,
                                         facecolor=C_ACCENT_LIGHT, edgecolor=C_ACCENT, lw=1.3, zorder=3)
        ax.add_patch(diamond)
        ax.text(x, y, text, ha='center', va='center', fontsize=8, fontweight='bold', color=C_DARK, zorder=4)

    draw_act(1.9, 6.2, 2.8, 0.7, "Nhập Email/Mã ND\nvà Mật khẩu")
    ax.annotate('', xy=(1.9, 6.55), xytext=(1.9, 7.04), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Send request
    ax.annotate('', xy=(4.2, 6.2), xytext=(3.3, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PRIMARY))
    ax.text(3.75, 6.35, "POST /api/auth/login", fontsize=7.5, ha='center', color=C_PRIMARY)

    # Check Blocked
    draw_decision(5.6, 6.2, 0.55, "Đang bị\nkhóa?")

    # If blocked: True
    ax.annotate('', xy=(5.6, 5.0), xytext=(5.6, 5.65), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))
    ax.text(5.95, 5.35, "Có (>=5 lần)", fontsize=8, color=C_DANGER, fontweight='bold')
    draw_act(5.6, 4.5, 3.0, 0.7, "Trả mã HTTP 429\n(Chặn Brute-force 15 phút)", C_DANGER_LIGHT, C_DANGER)

    # End node from blocked
    end_blocked = plt.Circle((1.9, 4.5), 0.16, facecolor=C_DANGER, zorder=4)
    ax.add_patch(end_blocked)
    circle_ring = plt.Circle((1.9, 4.5), 0.22, facecolor="none", edgecolor=C_DANGER, lw=1.5, zorder=4)
    ax.add_patch(circle_ring)
    ax.annotate('', xy=(2.2, 4.5), xytext=(4.1, 4.5), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))
    ax.text(3.1, 4.65, "Báo lỗi tạm khóa", fontsize=7.5, ha='center', color=C_DANGER)

    # If not blocked: False
    ax.annotate('', xy=(8.0, 6.2), xytext=(6.15, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))
    ax.text(7.0, 6.35, "Không (< 5 lần)", fontsize=8, color=C_SUCCESS, fontweight='bold')

    draw_act(9.2, 6.2, 2.8, 0.7, "Truy vấn CSDL &\nSo khớp BCrypt hash", C_SECONDARY_LIGHT, C_SECONDARY)

    # Decision: Password match?
    draw_decision(9.2, 4.5, 0.55, "Mật khẩu\nđúng?")
    ax.annotate('', xy=(9.2, 5.05), xytext=(9.2, 5.85), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Match = False
    ax.annotate('', xy=(6.8, 3.2), xytext=(8.65, 4.5), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))
    ax.text(8.0, 3.65, "Sai mật khẩu", fontsize=8, color=C_DANGER, fontweight='bold')
    draw_act(5.6, 3.0, 3.0, 0.8, "Ghi nhận 1 lần sai\n(attempts++ trong cache)\nBáo mã HTTP 401", C_DANGER_LIGHT, C_DANGER)
    ax.annotate('', xy=(3.3, 3.0), xytext=(4.1, 3.0), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))
    draw_act(1.9, 3.0, 2.6, 0.65, "Hiển thị thông báo\nvà số lần thử còn lại", C_WHITE, C_DANGER)

    # Match = True
    ax.annotate('', xy=(9.2, 2.4), xytext=(9.2, 3.95), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))
    ax.text(9.55, 3.3, "Chính xác", fontsize=8, color=C_SUCCESS, fontweight='bold')
    draw_act(9.2, 1.8, 2.8, 0.7, "Reset bộ đếm sai\n(loginSucceeded)\nTrả mã HTTP 200 OK", C_SUCCESS_LIGHT, C_SUCCESS)

    # Redirect to Dashboard
    ax.annotate('', xy=(3.3, 1.8), xytext=(7.8, 1.8), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))
    ax.text(5.5, 1.95, "Trả JSON User & Token phiên", fontsize=7.5, ha='center', color=C_SUCCESS)
    draw_act(1.9, 1.8, 2.6, 0.65, "Chuyển hướng vào\nTrang Dashboard KTX", C_SUCCESS_LIGHT, C_SUCCESS)

    # Success end node
    end_success = plt.Circle((1.9, 0.9), 0.16, facecolor=C_SUCCESS, zorder=4)
    ax.add_patch(end_success)
    circle_ring2 = plt.Circle((1.9, 0.9), 0.22, facecolor="none", edgecolor=C_SUCCESS, lw=1.5, zorder=4)
    ax.add_patch(circle_ring2)
    ax.annotate('', xy=(1.9, 1.15), xytext=(1.9, 1.48), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_2_activity_dang_nhap.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 4. SƠ ĐỒ HOẠT ĐỘNG: VÒNG ĐỜI HỢP ĐỒNG & XẾP PHÒNG
# -------------------------------------------------------------
def make_activity_contract():
    fig, ax = plt.subplots(figsize=(12, 8.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 9)
    ax.axis('off')

    ax.text(6, 8.6, "SƠ ĐỒ HOẠT ĐỘNG (ACTIVITY DIAGRAM): QUẢN LÝ VÒNG ĐỜI HỢP ĐỒNG & PHÒNG", 
            ha='center', va='center', fontsize=12.5, fontweight='bold', color=C_DARK)
    ax.text(6, 8.25, "Quy trình đăng ký, xét duyệt, điều chuyển phòng và thanh lý hợp đồng", 
            ha='center', va='center', fontsize=9, style='italic', color=C_MUTED)

    # Swimlanes
    ax.plot([4.0, 4.0], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.plot([8.0, 8.0], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.text(2.0, 7.8, "Sinh Viên (Student)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_PRIMARY)
    ax.text(6.0, 7.8, "Quản Lý KTX (Manager)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_SECONDARY)
    ax.text(10.0, 7.8, "Hệ Thống Backend (HopDongService)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_DARK)

    def draw_act(x, y, w, h, text, bg=C_WHITE, border=C_PRIMARY):
        box = FancyBboxPatch((x - w/2, y - h/2), w, h, boxstyle="round,pad=0,rounding_size=0.08",
                             facecolor=bg, edgecolor=border, lw=1.3, zorder=3)
        ax.add_patch(box)
        ax.text(x, y, text, ha='center', va='center', fontsize=8.5, fontweight='bold', color=C_TEXT, zorder=4)

    def draw_decision(x, y, size, text):
        diamond = patches.RegularPolygon((x, y), numVertices=4, radius=size, orientation=0,
                                         facecolor=C_ACCENT_LIGHT, edgecolor=C_ACCENT, lw=1.3, zorder=3)
        ax.add_patch(diamond)
        ax.text(x, y, text, ha='center', va='center', fontsize=8, fontweight='bold', color=C_DARK, zorder=4)

    # 1. Start & Register
    start = plt.Circle((2.0, 7.2), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(start)
    draw_act(2.0, 6.2, 3.2, 0.7, "Chọn phòng & điền thông tin\nđăng ký (trạng thái: 'cho_duyet')")
    ax.annotate('', xy=(2.0, 6.55), xytext=(2.0, 7.04), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Send to Manager
    ax.annotate('', xy=(4.6, 6.2), xytext=(3.6, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PRIMARY))
    draw_act(6.0, 6.2, 2.8, 0.7, "Xem xét hồ sơ đăng ký\n& thông tin sinh viên", C_SECONDARY_LIGHT, C_SECONDARY)

    # Manager Decision: Duyet?
    draw_decision(6.0, 4.9, 0.55, "Quyết định\nduyệt?")
    ax.annotate('', xy=(6.0, 5.45), xytext=(6.0, 5.85), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Từ chối
    ax.annotate('', xy=(3.5, 4.9), xytext=(5.45, 4.9), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))
    ax.text(4.4, 5.05, "Từ chối", fontsize=8, color=C_DANGER, fontweight='bold')
    draw_act(2.0, 4.9, 3.0, 0.7, "Cập nhật HĐ: 'tu_choi'\nThông báo sinh viên", C_DANGER_LIGHT, C_DANGER)

    # Phê duyệt -> Call Service
    ax.annotate('', xy=(8.6, 4.9), xytext=(6.55, 4.9), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))
    ax.text(7.5, 5.05, "Đồng ý", fontsize=8, color=C_SUCCESS, fontweight='bold')
    draw_act(10.0, 4.9, 3.2, 0.75, "Kiểm tra sức chứa phòng\n(so_nguoi < suc_chua)", C_WHITE, C_DARK)

    # Service update room
    draw_act(10.0, 3.5, 3.4, 0.9, "Tăng so_nguoi_hien_tai += 1\nNếu đủ: chuyển sang 'da_day'\nHĐ chuyển: 'dang_hieu_luc'", C_SUCCESS_LIGHT, C_SUCCESS)
    ax.annotate('', xy=(10.0, 3.95), xytext=(10.0, 4.52), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))

    # Trong quá trình ở
    draw_decision(6.0, 2.5, 0.55, "Nhu cầu\nphát sinh?")
    ax.annotate('', xy=(7.8, 3.5), xytext=(6.55, 2.5), arrowprops=dict(arrowstyle="<-", lw=1.5, color=C_PRIMARY))

    # Chuyển phòng
    draw_act(2.0, 2.5, 3.2, 0.75, "Yêu cầu chuyển phòng\n(chuyenPhong)", C_ACCENT_LIGHT, C_ACCENT)
    ax.annotate('', xy=(3.6, 2.5), xytext=(5.45, 2.5), arrowprops=dict(arrowstyle="<-", lw=1.5, color=C_ACCENT))
    ax.text(4.5, 2.65, "Chuyển phòng", fontsize=8, color=C_ACCENT, fontweight='bold')

    # Thanh lý
    draw_act(6.0, 1.2, 3.0, 0.75, "Thanh lý hợp đồng\n(thanhLyHopDong)", C_PURPLE_LIGHT, C_PURPLE)
    ax.annotate('', xy=(6.0, 1.58), xytext=(6.0, 1.95), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PURPLE))
    ax.text(6.35, 1.75, "Hết hạn/Trả phòng", fontsize=8, color=C_PURPLE, fontweight='bold')

    # Backend handles checkout
    draw_act(10.0, 1.2, 3.4, 0.75, "Giảm so_nguoi_hien_tai -= 1\nPhòng chuyển lại 'con_cho'\nHĐ chuyển 'da_ket_thuc'", C_WHITE, C_DARK)
    ax.annotate('', xy=(8.3, 1.2), xytext=(7.5, 1.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PURPLE))

    # End Node
    end = plt.Circle((10.0, 0.35), 0.14, facecolor=C_DARK, zorder=4)
    ax.add_patch(end)
    ring = plt.Circle((10.0, 0.35), 0.19, facecolor="none", edgecolor=C_DARK, lw=1.5, zorder=4)
    ax.add_patch(ring)
    ax.annotate('', xy=(10.0, 0.54), xytext=(10.0, 0.82), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_3_activity_hop_dong.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 5. SƠ ĐỒ HOẠT ĐỘNG: LẬP HÓA ĐƠN & THANH TOÁN VIETQR / MOMO
# -------------------------------------------------------------
def make_activity_invoice():
    fig, ax = plt.subplots(figsize=(12, 8.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 9)
    ax.axis('off')

    ax.text(6, 8.6, "SƠ ĐỒ HOẠT ĐỘNG (ACTIVITY DIAGRAM): LẬP HÓA ĐƠN & THANH TOÁN VIETQR", 
            ha='center', va='center', fontsize=12.5, fontweight='bold', color=C_DARK)
    ax.text(6, 8.25, "Quy trình lập hóa đơn tự động hàng loạt, thanh toán trực tuyến qua QR và in phiếu thu", 
            ha='center', va='center', fontsize=9, style='italic', color=C_MUTED)

    # Swimlanes
    ax.plot([4.0, 4.0], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.plot([8.0, 8.0], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.text(2.0, 7.8, "Ban Quản Lý KTX", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_PRIMARY)
    ax.text(6.0, 7.8, "Sinh Viên (Thanh toán)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_ACCENT)
    ax.text(10.0, 7.8, "Backend & Cổng QR (VietQR / MoMo)", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_SUCCESS)

    def draw_act(x, y, w, h, text, bg=C_WHITE, border=C_PRIMARY):
        box = FancyBboxPatch((x - w/2, y - h/2), w, h, boxstyle="round,pad=0,rounding_size=0.08",
                             facecolor=bg, edgecolor=border, lw=1.3, zorder=3)
        ax.add_patch(box)
        ax.text(x, y, text, ha='center', va='center', fontsize=8.5, fontweight='bold', color=C_TEXT, zorder=4)

    start = plt.Circle((2.0, 7.2), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(start)

    draw_act(2.0, 6.2, 3.2, 0.7, "Chọn Tháng, Tòa nhà &\nnhập đơn giá điện, nước, net")
    ax.annotate('', xy=(2.0, 6.55), xytext=(2.0, 7.04), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Send to backend
    ax.annotate('', xy=(8.4, 6.2), xytext=(3.6, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PRIMARY))
    ax.text(6.0, 6.35, "POST /api/hoa-don/lap-hang-loat", fontsize=8, ha='center', color=C_PRIMARY)

    draw_act(10.0, 6.2, 3.0, 0.75, "Quét HĐ 'dang_hieu_luc'\nTự tính tổng = Phòng+Điện+Nước\nLưu bản ghi KhoanThu", C_SECONDARY_LIGHT, C_SECONDARY)

    # Student views
    draw_act(6.0, 5.0, 3.2, 0.7, "Tra cứu hóa đơn cá nhân\n(Trạng thái: 'chua_thanh_toan')", C_ACCENT_LIGHT, C_ACCENT)
    ax.annotate('', xy=(6.0, 5.35), xytext=(8.5, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_ACCENT))

    # Student pays online
    draw_act(6.0, 3.8, 3.2, 0.75, "Chọn phương thức thanh toán\n(VietQR MBBank 24/7 / MoMo)", C_WHITE, C_ACCENT)
    ax.annotate('', xy=(6.0, 4.18), xytext=(6.0, 4.65), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_ACCENT))

    # Backend generates QR
    draw_act(10.0, 3.8, 3.2, 0.8, "Tạo mã QR động theo chuẩn VietQR\nkèm Số tiền & Nội dung chuyển\n(MaHD + MaSV)", C_SUCCESS_LIGHT, C_SUCCESS)
    ax.annotate('', xy=(8.4, 3.8), xytext=(7.6, 3.8), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))

    # Scan & Pay
    draw_act(6.0, 2.5, 3.2, 0.7, "Quét mã QR từ App ngân hàng\nhoặc chuyển khoản thành công", C_WHITE, C_ACCENT)
    ax.annotate('', xy=(6.0, 2.85), xytext=(6.0, 3.42), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_ACCENT))

    # Backend records payment
    draw_act(10.0, 2.5, 3.2, 0.8, "Ghi nhận bản ghi ThanhToan\nCập nhật da_thanh_toan, con_lai\nĐổi trạng thái: 'da_thanh_toan'", C_WHITE, C_DARK)
    ax.annotate('', xy=(8.4, 2.5), xytext=(7.6, 2.5), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Print Receipt
    draw_act(2.0, 2.5, 3.2, 0.75, "In phiếu thu tiền ký túc xá\n(MoneyToWords đọc chữ tiếng Việt)", C_PURPLE_LIGHT, C_PURPLE)
    ax.annotate('', xy=(3.6, 2.5), xytext=(4.4, 2.5), arrowprops=dict(arrowstyle="<-", lw=1.5, color=C_PURPLE))

    # End
    end = plt.Circle((2.0, 1.2), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(end)
    ring = plt.Circle((2.0, 1.2), 0.22, facecolor="none", edgecolor=C_DARK, lw=1.5, zorder=4)
    ax.add_patch(ring)
    ax.annotate('', xy=(2.0, 1.42), xytext=(2.0, 2.12), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_4_activity_hoa_don.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 6. SƠ ĐỒ HOẠT ĐỘNG: QUẢN LÝ THIẾT BỊ & TÀI SẢN PHÒNG
# -------------------------------------------------------------
def make_activity_equipment():
    fig, ax = plt.subplots(figsize=(11, 8.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 11)
    ax.set_ylim(0, 9)
    ax.axis('off')

    ax.text(5.5, 8.6, "SƠ ĐỒ HOẠT ĐỘNG (ACTIVITY DIAGRAM): QUẢN LÝ THIẾT BỊ PHÒNG", 
            ha='center', va='center', fontsize=12.5, fontweight='bold', color=C_DARK)
    ax.text(5.5, 8.25, "Quy trình cấp phát, báo hỏng, hoàn tất sửa chữa và thu hồi tài sản", 
            ha='center', va='center', fontsize=9, style='italic', color=C_MUTED)

    # Swimlanes
    ax.plot([3.8, 3.8], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.plot([7.4, 7.4], [0.5, 8.0], color=C_BORDER, lw=1.5, linestyle="--")
    ax.text(1.9, 7.8, "Cán Bộ Quản Lý KTX", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_PRIMARY)
    ax.text(5.6, 7.8, "Sinh Viên Trong Phòng", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_ACCENT)
    ax.text(9.2, 7.8, "ThietBiService & Database", ha='center', va='center', fontsize=10.5, fontweight='bold', color=C_SECONDARY)

    def draw_act(x, y, w, h, text, bg=C_WHITE, border=C_PRIMARY):
        box = FancyBboxPatch((x - w/2, y - h/2), w, h, boxstyle="round,pad=0,rounding_size=0.08",
                             facecolor=bg, edgecolor=border, lw=1.3, zorder=3)
        ax.add_patch(box)
        ax.text(x, y, text, ha='center', va='center', fontsize=8.5, fontweight='bold', color=C_TEXT, zorder=4)

    start = plt.Circle((1.9, 7.2), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(start)

    draw_act(1.9, 6.2, 2.8, 0.7, "Cấp phát trang bị cho phòng\n(chọn thiết bị & số lượng)")
    ax.annotate('', xy=(1.9, 6.55), xytext=(1.9, 7.04), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    # Backend saves allocation
    draw_act(9.2, 6.2, 2.8, 0.8, "Lưu TaiSanPhong ('dang_su_dung')\nGhi nhật ký LichSuTaiSan\n(loại: 'cap_phat')", C_SECONDARY_LIGHT, C_SECONDARY)
    ax.annotate('', xy=(7.8, 6.2), xytext=(3.3, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SECONDARY))

    # Student Reports Breakdown
    draw_act(5.6, 4.8, 2.8, 0.75, "Phát hiện thiết bị hỏng hóc\n(Gửi báo hỏng & lý do)", C_ACCENT_LIGHT, C_ACCENT)
    ax.annotate('', xy=(5.6, 5.18), xytext=(8.0, 6.2), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_ACCENT))

    # Backend updates breakdown
    draw_act(9.2, 4.8, 2.8, 0.8, "Đổi trạng thái: 'hong_hoc'\nGhi nhật ký LichSuTaiSan\n(loại: 'bao_hong')", C_DANGER_LIGHT, C_DANGER)
    ax.annotate('', xy=(7.8, 4.8), xytext=(7.0, 4.8), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DANGER))

    # Maintenance Flow
    draw_act(1.9, 3.4, 2.8, 0.75, "Tiến hành sửa chữa /\nThay thế linh kiện", C_WHITE, C_PRIMARY)
    ax.annotate('', xy=(3.3, 3.4), xytext=(7.8, 4.8), arrowprops=dict(arrowstyle="<-", lw=1.5, color=C_PRIMARY))

    # Complete Maintenance
    draw_act(9.2, 3.4, 2.8, 0.8, "Hoàn tất sửa chữa\nĐổi lại: 'dang_su_dung'\nGhi LichSuTaiSan 'sua_chua'", C_SUCCESS_LIGHT, C_SUCCESS)
    ax.annotate('', xy=(7.8, 3.4), xytext=(3.3, 3.4), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_SUCCESS))

    # Recall Flow
    draw_act(1.9, 1.8, 2.8, 0.75, "Thu hồi thiết bị về kho\n(ngay_thu_hoi, lý do)", C_PURPLE_LIGHT, C_PURPLE)
    draw_act(9.2, 1.8, 2.8, 0.8, "Cập nhật ngay_thu_hoi\nĐổi trạng thái: 'thu_hoi'\nGhi LichSuTaiSan 'thu_hoi'", C_WHITE, C_DARK)
    ax.annotate('', xy=(7.8, 1.8), xytext=(3.3, 1.8), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_PURPLE))

    # End
    end = plt.Circle((5.6, 1.0), 0.16, facecolor=C_DARK, zorder=4)
    ax.add_patch(end)
    ring = plt.Circle((5.6, 1.0), 0.22, facecolor="none", edgecolor=C_DARK, lw=1.5, zorder=4)
    ax.add_patch(ring)
    ax.annotate('', xy=(5.8, 1.0), xytext=(7.8, 1.8), arrowprops=dict(arrowstyle="->", lw=1.5, color=C_DARK))

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_5_activity_thiet_bi.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 7. SƠ ĐỒ THỰC THỂ LIÊN KẾT (ERD) ĐẦY ĐỦ 15 BẢNG
# -------------------------------------------------------------
def make_erd_diagram():
    fig, ax = plt.subplots(figsize=(15, 10.5), dpi=300)
    ax.set_facecolor("#F1F5F9")
    fig.patch.set_facecolor("#F1F5F9")
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 11)
    ax.axis('off')

    ax.text(8.0, 10.55, "SƠ ĐỒ CƠ SỞ DỮ LIỆU QUAN HỆ (ENTITY RELATIONSHIP DIAGRAM - ERD)", 
            ha='center', va='center', fontsize=14, fontweight='bold', color=C_DARK)
    ax.text(8.0, 10.22, "Mô hình quan hệ 15 thực thể CSDL ký túc xá (quan_ly_ky_tuc_xa.sql)", 
            ha='center', va='center', fontsize=9.5, style='italic', color=C_MUTED)

    # Table Drawer Helper
    def draw_erd_table(x, y, w, h, table_name, columns, header_bg=C_PRIMARY):
        # Outer Card
        card = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0,rounding_size=0.03",
                              facecolor=C_WHITE, edgecolor=header_bg, lw=1.5, zorder=3)
        ax.add_patch(card)
        # Header Box
        header = FancyBboxPatch((x, y + h - 0.32), w, 0.32, boxstyle="round,pad=0,rounding_size=0.03",
                                facecolor=header_bg, edgecolor="none", zorder=4)
        ax.add_patch(header)
        patch_rect = patches.Rectangle((x, y + h - 0.32), w, 0.12, facecolor=header_bg, edgecolor="none", zorder=4)
        ax.add_patch(patch_rect)
        ax.text(x + w/2, y + h - 0.16, table_name.upper(), ha='center', va='center', 
                fontsize=8.5, fontweight='bold', color=C_WHITE, zorder=5)
        # Columns
        cur_y = y + h - 0.50
        for col in columns:
            ax.text(x + 0.12, cur_y, col, ha='left', va='center', fontsize=7.2, color=C_TEXT, zorder=5)
            cur_y -= 0.20

    # 1. nguoi_dung
    draw_erd_table(0.5, 7.2, 2.7, 2.6, "nguoi_dung", 
                   ["[PK] id: BIGINT", "[UK] ma_nguoi_dung: VARCHAR", "ho_ten: VARCHAR", "[UK] email: VARCHAR",
                    "mat_khau: VARCHAR (BCrypt)", "so_dien_thoai: VARCHAR", "gioi_tinh: VARCHAR", "ngay_sinh: DATE",
                    "trang_thai: VARCHAR", "created_at: TIMESTAMP"], C_PRIMARY)

    # 2. vai_tro
    draw_erd_table(0.5, 4.8, 2.5, 1.4, "vai_tro", 
                   ["[PK] id: BIGINT", "[UK] ten_vai_tro: VARCHAR", "mo_ta: VARCHAR"], C_PRIMARY)

    # 3. nguoi_dung_vai_tro
    draw_erd_table(0.5, 2.8, 2.5, 1.4, "nguoi_dung_vai_tro", 
                   ["[PK,FK] nguoi_dung_id: BIGINT", "[PK,FK] vai_tro_id: BIGINT"], C_PRIMARY)

    # 4. sinh_vien
    draw_erd_table(3.8, 7.2, 2.6, 2.4, "sinh_vien", 
                   ["[PK] id: BIGINT", "[FK,UK] nguoi_dung_id: BIGINT", "[UK] ma_sinh_vien: VARCHAR",
                    "lop: VARCHAR", "khoa: VARCHAR", "nganh: VARCHAR", "khoa_hoc: INT", "que_quan: VARCHAR"], C_PRIMARY)

    # 5. khu
    draw_erd_table(3.8, 4.4, 2.4, 1.8, "khu", 
                   ["[PK] id: BIGINT", "[UK] ma_khu: VARCHAR", "ten_khu: VARCHAR", "dia_chi: VARCHAR", "mo_ta: TEXT"], C_SECONDARY)

    # 6. toa_nha
    draw_erd_table(3.8, 1.8, 2.4, 2.0, "toa_nha", 
                   ["[PK] id: BIGINT", "[FK] khu_id: BIGINT", "[UK] ma_toa: VARCHAR", "ten_toa: VARCHAR",
                    "so_tang: INT", "mo_ta: TEXT"], C_SECONDARY)

    # 7. tang
    draw_erd_table(6.8, 1.8, 2.2, 1.6, "tang", 
                   ["[PK] id: BIGINT", "[FK] toa_nha_id: BIGINT", "so_tang: INT", "[UQ] (toa_nha_id, so_tang)"], C_SECONDARY)

    # 8. phong
    draw_erd_table(6.8, 4.4, 2.6, 2.4, "phong", 
                   ["[PK] id: BIGINT", "[FK] tang_id: BIGINT", "[UK] ma_phong: VARCHAR", "suc_chua: INT",
                    "so_nguoi_hien_tai: INT", "loai_phong: VARCHAR", "gia_phong: DECIMAL", "trang_thai: VARCHAR"], C_SECONDARY)

    # 9. hop_dong
    draw_erd_table(6.8, 7.4, 2.6, 2.2, "hop_dong", 
                   ["[PK] id: BIGINT", "[FK] sinh_vien_id: BIGINT", "[FK] phong_id: BIGINT", "ngay_bat_dau: DATE",
                    "ngay_ket_thuc: DATE", "tien_phong: DECIMAL", "trang_thai: VARCHAR"], C_ACCENT)

    # 10. khoan_thu
    draw_erd_table(10.0, 6.2, 2.7, 3.6, "khoan_thu", 
                   ["[PK] id: BIGINT", "[UK] ma_hoa_don: VARCHAR", "thang: VARCHAR", "[FK] sinh_vien_id: BIGINT",
                    "[FK] hop_dong_id: BIGINT", "[FK] phong_id: BIGINT", "loai_khoan_thu: VARCHAR", "so_tien: DECIMAL",
                    "tien_phong, tien_dien: DECIMAL", "chi_so_dien_cu / moi: INT", "tien_nuoc, internet: DECIMAL",
                    "da_thanh_toan, con_lai: DECIMAL", "han_thanh_toan: DATE", "trang_thai: VARCHAR", "ghi_chu: TEXT"], C_DANGER)

    # 11. thanh_toan
    draw_erd_table(10.0, 3.2, 2.7, 2.4, "thanh_toan", 
                   ["[PK] id: BIGINT", "[FK] khoan_thu_id: BIGINT", "so_tien: DECIMAL", "phuong_thuc: VARCHAR",
                    "[UK] ma_giao_dich: VARCHAR", "thoi_gian: TIMESTAMP", "trang_thai: VARCHAR", "nguoi_thu: VARCHAR"], C_DANGER)

    # 12. loai_tai_san
    draw_erd_table(13.2, 8.4, 2.4, 1.4, "loai_tai_san", 
                   ["[PK] id: BIGINT", "[UK] ten_loai: VARCHAR", "mo_ta: TEXT"], C_PURPLE)

    # 13. tai_san
    draw_erd_table(13.2, 5.6, 2.5, 2.2, "tai_san", 
                   ["[PK] id: BIGINT", "[FK] loai_tai_san_id: BIGINT", "[UK] ma_tai_san: VARCHAR", "ten_tai_san: VARCHAR",
                    "ngay_nhap: DATE", "gia_tri: DECIMAL", "trang_thai: VARCHAR", "mo_ta: TEXT"], C_PURPLE)

    # 14. tai_san_phong
    draw_erd_table(13.2, 3.0, 2.5, 2.1, "tai_san_phong", 
                   ["[PK] id: BIGINT", "[FK] tai_san_id: BIGINT", "[FK] phong_id: BIGINT", "so_luong: INT",
                    "ngay_cap: DATE", "ngay_thu_hoi: DATE", "trang_thai: VARCHAR", "ghi_chu: TEXT"], C_PURPLE)

    # 15. lich_su_tai_san
    draw_erd_table(13.2, 0.4, 2.5, 2.1, "lich_su_tai_san", 
                   ["[PK] id: BIGINT", "[FK] tai_san_id: BIGINT", "[FK] phong_id: BIGINT", "loai_giao_dich: VARCHAR",
                    "so_luong: INT", "ly_do: TEXT", "thoi_gian: TIMESTAMP", "[FK] nguoi_thuc_hien_id: BIGINT"], C_PURPLE)

    # Connectors & Cardinalities
    def draw_rel(p1, p2, label="", color=C_MUTED):
        ax.plot([p1[0], p2[0]], [p1[1], p2[1]], color=color, lw=1.2, zorder=2)
        if label:
            mid_x = (p1[0] + p2[0]) / 2
            mid_y = (p1[1] + p2[1]) / 2
            ax.text(mid_x, mid_y + 0.12, label, fontsize=7.5, fontweight='bold', color=color,
                    bbox=dict(boxstyle="square,pad=0.1", fc=C_WHITE, ec="none", alpha=0.85))

    # Relations
    draw_rel((3.2, 8.4), (3.8, 8.4), "1 - 1", C_PRIMARY)          # nguoi_dung - sinh_vien
    draw_rel((1.7, 7.2), (1.7, 4.2), "1 - N", C_PRIMARY)          # nguoi_dung - nguoi_dung_vai_tro
    draw_rel((1.7, 4.8), (1.7, 4.2), "1 - N", C_PRIMARY)          # vai_tro - nguoi_dung_vai_tro
    draw_rel((5.0, 4.4), (5.0, 3.8), "1 - N", C_SECONDARY)        # khu - toa_nha
    draw_rel((6.2, 2.6), (6.8, 2.6), "1 - N", C_SECONDARY)        # toa_nha - tang
    draw_rel((7.9, 3.4), (7.9, 4.4), "1 - N", C_SECONDARY)        # tang - phong
    draw_rel((6.4, 8.4), (6.8, 8.4), "1 - N", C_ACCENT)           # sinh_vien - hop_dong
    draw_rel((8.1, 7.4), (8.1, 6.8), "1 - N", C_ACCENT)           # phong - hop_dong
    draw_rel((9.4, 8.4), (10.0, 8.4), "1 - N", C_DANGER)          # hop_dong - khoan_thu
    draw_rel((11.3, 6.2), (11.3, 5.6), "1 - N", C_DANGER)         # khoan_thu - thanh_toan
    draw_rel((14.4, 8.4), (14.4, 7.8), "1 - N", C_PURPLE)         # loai_tai_san - tai_san
    draw_rel((14.4, 5.6), (14.4, 5.1), "1 - N", C_PURPLE)         # tai_san - tai_san_phong
    draw_rel((9.4, 4.8), (13.2, 3.8), "1 - N", C_PURPLE)          # phong - tai_san_phong
    draw_rel((14.4, 3.0), (14.4, 2.5), "1 - N", C_PURPLE)         # tai_san - lich_su_tai_san

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_3_6_erd_quan_he.png"), bbox_inches='tight', dpi=300)
    plt.close()

# -------------------------------------------------------------
# 8. SƠ ĐỒ LUỒNG PHÂN TẦNG: CONTROLLER - SERVICE - REPOSITORY
# -------------------------------------------------------------
def make_layer_architecture():
    fig, ax = plt.subplots(figsize=(12, 7.5), dpi=300)
    ax.set_facecolor(C_BG)
    fig.patch.set_facecolor(C_BG)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 8)
    ax.axis('off')

    ax.text(6.0, 7.5, "MÔ HÌNH PHÂN TẦNG KIẾN TRÚC CONTROLLER – SERVICE – REPOSITORY", 
            ha='center', va='center', fontsize=12.5, fontweight='bold', color=C_DARK)
    ax.text(6.0, 7.15, "Luồng xử lý yêu cầu chuẩn trong ứng dụng Spring Boot Backend", 
            ha='center', va='center', fontsize=9, style='italic', color=C_MUTED)

    # 4 Layer blocks
    def draw_tier(x, y, w, h, title, subtitle, items, bg_color, border_color):
        card = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0,rounding_size=0.04",
                              facecolor=bg_color, edgecolor=border_color, lw=1.5, zorder=3)
        ax.add_patch(card)
        ax.text(x + w/2, y + h - 0.28, title, ha='center', va='center', fontsize=10, fontweight='bold', color=border_color, zorder=4)
        ax.text(x + w/2, y + h - 0.50, subtitle, ha='center', va='center', fontsize=7.8, style='italic', color=C_MUTED, zorder=4)
        ax.plot([x + 0.15, x + w - 0.15], [y + h - 0.65, y + h - 0.65], color=border_color, lw=0.8, zorder=4)
        cur_y = y + h - 0.88
        for it in items:
            ax.text(x + 0.2, cur_y, it, ha='left', va='center', fontsize=8, color=C_TEXT, zorder=4)
            cur_y -= 0.24

    draw_tier(0.5, 1.5, 2.4, 5.0, "1. Presentation Layer", "Controller & REST API",
              ["• Tiếp nhận HTTP Requests", "• Validate @Valid DTO", "• Điều hướng API URLs",
               "• Xử lý ResponseStatus", "• Trả JSON Response", "• AuthController", "• HopDongController",
               "• HoaDonController", "• ThietBiController"], C_PRIMARY_LIGHT, C_PRIMARY)

    draw_tier(3.4, 1.5, 2.6, 5.0, "2. Business Service Layer", "Xử lý Logic Nghiệp vụ",
              ["• Chứa toàn bộ Business Rules", "• Quản lý @Transactional", "• Kiểm tra ràng buộc chỗ ở",
               "• Tự động tính tiền điện/nước", "• Mã hóa BCrypt mật khẩu", "• Duyệt/Chuyển hợp đồng",
               "• HopDongService", "• KhoanThuService", "• ThietBiService"], C_SECONDARY_LIGHT, C_SECONDARY)

    draw_tier(6.5, 1.5, 2.5, 5.0, "3. Data Access Layer", "Spring Data JPA Repository",
              ["• Kế thừa JpaRepository", "• Derived Query Methods", "• JPQL Custom Queries",
               "• Tự động chuẩn bị SQL", "• PreparedStatements an toàn", "• Chống SQL Injection",
               "• HopDongRepository", "• KhoanThuRepository", "• PhongRepository"], C_ACCENT_LIGHT, C_ACCENT)

    draw_tier(9.4, 1.5, 2.2, 5.0, "4. Database Layer", "MySQL / MariaDB RDBMS",
              ["• 15 Bảng dữ liệu quan hệ", "• Khóa chính tự tăng (PK)", "• Ràng buộc toàn vẹn (FK)",
               "• Check constraints", "• Unique constraints", "• Transaction Engine InnoDB",
               "• ACID Compliance", "• H2 in-memory test"], C_PURPLE_LIGHT, C_PURPLE)

    # Inter-tier arrows
    def draw_flow_arrow(x1, x2, y, label, color=C_PRIMARY):
        ax.annotate('', xy=(x2, y), xytext=(x1, y),
                    arrowprops=dict(facecolor=color, edgecolor=color, width=1.5, headwidth=6, headlength=6))
        ax.text((x1 + x2)/2, y + 0.15, label, ha='center', va='bottom', fontsize=7.5, fontweight='bold', color=color)

    draw_flow_arrow(2.9, 3.4, 4.8, "Gọi Service", C_PRIMARY)
    draw_flow_arrow(3.4, 2.9, 3.5, "Trả DTO/Model", C_PRIMARY)

    draw_flow_arrow(6.0, 6.5, 4.8, "Gọi Repo", C_SECONDARY)
    draw_flow_arrow(6.5, 6.0, 3.5, "Trả Entities", C_SECONDARY)

    draw_flow_arrow(9.0, 9.4, 4.8, "SQL Query", C_ACCENT)
    draw_flow_arrow(9.4, 9.0, 3.5, "Result Set", C_ACCENT)

    plt.savefig(os.path.join(OUTPUT_DIR, "hinh_4_2_phan_tang_spring_boot.png"), bbox_inches='tight', dpi=300)
    plt.close()

if __name__ == '__main__':
    print("Generating diagrams...")
    make_system_architecture()
    print("1/6: hinh_4_1_kien_truc_tong_the.png done")
    make_usecase_general()
    print("2/6: hinh_3_1_usecase_tong_quat.png done")
    make_activity_login()
    print("3/6: hinh_3_2_activity_dang_nhap.png done")
    make_activity_contract()
    print("4/6: hinh_3_3_activity_hop_dong.png done")
    make_activity_invoice()
    print("5/6: hinh_3_4_activity_hoa_don.png done")
    make_activity_equipment()
    print("6/6: hinh_3_5_activity_thiet_bi.png done")
    make_erd_diagram()
    print("7/8: hinh_3_6_erd_quan_he.png done")
    make_layer_architecture()
    print("8/8: hinh_4_2_phan_tang_spring_boot.png done")
    print("All diagrams generated successfully!")

