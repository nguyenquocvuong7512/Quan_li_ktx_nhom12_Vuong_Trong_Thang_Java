-- ============================================================
-- DATABASE: quan_ly_ky_tuc_xa
-- TUONG THICH: MySQL 8.x / MariaDB 10.x (XAMPP, phpMyAdmin, Docker)
-- HE THONG QUAN LY KY TUC XA SINH VIEN - NHOM 12
-- ============================================================

CREATE DATABASE IF NOT EXISTS quan_ly_ky_tuc_xa
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE quan_ly_ky_tuc_xa;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS thanh_toan;
DROP TABLE IF EXISTS khoan_thu;
DROP TABLE IF EXISTS lich_su_tai_san;
DROP TABLE IF EXISTS tai_san_phong;
DROP TABLE IF EXISTS tai_san;
DROP TABLE IF EXISTS loai_tai_san;
DROP TABLE IF EXISTS hop_dong;
DROP TABLE IF EXISTS phong;
DROP TABLE IF EXISTS tang;
DROP TABLE IF EXISTS toa_nha;
DROP TABLE IF EXISTS khu;
DROP TABLE IF EXISTS sinh_vien;
DROP TABLE IF EXISTS nguoi_dung_vai_tro;
DROP TABLE IF EXISTS vai_tro;
DROP TABLE IF EXISTS nguoi_dung;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. BANG NGUOI DUNG
CREATE TABLE nguoi_dung (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ma_nguoi_dung VARCHAR(30) NOT NULL UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    so_dien_thoai VARCHAR(20) NULL,
    gioi_tinh VARCHAR(20) NULL,
    ngay_sinh DATE NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'hoat_dong',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. BANG VAI TRO VA PHAN QUYEN (RBAC)
CREATE TABLE vai_tro (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ten_vai_tro VARCHAR(50) NOT NULL UNIQUE,
    mo_ta VARCHAR(255) NULL
) ENGINE=InnoDB;

CREATE TABLE nguoi_dung_vai_tro (
    nguoi_dung_id BIGINT UNSIGNED NOT NULL,
    vai_tro_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (nguoi_dung_id, vai_tro_id),
    CONSTRAINT fk_ndvt_nguoi_dung FOREIGN KEY (nguoi_dung_id)
        REFERENCES nguoi_dung(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ndvt_vai_tro FOREIGN KEY (vai_tro_id)
        REFERENCES vai_tro(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. BANG SINH VIEN
CREATE TABLE sinh_vien (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id BIGINT UNSIGNED NOT NULL UNIQUE,
    ma_sinh_vien VARCHAR(30) NOT NULL UNIQUE,
    lop VARCHAR(50) NULL,
    khoa VARCHAR(100) NULL,
    nganh VARCHAR(100) NULL,
    khoa_hoc INT NULL,
    que_quan VARCHAR(150) NULL,
    CONSTRAINT fk_sinh_vien_nguoi_dung FOREIGN KEY (nguoi_dung_id)
        REFERENCES nguoi_dung(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. BANG KHU, TOA NHA, TANG, PHONG
CREATE TABLE khu (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ma_khu VARCHAR(20) NOT NULL UNIQUE,
    ten_khu VARCHAR(100) NOT NULL,
    dia_chi VARCHAR(255) NULL,
    mo_ta TEXT NULL
) ENGINE=InnoDB;

CREATE TABLE toa_nha (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    khu_id BIGINT UNSIGNED NOT NULL,
    ma_toa VARCHAR(20) NOT NULL UNIQUE,
    ten_toa VARCHAR(100) NOT NULL,
    so_tang INT UNSIGNED NOT NULL DEFAULT 1,
    mo_ta TEXT NULL,
    CONSTRAINT fk_toa_nha_khu FOREIGN KEY (khu_id)
        REFERENCES khu(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tang (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    toa_nha_id BIGINT UNSIGNED NOT NULL,
    so_tang INT UNSIGNED NOT NULL,
    CONSTRAINT uq_tang_toa_so UNIQUE (toa_nha_id, so_tang),
    CONSTRAINT fk_tang_toa_nha FOREIGN KEY (toa_nha_id)
        REFERENCES toa_nha(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE phong (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tang_id BIGINT UNSIGNED NOT NULL,
    ma_phong VARCHAR(30) NOT NULL UNIQUE,
    suc_chua INT UNSIGNED NOT NULL,
    so_nguoi_hien_tai INT UNSIGNED NOT NULL DEFAULT 0,
    loai_phong VARCHAR(50) NOT NULL,
    gia_phong DECIMAL(12,2) NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'con_cho',
    CONSTRAINT chk_phong_suc_chua CHECK (suc_chua > 0),
    CONSTRAINT chk_phong_so_nguoi CHECK (so_nguoi_hien_tai <= suc_chua),
    CONSTRAINT chk_phong_gia CHECK (gia_phong >= 0),
    CONSTRAINT fk_phong_tang FOREIGN KEY (tang_id)
        REFERENCES tang(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5. BANG HOP DONG
CREATE TABLE hop_dong (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sinh_vien_id BIGINT UNSIGNED NOT NULL,
    phong_id BIGINT UNSIGNED NOT NULL,
    ngay_bat_dau DATE NOT NULL,
    ngay_ket_thuc DATE NOT NULL,
    tien_phong DECIMAL(12,2) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'cho_duyet',
    CONSTRAINT chk_hop_dong_ngay CHECK (ngay_ket_thuc >= ngay_bat_dau),
    CONSTRAINT chk_hop_dong_tien CHECK (tien_phong >= 0),
    CONSTRAINT fk_hop_dong_sinh_vien FOREIGN KEY (sinh_vien_id)
        REFERENCES sinh_vien(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_hop_dong_phong FOREIGN KEY (phong_id)
        REFERENCES phong(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_hop_dong_sinh_vien (sinh_vien_id),
    INDEX idx_hop_dong_phong (phong_id),
    INDEX idx_hop_dong_trang_thai (trang_thai)
) ENGINE=InnoDB;

-- 6. BANG TAI SAN VA THIET BI
CREATE TABLE loai_tai_san (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ten_loai VARCHAR(100) NOT NULL UNIQUE,
    mo_ta TEXT NULL
) ENGINE=InnoDB;

CREATE TABLE tai_san (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    loai_tai_san_id BIGINT UNSIGNED NOT NULL,
    ma_tai_san VARCHAR(30) NOT NULL UNIQUE,
    ten_tai_san VARCHAR(150) NOT NULL,
    ngay_nhap DATE NULL,
    gia_tri DECIMAL(14,2) NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'dang_su_dung',
    mo_ta TEXT NULL,
    CONSTRAINT chk_tai_san_gia_tri CHECK (gia_tri >= 0),
    CONSTRAINT fk_tai_san_loai FOREIGN KEY (loai_tai_san_id)
        REFERENCES loai_tai_san(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tai_san_phong (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tai_san_id BIGINT UNSIGNED NOT NULL,
    phong_id BIGINT UNSIGNED NOT NULL,
    so_luong INT UNSIGNED NOT NULL DEFAULT 1,
    ngay_cap DATE NOT NULL,
    ngay_thu_hoi DATE NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'dang_su_dung',
    ghi_chu TEXT NULL,
    CONSTRAINT chk_tai_san_phong_so_luong CHECK (so_luong > 0),
    CONSTRAINT chk_tai_san_phong_ngay CHECK (ngay_thu_hoi IS NULL OR ngay_thu_hoi >= ngay_cap),
    CONSTRAINT fk_tsp_tai_san FOREIGN KEY (tai_san_id)
        REFERENCES tai_san(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tsp_phong FOREIGN KEY (phong_id)
        REFERENCES phong(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_tsp_phong (phong_id),
    INDEX idx_tsp_tai_san (tai_san_id)
) ENGINE=InnoDB;

CREATE TABLE lich_su_tai_san (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tai_san_id BIGINT UNSIGNED NOT NULL,
    phong_id BIGINT UNSIGNED NULL,
    loai_giao_dich VARCHAR(50) NOT NULL,
    so_luong INT UNSIGNED NOT NULL,
    ly_do TEXT NULL,
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nguoi_thuc_hien_id BIGINT UNSIGNED NULL,
    CONSTRAINT fk_lst_san FOREIGN KEY (tai_san_id)
        REFERENCES tai_san(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lst_phong FOREIGN KEY (phong_id)
        REFERENCES phong(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_lst_nguoi_thuc_hien FOREIGN KEY (nguoi_thuc_hien_id)
        REFERENCES nguoi_dung(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_lst_thoi_gian (thoi_gian)
) ENGINE=InnoDB;

-- 7. BANG KHOAN THU VA THANH TOAN
CREATE TABLE khoan_thu (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ma_hoa_don VARCHAR(50) NULL UNIQUE,
    thang VARCHAR(20) NULL,
    sinh_vien_id BIGINT UNSIGNED NOT NULL,
    hop_dong_id BIGINT UNSIGNED NULL,
    phong_id BIGINT UNSIGNED NULL,
    loai_khoan_thu VARCHAR(50) NOT NULL DEFAULT 'hoa_don_thang',
    so_tien DECIMAL(12,2) NOT NULL DEFAULT 0,
    tien_phong DECIMAL(12,2) NOT NULL DEFAULT 0,
    tien_dien DECIMAL(12,2) NOT NULL DEFAULT 0,
    chi_so_dien_cu INT NULL DEFAULT 0,
    chi_so_dien_moi INT NULL DEFAULT 0,
    tien_nuoc DECIMAL(12,2) NOT NULL DEFAULT 0,
    chi_so_nuoc_cu INT NULL DEFAULT 0,
    chi_so_nuoc_moi INT NULL DEFAULT 0,
    tien_internet DECIMAL(12,2) NOT NULL DEFAULT 0,
    tien_dich_vu DECIMAL(12,2) NOT NULL DEFAULT 0,
    da_thanh_toan DECIMAL(12,2) NOT NULL DEFAULT 0,
    con_lai DECIMAL(12,2) NOT NULL DEFAULT 0,
    han_thanh_toan DATE NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'chua_thanh_toan',
    ghi_chu TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_khoan_thu_so_tien CHECK (so_tien >= 0),
    CONSTRAINT fk_khoan_thu_sinh_vien FOREIGN KEY (sinh_vien_id)
        REFERENCES sinh_vien(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_khoan_thu_hop_dong FOREIGN KEY (hop_dong_id)
        REFERENCES hop_dong(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_khoan_thu_phong FOREIGN KEY (phong_id)
        REFERENCES phong(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_khoan_thu_sinh_vien (sinh_vien_id),
    INDEX idx_khoan_thu_phong (phong_id),
    INDEX idx_khoan_thu_thang (thang),
    INDEX idx_khoan_thu_trang_thai (trang_thai)
) ENGINE=InnoDB;

CREATE TABLE thanh_toan (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    khoan_thu_id BIGINT UNSIGNED NOT NULL,
    so_tien DECIMAL(12,2) NOT NULL,
    phuong_thuc VARCHAR(50) NOT NULL,
    ma_giao_dich VARCHAR(100) NULL UNIQUE,
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'thanh_cong',
    ghi_chu VARCHAR(255) NULL,
    nguoi_thu VARCHAR(100) NULL,
    CONSTRAINT chk_thanh_toan_so_tien CHECK (so_tien > 0),
    CONSTRAINT fk_thanh_toan_khoan_thu FOREIGN KEY (khoan_thu_id)
        REFERENCES khoan_thu(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_thanh_toan_khoan_thu (khoan_thu_id)
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA PHONG PHU, CHUAN XAC (BCrypt Hashes Thuc Te)
-- 1. Tai khoan admin: admin / Admin@123
-- 2. Tai khoan quan ly: quanly / QuanLy@123
-- 3. Tai khoan sinh vien: sinhvien / SinhVien@123
-- ============================================================

-- 1. NGUOI DUNG (12 nguoi dung)
-- Hash Admin@123: 
-- Hash QuanLy@123: .a
-- Hash SinhVien@123: /4gN9RiQXa

INSERT INTO nguoi_dung (id, ma_nguoi_dung, ho_ten, email, mat_khau, so_dien_thoai, gioi_tinh, ngay_sinh, trang_thai) VALUES
(1, 'ADMIN01', 'Quan Tri Vien He Thong', 'admin@ktx.edu.vn', '', '0901000001', 'Nam', '1985-01-01', 'hoat_dong'),
(2, 'QL001', 'Nguyen Van Quan Ly', 'quanly@ktx.edu.vn', '.a', '0901000002', 'Nam', '1990-05-15', 'hoat_dong'),
(3, 'SV001', 'Nguyen Van An', 'sinhvien@ktx.edu.vn', '/4gN9RiQXa', '0901000003', 'Nam', '2004-03-12', 'hoat_dong'),
(4, 'SV002', 'Tran Thi Binh', 'binh@example.com', '/4gN9RiQXa', '0901000004', 'Nu', '2004-08-25', 'hoat_dong'),
(5, 'SV003', 'Le Quoc Cuong', 'cuong@example.com', '/4gN9RiQXa', '0901000005', 'Nam', '2003-11-20', 'hoat_dong'),
(6, 'SV004', 'Pham Minh Dung', 'dung@example.com', '/4gN9RiQXa', '0901000006', 'Nam', '2004-01-10', 'hoat_dong'),
(7, 'SV005', 'Hoang My Hanh', 'hanh@example.com', '/4gN9RiQXa', '0901000007', 'Nu', '2004-07-18', 'hoat_dong'),
(8, 'SV006', 'Vo Thanh Long', 'long@example.com', '/4gN9RiQXa', '0901000008', 'Nam', '2003-09-05', 'hoat_dong'),
(9, 'SV007', 'Do Thi Mai', 'mai@example.com', '/4gN9RiQXa', '0901000009', 'Nu', '2004-12-30', 'hoat_dong'),
(10, 'SV008', 'Bui Tien Nam', 'nam@example.com', '/4gN9RiQXa', '0901000010', 'Nam', '2004-04-14', 'hoat_dong'),
(11, 'SV009', 'Dang Thu Trang', 'trang@example.com', '/4gN9RiQXa', '0901000011', 'Nu', '2003-06-22', 'hoat_dong'),
(12, 'QL002', 'Tran Thi Thu', 'thu.quanly@ktx.edu.vn', '.a', '0901000012', 'Nu', '1992-09-09', 'hoat_dong');

-- 2. VAI TRO (3 vai tro chinh + alias)
INSERT INTO vai_tro (id, ten_vai_tro, mo_ta) VALUES
(1, 'ADMIN', 'Quan tri vien he thong - Toan quyen'),
(2, 'QUAN_LY', 'Nhan vien quan ly ky tuc xa'),
(3, 'SINH_VIEN', 'Sinh vien luu tru tai ky tuc xa');

-- 3. PHAN QUYEN (nguoi_dung_vai_tro)
INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- quanly -> QUAN_LY
(12, 2), -- thu.quanly -> QUAN_LY
(3, 3), (4, 3), (5, 3), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3), (11, 3); -- sinh vien

-- 4. HO SO SINH VIEN (9 sinh vien)
INSERT INTO sinh_vien (id, nguoi_dung_id, ma_sinh_vien, lop, khoa, nganh, khoa_hoc, que_quan) VALUES
(1, 3, 'SV24001', 'CCQ2411D', 'Cong nghe thong tin', 'Ky thuat phan mem', 24, 'TP Ho Chi Minh'),
(2, 4, 'SV24002', 'CCQ2411D', 'Cong nghe thong tin', 'He thong thong tin', 24, 'Dong Nai'),
(3, 5, 'SV23003', 'CCQ2311A', 'Cong nghe thong tin', 'Khoa hoc may tinh', 23, 'Binh Duong'),
(4, 6, 'SV24004', 'CCQ2412B', 'Dien - Dien tu', 'Tu dong hoa', 24, 'Tay Ninh'),
(5, 7, 'SV24005', 'CCQ2413C', 'Kinh te', 'Tai chinh ngan hang', 24, 'Ba Ria - Vung Tau'),
(6, 8, 'SV23006', 'CCQ2312A', 'Kinh te', 'Quan tri kinh doanh', 23, 'Long An'),
(7, 9, 'SV24007', 'CCQ2414A', 'Ngoai ngu', 'Ngon ngu Anh', 24, 'Tien Giang'),
(8, 10, 'SV24008', 'CCQ2411A', 'Cong nghe thong tin', 'An toan thong tin', 24, 'Ben Tre'),
(9, 11, 'SV23009', 'CCQ2314B', 'Kinh te', 'Ke toan', 23, 'Can Tho');

-- 5. KHU & TOA NHA
INSERT INTO khu (id, ma_khu, ten_khu, dia_chi, mo_ta) VALUES
(1, 'KHU-A', 'Khu A (KTX Nam)', 'Cong chinh - Khuon vien phia Nam', 'Khu ky tuc xa nam danh cho sinh vien cac khoa ky thuat'),
(2, 'KHU-B', 'Khu B (KTX Nu)', 'Cong phu - Khuon vien phia Bac', 'Khu ky tuc xa nu yen tinh, an ninh cao');

INSERT INTO toa_nha (id, khu_id, ma_toa, ten_toa, so_tang, mo_ta) VALUES
(1, 1, 'TOA-A1', 'Toa A1 - Sao Bang', 3, 'Toa nha 3 tang gan thu vien'),
(2, 1, 'TOA-A2', 'Toa A2 - Thai Duong', 3, 'Toa nha 3 tang gan san the thao'),
(3, 2, 'TOA-B1', 'Toa B1 - Huong Duong', 3, 'Toa nha 3 tang nu'),
(4, 2, 'TOA-B2', 'Toa B2 - Do Quyen', 3, 'Toa nha 3 tang nu gan cang-tin');

-- 6. TANG (12 tang)
INSERT INTO tang (id, toa_nha_id, so_tang) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3),
(4, 2, 1), (5, 2, 2), (6, 2, 3),
(7, 3, 1), (8, 3, 2), (9, 3, 3),
(10, 4, 1), (11, 4, 2), (12, 4, 3);

-- 7. PHONG (36 phong da dang suc chua va trang thai)
INSERT INTO phong (id, tang_id, ma_phong, suc_chua, so_nguoi_hien_tai, loai_phong, gia_phong, trang_thai) VALUES
-- Toa A1
(1, 1, 'A1-101', 4, 2, '4 nguoi', 1200000, 'con_cho'),
(2, 1, 'A1-102', 6, 2, '6 nguoi', 900000, 'con_cho'),
(3, 1, 'A1-103', 2, 2, '2 nguoi', 1800000, 'da_day'),
(4, 2, 'A1-201', 4, 1, '4 nguoi', 1200000, 'con_cho'),
(5, 2, 'A1-202', 6, 0, '6 nguoi', 900000, 'con_cho'),
(6, 2, 'A1-203', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(7, 3, 'A1-301', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(8, 3, 'A1-302', 6, 0, '6 nguoi', 900000, 'con_cho'),
(9, 3, 'A1-303', 2, 0, '2 nguoi', 1800000, 'bao_tri'),

-- Toa A2
(10, 4, 'A2-101', 4, 1, '4 nguoi', 1200000, 'con_cho'),
(11, 4, 'A2-102', 6, 0, '6 nguoi', 900000, 'con_cho'),
(12, 4, 'A2-103', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(13, 5, 'A2-201', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(14, 5, 'A2-202', 6, 0, '6 nguoi', 900000, 'con_cho'),
(15, 5, 'A2-203', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(16, 6, 'A2-301', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(17, 6, 'A2-302', 6, 0, '6 nguoi', 900000, 'con_cho'),
(18, 6, 'A2-303', 2, 0, '2 nguoi', 1800000, 'con_cho'),

-- Toa B1
(19, 7, 'B1-101', 4, 2, '4 nguoi', 1200000, 'con_cho'),
(20, 7, 'B1-102', 6, 1, '6 nguoi', 900000, 'con_cho'),
(21, 7, 'B1-103', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(22, 8, 'B1-201', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(23, 8, 'B1-202', 6, 0, '6 nguoi', 900000, 'con_cho'),
(24, 8, 'B1-203', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(25, 9, 'B1-301', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(26, 9, 'B1-302', 6, 0, '6 nguoi', 900000, 'con_cho'),
(27, 9, 'B1-303', 2, 0, '2 nguoi', 1800000, 'con_cho'),

-- Toa B2
(28, 10, 'B2-101', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(29, 10, 'B2-102', 6, 0, '6 nguoi', 900000, 'con_cho'),
(30, 10, 'B2-103', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(31, 11, 'B2-201', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(32, 11, 'B2-202', 6, 0, '6 nguoi', 900000, 'con_cho'),
(33, 11, 'B2-203', 2, 0, '2 nguoi', 1800000, 'con_cho'),
(34, 12, 'B2-301', 4, 0, '4 nguoi', 1200000, 'con_cho'),
(35, 12, 'B2-302', 6, 0, '6 nguoi', 900000, 'con_cho'),
(36, 12, 'B2-303', 2, 0, '2 nguoi', 1800000, 'con_cho');

-- 8. HOP DONG (12 hop dong: hieu luc, cho duyet, ket thuc)
INSERT INTO hop_dong (id, sinh_vien_id, phong_id, ngay_bat_dau, ngay_ket_thuc, tien_phong, trang_thai) VALUES
(1, 1, 1, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(2, 2, 1, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(3, 3, 2, '2026-09-01', '2027-06-30', 900000, 'dang_hieu_luc'),
(4, 4, 2, '2026-09-01', '2027-06-30', 900000, 'dang_hieu_luc'),
(5, 5, 3, '2026-09-01', '2027-06-30', 1800000, 'dang_hieu_luc'),
(6, 6, 3, '2026-09-01', '2027-06-30', 1800000, 'dang_hieu_luc'),
(7, 7, 4, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(8, 8, 10, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(9, 9, 19, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(10, 1, 2, '2026-01-01', '2026-06-30', 900000, 'da_ket_thuc'),
(11, 2, 3, '2026-01-01', '2026-06-30', 1800000, 'da_ket_thuc'),
(12, 3, 1, '2026-10-01', '2027-06-30', 1200000, 'cho_duyet');

-- 9. LOAI TAI SAN & TAI SAN (8 loai, 16 tai san)
INSERT INTO loai_tai_san (id, ten_loai, mo_ta) VALUES
(1, 'Giuong tang sat', 'Giuong ngu khung sat 2 tang co thang leo'),
(2, 'Ban hoc sinh vien', 'Ban hoc go cong nghiep kem gia sach'),
(3, 'Ghe xoay van phong', 'Ghe xoay co dem tua lung'),
(4, 'Quat tran Vinawind', 'Quat tran 3 canh co hop so'),
(5, 'Dieu hoa Inverter Daikin', 'Dieu hoa 1.5HP tiet kiem dien'),
(6, 'Binh nong lanh Ariston', 'Binh nong lanh 20 lit chong giat ELCB'),
(7, 'Tu quan ao 2 buong', 'Tu sat son tinh dien 2 ngan khoa rieng'),
(8, 'Router Wifi Asus', 'Thiet bi phat Wifi chuan Wi-Fi 6');

INSERT INTO tai_san (id, loai_tai_san_id, ma_tai_san, ten_tai_san, ngay_nhap, gia_tri, trang_thai, mo_ta) VALUES
(1, 1, 'TS-GT-001', 'Giuong tang sat Hoa Phat A1', '2026-08-01', 1800000, 'dang_su_dung', 'Trang bi phong A1-101'),
(2, 1, 'TS-GT-002', 'Giuong tang sat Hoa Phat A2', '2026-08-01', 1800000, 'dang_su_dung', 'Trang bi phong A1-101'),
(3, 2, 'TS-BH-001', 'Ban hoc ca nhan go MDF 1', '2026-08-01', 750000, 'dang_su_dung', 'Ban hoc sinh vien phong A1-101'),
(4, 2, 'TS-BH-002', 'Ban hoc ca nhan go MDF 2', '2026-08-01', 750000, 'dang_su_dung', 'Ban hoc sinh vien phong A1-101'),
(5, 3, 'TS-GX-001', 'Ghe xoay luoi GL113', '2026-08-01', 450000, 'dang_su_dung', 'Ghe hoc phong A1-101'),
(6, 4, 'TS-QT-001', 'Quat tran Vinawind 1400', '2026-08-01', 950000, 'dang_su_dung', 'Quat phong A1-101'),
(7, 5, 'TS-DH-001', 'Dieu hoa Daikin 12000BTU', '2026-08-10', 8500000, 'dang_su_dung', 'Dieu hoa phong A1-101'),
(8, 6, 'TS-NL-001', 'Binh nong lanh Ariston 20L', '2026-08-10', 2800000, 'dang_su_dung', 'Binh nong lanh WC A1-101'),
(9, 7, 'TS-TU-001', 'Tu quan ao sat 2 buong', '2026-08-01', 1600000, 'dang_su_dung', 'Tu do ca nhan phong A1-101'),
(10, 8, 'TS-WF-001', 'Router Wifi Asus AX1800', '2026-08-15', 1200000, 'dang_su_dung', 'Wifi phong A1-101'),
(11, 4, 'TS-QT-002', 'Quat tran Vinawind hong so', '2026-08-01', 950000, 'can_sua_chua', 'Dang cho bao tri cuon day'),
(12, 5, 'TS-DH-002', 'Dieu hoa Panasonic 9000BTU', '2026-08-10', 7200000, 'can_sua_chua', 'Mat gas lanh phong A1-303'),
(13, 1, 'TS-GT-003', 'Giuong tang sat du phong', '2026-08-01', 1800000, 'trong_kho', 'Ton kho bao tri'),
(14, 2, 'TS-BH-003', 'Ban hoc du phong', '2026-08-01', 750000, 'trong_kho', 'Ton kho bao tri'),
(15, 7, 'TS-TU-002', 'Tu quan ao du phong', '2026-08-01', 1600000, 'trong_kho', 'Ton kho bao tri'),
(16, 6, 'TS-NL-002', 'Binh nong lanh cu thanh ly', '2024-05-01', 500000, 'thanh_ly', 'Da het khau hao');

-- 10. TAI SAN PHONG (Cap phat cho phong A1-101, A1-102)
INSERT INTO tai_san_phong (id, tai_san_id, phong_id, so_luong, ngay_cap, ngay_thu_hoi, trang_thai, ghi_chu) VALUES
(1, 1, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Giuong tang so 1 phong A1-101'),
(2, 2, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Giuong tang so 2 phong A1-101'),
(3, 3, 1, 2, '2026-08-25', NULL, 'dang_su_dung', 'Cap 2 ban hoc go'),
(4, 5, 1, 4, '2026-08-25', NULL, 'dang_su_dung', 'Cap 4 ghe xoay'),
(5, 6, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Quat tran hoat dong tot'),
(6, 7, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Dieu hoa Inverter tiet kiem dien'),
(7, 8, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Binh nong lanh nha ve sinh'),
(8, 9, 1, 2, '2026-08-25', NULL, 'dang_su_dung', 'Tu do ca nhan 2 buong'),
(9, 10, 1, 1, '2026-08-25', NULL, 'dang_su_dung', 'Wifi KTX toc do cao'),
(10, 11, 2, 1, '2026-08-25', NULL, 'can_sua_chua', 'Quat bi keu to, sinh vien da bao hong');

-- 11. LICH SU TAI SAN (Audit Trail)
INSERT INTO lich_su_tai_san (id, tai_san_id, phong_id, loai_giao_dich, so_luong, ly_do, nguoi_thuc_hien_id) VALUES
(1, 1, 1, 'cap_phat', 1, 'Trang bi dau nam hoc cho phong A1-101', 1),
(2, 2, 1, 'cap_phat', 1, 'Trang bi dau nam hoc cho phong A1-101', 1),
(3, 3, 1, 'cap_phat', 2, 'Ban hoc ca nhan cho sinh vien', 2),
(4, 7, 1, 'cap_phat', 1, 'Lap dat dieu hoa Daikin moi', 1),
(5, 11, 2, 'bao_hong', 1, 'Sinh vien bao hong quat tran khong quay', 2);

-- 12. KHOAN THU (HOA DON) - 18 hoa don da dang cac ky va trang thai
INSERT INTO khoan_thu (id, ma_hoa_don, thang, sinh_vien_id, hop_dong_id, phong_id, loai_khoan_thu, so_tien, tien_phong, tien_dien, chi_so_dien_cu, chi_so_dien_moi, tien_nuoc, chi_so_nuoc_cu, chi_so_nuoc_moi, tien_internet, tien_dich_vu, da_thanh_toan, con_lai, han_thanh_toan, trang_thai, ghi_chu) VALUES
-- Thang 2026-09: Da thanh toan du
(1, 'HD202609-0001', '2026-09', 1, 1, 1, 'hoa_don_thang', 1450000, 1200000, 120000, 100, 140, 50000, 20, 25, 50000, 30000, 1450000, 0, '2026-09-25', 'da_thanh_toan', 'Tien phong va dich vu thang 09/2026'),
(2, 'HD202609-0002', '2026-09', 2, 2, 1, 'hoa_don_thang', 1450000, 1200000, 120000, 100, 140, 50000, 20, 25, 50000, 30000, 1450000, 0, '2026-09-25', 'da_thanh_toan', 'Tien phong va dich vu thang 09/2026'),
(3, 'HD202609-0003', '2026-09', 3, 3, 2, 'hoa_don_thang', 1130000, 900000, 100000, 80, 115, 50000, 15, 20, 50000, 30000, 1130000, 0, '2026-09-25', 'da_thanh_toan', 'Tien phong va dich vu thang 09/2026'),

-- Thang 2026-10: Thanh toan mot phan & Chua thanh toan
(4, 'HD202610-0001', '2026-10', 1, 1, 1, 'hoa_don_thang', 1510000, 1200000, 150000, 140, 190, 80000, 25, 33, 50000, 30000, 1000000, 510000, '2026-10-25', 'thanh_toan_mot_phan', 'Sinh vien da nop truoc 1.000.000d'),
(5, 'HD202610-0002', '2026-10', 2, 2, 1, 'hoa_don_thang', 1510000, 1200000, 150000, 140, 190, 80000, 25, 33, 50000, 30000, 0, 1510000, '2026-10-25', 'chua_thanh_toan', 'Tien phong va dich vu ky thang 10/2026'),
(6, 'HD202610-0003', '2026-10', 3, 3, 2, 'hoa_don_thang', 1180000, 900000, 120000, 115, 155, 60000, 20, 26, 50000, 50000, 1180000, 0, '2026-10-25', 'da_thanh_toan', 'Da thanh toan qua VietQR'),
(7, 'HD202610-0004', '2026-10', 4, 4, 2, 'hoa_don_thang', 1180000, 900000, 120000, 115, 155, 60000, 20, 26, 50000, 50000, 0, 1180000, '2026-10-25', 'chua_thanh_toan', 'Chua thanh toan'),

-- Thang 2026-08 (Qua han)
(8, 'HD202608-0001', '2026-08', 5, 5, 3, 'hoa_don_thang', 2050000, 1800000, 100000, 50, 85, 70000, 10, 17, 50000, 30000, 0, 2050000, '2026-08-25', 'qua_han', 'Hoa don thang 8 qua han thanh toan'),

-- Thang 2026-11 (Hien tai)
(9, 'HD202611-0001', '2026-11', 1, 1, 1, 'hoa_don_thang', 1480000, 1200000, 130000, 190, 235, 70000, 33, 40, 50000, 30000, 0, 1480000, '2026-11-25', 'chua_thanh_toan', 'Hoa don ky thang 11/2026'),
(10, 'HD202611-0002', '2026-11', 2, 2, 1, 'hoa_don_thang', 1480000, 1200000, 130000, 190, 235, 70000, 33, 40, 50000, 30000, 0, 1480000, '2026-11-25', 'chua_thanh_toan', 'Hoa don ky thang 11/2026'),
(11, 'HD202611-0003', '2026-11', 7, 7, 4, 'hoa_don_thang', 1400000, 1200000, 100000, 10, 45, 50000, 5, 10, 50000, 0, 1400000, 0, '2026-11-25', 'da_thanh_toan', 'Thanh toan VNPAY Sandbox thanh cong'),
(12, 'HD202611-0004', '2026-11', 8, 8, 10, 'hoa_don_thang', 1420000, 1200000, 110000, 15, 50, 60000, 8, 14, 50000, 0, 500000, 920000, '2026-11-25', 'thanh_toan_mot_phan', 'Nop dot 1 bang MoMo');

-- 13. THANH TOAN (10 giao dich da kenh: vnpay, vietqr, momo, tien_mat, chuyen_khoan)
INSERT INTO thanh_toan (id, khoan_thu_id, so_tien, phuong_thuc, ma_giao_dich, thoi_gian, trang_thai, ghi_chu, nguoi_thu) VALUES
(1, 1, 1450000, 'chuyen_khoan', 'GD-20260920-001', '2026-09-20 09:30:00', 'thanh_cong', 'Chuyen khoan BIDV', 'Ke toan KTX'),
(2, 2, 1450000, 'tien_mat', 'GD-20260921-002', '2026-09-21 14:15:00', 'thanh_cong', 'Thu tien mat tai van phong KTX', 'Ke toan KTX'),
(3, 3, 1130000, 'vietqr', 'MB_VQR_20260922_987', '2026-09-22 10:05:00', 'thanh_cong', 'Quet ma VietQR MBBank Napas 24/7', 'HeThong_VietQR_MB'),
(4, 4, 1000000, 'momo', 'MOMO_PAY_20261015_456', '2026-10-15 16:45:00', 'thanh_cong', 'Thanh toan dot 1 qua Vi MoMo', 'HeThong_MoMo_Auto'),
(5, 6, 1180000, 'vietqr', 'MB_VQR_20261018_112', '2026-10-18 11:20:00', 'thanh_cong', 'Xac nhan tu dong VietQR Napas', 'HeThong_VietQR_MB'),
(6, 11, 1400000, 'vnpay', 'VNPAY_11_20261102_8899', '2026-11-02 08:30:00', 'thanh_cong', 'Thanh toan truc tuyen VNPAY Sandbox thanh cong', 'Cong VNPAY'),
(7, 12, 500000, 'momo', 'MOMO_PAY_20261102_334', '2026-11-02 13:10:00', 'thanh_cong', 'Tam ung dot 1 tien phong thang 11', 'HeThong_MoMo_Auto');

-- ============================================================
-- HOAN TAT KHOI TAO CO SO DU LIEU KY TUC XA
-- ============================================================
