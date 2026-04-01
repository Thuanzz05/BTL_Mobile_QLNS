package com.example.btl_mobile_qlns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "qlns.db";
    private static final int DATABASE_VERSION = 4;
    
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
        String createChucVuTable = "CREATE TABLE " + TABLE_CHUC_VU + " (" +
                "MaChucVu TEXT PRIMARY KEY, " +
                "TenChucVu TEXT NOT NULL, " +
                "MucLuongCoBan REAL, " +
                "TrangThai INTEGER DEFAULT 1)";
        db.execSQL(createChucVuTable);
        
        String createPhongBanTable = "CREATE TABLE " + TABLE_PHONG_BAN + " (" +
                "MaPhongBan TEXT PRIMARY KEY, " +
                "TenPhongBan TEXT NOT NULL, " +
                "TruongPhong TEXT, " +
                "TrangThai INTEGER DEFAULT 1)";
        db.execSQL(createPhongBanTable);
        
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
                "HinhAnh TEXT, " +
                "TrangThaiLamViec TEXT DEFAULT 'Đang làm việc')";
        db.execSQL(createNhanVienTable);
        
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
        if (oldVersion < 4) {
            // Xóa hoàn toàn dữ liệu cũ và tạo lại với cấu trúc mới
            db.execSQL("DELETE FROM " + TABLE_TAI_KHOAN);
            db.execSQL("DELETE FROM " + TABLE_NHAN_VIEN);
            db.execSQL("DELETE FROM " + TABLE_PHONG_BAN);
            db.execSQL("DELETE FROM " + TABLE_CHUC_VU);
            
            // Tạo lại dữ liệu mẫu với admin riêng
            insertSampleData(db);
        }
    }
    
    private void insertSampleData(SQLiteDatabase db) {
        // Thêm dữ liệu Chức vụ
        ContentValues cv1 = new ContentValues();
        cv1.put("MaChucVu", "CV001");
        cv1.put("TenChucVu", "Giám đốc");
        cv1.put("MucLuongCoBan", 50000000);
        cv1.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv1);
        
        ContentValues cv2 = new ContentValues();
        cv2.put("MaChucVu", "CV002");
        cv2.put("TenChucVu", "Trưởng phòng");
        cv2.put("MucLuongCoBan", 25000000);
        cv2.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv2);
        
        ContentValues cv3 = new ContentValues();
        cv3.put("MaChucVu", "CV003");
        cv3.put("TenChucVu", "Nhân viên");
        cv3.put("MucLuongCoBan", 12000000);
        cv3.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv3);
        
        // Thêm dữ liệu Phòng ban
        ContentValues pb1 = new ContentValues();
        pb1.put("MaPhongBan", "PB001");
        pb1.put("TenPhongBan", "Phòng Nhân sự");
        pb1.put("TrangThai", 1);
        db.insert(TABLE_PHONG_BAN, null, pb1);
        
        ContentValues pb2 = new ContentValues();
        pb2.put("MaPhongBan", "PB002");
        pb2.put("TenPhongBan", "Phòng Kế toán");
        pb2.put("TrangThai", 1);
        db.insert(TABLE_PHONG_BAN, null, pb2);
        
        ContentValues pb3 = new ContentValues();
        pb3.put("MaPhongBan", "PB003");
        pb3.put("TenPhongBan", "Phòng Kỹ thuật");
        pb3.put("TrangThai", 1);
        db.insert(TABLE_PHONG_BAN, null, pb3);
        
        // Thêm dữ liệu Nhân viên
        ContentValues nv1 = new ContentValues();
        nv1.put("MaNhanVien", "NV001");
        nv1.put("HoTen", "Nguyễn Văn An");
        nv1.put("NgaySinh", "1985-05-15");
        nv1.put("GioiTinh", "Nam");
        nv1.put("SoDienThoai", "0901234567");
        nv1.put("Email", "an@company.com");
        nv1.put("NgayVaoLam", "2020-01-20");
        nv1.put("MaPhongBan", "PB001");
        nv1.put("MaChucVu", "CV002"); // Trưởng phòng thay vì Giám đốc
        nv1.put("TrangThaiLamViec", "Đang làm việc");
        db.insert(TABLE_NHAN_VIEN, null, nv1);
        
        ContentValues nv2 = new ContentValues();
        nv2.put("MaNhanVien", "NV002");
        nv2.put("HoTen", "Trần Thị Bình");
        nv2.put("NgaySinh", "1990-08-22");
        nv2.put("GioiTinh", "Nữ");
        nv2.put("SoDienThoai", "0901234568");
        nv2.put("Email", "binh@company.com");
        nv2.put("NgayVaoLam", "2020-02-01");
        nv2.put("MaPhongBan", "PB001");
        nv2.put("MaChucVu", "CV002");
        nv2.put("TrangThaiLamViec", "Đang làm việc");
        db.insert(TABLE_NHAN_VIEN, null, nv2);
        
        ContentValues nv3 = new ContentValues();
        nv3.put("MaNhanVien", "NV003");
        nv3.put("HoTen", "Lê Văn Cường");
        nv3.put("NgaySinh", "1988-12-10");
        nv3.put("GioiTinh", "Nam");
        nv3.put("SoDienThoai", "0901234569");
        nv3.put("Email", "cuong@company.com");
        nv3.put("NgayVaoLam", "2020-03-15");
        nv3.put("MaPhongBan", "PB002");
        nv3.put("MaChucVu", "CV002");
        nv3.put("TrangThaiLamViec", "Đang làm việc");
        db.insert(TABLE_NHAN_VIEN, null, nv3);
        
        // Thêm tài khoản ADMIN riêng (không liên kết với nhân viên)
        ContentValues tkAdmin = new ContentValues();
        tkAdmin.put("MaNhanVien", "ADMIN"); // Mã đặc biệt cho admin
        tkAdmin.put("TenDangNhap", "admin");
        tkAdmin.put("MatKhau", "123456");
        tkAdmin.put("VaiTro", "Admin");
        tkAdmin.put("TrangThai", 1);
        db.insert(TABLE_TAI_KHOAN, null, tkAdmin);
        
        // Thêm tài khoản cho nhân viên
        ContentValues tk1 = new ContentValues();
        tk1.put("MaNhanVien", "NV001");
        tk1.put("TenDangNhap", "manager");
        tk1.put("MatKhau", "123456");
        tk1.put("VaiTro", "Manager");
        tk1.put("TrangThai", 1);
        db.insert(TABLE_TAI_KHOAN, null, tk1);
        
        ContentValues tk2 = new ContentValues();
        tk2.put("MaNhanVien", "NV002");
        tk2.put("TenDangNhap", "hr");
        tk2.put("MatKhau", "123456");
        tk2.put("VaiTro", "HR");
        tk2.put("TrangThai", 1);
        db.insert(TABLE_TAI_KHOAN, null, tk2);
        
        ContentValues tk3 = new ContentValues();
        tk3.put("MaNhanVien", "NV003");
        tk3.put("TenDangNhap", "user");
        tk3.put("MatKhau", "123456");
        tk3.put("VaiTro", "Employee");
        tk3.put("TrangThai", 1);
        db.insert(TABLE_TAI_KHOAN, null, tk3);
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

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAI_KHOAN + 
                " WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 1", 
                new String[]{username, password});
        boolean success = (cursor.getCount() > 0);
        cursor.close();
        return success;
    }

    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "CASE WHEN tk.MaNhanVien = 'ADMIN' THEN 'Administrator' ELSE nv.HoTen END as HoTen, " +
                "tk.VaiTro " +
                "FROM " + TABLE_TAI_KHOAN + " tk " +
                "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON tk.MaNhanVien = nv.MaNhanVien " +
                "WHERE tk.TenDangNhap = ?";
        return db.rawQuery(query, new String[]{username});
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

    public boolean registerUser(String maNV, String hoTen, String ngaySinh, String gioiTinh,
                               String sdt, String email, String ngayVaoLam, String maPB, 
                               String maCV, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert into NhanVien
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

            // Insert into TaiKhoan
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

    public Cursor getAllEmployees() {
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan FROM " + TABLE_NHAN_VIEN + " nv " +
                      "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                      "LEFT JOIN " + TABLE_PHONG_BAN + " pb ON nv.MaPhongBan = pb.MaPhongBan " +
                      "WHERE nv.TrangThaiLamViec = 'Đang làm việc'";
        return getReadableDatabase().rawQuery(query, null);
    }

    public Cursor searchEmployees(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan FROM " + TABLE_NHAN_VIEN + " nv " +
                      "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                      "LEFT JOIN " + TABLE_PHONG_BAN + " pb ON nv.MaPhongBan = pb.MaPhongBan " +
                      "WHERE nv.TrangThaiLamViec = 'Đang làm việc' AND " +
                      "(nv.HoTen LIKE ? OR nv.MaNhanVien LIKE ? OR pb.TenPhongBan LIKE ?)";
        String wildcardKeyword = "%" + keyword + "%";
        return db.rawQuery(query, new String[]{wildcardKeyword, wildcardKeyword, wildcardKeyword});
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

    public Cursor getAllDepartments() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_PHONG_BAN + " WHERE TrangThai = 1", null);
    }

    public Cursor getAllPositions() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_CHUC_VU + " WHERE TrangThai = 1", null);
    }
}
