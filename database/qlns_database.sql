-- Cơ sở dữ liệu Quản lý Nhân sự (QLNS)
-- Tạo database
CREATE DATABASE IF NOT EXISTS QLNS_DB;
USE QLNS_DB;

-- Bảng Phòng ban
CREATE TABLE PhongBan (
    MaPhongBan VARCHAR(10) PRIMARY KEY,
    TenPhongBan NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255),
    NgayThanhLap DATE,
    TruongPhong VARCHAR(10),
    TrangThai TINYINT DEFAULT 1 -- 1: Hoạt động, 0: Không hoạt động
);

-- Bảng Chức vụ
CREATE TABLE ChucVu (
    MaChucVu VARCHAR(10) PRIMARY KEY,
    TenChucVu NVARCHAR(50) NOT NULL,
    MoTa NVARCHAR(255),
    MucLuongCoBan DECIMAL(15,2),
    TrangThai TINYINT DEFAULT 1
);

-- Bảng Nhân viên
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh ENUM('Nam', 'Nữ', 'Khác') DEFAULT 'Nam',
    CCCD VARCHAR(12) UNIQUE,
    SoDienThoai VARCHAR(15),
    Email VARCHAR(100),
    DiaChi NVARCHAR(255),
    NgayVaoLam DATE NOT NULL,
    MaPhongBan VARCHAR(10),
    MaChucVu VARCHAR(10),
    TrangThaiLamViec ENUM('Đang làm việc', 'Nghỉ việc', 'Tạm nghỉ') DEFAULT 'Đang làm việc',
    NgayTaoTaiKhoan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan),
    FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)
);

