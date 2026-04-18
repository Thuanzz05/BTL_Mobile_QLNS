-- SQL Script for HRM System (BTL_Mobile_QLNS)
-- Author: Nguyễn Duy Thuấn & Đào Duy Huy

PRAGMA foreign_keys = ON;

-- 1. Table: ChucVu
CREATE TABLE ChucVu (
    MaChucVu TEXT PRIMARY KEY,
    TenChucVu TEXT NOT NULL,
    MucLuongCoBan REAL,
    TrangThai INTEGER DEFAULT 1
);

-- 2. Table: PhongBan
CREATE TABLE PhongBan (
    MaPhongBan TEXT PRIMARY KEY,
    TenPhongBan TEXT NOT NULL,
    TruongPhong TEXT,
    TrangThai INTEGER DEFAULT 1
);

-- 3. Table: NhanVien
CREATE TABLE NhanVien (
    MaNhanVien TEXT PRIMARY KEY,
    HoTen TEXT NOT NULL,
    NgaySinh DATE,
    GioiTinh TEXT DEFAULT 'Nam',
    SoDienThoai TEXT,
    Email TEXT,
    NgayVaoLam DATE NOT NULL,
    MaPhongBan TEXT,
    MaChucVu TEXT,
    HinhAnh TEXT,
    TrangThaiLamViec TEXT DEFAULT 'Đang làm việc',
    FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan),
    FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)
);

-- 4. Table: HopDongLaoDong
CREATE TABLE HopDongLaoDong (
    MaHopDong TEXT PRIMARY KEY,
    MaNhanVien TEXT NOT NULL,
    LoaiHopDong TEXT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE,
    MucLuong REAL NOT NULL,
    TrangThai TEXT DEFAULT 'Hiệu lực',
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 5. Table: ChamCong
CREATE TABLE ChamCong (
    MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    NgayChamCong DATE NOT NULL,
    GioVao TIME,
    GioRa TIME,
    SoGioLam REAL DEFAULT 0,
    TrangThai TEXT DEFAULT 'Có mặt',
    GhiChu TEXT,
    UNIQUE (MaNhanVien, NgayChamCong),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 6. Table: NghiPhep
CREATE TABLE NghiPhep (
    MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    SoNgayNghi INTEGER NOT NULL,
    LyDo TEXT,
    TrangThai TEXT DEFAULT 'Chờ duyệt',
    NguoiDuyet TEXT,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 7. Table: Luong
CREATE TABLE Luong (
    MaLuong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    ThangNam TEXT NOT NULL,
    LuongCoBan REAL NOT NULL,
    PhuCap REAL DEFAULT 0,
    SoGioLam REAL DEFAULT 0,
    TongLuong REAL NOT NULL,
    TrangThai TEXT DEFAULT 'Chưa thanh toán',
    NgayTinhLuong DATE,
    UNIQUE (MaNhanVien, ThangNam),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 8. Table: TaiKhoan
CREATE TABLE TaiKhoan (
    MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL UNIQUE,
    TenDangNhap TEXT NOT NULL UNIQUE,
    MatKhau TEXT NOT NULL,
    VaiTro TEXT DEFAULT 'Employee',
    TrangThai INTEGER DEFAULT 1,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- DỮ LIỆU MẪU (Sample Data)

-- Thêm Chức vụ
INSERT INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV001', 'Giám đốc', 15000000);
INSERT INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV002', 'Trưởng phòng', 8000000);
INSERT INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV003', 'Nhân viên', 5000000);
INSERT INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV004', 'Phó phòng', 6500000);
INSERT INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV005', 'Thực tập sinh', 3000000);

-- Thêm Phòng ban
INSERT INTO PhongBan (MaPhongBan, TenPhongBan, TruongPhong) VALUES ('PB001', 'Phòng Nhân sự', 'NV002');
INSERT INTO PhongBan (MaPhongBan, TenPhongBan, TruongPhong) VALUES ('PB002', 'Phòng Kế toán', 'NV003');
INSERT INTO PhongBan (MaPhongBan, TenPhongBan) VALUES ('PB003', 'Phòng Kỹ thuật');
INSERT INTO PhongBan (MaPhongBan, TenPhongBan) VALUES ('PB004', 'Phòng Marketing');
INSERT INTO PhongBan (MaPhongBan, TenPhongBan) VALUES ('PB005', 'Phòng Kinh doanh');

-- Thêm Nhân viên
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV002', 'Trần Thị Bình', '1990-08-22', 'Nữ', '0901234568', 'binh@company.com', '2020-02-01', 'PB001', 'CV002');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV003', 'Lê Văn Cường', '1988-12-10', 'Nam', '0901234569', 'cuong@company.com', '2020-03-15', 'PB002', 'CV002');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV004', 'Phạm Thị Dung', '1992-03-25', 'Nữ', '0901234570', 'dung@company.com', '2020-04-01', 'PB003', 'CV003');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV005', 'Hoàng Văn Em', '1995-07-18', 'Nam', '0901234571', 'em@company.com', '2020-05-10', 'PB003', 'CV003');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV006', 'Nguyễn Thị Hoa', '1993-11-05', 'Nữ', '0901234572', 'hoa@company.com', '2021-01-15', 'PB004', 'CV003');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV007', 'Đặng Minh Khoa', '1987-09-30', 'Nam', '0901234573', 'khoa@company.com', '2021-02-20', 'PB004', 'CV004');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV008', 'Vũ Thị Lan', '1994-06-12', 'Nữ', '0901234574', 'lan@company.com', '2021-03-10', 'PB005', 'CV003');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV009', 'Bùi Văn Minh', '1991-04-08', 'Nam', '0901234575', 'minh@company.com', '2021-04-05', 'PB005', 'CV004');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV010', 'Lý Thị Nga', '1996-12-20', 'Nữ', '0901234576', 'nga@company.com', '2022-01-10', 'PB001', 'CV003');
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('NV011', 'Trịnh Văn Phong', '1998-02-14', 'Nam', '0901234577', 'phong@company.com', '2023-06-01', 'PB002', 'CV005');

-- Thêm Tài khoản
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('ADMIN', 'admin', '123456', 'Admin');
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('NV002', 'hr', '123456', 'HR');
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('NV003', 'manager1', '123456', 'Manager');
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('NV004', 'user1', '123456', 'Employee');
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('NV010', 'user5', '123456', 'Employee');

-- Thêm Hợp đồng mẫu
INSERT INTO HopDongLaoDong (MaHopDong, MaNhanVien, LoaiHopDong, NgayBatDau, MucLuong) VALUES ('HD001', 'NV002', 'Không thời hạn', '2020-02-01', 8000000);
INSERT INTO HopDongLaoDong (MaHopDong, MaNhanVien, LoaiHopDong, NgayBatDau, MucLuong) VALUES ('HD002', 'NV003', 'Có thời hạn (3 năm)', '2020-03-15', 8000000);

-- Thêm Đơn nghỉ phép mẫu
INSERT INTO NghiPhep (MaNhanVien, NgayBatDau, NgayKetThuc, SoNgayNghi, LyDo, TrangThai) VALUES ('NV004', '2026-04-20', '2026-04-22', 3, 'Giải quyết việc gia đình', 'Chờ duyệt');
INSERT INTO NghiPhep (MaNhanVien, NgayBatDau, NgayKetThuc, SoNgayNghi, LyDo, TrangThai) VALUES ('NV010', '2026-04-15', '2026-04-15', 1, 'Đi khám bệnh', 'Đã duyệt');

