package com.example.btl_mobile_qlns.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QLNS.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bảng Chức vụ
        db.execSQL("CREATE TABLE IF NOT EXISTS ChucVu (" +
                "MaChucVu TEXT PRIMARY KEY, " +
                "TenChucVu TEXT NOT NULL, " +
                "MoTa TEXT, " +
                "MucLuongCoBan REAL, " +
                "TrangThai INTEGER DEFAULT 1)");

        // 2. Bảng Phòng ban
        db.execSQL("CREATE TABLE IF NOT EXISTS PhongBan (" +
                "MaPhongBan TEXT PRIMARY KEY, " +
                "TenPhongBan TEXT NOT NULL, " +
                "MoTa TEXT, " +
                "NgayThanhLap DATE, " +
                "TruongPhong TEXT, " +
                "TrangThai INTEGER DEFAULT 1)");

        // 3. Bảng Nhân viên
        db.execSQL("CREATE TABLE IF NOT EXISTS NhanVien (" +
                "MaNhanVien TEXT PRIMARY KEY, " +
                "HoTen TEXT NOT NULL, " +
                "NgaySinh DATE, " +
                "GioiTinh TEXT DEFAULT 'Nam' CHECK (GioiTinh IN ('Nam', 'Nữ', 'Khác')), " +
                "CCCD TEXT UNIQUE, " +
                "SoDienThoai TEXT, " +
                "Email TEXT, " +
                "DiaChi TEXT, " +
                "NgayVaoLam DATE NOT NULL, " +
                "MaPhongBan TEXT, " +
                "MaChucVu TEXT, " +
                "TrangThaiLamViec TEXT DEFAULT 'Đang làm việc' CHECK (TrangThaiLamViec IN ('Đang làm việc', 'Nghỉ việc', 'Tạm nghỉ')), " +
                "NgayTaoTaiKhoan DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "NgayCapNhat DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan), " +
                "FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu))");

        // 4. Bảng Hợp đồng lao động
        db.execSQL("CREATE TABLE IF NOT EXISTS HopDongLaoDong (" +
                "MaHopDong TEXT PRIMARY KEY, " +
                "MaNhanVien TEXT NOT NULL, " +
                "LoaiHopDong TEXT NOT NULL CHECK (LoaiHopDong IN ('Thử việc', 'Có thời hạn', 'Không thời hạn')), " +
                "NgayBatDau DATE NOT NULL, " +
                "NgayKetThuc DATE, " +
                "MucLuong REAL NOT NULL, " +
                "PhuCap REAL DEFAULT 0, " +
                "NoiDung TEXT, " +
                "TrangThai TEXT DEFAULT 'Hiệu lực' CHECK (TrangThai IN ('Hiệu lực', 'Hết hạn', 'Hủy bỏ')), " +
                "NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien))");

        // 5. Bảng Chấm công
        db.execSQL("CREATE TABLE IF NOT EXISTS ChamCong (" +
                "MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "NgayChamCong DATE NOT NULL, " +
                "GioVao TIME, " +
                "GioRa TIME, " +
                "SoGioLam REAL DEFAULT 0, " +
                "SoGioTangCa REAL DEFAULT 0, " +
                "TrangThai TEXT DEFAULT 'Có mặt' CHECK (TrangThai IN ('Có mặt', 'Vắng mặt', 'Nghỉ phép', 'Nghỉ ốm', 'Đi muộn')), " +
                "GhiChu TEXT, " +
                "NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien), " +
                "UNIQUE (MaNhanVien, NgayChamCong))");

        // 6. Bảng Nghỉ phép
        db.execSQL("CREATE TABLE IF NOT EXISTS NghiPhep (" +
                "MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "LoaiNghiPhep TEXT NOT NULL CHECK (LoaiNghiPhep IN ('Nghỉ phép năm', 'Nghỉ ốm', 'Nghỉ thai sản', 'Nghỉ không lương', 'Khác')), " +
                "NgayBatDau DATE NOT NULL, " +
                "NgayKetThuc DATE NOT NULL, " +
                "SoNgayNghi INTEGER NOT NULL, " +
                "LyDo TEXT, " +
                "TrangThai TEXT DEFAULT 'Chờ duyệt' CHECK (TrangThai IN ('Chờ duyệt', 'Đã duyệt', 'Từ chối')), " +
                "NguoiDuyet TEXT, " +
                "NgayDuyet DATE, " +
                "NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien), " +
                "FOREIGN KEY (NguoiDuyet) REFERENCES NhanVien(MaNhanVien))");

        // 7. Bảng Lương
        db.execSQL("CREATE TABLE IF NOT EXISTS Luong (" +
                "MaLuong INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "ThangNam TEXT NOT NULL, " +
                "LuongCoBan REAL NOT NULL, " +
                "PhuCap REAL DEFAULT 0, " +
                "ThuongHieuSuat REAL DEFAULT 0, " +
                "SoGioLam REAL DEFAULT 0, " +
                "SoGioTangCa REAL DEFAULT 0, " +
                "TienTangCa REAL DEFAULT 0, " +
                "KhauTru REAL DEFAULT 0, " +
                "TongLuong REAL NOT NULL, " +
                "TrangThai TEXT DEFAULT 'Chưa thanh toán' CHECK (TrangThai IN ('Chưa thanh toán', 'Đã thanh toán')), " +
                "NgayTinhLuong DATE, " +
                "NgayThanhToan DATE, " +
                "NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien), " +
                "UNIQUE (MaNhanVien, ThangNam))");

        // 8. Bảng Tài khoản
        db.execSQL("CREATE TABLE IF NOT EXISTS TaiKhoan (" +
                "MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL UNIQUE, " +
                "TenDangNhap TEXT NOT NULL UNIQUE, " +
                "MatKhau TEXT NOT NULL, " +
                "VaiTro TEXT DEFAULT 'Employee' CHECK (VaiTro IN ('Admin', 'HR', 'Manager', 'Employee')), " +
                "TrangThai INTEGER DEFAULT 1, " +
                "LanDangNhapCuoi DATETIME, " +
                "NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "NgayCapNhat DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien))");

        // Tạo Indexes
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_nhanvien_phongban ON NhanVien(MaPhongBan)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_nhanvien_chucvu ON NhanVien(MaChucVu)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_chamcong_nhanvien ON ChamCong(MaNhanVien)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_chamcong_ngay ON ChamCong(NgayChamCong)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_nghiphep_nhanvien ON NghiPhep(MaNhanVien)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_luong_nhanvien ON Luong(MaNhanVien)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_luong_thangnam ON Luong(ThangNam)");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Chức vụ
        db.execSQL("INSERT OR IGNORE INTO ChucVu (MaChucVu, TenChucVu, MoTa, MucLuongCoBan) VALUES ('CV001', 'Giám đốc', 'Giám đốc điều hành', 50000000)");
        db.execSQL("INSERT OR IGNORE INTO ChucVu (MaChucVu, TenChucVu, MoTa, MucLuongCoBan) VALUES ('CV003', 'Trưởng phòng', 'Trưởng phòng ban', 25000000)");
        db.execSQL("INSERT OR IGNORE INTO ChucVu (MaChucVu, TenChucVu, MoTa, MucLuongCoBan) VALUES ('CV006', 'Nhân viên', 'Nhân viên thường', 12000000)");

        // Phòng ban
        db.execSQL("INSERT OR IGNORE INTO PhongBan (MaPhongBan, TenPhongBan, MoTa, NgayThanhLap) VALUES ('PB001', 'Phòng Nhân sự', 'Quản lý nhân sự', '2020-01-15')");
        db.execSQL("INSERT OR IGNORE INTO PhongBan (MaPhongBan, TenPhongBan, MoTa, NgayThanhLap) VALUES ('PB004', 'Phòng Kỹ thuật', 'Phát triển công nghệ', '2020-03-01')");

        // Nhân viên
        db.execSQL("INSERT OR IGNORE INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email, DiaChi, NgayVaoLam, MaPhongBan, MaChucVu) " +
                "VALUES ('NV001', 'Nguyễn Văn An', '1985-05-15', 'Nam', '123456789012', '0901234567', 'an.nguyen@company.com', 'Hà Nội', '2020-01-20', 'PB001', 'CV001')");
        db.execSQL("INSERT OR IGNORE INTO NhanVien (MaNhanVien, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email, DiaChi, NgayVaoLam, MaPhongBan, MaChucVu) " +
                "VALUES ('NV002', 'Trần Thị Bình', '1990-08-22', 'Nữ', '123456789013', '0901234568', 'binh.tran@company.com', 'Hà Nội', '2020-02-01', 'PB001', 'CV003')");

        // Tài khoản (Mật khẩu mặc định 'secret123' đã hash)
        db.execSQL("INSERT OR IGNORE INTO TaiKhoan (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('NV001', 'admin', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TaiKhoan");
        db.execSQL("DROP TABLE IF EXISTS Luong");
        db.execSQL("DROP TABLE IF EXISTS NghiPhep");
        db.execSQL("DROP TABLE IF EXISTS ChamCong");
        db.execSQL("DROP TABLE IF EXISTS HopDongLaoDong");
        db.execSQL("DROP TABLE IF EXISTS NhanVien");
        db.execSQL("DROP TABLE IF EXISTS PhongBan");
        db.execSQL("DROP TABLE IF EXISTS ChucVu");
        onCreate(db);
    }
}
