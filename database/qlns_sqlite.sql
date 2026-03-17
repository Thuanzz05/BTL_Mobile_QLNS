-- Cơ sở dữ liệu QLNS - giữ các trường quan trọng

-- Bảng Chức vụ
CREATE TABLE IF NOT EXISTS ChucVu (
    MaChucVu TEXT PRIMARY KEY,
    TenChucVu TEXT NOT NULL,
    MucLuongCoBan REAL,
    TrangThai INTEGER DEFAULT 1
);

-- Bảng Phòng ban
CREATE TABLE IF NOT EXISTS PhongBan (
    MaPhongBan TEXT PRIMARY KEY,
    TenPhongBan TEXT NOT NULL,
    TruongPhong TEXT,
    TrangThai INTEGER DEFAULT 1
);

-- Bảng Nhân viên
CREATE TABLE IF NOT EXISTS NhanVien (
    MaNhanVien TEXT PRIMARY KEY,
    HoTen TEXT NOT NULL,
    NgaySinh DATE,
    GioiTinh TEXT DEFAULT 'Nam',
    SoDienThoai TEXT,
    Email TEXT,
    NgayVaoLam DATE NOT NULL,
    MaPhongBan TEXT,
    MaChucVu TEXT,
    TrangThaiLamViec TEXT DEFAULT 'Đang làm việc'
);

-- Bảng Hợp đồng lao động
CREATE TABLE IF NOT EXISTS HopDongLaoDong (
    MaHopDong TEXT PRIMARY KEY,
    MaNhanVien TEXT NOT NULL,
    LoaiHopDong TEXT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE,
    MucLuong REAL NOT NULL,
    TrangThai TEXT DEFAULT 'Hiệu lực'
);

-- Bảng Chấm công
CREATE TABLE IF NOT EXISTS ChamCong (
    MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    NgayChamCong DATE NOT NULL,
    GioVao TIME,
    GioRa TIME,
    SoGioLam REAL DEFAULT 0,
    TrangThai TEXT DEFAULT 'Có mặt',
    UNIQUE (MaNhanVien, NgayChamCong)
);

-- Bảng Nghỉ phép
CREATE TABLE IF NOT EXISTS NghiPhep (
    MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    SoNgayNghi INTEGER NOT NULL,
    LyDo TEXT,
    TrangThai TEXT DEFAULT 'Chờ duyệt',
    NguoiDuyet TEXT
);

-- Bảng Lương
CREATE TABLE IF NOT EXISTS Luong (
    MaLuong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    ThangNam TEXT NOT NULL,
    LuongCoBan REAL NOT NULL,
    PhuCap REAL DEFAULT 0,
    SoGioLam REAL DEFAULT 0,
    TongLuong REAL NOT NULL,
    TrangThai TEXT DEFAULT 'Chưa thanh toán',
    NgayTinhLuong DATE,
    UNIQUE (MaNhanVien, ThangNam)
);

-- Bảng Tài khoản đăng nhập
CREATE TABLE IF NOT EXISTS TaiKhoan (
    MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL UNIQUE,
    TenDangNhap TEXT NOT NULL UNIQUE,
    MatKhau TEXT NOT NULL,
    VaiTro TEXT DEFAULT 'Employee',
    TrangThai INTEGER DEFAULT 1
);

-- Dữ liệu mẫu

-- Thêm dữ liệu Chức vụ
INSERT OR IGNORE INTO ChucVu (MaChucVu, TenChucVu, MucLuongCoBan, TrangThai) VALUES
('CV001', 'Giám đốc', 50000000, 1),
('CV002', 'Trưởng phòng', 25000000, 1),
('CV003', 'Nhân viên', 12000000, 1);

-- Thêm dữ liệu Phòng ban
INSERT OR IGNORE INTO PhongBan (MaPhongBan, TenPhongBan, TrangThai) VALUES
('PB001', 'Phòng Nhân sự', 1),
('PB002', 'Phòng Kế toán', 1),
('PB003', 'Phòng Kỹ thuật', 1);

