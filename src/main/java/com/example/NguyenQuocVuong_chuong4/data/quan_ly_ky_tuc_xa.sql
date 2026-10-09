-- ============================================================
-- DATABASE: quan_ly_ky_tuc_xa
-- Tuong thich MySQL / MariaDB (XAMPP, phpMyAdmin)
-- Chay file nay trong phpMyAdmin hoac MySQL.
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

-- 1. Tai khoan nguoi dung
CREATE TABLE nguoi_dung (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ma_nguoi_dung VARCHAR(30) NOT NULL UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL COMMENT 'Nen luu mat khau da bam (hash)',
    so_dien_thoai VARCHAR(20) NULL,
    gioi_tinh VARCHAR(20) NULL,
    ngay_sinh DATE NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'hoat_dong',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Vai tro va bang lien ket phan quyen
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

-- 3. Ho so sinh vien (moi tai khoan toi da mot ho so sinh vien)
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

-- 4. Khu, toa nha, tang, phong
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

-- 5. Hop dong o ky tuc xa
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

-- 6. Tai san
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

-- 7. Khoan thu va thanh toan
CREATE TABLE khoan_thu (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sinh_vien_id BIGINT UNSIGNED NOT NULL,
    hop_dong_id BIGINT UNSIGNED NULL,
    loai_khoan_thu VARCHAR(50) NOT NULL,
    so_tien DECIMAL(12,2) NOT NULL,
    han_thanh_toan DATE NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'chua_thanh_toan',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_khoan_thu_so_tien CHECK (so_tien >= 0),
    CONSTRAINT fk_khoan_thu_sinh_vien FOREIGN KEY (sinh_vien_id)
        REFERENCES sinh_vien(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_khoan_thu_hop_dong FOREIGN KEY (hop_dong_id)
        REFERENCES hop_dong(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_khoan_thu_sinh_vien (sinh_vien_id),
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
    CONSTRAINT chk_thanh_toan_so_tien CHECK (so_tien > 0),
    CONSTRAINT fk_thanh_toan_khoan_thu FOREIGN KEY (khoan_thu_id)
        REFERENCES khoan_thu(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_thanh_toan_khoan_thu (khoan_thu_id)
) ENGINE=InnoDB;

-- ============================================================
-- DU LIEU MAU DE KIEM TRA CAC BANG (co the xoa neu chi can schema)
-- Mat khau mau ben duoi chi la chuoi minh hoa; ung dung that phai
-- thay bang mat khau da bam bang bcrypt/Argon2.
-- ============================================================

INSERT INTO nguoi_dung
(id, ma_nguoi_dung, ho_ten, email, mat_khau, so_dien_thoai, gioi_tinh, ngay_sinh, trang_thai)
VALUES
(1, 'ND001', 'Nguyen Van An', 'an@example.com', '$2y$10$example_hash_replace_before_use', '0901000001', 'Nam', '2004-03-12', 'hoat_dong'),
(2, 'ND002', 'Tran Thi Binh', 'binh@example.com', '$2y$10$example_hash_replace_before_use', '0901000002', 'Nu', '2004-08-25', 'hoat_dong'),
(3, 'ND003', 'Le Quoc Cuong', 'cuong@example.com', '$2y$10$example_hash_replace_before_use', '0901000003', 'Nam', '1985-02-10', 'hoat_dong');

INSERT INTO vai_tro (id, ten_vai_tro, mo_ta) VALUES
(1, 'quan_tri', 'Quan tri he thong'),
(2, 'sinh_vien', 'Tai khoan sinh vien'),
(3, 'quan_ly_ky_tuc', 'Quan ly ky tuc xa');

INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id) VALUES
(1, 2), (2, 2), (3, 1), (3, 3);

INSERT INTO sinh_vien
(id, nguoi_dung_id, ma_sinh_vien, lop, khoa, nganh, khoa_hoc, que_quan)
VALUES
(1, 1, 'SV24001', 'CCQ2411D', 'Cong nghe thong tin', 'Cong nghe phan mem', 24, 'TP Ho Chi Minh'),
(2, 2, 'SV24002', 'CCQ2411D', 'Cong nghe thong tin', 'He thong thong tin', 24, 'Dong Nai');

INSERT INTO khu (id, ma_khu, ten_khu, dia_chi, mo_ta) VALUES
(1, 'KHU-A', 'Khu A', 'Co so chinh', 'Khu ky tuc xa sinh vien');

INSERT INTO toa_nha (id, khu_id, ma_toa, ten_toa, so_tang, mo_ta) VALUES
(1, 1, 'TOA-A1', 'Toa A1', 3, 'Toa nha nam gan cong chinh');

INSERT INTO tang (id, toa_nha_id, so_tang) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3);

INSERT INTO phong
(id, tang_id, ma_phong, suc_chua, so_nguoi_hien_tai, loai_phong, gia_phong, trang_thai)
VALUES
(1, 1, 'A1-101', 4, 1, '4 nguoi', 1200000, 'con_cho'),
(2, 1, 'A1-102', 6, 1, '6 nguoi', 900000, 'con_cho'),
(3, 2, 'A1-201', 4, 0, '4 nguoi', 1200000, 'con_cho');

INSERT INTO hop_dong
(id, sinh_vien_id, phong_id, ngay_bat_dau, ngay_ket_thuc, tien_phong, trang_thai)
VALUES
(1, 1, 1, '2026-09-01', '2027-06-30', 1200000, 'dang_hieu_luc'),
(2, 2, 2, '2026-09-01', '2027-06-30', 900000, 'dang_hieu_luc');

INSERT INTO loai_tai_san (id, ten_loai, mo_ta) VALUES
(1, 'Giuong', 'Giuong ngu trong phong'),
(2, 'Ban hoc', 'Ban hoc sinh vien'),
(3, 'Quat dien', 'Quat dien su dung trong phong');

INSERT INTO tai_san
(id, loai_tai_san_id, ma_tai_san, ten_tai_san, ngay_nhap, gia_tri, trang_thai, mo_ta)
VALUES
(1, 1, 'TS-G-001', 'Giuong tang sat', '2026-08-01', 1800000, 'dang_su_dung', 'Giuong sat 2 tang'),
(2, 2, 'TS-BH-001', 'Ban hoc go', '2026-08-01', 750000, 'dang_su_dung', 'Ban hoc ca nhan'),
(3, 3, 'TS-QD-001', 'Quat dien', '2026-08-01', 420000, 'dang_su_dung', 'Quat dung');

INSERT INTO tai_san_phong
(id, tai_san_id, phong_id, so_luong, ngay_cap, ngay_thu_hoi, trang_thai)
VALUES
(1, 1, 1, 2, '2026-08-25', NULL, 'dang_su_dung'),
(2, 2, 1, 4, '2026-08-25', NULL, 'dang_su_dung'),
(3, 3, 1, 2, '2026-08-25', NULL, 'dang_su_dung');

INSERT INTO lich_su_tai_san
(id, tai_san_id, phong_id, loai_giao_dich, so_luong, ly_do, nguoi_thuc_hien_id)
VALUES
(1, 1, 1, 'cap_phat', 2, 'Trang bi ban dau cho phong A1-101', 3),
(2, 2, 1, 'cap_phat', 4, 'Trang bi ban hoc cho sinh vien', 3),
(3, 3, 1, 'cap_phat', 2, 'Cap quat cho phong', 3);

INSERT INTO khoan_thu
(id, sinh_vien_id, hop_dong_id, loai_khoan_thu, so_tien, han_thanh_toan, trang_thai)
VALUES
(1, 1, 1, 'tien_phong', 1200000, '2026-10-05', 'da_thanh_toan'),
(2, 2, 2, 'tien_phong', 900000, '2026-10-05', 'chua_thanh_toan'),
(3, 1, 1, 'phi_dich_vu', 150000, '2026-10-10', 'chua_thanh_toan');

INSERT INTO thanh_toan
(id, khoan_thu_id, so_tien, phuong_thuc, ma_giao_dich, thoi_gian, trang_thai)
VALUES
(1, 1, 1200000, 'chuyen_khoan', 'GD-20261001-001', '2026-10-01 09:30:00', 'thanh_cong');

-- Mot so truy van kiem tra nhanh:
-- SELECT * FROM nguoi_dung;
-- SELECT * FROM sinh_vien;
-- SELECT p.ma_phong, p.suc_chua, p.so_nguoi_hien_tai, p.gia_phong
-- FROM phong p;
-- SELECT kt.id, sv.ma_sinh_vien, kt.loai_khoan_thu, kt.so_tien, kt.trang_thai
-- FROM khoan_thu kt JOIN sinh_vien sv ON sv.id = kt.sinh_vien_id;
