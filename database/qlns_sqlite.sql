-- Cơ sở dữ liệu Quản lý Nhân sự (QLNS) - SQLite
-- File: qlns_sqlite.sql

-- Bật foreign key constraints
PRAGMA foreign_keys = ON;

-- Bảng Chức vụ (tạo trước để tránh lỗi foreign key)
CREATE TABLE IF NOT EXISTS ChucVu (
    MaChucVu TEXT PRIMARY KEY,
    TenChucVu TEXT NOT NULL,
    MoTa TEXT,
    MucLuongCoBan REAL,
    TrangThai INTEGER DEFAULT 1
);

-- Bảng Phòng ban (không có foreign key TruongPhong ngay từ đầu)
CREATE TABLE IF NOT EXISTS PhongBan (
    MaPhongBan TEXT PRIMARY KEY,
    TenPhongBan TEXT NOT NULL,
    MoTa TEXT,
    NgayThanhLap DATE,
    TruongPhong TEXT,
    TrangThai INTEGER DEFAULT 1 -- 1: Hoạt động, 0: Không hoạt động
);

-- Bảng Nhân viên
CREATE TABLE IF NOT EXISTS NhanVien (
    MaNhanVien TEXT PRIMARY KEY,
    HoTen TEXT NOT NULL,
    NgaySinh DATE,
    GioiTinh TEXT DEFAULT 'Nam' CHECK (GioiTinh IN ('Nam', 'Nữ', 'Khác')),
    CCCD TEXT UNIQUE,
    SoDienThoai TEXT,
    Email TEXT,
    DiaChi TEXT,
    NgayVaoLam DATE NOT NULL,
    MaPhongBan TEXT,
    MaChucVu TEXT,
    TrangThaiLamViec TEXT DEFAULT 'Đang làm việc' CHECK (TrangThaiLamViec IN ('Đang làm việc', 'Nghỉ việc', 'Tạm nghỉ')),
    NgayTaoTaiKhoan DATETIME DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan),
    FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)
);

