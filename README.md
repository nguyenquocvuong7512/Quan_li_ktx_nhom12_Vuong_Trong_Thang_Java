## 🗄️ ERD - Hệ thống quản lý ký túc xá

### Sơ đồ cơ sở dữ liệu

```mermaid
erDiagram

    NGUOI_DUNG {
        BIGINT id PK
        VARCHAR ma_nguoi_dung
        VARCHAR ho_ten
        VARCHAR email
        VARCHAR mat_khau
        VARCHAR so_dien_thoai
        VARCHAR gioi_tinh
        DATE ngay_sinh
        VARCHAR trang_thai
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    VAI_TRO {
        BIGINT id PK
        VARCHAR ten_vai_tro
        VARCHAR mo_ta
    }

    NGUOI_DUNG_VAI_TRO {
        BIGINT nguoi_dung_id PK, FK
        BIGINT vai_tro_id PK, FK
    }

    SINH_VIEN {
        BIGINT id PK
        BIGINT nguoi_dung_id FK
        VARCHAR ma_sinh_vien UK
        VARCHAR lop
        VARCHAR khoa
        VARCHAR nganh
        INT khoa_hoc
        VARCHAR que_quan
    }

    KHU {
        BIGINT id PK
        VARCHAR ma_khu UK
        VARCHAR ten_khu
        VARCHAR dia_chi
        TEXT mo_ta
    }

    TOA_NHA {
        BIGINT id PK
        BIGINT khu_id FK
        VARCHAR ma_toa UK
        VARCHAR ten_toa
        INT so_tang
        TEXT mo_ta
    }

    TANG {
        BIGINT id PK
        BIGINT toa_nha_id FK
        INT so_tang
    }

    PHONG {
        BIGINT id PK
        BIGINT tang_id FK
        VARCHAR ma_phong UK
        INT suc_chua
        INT so_nguoi_hien_tai
        VARCHAR loai_phong
        DECIMAL gia_phong
        VARCHAR trang_thai
    }

    HOP_DONG {
        BIGINT id PK
        BIGINT sinh_vien_id FK
        BIGINT phong_id FK
        DATE ngay_bat_dau
        DATE ngay_ket_thuc
        DECIMAL tien_phong
        VARCHAR trang_thai
    }

    LOAI_TAI_SAN {
        BIGINT id PK
        VARCHAR ten_loai
        TEXT mo_ta
    }

    TAI_SAN {
        BIGINT id PK
        BIGINT loai_tai_san_id FK
        VARCHAR ma_tai_san UK
        VARCHAR ten_tai_san
        DATE ngay_nhap
        DECIMAL gia_tri
        VARCHAR trang_thai
        TEXT mo_ta
    }

    TAI_SAN_PHONG {
        BIGINT id PK
        BIGINT tai_san_id FK
        BIGINT phong_id FK
        INT so_luong
        DATE ngay_cap
        DATE ngay_thu_hoi
        VARCHAR trang_thai
    }

    LICH_SU_TAI_SAN {
        BIGINT id PK
        BIGINT tai_san_id FK
        BIGINT phong_id FK
        VARCHAR loai_giao_dich
        INT so_luong
        TEXT ly_do
        TIMESTAMP thoi_gian
        BIGINT nguoi_thuc_hien_id FK
    }

    KHOAN_THU {
        BIGINT id PK
        BIGINT sinh_vien_id FK
        BIGINT hop_dong_id FK
        VARCHAR loai_khoan_thu
        DECIMAL so_tien
        DATE han_thanh_toan
        VARCHAR trang_thai
        TIMESTAMP created_at
    }

    THANH_TOAN {
        BIGINT id PK
        BIGINT khoan_thu_id FK
        DECIMAL so_tien
        VARCHAR phuong_thuc
        VARCHAR ma_giao_dich
        TIMESTAMP thoi_gian
        VARCHAR trang_thai
    }

    NGUOI_DUNG ||--o| SINH_VIEN : "la"
    NGUOI_DUNG ||--o{ NGUOI_DUNG_VAI_TRO : "duoc gan"
    VAI_TRO ||--o{ NGUOI_DUNG_VAI_TRO : "co"

    KHU ||--o{ TOA_NHA : "co"
    TOA_NHA ||--o{ TANG : "co"
    TANG ||--o{ PHONG : "co"

    SINH_VIEN ||--o{ HOP_DONG : "ky"
    PHONG ||--o{ HOP_DONG : "co"

    LOAI_TAI_SAN ||--o{ TAI_SAN : "phan loai"
    TAI_SAN ||--o{ TAI_SAN_PHONG : "duoc cap"
    PHONG ||--o{ TAI_SAN_PHONG : "duoc trang bi"

    TAI_SAN ||--o{ LICH_SU_TAI_SAN : "co lich su"
    PHONG ||--o{ LICH_SU_TAI_SAN : "tai phong"
    NGUOI_DUNG ||--o{ LICH_SU_TAI_SAN : "thuc hien"

    SINH_VIEN ||--o{ KHOAN_THU : "co"
    HOP_DONG ||--o{ KHOAN_THU : "phat sinh"
    KHOAN_THU ||--o{ THANH_TOAN : "duoc thanh toan"
```

### 📌 Mô tả các nhóm bảng

| Nhóm | Các bảng | Chức năng |
|---|---|---|
| 👤 Người dùng & phân quyền | `nguoi_dung`, `vai_tro`, `nguoi_dung_vai_tro`, `sinh_vien` | Quản lý tài khoản, sinh viên và phân quyền |
| 🏢 Cơ sở vật chất | `khu`, `toa_nha`, `tang`, `phong` | Quản lý khu, tòa, tầng và phòng |
| 📝 Hợp đồng | `hop_dong` | Quản lý việc đăng ký/ở phòng của sinh viên |
| 🛏️ Tài sản | `loai_tai_san`, `tai_san`, `tai_san_phong`, `lich_su_tai_san` | Quản lý tài sản và thiết bị trong phòng |
| 💰 Khoản thu | `khoan_thu`, `thanh_toan` | Quản lý tiền phòng và các khoản thu |
