package com.example.btl_mobile_qlns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "qlns.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để thực hiện upgrade
    
    // Tên bảng
    public static final String TABLE_CHUC_VU = "ChucVu";
    public static final String TABLE_PHONG_BAN = "PhongBan";
    public static final String TABLE_NHAN_VIEN = "NhanVien";
    public static final String TABLE_HOP_DONG = "HopDongLaoDong";
    public static final String TABLE_CHAM_CONG = "ChamCong";
    public static final String TABLE_NGHI_PHEP = "NghiPhep";
    public static final String TABLE_LUONG = "Luong";
    public static final String TABLE_TAI_KHOAN = "TaiKhoan";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Chức vụ
        String createChucVuTable = "CREATE TABLE " + TABLE_CHUC_VU + " (" +
                "MaChucVu TEXT PRIMARY KEY, " +
                "TenChucVu TEXT NOT NULL, " +
                "MucLuongCoBan REAL, " +
                "TrangThai INTEGER DEFAULT 1)";
        db.execSQL(createChucVuTable);
        
        // Tạo bảng Phòng ban
        String createPhongBanTable = "CREATE TABLE " + TABLE_PHONG_BAN + " (" +
                "MaPhongBan TEXT PRIMARY KEY, " +
                "TenPhongBan TEXT NOT NULL, " +
                "TruongPhong TEXT, " +
                "TrangThai INTEGER DEFAULT 1)";
        db.execSQL(createPhongBanTable);
        
        // Tạo bảng Nhân viên (Thêm cột HinhAnh)
        String createNhanVienTable = "CREATE TABLE " + TABLE_NHAN_VIEN + " (" +
                "MaNhanVien TEXT PRIMARY KEY, " +
                "HoTen TEXT NOT NULL, " +
                "NgaySinh DATE, " +
                "GioiTinh TEXT DEFAULT 'Nam', " +
                "SoDienThoai TEXT, " +
                "Email TEXT, " +
                "NgayVaoLam DATE NOT NULL, " +
                "MaPhongBan TEXT, " +
                "MaChucVu TEXT, " +
                "HinhAnh TEXT, " + // Cột mới để lưu URI ảnh
                "TrangThaiLamViec TEXT DEFAULT 'Đang làm việc')";
        db.execSQL(createNhanVienTable);
        
        // Các bảng khác giữ nguyên...
        db.execSQL("CREATE TABLE " + TABLE_HOP_DONG + " (MaHopDong TEXT PRIMARY KEY, MaNhanVien TEXT NOT NULL, LoaiHopDong TEXT NOT NULL, NgayBatDau DATE NOT NULL, NgayKetThuc DATE, MucLuong REAL NOT NULL, TrangThai TEXT DEFAULT 'Hiệu lực')");
        db.execSQL("CREATE TABLE " + TABLE_CHAM_CONG + " (MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT, MaNhanVien TEXT NOT NULL, NgayChamCong DATE NOT NULL, GioVao TIME, GioRa TIME, SoGioLam REAL DEFAULT 0, TrangThai TEXT DEFAULT 'Có mặt', UNIQUE (MaNhanVien, NgayChamCong))");
        db.execSQL("CREATE TABLE " + TABLE_NGHI_PHEP + " (MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT, MaNhanVien TEXT NOT NULL, NgayBatDau DATE NOT NULL, NgayKetThuc DATE NOT NULL, SoNgayNghi INTEGER NOT NULL, LyDo TEXT, TrangThai TEXT DEFAULT 'Chờ duyệt', NguoiDuyet TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_LUONG + " (MaLuong INTEGER PRIMARY KEY AUTOINCREMENT, MaNhanVien TEXT NOT NULL, ThangNam TEXT NOT NULL, LuongCoBan REAL NOT NULL, PhuCap REAL DEFAULT 0, SoGioLam REAL DEFAULT 0, TongLuong REAL NOT NULL, TrangThai TEXT DEFAULT 'Chưa thanh toán', NgayTinhLuong DATE, UNIQUE (MaNhanVien, ThangNam))");
        db.execSQL("CREATE TABLE " + TABLE_TAI_KHOAN + " (MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, MaNhanVien TEXT NOT NULL UNIQUE, TenDangNhap TEXT NOT NULL UNIQUE, MatKhau TEXT NOT NULL, VaiTro TEXT DEFAULT 'Employee', TrangThai INTEGER DEFAULT 1)");
        
        insertSampleData(db);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NHAN_VIEN + " ADD COLUMN HinhAnh TEXT");
        }
    }
    
    private void insertSampleData(SQLiteDatabase db) {
        // Dữ liệu mẫu
        db.execSQL("INSERT INTO " + TABLE_PHONG_BAN + " (MaPhongBan, TenPhongBan) VALUES ('PB001', 'Phòng Hành chính')");
        db.execSQL("INSERT INTO " + TABLE_PHONG_BAN + " (MaPhongBan, TenPhongBan) VALUES ('PB002', 'Phòng Kỹ thuật')");
        db.execSQL("INSERT INTO " + TABLE_CHUC_VU + " (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV001', 'Giám đốc', 20000000)");
        db.execSQL("INSERT INTO " + TABLE_CHUC_VU + " (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV002', 'Trưởng phòng', 15000000)");
        db.execSQL("INSERT INTO " + TABLE_CHUC_VU + " (MaChucVu, TenChucVu, MucLuongCoBan) VALUES ('CV003', 'Nhân viên', 8000000)");
        
        // Tài khoản admin mặc định
        db.execSQL("INSERT INTO " + TABLE_NHAN_VIEN + " (MaNhanVien, HoTen, NgayVaoLam, MaPhongBan, MaChucVu) VALUES ('ADMIN', 'Administrator', '2023-01-01', 'PB001', 'CV001')");
        db.execSQL("INSERT INTO " + TABLE_TAI_KHOAN + " (MaNhanVien, TenDangNhap, MatKhau, VaiTro) VALUES ('ADMIN', 'admin', 'admin123', 'Admin')");
    }

    public boolean addEmployee(String maNV, String hoTen, String ngaySinh, String gioiTinh, 
                              String sdt, String email, String maPB, String maCV, String hinhAnh) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("MaNhanVien", maNV);
        cv.put("HoTen", hoTen);
        cv.put("NgaySinh", ngaySinh);
        cv.put("GioiTinh", gioiTinh);
        cv.put("SoDienThoai", sdt);
        cv.put("Email", email);
        cv.put("NgayVaoLam", java.time.LocalDate.now().toString());
        cv.put("MaPhongBan", maPB);
        cv.put("MaChucVu", maCV);
        cv.put("HinhAnh", hinhAnh);
        cv.put("TrangThaiLamViec", "Đang làm việc");
        
        long result = db.insert(TABLE_NHAN_VIEN, null, cv);
        return result != -1;
    }

    public boolean updateEmployee(String maNV, String hoTen, String ngaySinh, String gioiTinh, 
                                 String sdt, String email, String maPB, String maCV, String hinhAnh) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("HoTen", hoTen);
        cv.put("NgaySinh", ngaySinh);
        cv.put("GioiTinh", gioiTinh);
        cv.put("SoDienThoai", sdt);
        cv.put("Email", email);
        cv.put("MaPhongBan", maPB);
        cv.put("MaChucVu", maCV);
        cv.put("HinhAnh", hinhAnh);
        
        int result = db.update(TABLE_NHAN_VIEN, cv, "MaNhanVien = ?", new String[]{maNV});
        return result > 0;
    }

    public boolean checkEmployeeExists(String maNhanVien) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NHAN_VIEN + " WHERE MaNhanVien = ?", new String[]{maNhanVien});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAI_KHOAN + " WHERE TenDangNhap = ?", new String[]{username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAI_KHOAN + 
                " WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 1", 
                new String[]{username, password});
        boolean success = (cursor.getCount() > 0);
        cursor.close();
        return success;
    }

    public boolean registerUser(String maNV, String hoTen, String ngaySinh, String gioiTinh,
                               String sdt, String email, String ngayVaoLam, String maPB, 
                               String maCV, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Thêm nhân viên
            ContentValues nvValues = new ContentValues();
            nvValues.put("MaNhanVien", maNV);
            nvValues.put("HoTen", hoTen);
            nvValues.put("NgaySinh", ngaySinh);
            nvValues.put("GioiTinh", gioiTinh);
            nvValues.put("SoDienThoai", sdt);
            nvValues.put("Email", email);
            nvValues.put("NgayVaoLam", ngayVaoLam);
            nvValues.put("MaPhongBan", maPB);
            nvValues.put("MaChucVu", maCV);
            nvValues.put("TrangThaiLamViec", "Đang làm việc");
            
            long nvResult = db.insert(TABLE_NHAN_VIEN, null, nvValues);
            if (nvResult == -1) return false;

            // 2. Thêm tài khoản
            ContentValues tkValues = new ContentValues();
            tkValues.put("MaNhanVien", maNV);
            tkValues.put("TenDangNhap", username);
            tkValues.put("MatKhau", password);
            tkValues.put("VaiTro", "Employee");
            tkValues.put("TrangThai", 1);
            
            long tkResult = db.insert(TABLE_TAI_KHOAN, null, tkValues);
            if (tkResult == -1) return false;

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllDepartments() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_PHONG_BAN + " WHERE TrangThai = 1", null);
    }

    public Cursor getAllPositions() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_CHUC_VU + " WHERE TrangThai = 1", null);
    }

    public Cursor getAllEmployees() {
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan FROM " + TABLE_NHAN_VIEN + " nv " +
                      "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                      "LEFT JOIN " + TABLE_PHONG_BAN + " pb ON nv.MaPhongBan = pb.MaPhongBan " +
                      "WHERE nv.TrangThaiLamViec = 'Đang làm việc'";
        return getReadableDatabase().rawQuery(query, null);
    }

    public String getNextEmployeeCode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MaNhanVien FROM " + TABLE_NHAN_VIEN + 
                      " WHERE MaNhanVien LIKE 'NV%' " +
                      " ORDER BY length(MaNhanVien) DESC, MaNhanVien DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String lastCode = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) lastCode = cursor.getString(0).trim();
            cursor.close();
        }
        if (lastCode == null) return "NV001";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^([a-zA-Z]+)(\\d+)$").matcher(lastCode);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            String numberStr = matcher.group(2);
            try {
                int nextNumber = Integer.parseInt(numberStr) + 1;
                return String.format("%s%0" + numberStr.length() + "d", prefix, nextNumber);
            } catch (Exception e) { return "NV001"; }
        }
        return "NV001";
    }

    public boolean deleteEmployee(String maNhanVien) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThaiLamViec", "Đã nghỉ việc");
        return db.update(TABLE_NHAN_VIEN, values, "MaNhanVien = ?", new String[]{maNhanVien}) > 0;
    }

    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT nv.HoTen, tk.VaiTro FROM " + TABLE_TAI_KHOAN + " tk " +
                      "JOIN " + TABLE_NHAN_VIEN + " nv ON tk.MaNhanVien = nv.MaNhanVien " +
                      "WHERE tk.TenDangNhap = ?";
        return db.rawQuery(query, new String[]{username});
    }
}
