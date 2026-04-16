package com.example.btl_mobile_qlns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "qlns.db";
    private static final int DATABASE_VERSION = 10;
    
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
        db.execSQL("CREATE TABLE " + TABLE_CHAM_CONG + " (MaChamCong INTEGER PRIMARY KEY AUTOINCREMENT, MaNhanVien TEXT NOT NULL, NgayChamCong DATE NOT NULL, GioVao TIME, GioRa TIME, SoGioLam REAL DEFAULT 0, TrangThai TEXT DEFAULT 'Có mặt', GhiChu TEXT, UNIQUE (MaNhanVien, NgayChamCong))");
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
        if (oldVersion < 5) {
            db.execSQL("DELETE FROM " + TABLE_TAI_KHOAN);
            db.execSQL("DELETE FROM " + TABLE_NHAN_VIEN);
            db.execSQL("DELETE FROM " + TABLE_PHONG_BAN);
            db.execSQL("DELETE FROM " + TABLE_CHUC_VU);
            insertSampleData(db);
        }
        if (oldVersion < 8) {
            // Thêm cột GhiChu nếu chưa có
            try {
                db.execSQL("ALTER TABLE " + TABLE_CHAM_CONG + " ADD COLUMN GhiChu TEXT");
            } catch (Exception e) {
                // Cột đã tồn tại
            }
            // Xóa dữ liệu chấm công cũ và thêm dữ liệu mẫu mới
            db.execSQL("DELETE FROM " + TABLE_CHAM_CONG);
            insertSampleAttendance(db);
        }
        if (oldVersion < 10) {
            // Thêm nhiều dữ liệu mẫu hơn
            db.execSQL("DELETE FROM " + TABLE_TAI_KHOAN);
            db.execSQL("DELETE FROM " + TABLE_NHAN_VIEN);
            db.execSQL("DELETE FROM " + TABLE_PHONG_BAN);
            db.execSQL("DELETE FROM " + TABLE_CHUC_VU);
            db.execSQL("DELETE FROM " + TABLE_HOP_DONG);
            db.execSQL("DELETE FROM " + TABLE_CHAM_CONG);
            db.execSQL("DELETE FROM " + TABLE_NGHI_PHEP);
            db.execSQL("DELETE FROM " + TABLE_LUONG);
            insertSampleData(db);
        }
    }
    
    private void insertSampleData(SQLiteDatabase db) {
        // Thêm dữ liệu Chức vụ
        ContentValues cv1 = new ContentValues();
        cv1.put("MaChucVu", "CV001");
        cv1.put("TenChucVu", "Giám đốc");
        cv1.put("MucLuongCoBan", 15000000);
        cv1.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv1);
        
        ContentValues cv2 = new ContentValues();
        cv2.put("MaChucVu", "CV002");
        cv2.put("TenChucVu", "Trưởng phòng");
        cv2.put("MucLuongCoBan", 8000000);
        cv2.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv2);
        
        ContentValues cv3 = new ContentValues();
        cv3.put("MaChucVu", "CV003");
        cv3.put("TenChucVu", "Nhân viên");
        cv3.put("MucLuongCoBan", 5000000);
        cv3.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv3);
        
        ContentValues cv4 = new ContentValues();
        cv4.put("MaChucVu", "CV004");
        cv4.put("TenChucVu", "Phó phòng");
        cv4.put("MucLuongCoBan", 6500000);
        cv4.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv4);
        
        ContentValues cv5 = new ContentValues();
        cv5.put("MaChucVu", "CV005");
        cv5.put("TenChucVu", "Thực tập sinh");
        cv5.put("MucLuongCoBan", 3000000);
        cv5.put("TrangThai", 1);
        db.insert(TABLE_CHUC_VU, null, cv5);
        
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
        
        ContentValues pb4 = new ContentValues();
        pb4.put("MaPhongBan", "PB004");
        pb4.put("TenPhongBan", "Phòng Marketing");
        pb4.put("TrangThai", 1);
        db.insert(TABLE_PHONG_BAN, null, pb4);
        
        ContentValues pb5 = new ContentValues();
        pb5.put("MaPhongBan", "PB005");
        pb5.put("TenPhongBan", "Phòng Kinh doanh");
        pb5.put("TrangThai", 1);
        db.insert(TABLE_PHONG_BAN, null, pb5);
        
        // Thêm dữ liệu Nhân viên (10 nhân viên)
        String[][] employees = {
            {"NV002", "Trần Thị Bình", "1990-08-22", "Nữ", "0901234568", "binh@company.com", "2020-02-01", "PB001", "CV002"},
            {"NV003", "Lê Văn Cường", "1988-12-10", "Nam", "0901234569", "cuong@company.com", "2020-03-15", "PB002", "CV002"},
            {"NV004", "Phạm Thị Dung", "1992-03-25", "Nữ", "0901234570", "dung@company.com", "2020-04-01", "PB003", "CV003"},
            {"NV005", "Hoàng Văn Em", "1995-07-18", "Nam", "0901234571", "em@company.com", "2020-05-10", "PB003", "CV003"},
            {"NV006", "Nguyễn Thị Hoa", "1993-11-05", "Nữ", "0901234572", "hoa@company.com", "2021-01-15", "PB004", "CV003"},
            {"NV007", "Đặng Minh Khoa", "1987-09-30", "Nam", "0901234573", "khoa@company.com", "2021-02-20", "PB004", "CV004"},
            {"NV008", "Vũ Thị Lan", "1994-06-12", "Nữ", "0901234574", "lan@company.com", "2021-03-10", "PB005", "CV003"},
            {"NV009", "Bùi Văn Minh", "1991-04-08", "Nam", "0901234575", "minh@company.com", "2021-04-05", "PB005", "CV004"},
            {"NV010", "Lý Thị Nga", "1996-12-20", "Nữ", "0901234576", "nga@company.com", "2022-01-10", "PB001", "CV003"},
            {"NV011", "Trịnh Văn Phong", "1998-02-14", "Nam", "0901234577", "phong@company.com", "2023-06-01", "PB002", "CV005"}
        };
        
        for (String[] emp : employees) {
            ContentValues nv = new ContentValues();
            nv.put("MaNhanVien", emp[0]);
            nv.put("HoTen", emp[1]);
            nv.put("NgaySinh", emp[2]);
            nv.put("GioiTinh", emp[3]);
            nv.put("SoDienThoai", emp[4]);
            nv.put("Email", emp[5]);
            nv.put("NgayVaoLam", emp[6]);
            nv.put("MaPhongBan", emp[7]);
            nv.put("MaChucVu", emp[8]);
            nv.put("TrangThaiLamViec", "Đang làm việc");
            db.insert(TABLE_NHAN_VIEN, null, nv);
        }
        
        // Cập nhật Trưởng phòng
        ContentValues updatePB1 = new ContentValues();
        updatePB1.put("TruongPhong", "NV002");
        db.update(TABLE_PHONG_BAN, updatePB1, "MaPhongBan = ?", new String[]{"PB001"});
        
        ContentValues updatePB2 = new ContentValues();
        updatePB2.put("TruongPhong", "NV003");
        db.update(TABLE_PHONG_BAN, updatePB2, "MaPhongBan = ?", new String[]{"PB002"});
        
        // Thêm tài khoản ADMIN riêng
        ContentValues tkAdmin = new ContentValues();
        tkAdmin.put("MaNhanVien", "ADMIN");
        tkAdmin.put("TenDangNhap", "admin");
        tkAdmin.put("MatKhau", "123456");
        tkAdmin.put("VaiTro", "Admin");
        tkAdmin.put("TrangThai", 1);
        db.insert(TABLE_TAI_KHOAN, null, tkAdmin);
        
        // Thêm tài khoản cho nhân viên
        String[][] accounts = {
            {"NV002", "hr", "123456", "HR"},
            {"NV003", "manager1", "123456", "Manager"},
            {"NV004", "user1", "123456", "Employee"},
            {"NV005", "user2", "123456", "Employee"},
            {"NV006", "user3", "123456", "Employee"},
            {"NV007", "manager2", "123456", "Manager"},
            {"NV008", "user4", "123456", "Employee"},
            {"NV009", "manager3", "123456", "Manager"},
            {"NV010", "user5", "123456", "Employee"},
            {"NV011", "intern1", "123456", "Employee"}
        };
        
        for (String[] acc : accounts) {
            ContentValues tk = new ContentValues();
            tk.put("MaNhanVien", acc[0]);
            tk.put("TenDangNhap", acc[1]);
            tk.put("MatKhau", acc[2]);
            tk.put("VaiTro", acc[3]);
            tk.put("TrangThai", 1);
            db.insert(TABLE_TAI_KHOAN, null, tk);
        }
        
        // Thêm dữ liệu Hợp đồng lao động
        String[][] contracts = {
            {"HD001", "NV002", "Không thời hạn", "2020-02-01", null, "8000000"},
            {"HD002", "NV003", "Không thời hạn", "2020-03-15", null, "8000000"},
            {"HD003", "NV004", "Có thời hạn", "2020-04-01", "2025-03-31", "5000000"},
            {"HD004", "NV005", "Có thời hạn", "2020-05-10", "2025-05-09", "5000000"},
            {"HD005", "NV006", "Có thời hạn", "2021-01-15", "2026-01-14", "5000000"},
            {"HD006", "NV007", "Không thời hạn", "2021-02-20", null, "6500000"},
            {"HD007", "NV008", "Có thời hạn", "2021-03-10", "2026-03-09", "5000000"},
            {"HD008", "NV009", "Không thời hạn", "2021-04-05", null, "6500000"},
            {"HD009", "NV010", "Có thời hạn", "2022-01-10", "2027-01-09", "5000000"},
            {"HD010", "NV011", "Có thời hạn", "2023-06-01", "2024-05-31", "3000000"}
        };
        
        for (String[] contract : contracts) {
            ContentValues hd = new ContentValues();
            hd.put("MaHopDong", contract[0]);
            hd.put("MaNhanVien", contract[1]);
            hd.put("LoaiHopDong", contract[2]);
            hd.put("NgayBatDau", contract[3]);
            if (contract[4] != null) hd.put("NgayKetThuc", contract[4]);
            hd.put("MucLuong", Double.parseDouble(contract[5]));
            hd.put("TrangThai", "Hiệu lực");
            db.insert(TABLE_HOP_DONG, null, hd);
        }
        
        // Thêm dữ liệu nghỉ phép
        insertSampleLeaveRequests(db);
        
        // Thêm dữ liệu chấm công mẫu
        insertSampleAttendance(db);
    }
    
    private void insertSampleLeaveRequests(SQLiteDatabase db) {
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // Một số đơn nghỉ phép trong tháng hiện tại và tháng trước
        String[][] leaveRequests = {
            {"NV004", today.minusDays(15).toString(), today.minusDays(15).toString(), "1", "Khám bệnh định kỳ", "Đã duyệt", "NV002"},
            {"NV005", today.minusDays(10).toString(), today.minusDays(8).toString(), "3", "Về quê thăm gia đình", "Đã duyệt", "NV003"},
            {"NV006", today.minusDays(5).toString(), today.minusDays(5).toString(), "1", "Bị cảm cúm", "Đã duyệt", "NV002"},
            {"NV008", today.minusDays(3).toString(), today.minusDays(2).toString(), "2", "Công việc cá nhân", "Chờ duyệt", null},
            {"NV010", today.plusDays(5).toString(), today.plusDays(7).toString(), "3", "Nghỉ lễ gia đình", "Chờ duyệt", null},
            {"NV011", today.minusDays(20).toString(), today.minusDays(20).toString(), "1", "Đi học", "Từ chối", "NV003"},
            {"NV007", today.minusDays(12).toString(), today.minusDays(11).toString(), "2", "Nghỉ phép năm", "Đã duyệt", "NV002"}
        };
        
        for (String[] leave : leaveRequests) {
            ContentValues np = new ContentValues();
            np.put("MaNhanVien", leave[0]);
            np.put("NgayBatDau", leave[1]);
            np.put("NgayKetThuc", leave[2]);
            np.put("SoNgayNghi", Integer.parseInt(leave[3]));
            np.put("LyDo", leave[4]);
            np.put("TrangThai", leave[5]);
            if (leave[6] != null) np.put("NguoiDuyet", leave[6]);
            db.insert(TABLE_NGHI_PHEP, null, np);
        }
    }
    
    private void insertSampleAttendance(SQLiteDatabase db) {
        // Lấy ngày hiện tại và các ngày trước đó
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // Danh sách nhân viên và thời gian làm việc đa dạng
        String[][] attendanceData = {
            {"NV002", "08:00:00", "17:00:00", "8.0", "Có mặt", ""},
            {"NV003", "08:15:00", "17:30:00", "8.25", "Có mặt", "Tăng ca"},
            {"NV004", "08:30:00", "17:15:00", "7.75", "Có mặt", "Đi muộn"},
            {"NV005", "08:00:00", "16:45:00", "7.75", "Có mặt", "Về sớm"},
            {"NV006", "08:10:00", "17:20:00", "8.17", "Có mặt", ""},
            {"NV007", "07:45:00", "18:00:00", "9.25", "Có mặt", "Tăng ca nhiều"},
            {"NV008", "08:00:00", "17:00:00", "8.0", "Có mặt", ""},
            {"NV009", "08:20:00", "17:40:00", "8.33", "Có mặt", "Tăng ca"},
            {"NV010", "08:05:00", "17:05:00", "8.0", "Có mặt", ""},
            {"NV011", "09:00:00", "17:30:00", "7.5", "Có mặt", "Đi muộn 1h"}
        };
        
        // Thêm dữ liệu chấm công cho 10 ngày gần nhất
        for (int i = 0; i < 10; i++) {
            java.time.LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            
            // Bỏ qua cuối tuần (thứ 7, chủ nhật)
            if (date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7) {
                continue;
            }
            
            for (String[] empData : attendanceData) {
                // Một số nhân viên có thể nghỉ ngẫu nhiên
                if (Math.random() < 0.05) { // 5% khả năng nghỉ
                    ContentValues cc = new ContentValues();
                    cc.put("MaNhanVien", empData[0]);
                    cc.put("NgayChamCong", dateStr);
                    cc.put("GioVao", (String) null);
                    cc.put("GioRa", (String) null);
                    cc.put("SoGioLam", 0.0);
                    cc.put("TrangThai", "Vắng mặt");
                    cc.put("GhiChu", "Nghỉ không phép");
                    db.insert(TABLE_CHAM_CONG, null, cc);
                } else {
                    // Thêm một chút biến động vào giờ làm việc
                    double baseHours = Double.parseDouble(empData[3]);
                    double variation = (Math.random() - 0.5) * 0.5; // ±15 phút
                    double actualHours = Math.max(0, baseHours + variation);
                    
                    ContentValues cc = new ContentValues();
                    cc.put("MaNhanVien", empData[0]);
                    cc.put("NgayChamCong", dateStr);
                    cc.put("GioVao", empData[1]);
                    cc.put("GioRa", empData[2]);
                    cc.put("SoGioLam", actualHours);
                    cc.put("TrangThai", empData[4]);
                    cc.put("GhiChu", empData[5]);
                    db.insert(TABLE_CHAM_CONG, null, cc);
                }
            }
        }
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
        if (lastCode == null) return "NV002";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^([a-zA-Z]+)(\\d+)$").matcher(lastCode);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            String numberStr = matcher.group(2);
            try {
                int nextNumber = Integer.parseInt(numberStr) + 1;
                return String.format("%s%0" + numberStr.length() + "d", prefix, nextNumber);
            } catch (Exception e) { return "NV002"; }
        }
        return "NV002";
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
    // Methods cho chấm công
    public String getMaNhanVienByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MaNhanVien FROM " + TABLE_TAI_KHOAN + " WHERE TenDangNhap = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        String maNV = null;
        if (cursor != null && cursor.moveToFirst()) {
            maNV = cursor.getString(0);
            cursor.close();
        }
        return maNV;
    }

    public boolean[] getTodayAttendanceStatus(String maNhanVien, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT GioVao, GioRa FROM " + TABLE_CHAM_CONG + 
                      " WHERE MaNhanVien = ? AND NgayChamCong = ?";
        Cursor cursor = db.rawQuery(query, new String[]{maNhanVien, date});
        
        boolean hasCheckedIn = false;
        boolean hasCheckedOut = false;
        
        if (cursor != null && cursor.moveToFirst()) {
            String gioVao = cursor.getString(0);
            String gioRa = cursor.getString(1);
            
            hasCheckedIn = (gioVao != null && !gioVao.isEmpty());
            hasCheckedOut = (gioRa != null && !gioRa.isEmpty());
            
            cursor.close();
        }
        
        return new boolean[]{hasCheckedIn, hasCheckedOut};
    }

    public boolean chamCongVao(String maNhanVien, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String checkQuery = "SELECT MaChamCong FROM " + TABLE_CHAM_CONG + 
                           " WHERE MaNhanVien = ? AND NgayChamCong = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{maNhanVien, date});
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        
        if (exists) {
            ContentValues values = new ContentValues();
            values.put("GioVao", time);
            values.put("TrangThai", "Có mặt");
            
            int result = db.update(TABLE_CHAM_CONG, values, 
                                 "MaNhanVien = ? AND NgayChamCong = ?", 
                                 new String[]{maNhanVien, date});
            return result > 0;
        } else {
            ContentValues values = new ContentValues();
            values.put("MaNhanVien", maNhanVien);
            values.put("NgayChamCong", date);
            values.put("GioVao", time);
            values.put("SoGioLam", 0);
            values.put("TrangThai", "Có mặt");
            
            long result = db.insert(TABLE_CHAM_CONG, null, values);
            return result != -1;
        }
    }
    public boolean chamCongRa(String maNhanVien, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String query = "SELECT GioVao FROM " + TABLE_CHAM_CONG + 
                      " WHERE MaNhanVien = ? AND NgayChamCong = ?";
        Cursor cursor = db.rawQuery(query, new String[]{maNhanVien, date});
        
        double soGioLam = 0;
        if (cursor != null && cursor.moveToFirst()) {
            String gioVao = cursor.getString(0);
            if (gioVao != null) {
                soGioLam = tinhSoGioLam(gioVao, time);
            }
            cursor.close();
        }
        
        ContentValues values = new ContentValues();
        values.put("GioRa", time);
        values.put("SoGioLam", soGioLam);
        
        int result = db.update(TABLE_CHAM_CONG, values, 
                             "MaNhanVien = ? AND NgayChamCong = ?", 
                             new String[]{maNhanVien, date});
        return result > 0;
    }

    private double tinhSoGioLam(String gioVao, String gioRa) {
        try {
            String[] vao = gioVao.split(":");
            String[] ra = gioRa.split(":");
            
            int gioVaoMinutes = Integer.parseInt(vao[0]) * 60 + Integer.parseInt(vao[1]);
            int gioRaMinutes = Integer.parseInt(ra[0]) * 60 + Integer.parseInt(ra[1]);
            
            int diffMinutes = gioRaMinutes - gioVaoMinutes;
            return diffMinutes / 60.0;
        } catch (Exception e) {
            return 0;
        }
    }

    // Tính giờ tăng ca (giờ làm > 8 giờ)
    public double tinhGioTangCa(double soGioLam) {
        if (soGioLam > 8.0) {
            return soGioLam - 8.0;
        }
        return 0;
    }

    public Cursor getAttendanceHistory(String maNhanVien, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + TABLE_CHAM_CONG + 
                          " WHERE MaNhanVien = ? ORDER BY NgayChamCong DESC LIMIT ?";
            return db.rawQuery(query, new String[]{maNhanVien, String.valueOf(limit)});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getAllAttendanceHistory(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + TABLE_CHAM_CONG + " ORDER BY NgayChamCong DESC, MaNhanVien ASC LIMIT ?";
            return db.rawQuery(query, new String[]{String.valueOf(limit)});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getEmployeeNameByMa(String maNhanVien) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT HoTen FROM " + TABLE_NHAN_VIEN + " WHERE MaNhanVien = ?";
        Cursor cursor = db.rawQuery(query, new String[]{maNhanVien});
        String hoTen = null;
        if (cursor != null && cursor.moveToFirst()) {
            hoTen = cursor.getString(0);
            cursor.close();
        }
        return hoTen;
    }

    public boolean updateAttendance(String maNhanVien, String ngayChamCong, String gioVao, String gioRa, String ghiChu) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            
            if (gioVao != null && !gioVao.isEmpty()) {
                values.put("GioVao", gioVao);
            }
            
            if (gioRa != null && !gioRa.isEmpty()) {
                values.put("GioRa", gioRa);
                
                // Tính lại số giờ làm nếu có cả giờ vào và giờ ra
                if (gioVao != null && !gioVao.isEmpty()) {
                    double soGioLam = tinhSoGioLam(gioVao, gioRa);
                    values.put("SoGioLam", soGioLam);
                }
            }
            
            // Cập nhật ghi chú
            if (ghiChu != null) {
                values.put("GhiChu", ghiChu);
            }
            
            int result = db.update(TABLE_CHAM_CONG, values, 
                                 "MaNhanVien = ? AND NgayChamCong = ?", 
                                 new String[]{maNhanVien, ngayChamCong});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Phương thức cũ để tương thích (không có ghi chú)
    public boolean updateAttendance(String maNhanVien, String ngayChamCong, String gioVao, String gioRa) {
        return updateAttendance(maNhanVien, ngayChamCong, gioVao, gioRa, null);
    }

    public boolean deleteAttendance(String maNhanVien, String ngayChamCong) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_CHAM_CONG, 
                                 "MaNhanVien = ? AND NgayChamCong = ?", 
                                 new String[]{maNhanVien, ngayChamCong});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Methods cho nghỉ phép
    public boolean addLeaveRequest(String maNhanVien, String ngayBatDau, String ngayKetThuc, 
                                  int soNgayNghi, String lyDo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaNhanVien", maNhanVien);
        values.put("NgayBatDau", ngayBatDau);
        values.put("NgayKetThuc", ngayKetThuc);
        values.put("SoNgayNghi", soNgayNghi);
        values.put("LyDo", lyDo);
        values.put("TrangThai", "Chờ duyệt");
        
        long result = db.insert(TABLE_NGHI_PHEP, null, values);
        return result != -1;
    }

    public boolean submitLeaveRequest(String maNhanVien, String ngayBatDau, String ngayKetThuc, 
                                    int soNgayNghi, String lyDo) {
        return addLeaveRequest(maNhanVien, ngayBatDau, ngayKetThuc, soNgayNghi, lyDo);
    }

    public Cursor getLeaveRequests(String maNhanVien) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NGHI_PHEP + 
                      " WHERE MaNhanVien = ? ORDER BY NgayBatDau DESC";
        return db.rawQuery(query, new String[]{maNhanVien});
    }

    public Cursor getLeaveHistory(String maNhanVien) {
        return getLeaveRequests(maNhanVien);
    }

    public Cursor getAllLeaveRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT np.*, nv.HoTen FROM " + TABLE_NGHI_PHEP + " np " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON np.MaNhanVien = nv.MaNhanVien " +
                      "ORDER BY np.NgayBatDau DESC";
        return db.rawQuery(query, null);
    }

    public boolean updateLeaveRequestStatus(int maNghiPhep, String trangThai, String nguoiDuyet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThai", trangThai);
        values.put("NguoiDuyet", nguoiDuyet);
        
        int result = db.update(TABLE_NGHI_PHEP, values, 
                             "MaNghiPhep = ?", new String[]{String.valueOf(maNghiPhep)});
        return result > 0;
    }

    public boolean approveLeaveRequest(int maNghiPhep, String trangThai) {
        return updateLeaveRequestStatus(maNghiPhep, trangThai, "Admin");
    }

    // Methods cho thông tin cá nhân
    public Cursor getEmployeeByMa(String maNhanVien) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan FROM " + TABLE_NHAN_VIEN + " nv " +
                      "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                      "LEFT JOIN " + TABLE_PHONG_BAN + " pb ON nv.MaPhongBan = pb.MaPhongBan " +
                      "WHERE nv.MaNhanVien = ?";
        return db.rawQuery(query, new String[]{maNhanVien});
    }

    public boolean updateEmployeePersonalInfo(String maNhanVien, String hoTen, String ngaySinh, 
                                            String gioiTinh, String soDienThoai, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("HoTen", hoTen);
        values.put("NgaySinh", ngaySinh);
        values.put("GioiTinh", gioiTinh);
        values.put("SoDienThoai", soDienThoai);
        values.put("Email", email);
        
        int result = db.update(TABLE_NHAN_VIEN, values, 
                             "MaNhanVien = ?", new String[]{maNhanVien});
        return result > 0;
    }

    // Methods cho quản lý phòng ban
    public Cursor getAllDepartmentsWithDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pb.*, " +
                      "COALESCE(nv.HoTen, '') as TenTruongPhong, " +
                      "COUNT(nv2.MaNhanVien) as SoNhanVien " +
                      "FROM " + TABLE_PHONG_BAN + " pb " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON pb.TruongPhong = nv.MaNhanVien " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv2 ON pb.MaPhongBan = nv2.MaPhongBan AND nv2.TrangThaiLamViec = 'Đang làm việc' " +
                      "WHERE pb.TrangThai = 1 " +
                      "GROUP BY pb.MaPhongBan " +
                      "ORDER BY pb.MaPhongBan";
        return db.rawQuery(query, null);
    }

    public Cursor searchDepartments(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pb.*, " +
                      "COALESCE(nv.HoTen, '') as TenTruongPhong, " +
                      "COUNT(nv2.MaNhanVien) as SoNhanVien " +
                      "FROM " + TABLE_PHONG_BAN + " pb " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON pb.TruongPhong = nv.MaNhanVien " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv2 ON pb.MaPhongBan = nv2.MaPhongBan AND nv2.TrangThaiLamViec = 'Đang làm việc' " +
                      "WHERE pb.TrangThai = 1 AND (pb.TenPhongBan LIKE ? OR pb.MaPhongBan LIKE ? OR nv.HoTen LIKE ?) " +
                      "GROUP BY pb.MaPhongBan " +
                      "ORDER BY pb.MaPhongBan";
        String wildcardKeyword = "%" + keyword + "%";
        return db.rawQuery(query, new String[]{wildcardKeyword, wildcardKeyword, wildcardKeyword});
    }

    public String getNextDepartmentCode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MaPhongBan FROM " + TABLE_PHONG_BAN + 
                      " WHERE MaPhongBan LIKE 'PB%' " +
                      " ORDER BY length(MaPhongBan) DESC, MaPhongBan DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String lastCode = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) lastCode = cursor.getString(0).trim();
            cursor.close();
        }
        if (lastCode == null) return "PB001";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^([a-zA-Z]+)(\\d+)$").matcher(lastCode);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            String numberStr = matcher.group(2);
            try {
                int nextNumber = Integer.parseInt(numberStr) + 1;
                return String.format("%s%0" + numberStr.length() + "d", prefix, nextNumber);
            } catch (Exception e) { return "PB001"; }
        }
        return "PB001";
    }

    public Cursor getManagerCandidates() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT nv.*, cv.TenChucVu FROM " + TABLE_NHAN_VIEN + " nv " +
                      "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                      "WHERE nv.TrangThaiLamViec = 'Đang làm việc' " +
                      "AND (cv.TenChucVu LIKE '%Trưởng phòng%' OR cv.TenChucVu LIKE '%Giám đốc%') " +
                      "ORDER BY nv.HoTen";
        return db.rawQuery(query, null);
    }

    public boolean addDepartment(String maPhongBan, String tenPhongBan, String truongPhong, int trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaPhongBan", maPhongBan);
        values.put("TenPhongBan", tenPhongBan);
        values.put("TruongPhong", truongPhong);
        values.put("TrangThai", trangThai);
        
        long result = db.insert(TABLE_PHONG_BAN, null, values);
        return result != -1;
    }

    public boolean updateDepartment(String maPhongBan, String tenPhongBan, String truongPhong, int trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenPhongBan", tenPhongBan);
        values.put("TruongPhong", truongPhong);
        values.put("TrangThai", trangThai);
        
        int result = db.update(TABLE_PHONG_BAN, values, 
                             "MaPhongBan = ?", new String[]{maPhongBan});
        return result > 0;
    }

    public boolean deleteDepartment(String maPhongBan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThai", 0); // Đánh dấu ngừng hoạt động thay vì xóa
        
        int result = db.update(TABLE_PHONG_BAN, values, 
                             "MaPhongBan = ?", new String[]{maPhongBan});
        return result > 0;
    }

    // Methods cho quản lý chức vụ
    public Cursor getAllPositionsWithDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cv.*, " +
                      "COUNT(nv.MaNhanVien) as SoNhanVien " +
                      "FROM " + TABLE_CHUC_VU + " cv " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON cv.MaChucVu = nv.MaChucVu AND nv.TrangThaiLamViec = 'Đang làm việc' " +
                      "WHERE cv.TrangThai = 1 " +
                      "GROUP BY cv.MaChucVu " +
                      "ORDER BY cv.MaChucVu";
        return db.rawQuery(query, null);
    }

    public Cursor searchPositions(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cv.*, " +
                      "COUNT(nv.MaNhanVien) as SoNhanVien " +
                      "FROM " + TABLE_CHUC_VU + " cv " +
                      "LEFT JOIN " + TABLE_NHAN_VIEN + " nv ON cv.MaChucVu = nv.MaChucVu AND nv.TrangThaiLamViec = 'Đang làm việc' " +
                      "WHERE cv.TrangThai = 1 AND (cv.TenChucVu LIKE ? OR cv.MaChucVu LIKE ?) " +
                      "GROUP BY cv.MaChucVu " +
                      "ORDER BY cv.MaChucVu";
        String wildcardKeyword = "%" + keyword + "%";
        return db.rawQuery(query, new String[]{wildcardKeyword, wildcardKeyword});
    }

    public String getNextPositionCode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MaChucVu FROM " + TABLE_CHUC_VU + 
                      " WHERE MaChucVu LIKE 'CV%' " +
                      " ORDER BY length(MaChucVu) DESC, MaChucVu DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String lastCode = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) lastCode = cursor.getString(0).trim();
            cursor.close();
        }
        if (lastCode == null) return "CV001";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^([a-zA-Z]+)(\\d+)$").matcher(lastCode);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            String numberStr = matcher.group(2);
            try {
                int nextNumber = Integer.parseInt(numberStr) + 1;
                return String.format("%s%0" + numberStr.length() + "d", prefix, nextNumber);
            } catch (Exception e) { return "CV001"; }
        }
        return "CV001";
    }

    public boolean addPosition(String maChucVu, String tenChucVu, double mucLuongCoBan, int trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaChucVu", maChucVu);
        values.put("TenChucVu", tenChucVu);
        values.put("MucLuongCoBan", mucLuongCoBan);
        values.put("TrangThai", trangThai);
        
        long result = db.insert(TABLE_CHUC_VU, null, values);
        return result != -1;
    }

    public boolean updatePosition(String maChucVu, String tenChucVu, double mucLuongCoBan, int trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenChucVu", tenChucVu);
        values.put("MucLuongCoBan", mucLuongCoBan);
        values.put("TrangThai", trangThai);
        
        int result = db.update(TABLE_CHUC_VU, values, 
                             "MaChucVu = ?", new String[]{maChucVu});
        return result > 0;
    }

    public boolean deletePosition(String maChucVu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThai", 0); // Đánh dấu ngừng hoạt động thay vì xóa
        
        int result = db.update(TABLE_CHUC_VU, values, 
                             "MaChucVu = ?", new String[]{maChucVu});
        return result > 0;
    }

    // Methods cho quản lý lương
    public int calculateMonthlySalary(String thangNam) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        
        try {
            // Lấy danh sách nhân viên đang làm việc
            String query = "SELECT nv.MaNhanVien, cv.MucLuongCoBan " +
                          "FROM " + TABLE_NHAN_VIEN + " nv " +
                          "LEFT JOIN " + TABLE_CHUC_VU + " cv ON nv.MaChucVu = cv.MaChucVu " +
                          "WHERE nv.TrangThaiLamViec = 'Đang làm việc'";
            Cursor cursor = db.rawQuery(query, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maNhanVien = cursor.getString(0);
                    double luongCoBan = cursor.getDouble(1);
                    
                    // Tính tổng số giờ làm và giờ tăng ca trong tháng
                    AttendanceStats stats = getAttendanceStats(maNhanVien, thangNam);
                    
                    // Tính lương giờ = lương cơ bản / 208 giờ (26 ngày × 8 giờ)
                    double luongGio = luongCoBan / 208.0;
                    
                    // Phụ cấp cố định (10% lương cơ bản)
                    double phuCap = luongCoBan * 0.1;
                    
                    // Lương tăng ca = số giờ tăng ca × lương giờ × 1.5
                    double luongTangCa = stats.soGioTangCa * luongGio * 1.5;
                    
                    // Tổng lương = lương cơ bản + phụ cấp + lương tăng ca
                    double tongLuong = luongCoBan + phuCap + luongTangCa;
                    
                    // Kiểm tra xem đã có bản ghi lương chưa
                    String checkQuery = "SELECT MaLuong FROM " + TABLE_LUONG + 
                                      " WHERE MaNhanVien = ? AND ThangNam = ?";
                    Cursor checkCursor = db.rawQuery(checkQuery, new String[]{maNhanVien, thangNam});
                    
                    ContentValues values = new ContentValues();
                    values.put("MaNhanVien", maNhanVien);
                    values.put("ThangNam", thangNam);
                    values.put("LuongCoBan", luongCoBan);
                    values.put("PhuCap", phuCap);
                    values.put("SoGioLam", stats.soGioLam);
                    values.put("TongLuong", tongLuong);
                    values.put("TrangThai", "Chưa thanh toán");
                    values.put("NgayTinhLuong", java.time.LocalDate.now().toString());
                    
                    if (checkCursor != null && checkCursor.moveToFirst()) {
                        // Cập nhật nếu đã tồn tại
                        int maLuong = checkCursor.getInt(0);
                        db.update(TABLE_LUONG, values, "MaLuong = ?", new String[]{String.valueOf(maLuong)});
                    } else {
                        // Thêm mới nếu chưa tồn tại
                        db.insert(TABLE_LUONG, null, values);
                    }
                    
                    if (checkCursor != null) checkCursor.close();
                    count++;
                    
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Class để lưu thống kê chấm công
    public static class AttendanceStats {
        public double soGioLam = 0;
        public double soGioTangCa = 0;
        public int soNgayLam = 0;
    }
    
    public AttendanceStats getAttendanceStatsForSalary(String maNhanVien, String thangNam) {
        return getAttendanceStats(maNhanVien, thangNam);
    }
    
    private AttendanceStats getAttendanceStats(String maNhanVien, String thangNam) {
        SQLiteDatabase db = this.getReadableDatabase();
        AttendanceStats stats = new AttendanceStats();
        
        try {
            String query = "SELECT SoGioLam, COUNT(*) as SoNgayLam FROM " + TABLE_CHAM_CONG + 
                          " WHERE MaNhanVien = ? AND strftime('%Y-%m', NgayChamCong) = ? " +
                          " AND SoGioLam > 0";
            Cursor cursor = db.rawQuery(query, new String[]{maNhanVien, thangNam});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double gioLam = cursor.getDouble(0);
                    stats.soGioLam += gioLam;
                    stats.soNgayLam++;
                    
                    // Tính giờ tăng ca (> 8 giờ/ngày)
                    if (gioLam > 8.0) {
                        stats.soGioTangCa += (gioLam - 8.0);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    private double getTotalWorkingHours(String maNhanVien, String thangNam) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalHours = 0;
        
        try {
            String query = "SELECT SUM(SoGioLam) FROM " + TABLE_CHAM_CONG + 
                          " WHERE MaNhanVien = ? AND strftime('%Y-%m', NgayChamCong) = ?";
            Cursor cursor = db.rawQuery(query, new String[]{maNhanVien, thangNam});
            
            if (cursor != null && cursor.moveToFirst()) {
                totalHours = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return totalHours;
    }

    public Cursor getSalaryByMonth(String thangNam) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LUONG + 
                      " WHERE ThangNam = ? ORDER BY MaNhanVien";
        return db.rawQuery(query, new String[]{thangNam});
    }

    public Cursor getSalaryByEmployee(String maNhanVien, String thangNam) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LUONG + 
                      " WHERE MaNhanVien = ? AND ThangNam = ?";
        return db.rawQuery(query, new String[]{maNhanVien, thangNam});
    }

    public boolean updateSalaryStatus(int maLuong, String trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TrangThai", trangThai);
        
        int result = db.update(TABLE_LUONG, values, 
                             "MaLuong = ?", new String[]{String.valueOf(maLuong)});
        return result > 0;
    }
}