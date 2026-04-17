package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_mobile_qlns.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvTongNhanVien, tvNhanVienDangLam, tvNhanVienNghiViec;
    private TextView tvTongPhongBan, tvTongChucVu;
    private TextView tvTongChamCongThangNay, tvTyLeDiLam;
    private TextView tvTongDonNghiPhep, tvDonChoDuyet, tvDonDaDuyet;
    private TextView tvTongLuongThangNay, tvLuongTrungBinh;
    private TextView tvThangThongKe;
    
    private DatabaseHelper dbHelper;
    private String currentRole;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        
        initViews();
        setupDatabase();
        loadStatistics();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        
        // Thống kê nhân viên
        tvTongNhanVien = findViewById(R.id.tv_tong_nhan_vien);
        tvNhanVienDangLam = findViewById(R.id.tv_nhan_vien_dang_lam);
        tvNhanVienNghiViec = findViewById(R.id.tv_nhan_vien_nghi_viec);
        
        // Thống kê tổ chức
        tvTongPhongBan = findViewById(R.id.tv_tong_phong_ban);
        tvTongChucVu = findViewById(R.id.tv_tong_chuc_vu);
        
        // Thống kê chấm công
        tvTongChamCongThangNay = findViewById(R.id.tv_tong_cham_cong_thang_nay);
        tvTyLeDiLam = findViewById(R.id.tv_ty_le_di_lam);
        
        // Thống kê nghỉ phép
        tvTongDonNghiPhep = findViewById(R.id.tv_tong_don_nghi_phep);
        tvDonChoDuyet = findViewById(R.id.tv_don_cho_duyet);
        tvDonDaDuyet = findViewById(R.id.tv_don_da_duyet);
        
        // Thống kê lương
        tvTongLuongThangNay = findViewById(R.id.tv_tong_luong_thang_nay);
        tvLuongTrungBinh = findViewById(R.id.tv_luong_trung_binh);
        
        tvThangThongKe = findViewById(R.id.tv_thang_thong_ke);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
        currentUsername = getIntent().getStringExtra("username");
        
        tvTitle.setText("THỐNG KÊ TỔNG QUAN");
        
        // Hiển thị tháng thống kê
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvThangThongKe.setText("Thống kê tháng: " + sdf.format(calendar.getTime()));
    }
    
    private void loadStatistics() {
        loadEmployeeStatistics();
        loadOrganizationStatistics();
        loadAttendanceStatistics();
        loadLeaveStatistics();
        loadSalaryStatistics();
    }
    
    private void loadEmployeeStatistics() {
        try {
            // Tổng số nhân viên
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NhanVien", null);
            int tongNhanVien = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongNhanVien = cursor.getInt(0);
                cursor.close();
            }
            
            // Nhân viên đang làm việc
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NhanVien WHERE TrangThaiLamViec = 'Đang làm việc'", null);
            int nhanVienDangLam = 0;
            if (cursor != null && cursor.moveToFirst()) {
                nhanVienDangLam = cursor.getInt(0);
                cursor.close();
            }
            
            // Nhân viên đã nghỉ việc
            int nhanVienNghiViec = tongNhanVien - nhanVienDangLam;
            
            tvTongNhanVien.setText(String.valueOf(tongNhanVien));
            tvNhanVienDangLam.setText(String.valueOf(nhanVienDangLam));
            tvNhanVienNghiViec.setText(String.valueOf(nhanVienNghiViec));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadOrganizationStatistics() {
        try {
            // Tổng số phòng ban
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM PhongBan WHERE TrangThai = 1", null);
            int tongPhongBan = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongPhongBan = cursor.getInt(0);
                cursor.close();
            }
            
            // Tổng số chức vụ
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM ChucVu WHERE TrangThai = 1", null);
            int tongChucVu = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongChucVu = cursor.getInt(0);
                cursor.close();
            }
            
            tvTongPhongBan.setText(String.valueOf(tongPhongBan));
            tvTongChucVu.setText(String.valueOf(tongChucVu));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAttendanceStatistics() {
        try {
            Calendar calendar = Calendar.getInstance();
            String thangNam = String.format("%04d-%02d", 
                calendar.get(Calendar.YEAR), 
                calendar.get(Calendar.MONTH) + 1);
            
            // Tổng số lần chấm công tháng này
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM ChamCong WHERE strftime('%Y-%m', NgayChamCong) = ?",
                new String[]{thangNam});
            int tongChamCong = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongChamCong = cursor.getInt(0);
                cursor.close();
            }
            
            // Tính tỷ lệ đi làm (số ngày có chấm công / tổng số ngày làm việc dự kiến)
            int soNhanVienDangLam = Integer.parseInt(tvNhanVienDangLam.getText().toString());
            int soNgayLamViecTrongThang = getWorkingDaysInMonth();
            int tongNgayLamViecDuKien = soNhanVienDangLam * soNgayLamViecTrongThang;
            
            double tyLeDiLam = tongNgayLamViecDuKien > 0 ? 
                (double) tongChamCong / tongNgayLamViecDuKien * 100 : 0;
            
            tvTongChamCongThangNay.setText(String.valueOf(tongChamCong));
            tvTyLeDiLam.setText(String.format("%.1f%%", tyLeDiLam));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadLeaveStatistics() {
        try {
            // Tổng số đơn nghỉ phép
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep", null);
            int tongDonNghiPhep = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongDonNghiPhep = cursor.getInt(0);
                cursor.close();
            }
            
            // Đơn chờ duyệt
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep WHERE TrangThai = 'Chờ duyệt'", null);
            int donChoDuyet = 0;
            if (cursor != null && cursor.moveToFirst()) {
                donChoDuyet = cursor.getInt(0);
                cursor.close();
            }
            
            // Đơn đã duyệt
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep WHERE TrangThai = 'Đã duyệt'", null);
            int donDaDuyet = 0;
            if (cursor != null && cursor.moveToFirst()) {
                donDaDuyet = cursor.getInt(0);
                cursor.close();
            }
            
            tvTongDonNghiPhep.setText(String.valueOf(tongDonNghiPhep));
            tvDonChoDuyet.setText(String.valueOf(donChoDuyet));
            tvDonDaDuyet.setText(String.valueOf(donDaDuyet));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadSalaryStatistics() {
        try {
            Calendar calendar = Calendar.getInstance();
            String thangNam = String.format("%04d-%02d", 
                calendar.get(Calendar.YEAR), 
                calendar.get(Calendar.MONTH) + 1);
            
            // Tổng lương tháng này
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT SUM(TongLuong), COUNT(*) FROM Luong WHERE ThangNam = ?",
                new String[]{thangNam});
            
            double tongLuong = 0;
            int soNhanVienCoLuong = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongLuong = cursor.getDouble(0);
                soNhanVienCoLuong = cursor.getInt(1);
                cursor.close();
            }
            
            double luongTrungBinh = soNhanVienCoLuong > 0 ? tongLuong / soNhanVienCoLuong : 0;
            
            tvTongLuongThangNay.setText(formatCurrency(tongLuong));
            tvLuongTrungBinh.setText(formatCurrency(luongTrungBinh));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int getWorkingDaysInMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        
        calendar.set(year, month, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        int workingDays = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // Không tính thứ 7 và chủ nhật
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                workingDays++;
            }
        }
        
        return workingDays;
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f đ", amount);
    }
}