-- Bảng Hợp đồng lao động
CREATE TABLE IF NOT EXISTS HopDongLaoDong (
    MaHopDong TEXT PRIMARY KEY,
    MaNhanVien TEXT NOT NULL,
    LoaiHopDong TEXT NOT NULL CHECK (LoaiHopDong IN ('Thử việc', 'Có thời hạn', 'Không thời hạn')),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE,
    MucLuong REAL NOT NULL,
    PhuCap REAL DEFAULT 0,
    NoiDung TEXT,
    TrangThai TEXT DEFAULT 'Hiệu lực' CHECK (TrangThai IN ('Hiệu lực', 'Hết hạn', 'Hủy bỏ')),
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- Bảng Chấm công
CREATE TABLE IF NOT EXISTS ChamCong (
    MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    NgayChamCong DATE NOT NULL,
    GioVao TIME,
    GioRa TIME,
    SoGioLam REAL DEFAULT 0,
    SoGioTangCa REAL DEFAULT 0,
    TrangThai TEXT DEFAULT 'Có mặt' CHECK (TrangThai IN ('Có mặt', 'Vắng mặt', 'Nghỉ phép', 'Nghỉ ốm', 'Đi muộn')),
    GhiChu TEXT,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    UNIQUE (MaNhanVien, NgayChamCong)
);

-- Bảng Nghỉ phép
CREATE TABLE IF NOT EXISTS NghiPhep (
    MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    LoaiNghiPhep TEXT NOT NULL CHECK (LoaiNghiPhep IN ('Nghỉ phép năm', 'Nghỉ ốm', 'Nghỉ thai sản', 'Nghỉ không lương', 'Khác')),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    SoNgayNghi INTEGER NOT NULL,
    LyDo TEXT,
    TrangThai TEXT DEFAULT 'Chờ duyệt' CHECK (TrangThai IN ('Chờ duyệt', 'Đã duyệt', 'Từ chối')),
    NguoiDuyet TEXT,
    NgayDuyet DATE,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    FOREIGN KEY (NguoiDuyet) REFERENCES NhanVien(MaNhanVien)
);

-- Bảng Lương
CREATE TABLE IF NOT EXISTS Luong (
    MaLuong INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL,
    ThangNam TEXT NOT NULL, -- Format: YYYY-MM
    LuongCoBan REAL NOT NULL,
    PhuCap REAL DEFAULT 0,
    ThuongHieuSuat REAL DEFAULT 0,
    SoGioLam REAL DEFAULT 0,
    SoGioTangCa REAL DEFAULT 0,
    TienTangCa REAL DEFAULT 0,
    KhauTru REAL DEFAULT 0,
    TongLuong REAL NOT NULL,
    TrangThai TEXT DEFAULT 'Chưa thanh toán' CHECK (TrangThai IN ('Chưa thanh toán', 'Đã thanh toán')),
    NgayTinhLuong DATE,
    NgayThanhToan DATE,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    UNIQUE (MaNhanVien, ThangNam)
);

-- Bảng Tài khoản đăng nhập
CREATE TABLE IF NOT EXISTS TaiKhoan (
    MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT,
    MaNhanVien TEXT NOT NULL UNIQUE,
    TenDangNhap TEXT NOT NULL UNIQUE,
    MatKhau TEXT NOT NULL,
    VaiTro TEXT DEFAULT 'Employee' CHECK (VaiTro IN ('Admin', 'HR', 'Manager', 'Employee')),
    TrangThai INTEGER DEFAULT 1, -- 1: Hoạt động, 0: Khóa
    LanDangNhapCuoi DATETIME,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- Tạo các chỉ mục để tối ưu hiệu suất
CREATE INDEX IF NOT EXISTS idx_nhanvien_phongban ON NhanVien(MaPhongBan);
CREATE INDEX IF NOT EXISTS idx_nhanvien_chucvu ON NhanVien(MaChucVu);
CREATE INDEX IF NOT EXISTS idx_chamcong_nhanvien ON ChamCong(MaNhanVien);
CREATE INDEX IF NOT EXISTS idx_chamcong_ngay ON ChamCong(NgayChamCong);
CREATE INDEX IF NOT EXISTS idx_nghiphep_nhanvien ON NghiPhep(MaNhanVien);
CREATE INDEX IF NOT EXISTS idx_luong_nhanvien ON Luong(MaNhanVien);
CREATE INDEX IF NOT EXISTS idx_luong_thangnam ON Luong(ThangNam);

-- Dữ liệu mẫu

-- Thêm dữ liệu Chức vụ
INSERT OR IGNORE INTO ChucVu (MaChucVu, TenChucVu, MoTa, MucLuongCoBan, TrangThai) VALUES
('CV001', 'Giám đốc', 'Giám đốc điều hành công ty', 50000000, 1),
('CV002', 'Phó Giám đốc', 'Phó giám đốc', 35000000, 1),
('CV003', 'Trưởng phòng', 'Trưởng phòng ban', 25000000, 1),
('CV004', 'Phó phòng', 'Phó trưởng phòng', 20000000, 1),
('CV005', 'Nhân viên chính', 'Nhân viên có kinh nghiệm', 15000000, 1),
('CV006', 'Nhân viên', 'Nhân viên thường', 12000000, 1),
('CV007', 'Thực tập sinh', 'Sinh viên thực tập', 5000000, 1);

-- Thêm dữ liệu Phòng ban
INSERT OR IGNORE INTO PhongBan (MaPhongBan, TenPhongBan, MoTa, NgayThanhLap, TrangThai) VALUES
('PB001', 'Phòng Nhân sự', 'Quản lý nhân sự và tuyển dụng', '2020-01-15', 1),
('PB002', 'Phòng Kế toán', 'Quản lý tài chính và kế toán', '2020-01-15', 1),
('PB003', 'Phòng Kinh doanh', 'Phát triển kinh doanh và bán hàng', '2020-02-01', 1),
('PB004', 'Phòng Kỹ thuật', 'Phát triển sản phẩm và công nghệ', '2020-03-01', 1),
('PB005', 'Phòng Marketing', 'Marketing và truyền thông', '2020-04-01', 1);

-- Thêm dữ liệu Nhân viên
INSERT OR IGNORE INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email, DiaChi, NgayVaoLam, MaPhongBan, MaChucVu, TrangThaiLamViec) VALUES
('NV001', 'Nguyễn Văn An', '1985-05-15', 'Nam', '123456789012', '0901234567', 'an.nguyen@company.com', '123 Đường ABC, Quận 1, TP.HCM', '2020-01-20', 'PB001', 'CV001', 'Đang làm việc'),
('NV002', 'Trần Thị Bình', '1990-08-22', 'Nữ', '123456789013', '0901234568', 'binh.tran@company.com', '456 Đường DEF, Quận 2, TP.HCM', '2020-02-01', 'PB001', 'CV003', 'Đang làm việc'),
('NV003', 'Lê Văn Cường', '1988-12-10', 'Nam', '123456789014', '0901234569', 'cuong.le@company.com', '789 Đường GHI, Quận 3, TP.HCM', '2020-03-15', 'PB002', 'CV003', 'Đang làm việc'),
('NV004', 'Phạm Thị Dung', '1992-03-25', 'Nữ', '123456789015', '0901234570', 'dung.pham@company.com', '321 Đường JKL, Quận 4, TP.HCM', '2020-04-01', 'PB003', 'CV005', 'Đang làm việc'),
('NV005', 'Hoàng Văn Em', '1995-07-18', 'Nam', '123456789016', '0901234571', 'em.hoang@company.com', '654 Đường MNO, Quận 5, TP.HCM', '2020-05-10', 'PB004', 'CV006', 'Đang làm việc');

-- Cập nhật Trưởng phòng
UPDATE PhongBan SET TruongPhong = 'NV002' WHERE MaPhongBan = 'PB001';
UPDATE PhongBan SET TruongPhong = 'NV003' WHERE MaPhongBan = 'PB002';

-- Thêm dữ liệu Tài khoản (mật khẩu đã hash SHA256: secret123)
INSERT OR IGNORE INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES
('NV001', 'admin', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Admin', 1),
('NV002', 'hr_manager', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'HR', 1),
('NV003', 'acc_manager', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Manager', 1),
('NV004', 'employee1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Employee', 1),
('NV005', 'employee2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Employee', 1);

-- Thêm dữ liệu Hợp đồng lao động
INSERT OR IGNORE INTO HopDongLaoDong (MaHopDong, MaNhanVien, LoaiHopDong, NgayBatDau, NgayKetThuc, MucLuong, PhuCap, NoiDung, TrangThai) VALUES
('HD001', 'NV001', 'Không thời hạn', '2020-01-20', NULL, 50000000, 5000000, 'Hợp đồng lao động Giám đốc', 'Hiệu lực'),
('HD002', 'NV002', 'Không thời hạn', '2020-02-01', NULL, 25000000, 3000000, 'Hợp đồng lao động Trưởng phòng NS', 'Hiệu lực'),
('HD003', 'NV003', 'Có thời hạn', '2020-03-15', '2025-03-14', 25000000, 2500000, 'Hợp đồng lao động Trưởng phòng KT', 'Hiệu lực'),
('HD004', 'NV004', 'Có thời hạn', '2020-04-01', '2025-03-31', 15000000, 1500000, 'Hợp đồng lao động Nhân viên KD', 'Hiệu lực'),
('HD005', 'NV005', 'Có thời hạn', '2020-05-10', '2025-05-09', 12000000, 1000000, 'Hợp đồng nhân viên', 'Hiệu lực');

-- Thêm dữ liệu Chấm công mẫu
INSERT OR IGNORE INTO ChamCong (MaNhanVien, NgayChamCong, GioVao, GioRa, SoGioLam, SoGioTangCa, TrangThai, GhiChu) VALUES
('NV001', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV001', '2024-03-02', '08:15:00', '17:30:00', 8.25, 0, 'Đi muộn', 'Đi muộn 15 phút'),
('NV002', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV002', '2024-03-02', NULL, NULL, 0, 0, 'Nghỉ phép', 'Nghỉ phép năm'),
('NV003', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV003', '2024-03-02', '08:00:00', '19:00:00', 8.5, 2.5, 'Có mặt', 'Tăng ca');

-- Thêm dữ liệu Nghỉ phép mẫu
INSERT OR IGNORE INTO NghiPhep (MaNhanVien, LoaiNghiPhep, NgayBatDau, NgayKetThuc, SoNgayNghi, LyDo, TrangThai, NguoiDuyet, NgayDuyet) VALUES
('NV002', 'Nghỉ phép năm', '2024-03-02', '2024-03-02', 1, 'Nghỉ phép cá nhân', 'Đã duyệt', 'NV001', '2024-02-28'),
('NV004', 'Nghỉ ốm', '2024-02-15', '2024-02-16', 2, 'Bị cảm cúm', 'Đã duyệt', 'NV002', '2024-02-14'),
('NV005', 'Nghỉ phép năm', '2024-03-10', '2024-03-12', 3, 'Về quê thăm gia đình', 'Chờ duyệt', NULL, NULL);

-- Thêm dữ liệu Lương mẫu
INSERT OR IGNORE INTO Luong (MaNhanVien, ThangNam, LuongCoBan, PhuCap, ThuongHieuSuat, SoGioLam, SoGioTangCa, TienTangCa, KhauTru, TongLuong, TrangThai, NgayTinhLuong, NgayThanhToan) VALUES
('NV001', '2024-02', 50000000, 5000000, 2000000, 176, 8, 2000000, 5700000, 53300000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV002', '2024-02', 25000000, 3000000, 1000000, 168, 4, 800000, 2880000, 26920000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV003', '2024-02', 25000000, 2500000, 800000, 176, 12, 2400000, 3030000, 27670000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV004', '2024-02', 15000000, 1500000, 500000, 160, 0, 0, 1700000, 15300000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV005', '2024-02', 12000000, 1000000, 0, 176, 2, 300000, 1330000, 11970000, 'Chưa thanh toán', '2024-02-28', NULL);