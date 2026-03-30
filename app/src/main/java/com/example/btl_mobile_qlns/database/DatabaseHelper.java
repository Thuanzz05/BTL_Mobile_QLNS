package com.example.btl_mobile_qlns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "qlns.db";
    private static final int DATABASE_VERSION = 1;
    
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
        
        // Tạo bảng Nhân viên
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
                "TrangThaiLamViec TEXT DEFAULT 'Đang làm việc')";
        db.execSQL(createNhanVienTable);
        
        // Tạo bảng Hợp đồng lao động
        String createHopDongTable = "CREATE TABLE " + TABLE_HOP_DONG + " (" +
                "MaHopDong TEXT PRIMARY KEY, " +
                "MaNhanVien TEXT NOT NULL, " +
                "LoaiHopDong TEXT NOT NULL, " +
                "NgayBatDau DATE NOT NULL, " +
                "NgayKetThuc DATE, " +
                "MucLuong REAL NOT NULL, " +
                "TrangThai TEXT DEFAULT 'Hiệu lực')";
        db.execSQL(createHopDongTable);
        
        // Tạo bảng Chấm công
        String createChamCongTable = "CREATE TABLE " + TABLE_CHAM_CONG + " (" +
                "MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "NgayChamCong DATE NOT NULL, " +
                "GioVao TIME, " +
                "GioRa TIME, " +
                "SoGioLam REAL DEFAULT 0, " +
                "TrangThai TEXT DEFAULT 'Có mặt', " +
                "UNIQUE (MaNhanVien, NgayChamCong))";
        db.execSQL(createChamCongTable);
        
        // Tạo bảng Nghỉ phép
        String createNghiPhepTable = "CREATE TABLE " + TABLE_NGHI_PHEP + " (" +
                "MaNghiPhep INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "NgayBatDau DATE NOT NULL, " +
                "NgayKetThuc DATE NOT NULL, " +
                "SoNgayNghi INTEGER NOT NULL, " +
                "LyDo TEXT, " +
                "TrangThai TEXT DEFAULT 'Chờ duyệt', " +
                "NguoiDuyet TEXT)";
        db.execSQL(createNghiPhepTable);
        
        // Tạo bảng Lương
        String createLuongTable = "CREATE TABLE " + TABLE_LUONG + " (" +
                "MaLuong INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL, " +
                "ThangNam TEXT NOT NULL, " +
                "LuongCoBan REAL NOT NULL, " +
                "PhuCap REAL DEFAULT 0, " +
                "SoGioLam REAL DEFAULT 0, " +
                "TongLuong REAL NOT NULL, " +
                "TrangThai TEXT DEFAULT 'Chưa thanh toán', " +
                "NgayTinhLuong DATE, " +
                "UNIQUE (MaNhanVien, ThangNam))";
        db.execSQL(createLuongTable);
        
        // Tạo bảng Tài khoản
        String createTaiKhoanTable = "CREATE TABLE " + TABLE_TAI_KHOAN + " (" +
                "MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaNhanVien TEXT NOT NULL UNIQUE, " +
                "TenDangNhap TEXT NOT NULL UNIQUE, " +
                "MatKhau TEXT NOT NULL, " +
                "VaiTro TEXT DEFAULT 'Employee', " +
                "TrangThai INTEGER DEFAULT 1)";
        db.execSQL(createTaiKhoanTable);
        
        // Thêm dữ liệu mẫu
        insertSampleData(db);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAI_KHOAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NGHI_PHEP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAM_CONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOP_DONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHAN_VIEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONG_BAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHUC_VU);
        onCreate(db);
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
        nv1.put("MaChucVu", "CV001");
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
        
        // Thêm tài khoản
        ContentValues tk1 = new ContentValues();
        tk1.put("MaNhanVien", "NV001");
        tk1.put("TenDangNhap", "admin");
        tk1.put("MatKhau", "123456");
        tk1.put("VaiTro", "Admin");
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
    
    // Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAI_KHOAN + 
                      " WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 1";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
    
    // Lấy thông tin user
    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT tk.*, nv.HoTen FROM " + TABLE_TAI_KHOAN + " tk " +
                      "JOIN " + TABLE_NHAN_VIEN + " nv ON tk.MaNhanVien = nv.MaNhanVien " +
                      "WHERE tk.TenDangNhap = ?";
        return db.rawQuery(query, new String[]{username});
    }
    
    // Kiểm tra username đã tồn tại
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAI_KHOAN + " WHERE TenDangNhap = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    
    // Kiểm tra mã nhân viên đã tồn tại
    public boolean checkEmployeeExists(String maNhanVien) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NHAN_VIEN + " WHERE MaNhanVien = ?";
        Cursor cursor = db.rawQuery(query, new String[]{maNhanVien});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    
    // Lấy danh sách phòng ban
    public Cursor getAllDepartments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PHONG_BAN + " WHERE TrangThai = 1", null);
    }
    
    // Lấy danh sách chức vụ
    public Cursor getAllPositions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CHUC_VU + " WHERE TrangThai = 1", null);
    }
    
    // Thêm nhân viên
    public boolean addEmployee(String maNV, String hoTen, String ngaySinh, String gioiTinh, 
                              String sdt, String email, String maPB, String maCV) {
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
        cv.put("TrangThaiLamViec", "Đang làm việc");
        
        long result = db.insert(TABLE_NHAN_VIEN, null, cv);
        return result != -1;
    }

    // Cập nhật nhân viên
    public boolean updateEmployee(String maNV, String hoTen, String ngaySinh, String gioiTinh, 
                                 String sdt, String email, String maPB, String maCV) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("HoTen", hoTen);
        cv.put("NgaySinh", ngaySinh);
        cv.put("GioiTinh", gioiTinh);
        cv.put("SoDienThoai", sdt);
        cv.put("Email", email);
        cv.put("MaPhongBan", maPB);
        cv.put("MaChucVu", maCV);
        
        int result = db.update(TABLE_NHAN_VIEN, cv, "MaNhanVien = ?", new String[]{maNV});
        return result > 0;
    }
    
    // Đăng ký tài khoản mới (tạo cả nhân viên và tài khoản)
    public boolean registerUser(String maNhanVien, String hoTen, String ngaySinh, String gioiTinh,
                               String soDienThoai, String email, String ngayVaoLam, String maPhongBan,
                               String maChucVu, String tenDangNhap, String matKhau) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            db.beginTransaction();
            
            // Thêm nhân viên trước
            ContentValues nvValues = new ContentValues();
            nvValues.put("MaNhanVien", maNhanVien);
            nvValues.put("HoTen", hoTen);
            nvValues.put("NgaySinh", ngaySinh);
            nvValues.put("GioiTinh", gioiTinh);
            nvValues.put("SoDienThoai", soDienThoai);
            nvValues.put("Email", email);
            nvValues.put("NgayVaoLam", ngayVaoLam);
            nvValues.put("MaPhongBan", maPhongBan);
            nvValues.put("MaChucVu", maChucVu);
            nvValues.put("TrangThaiLamViec", "Đang làm việc");
            
            long nvResult = db.insert(TABLE_NHAN_VIEN, null, nvValues);
            
            if (nvResult != -1) {
                // Thêm tài khoản
                ContentValues tkValues = new ContentValues();
                tkValues.put("MaNhanVien", maNhanVien);
                tkValues.put("TenDangNhap", tenDangNhap);
                tkValues.put("MatKhau", matKhau);
                tkValues.put("VaiTro", "Employee");
                tkValues.put("TrangThai", 1);
                
                long tkResult = db.insert(TABLE_TAI_KHOAN, null, tkValues);
                
                if (tkResult != -1) {
                    db.setTransactionSuccessful();
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }
    
    // Lấy tất cả nhân viên với thông tin chức vụ
    public Cursor getAllEmployees() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT nv.MaNhanVien, nv.HoTen, nv.GioiTinh, cv.TenChucVu, pb.TenPhongBan, nv.NgaySinh, nv.SoDienThoai, nv.Email, nv.MaPhongBan, nv.MaChucVu " +
                      "FROM NhanVien nv " +
                      "LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu " +
                      "LEFT JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan " +
                      "WHERE nv.TrangThaiLamViec = 'Đang làm việc'";
        return db.rawQuery(query, null);
    }

    public boolean deleteEmployee(String maNhanVien) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThaiLamViec", "Đã nghỉ việc");
        int result = db.update(TABLE_NHAN_VIEN, values, "MaNhanVien = ?", new String[]{maNhanVien});
        return result > 0;
    }
}