-- Thêm dữ liệu Nhân viên
INSERT OR IGNORE INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, NgayVaoLam, MaPhongBan, MaChucVu, TrangThaiLamViec) VALUES
('NV001', 'Nguyễn Văn An', '1985-05-15', 'Nam', '0901234567', 'an@company.com', '2020-01-20', 'PB001', 'CV001', 'Đang làm việc'),
('NV002', 'Trần Thị Bình', '1990-08-22', 'Nữ', '0901234568', 'binh@company.com', '2020-02-01', 'PB001', 'CV002', 'Đang làm việc'),
('NV003', 'Lê Văn Cường', '1988-12-10', 'Nam', '0901234569', 'cuong@company.com', '2020-03-15', 'PB002', 'CV002', 'Đang làm việc'),
('NV004', 'Phạm Thị Dung', '1992-03-25', 'Nữ', '0901234570', 'dung@company.com', '2020-04-01', 'PB003', 'CV003', 'Đang làm việc'),
('NV005', 'Hoàng Văn Em', '1995-07-18', 'Nam', '0901234571', 'em@company.com', '2020-05-10', 'PB003', 'CV003', 'Đang làm việc');

-- Cập nhật Trưởng phòng
UPDATE PhongBan SET TruongPhong = 'NV002' WHERE MaPhongBan = 'PB001';
UPDATE PhongBan SET TruongPhong = 'NV003' WHERE MaPhongBan = 'PB002';

-- Thêm dữ liệu Tài khoản
INSERT OR IGNORE INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES
('NV001', 'admin', '123456', 'Admin', 1),
('NV002', 'hr', '123456', 'HR', 1),
('NV003', 'manager', '123456', 'Manager', 1),
('NV004', 'user1', '123456', 'Employee', 1),
('NV005', 'user2', '123456', 'Employee', 1);

-- Thêm dữ liệu Hợp đồng lao động
INSERT OR IGNORE INTO HopDongLaoDong (MaHopDong, MaNhanVien, LoaiHopDong, NgayBatDau, NgayKetThuc, MucLuong, TrangThai) VALUES
('HD001', 'NV001', 'Không thời hạn', '2020-01-20', NULL, 50000000, 'Hiệu lực'),
('HD002', 'NV002', 'Không thời hạn', '2020-02-01', NULL, 25000000, 'Hiệu lực'),
('HD003', 'NV003', 'Có thời hạn', '2020-03-15', '2025-03-14', 25000000, 'Hiệu lực'),
('HD004', 'NV004', 'Có thời hạn', '2020-04-01', '2025-03-31', 15000000, 'Hiệu lực'),
('HD005', 'NV005', 'Có thời hạn', '2020-05-10', '2025-05-09', 12000000, 'Hiệu lực');

-- Thêm dữ liệu Chấm công
INSERT OR IGNORE INTO ChamCong (MaNhanVien, NgayChamCong, GioVao, GioRa, SoGioLam, TrangThai) VALUES
('NV001', '2024-03-01', '08:00:00', '17:30:00', 8.5, 'Có mặt'),
('NV001', '2024-03-02', '08:15:00', '17:30:00', 8.25, 'Đi muộn'),
('NV002', '2024-03-01', '08:00:00', '17:30:00', 8.5, 'Có mặt'),
('NV002', '2024-03-02', NULL, NULL, 0, 'Nghỉ phép'),
('NV003', '2024-03-01', '08:00:00', '17:30:00', 8.5, 'Có mặt'),
('NV003', '2024-03-02', '08:00:00', '18:00:00', 9, 'Có mặt');

-- Thêm dữ liệu Nghỉ phép
INSERT OR IGNORE INTO NghiPhep (MaNhanVien, NgayBatDau, NgayKetThuc, SoNgayNghi, LyDo, TrangThai, NguoiDuyet) VALUES
('NV002', '2024-03-02', '2024-03-02', 1, 'Nghỉ phép cá nhân', 'Đã duyệt', 'NV001'),
('NV004', '2024-02-15', '2024-02-16', 2, 'Bị cảm cúm', 'Đã duyệt', 'NV002'),
('NV005', '2024-03-10', '2024-03-12', 3, 'Về quê thăm gia đình', 'Chờ duyệt', NULL);

-- Thêm dữ liệu Lương
INSERT OR IGNORE INTO Luong (MaNhanVien, ThangNam, LuongCoBan, PhuCap, SoGioLam, TongLuong, TrangThai, NgayTinhLuong) VALUES
('NV001', '2024-02', 50000000, 5000000, 176, 55000000, 'Đã thanh toán', '2024-02-28'),
('NV002', '2024-02', 25000000, 3000000, 168, 28000000, 'Đã thanh toán', '2024-02-28'),
('NV003', '2024-02', 25000000, 2500000, 176, 27500000, 'Đã thanh toán', '2024-02-28'),
('NV004', '2024-02', 15000000, 1500000, 160, 16500000, 'Đã thanh toán', '2024-02-28'),
('NV005', '2024-02', 12000000, 1000000, 176, 13000000, 'Chưa thanh toán', '2024-02-28');