-- Bảng Hợp đồng lao động
CREATE TABLE HopDongLaoDong (
    MaHopDong VARCHAR(15) PRIMARY KEY,
    MaNhanVien VARCHAR(10) NOT NULL,
    LoaiHopDong ENUM('Thử việc', 'Có thời hạn', 'Không thời hạn') NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE,
    MucLuong DECIMAL(15,2) NOT NULL,
    PhuCap DECIMAL(15,2) DEFAULT 0,
    NoiDung NVARCHAR(500),
    TrangThai ENUM('Hiệu lực', 'Hết hạn', 'Hủy bỏ') DEFAULT 'Hiệu lực',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
-- Bảng Chấm công
CREATE TABLE ChamCong (
    MaChamCong INT AUTO_INCREMENT PRIMARY KEY,
    MaNhanVien VARCHAR(10) NOT NULL,
    NgayChamCong DATE NOT NULL,
    GioVao TIME,
    GioRa TIME,
    SoGioLam DECIMAL(4,2) DEFAULT 0,
    SoGioTangCa DECIMAL(4,2) DEFAULT 0,
    TrangThai ENUM('Có mặt', 'Vắng mặt', 'Nghỉ phép', 'Nghỉ ốm', 'Đi muộn') DEFAULT 'Có mặt',
    GhiChu NVARCHAR(255),
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    UNIQUE KEY unique_nhanvien_ngay (MaNhanVien, NgayChamCong)
);

-- Bảng Nghỉ phép
CREATE TABLE NghiPhep (
    MaNghiPhep INT AUTO_INCREMENT PRIMARY KEY,
    MaNhanVien VARCHAR(10) NOT NULL,
    LoaiNghiPhep ENUM('Nghỉ phép năm', 'Nghỉ ốm', 'Nghỉ thai sản', 'Nghỉ không lương', 'Khác') NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    SoNgayNghi INT NOT NULL,
    LyDo NVARCHAR(255),
    TrangThai ENUM('Chờ duyệt', 'Đã duyệt', 'Từ chối') DEFAULT 'Chờ duyệt',
    NguoiDuyet VARCHAR(10),
    NgayDuyet DATE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    FOREIGN KEY (NguoiDuyet) REFERENCES NhanVien(MaNhanVien)
);

-- Bảng Lương
CREATE TABLE Luong (
    MaLuong INT AUTO_INCREMENT PRIMARY KEY,
    MaNhanVien VARCHAR(10) NOT NULL,
    ThangNam VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    LuongCoBan DECIMAL(15,2) NOT NULL,
    PhuCap DECIMAL(15,2) DEFAULT 0,
    ThuongHieuSuat DECIMAL(15,2) DEFAULT 0,
    SoGioLam DECIMAL(6,2) DEFAULT 0,
    SoGioTangCa DECIMAL(6,2) DEFAULT 0,
    TienTangCa DECIMAL(15,2) DEFAULT 0,
    KhauTru DECIMAL(15,2) DEFAULT 0,
    TongLuong DECIMAL(15,2) NOT NULL,
    TrangThai ENUM('Chưa thanh toán', 'Đã thanh toán') DEFAULT 'Chưa thanh toán',
    NgayTinhLuong DATE,
    NgayThanhToan DATE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    UNIQUE KEY unique_nhanvien_thang (MaNhanVien, ThangNam)
);

-- Bảng Tài khoản đăng nhập
CREATE TABLE TaiKhoan (
    MaTaiKhoan INT AUTO_INCREMENT PRIMARY KEY,
    MaNhanVien VARCHAR(10) NOT NULL UNIQUE,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    VaiTro ENUM('Admin', 'HR', 'Manager', 'Employee') DEFAULT 'Employee',
    TrangThai TINYINT DEFAULT 1, -- 1: Hoạt động, 0: Khóa
    LanDangNhapCuoi TIMESTAMP NULL,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
-- Thêm ràng buộc khóa ngoại cho TruongPhong trong bảng PhongBan
ALTER TABLE PhongBan ADD FOREIGN KEY (TruongPhong) REFERENCES NhanVien(MaNhanVien);

-- Tạo các chỉ mục để tối ưu hiệu suất
CREATE INDEX idx_nhanvien_phongban ON NhanVien(MaPhongBan);
CREATE INDEX idx_nhanvien_chucvu ON NhanVien(MaChucVu);
CREATE INDEX idx_chamcong_nhanvien ON ChamCong(MaNhanVien);
CREATE INDEX idx_chamcong_ngay ON ChamCong(NgayChamCong);
CREATE INDEX idx_nghiphep_nhanvien ON NghiPhep(MaNhanVien);
CREATE INDEX idx_luong_nhanvien ON Luong(MaNhanVien);
CREATE INDEX idx_luong_thangnam ON Luong(ThangNam);

-- Dữ liệu mẫu

-- Thêm dữ liệu Phòng ban
INSERT INTO PhongBan (MaPhongBan, TenPhongBan, MoTa, NgayThanhLap, TrangThai) VALUES
('PB001', N'Phòng Nhân sự', N'Quản lý nhân sự và tuyển dụng', '2020-01-15', 1),
('PB002', N'Phòng Kế toán', N'Quản lý tài chính và kế toán', '2020-01-15', 1),
('PB003', N'Phòng Kinh doanh', N'Phát triển kinh doanh và bán hàng', '2020-02-01', 1),
('PB004', N'Phòng Kỹ thuật', N'Phát triển sản phẩm và công nghệ', '2020-03-01', 1),
('PB005', N'Phòng Marketing', N'Marketing và truyền thông', '2020-04-01', 1);

-- Thêm dữ liệu Chức vụ
INSERT INTO ChucVu (MaChucVu, TenChucVu, MoTa, MucLuongCoBan, TrangThai) VALUES
('CV001', N'Giám đốc', N'Giám đốc điều hành công ty', 50000000, 1),
('CV002', N'Phó Giám đốc', N'Phó giám đốc', 35000000, 1),
('CV003', N'Trưởng phòng', N'Trưởng phòng ban', 25000000, 1),
('CV004', N'Phó phòng', N'Phó trưởng phòng', 20000000, 1),
('CV005', N'Nhân viên chính', N'Nhân viên có kinh nghiệm', 15000000, 1),
('CV006', N'Nhân viên', N'Nhân viên thường', 12000000, 1),
('CV007', N'Thực tập sinh', N'Sinh viên thực tập', 5000000, 1);

-- Thêm dữ liệu Nhân viên
INSERT INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email, DiaChi, NgayVaoLam, MaPhongBan, MaChucVu, TrangThaiLamViec) VALUES
('NV001', N'Nguyễn Văn An', '1985-05-15', 'Nam', '123456789012', '0901234567', 'an.nguyen@company.com', N'123 Đường ABC, Quận 1, TP.HCM', '2020-01-20', 'PB001', 'CV001', 'Đang làm việc'),
('NV002', N'Trần Thị Bình', '1990-08-22', 'Nữ', '123456789013', '0901234568', 'binh.tran@company.com', N'456 Đường DEF, Quận 2, TP.HCM', '2020-02-01', 'PB001', 'CV003', 'Đang làm việc'),
('NV003', N'Lê Văn Cường', '1988-12-10', 'Nam', '123456789014', '0901234569', 'cuong.le@company.com', N'789 Đường GHI, Quận 3, TP.HCM', '2020-03-15', 'PB002', 'CV003', 'Đang làm việc'),
('NV004', N'Phạm Thị Dung', '1992-03-25', 'Nữ', '123456789015', '0901234570', 'dung.pham@company.com', N'321 Đường JKL, Quận 4, TP.HCM', '2020-04-01', 'PB003', 'CV005', 'Đang làm việc'),
('NV005', N'Hoàng Văn Em', '1995-07-18', 'Nam', '123456789016', '0901234571', 'em.hoang@company.com', N'654 Đường MNO, Quận 5, TP.HCM', '2020-05-10', 'PB004', 'CV006', 'Đang làm việc');

-- Cập nhật Trưởng phòng
UPDATE PhongBan SET TruongPhong = 'NV002' WHERE MaPhongBan = 'PB001';
UPDATE PhongBan SET TruongPhong = 'NV003' WHERE MaPhongBan = 'PB002';

-- Thêm dữ liệu Tài khoản
INSERT INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES
('NV001', 'admin', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 1), -- password: password
('NV002', 'hr_manager', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'HR', 1),
('NV003', 'acc_manager', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Manager', 1),
('NV004', 'employee1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Employee', 1),
('NV005', 'employee2', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Employee', 1);