package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_mobile_qlns.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvTongNhanVien, tvNhanVienDangLam, tvNhanVienNghiViec;
    private TextView tvTongPhongBan, tvTongChucVu;
    private TextView tvTongChamCongThangNay;
    private TextView tvTongDonNghiPhep, tvDonChoDuyet, tvDonDaDuyet;
    private TextView tvTongLuongThangNay, tvLuongTrungBinh;
    private Spinner spThang, spNam;
    private boolean isInitialLoad = true;
    
    private DatabaseHelper dbHelper;
    private String currentRole;
    private String currentUsername;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        
        initViews();
        setupDatabase();
        setupSpinners();
        isInitialLoad = false;
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
        
        // Thống kê nghỉ phép
        tvTongDonNghiPhep = findViewById(R.id.tv_tong_don_nghi_phep);
        tvDonChoDuyet = findViewById(R.id.tv_don_cho_duyet);
        tvDonDaDuyet = findViewById(R.id.tv_don_da_duyet);
        
        // Thống kê lương
        tvTongLuongThangNay = findViewById(R.id.tv_tong_luong_thang_nay);
        tvLuongTrungBinh = findViewById(R.id.tv_luong_trung_binh);
        
        spThang = findViewById(R.id.sp_thang_thong_ke);
        spNam = findViewById(R.id.sp_nam_thong_ke);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
        currentUsername = getIntent().getStringExtra("username");
        
        tvTitle.setText("THỐNG KÊ TỔNG QUAN");
    }
    
    private void setupSpinners() {
        List<String> listThang = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            listThang.add(String.format(Locale.getDefault(), "%02d", i));
        }
        ArrayAdapter<String> thangAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listThang);
        thangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(thangAdapter);
        
        List<String> listNam = new ArrayList<>();
        for (int i = 2020; i <= 2030; i++) {
            listNam.add(String.valueOf(i));
        }
        ArrayAdapter<String> namAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listNam);
        namAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNam.setAdapter(namAdapter);
        
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH); 
        int currentYear = cal.get(Calendar.YEAR);
        
        spThang.setSelection(currentMonth);
        int yearPosition = listNam.indexOf(String.valueOf(currentYear));
        if (yearPosition >= 0) {
            spNam.setSelection(yearPosition);
        }
        
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isInitialLoad) {
                    loadStatistics();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        
        spThang.setOnItemSelectedListener(listener);
        spNam.setOnItemSelectedListener(listener);
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
            String selectedMonth = spThang.getSelectedItem().toString();
            String selectedYear = spNam.getSelectedItem().toString();
            String thangNam = selectedYear + "-" + selectedMonth;
            
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
            tvTongChamCongThangNay.setText(String.valueOf(tongChamCong));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadLeaveStatistics() {
        try {
            String selectedMonth = spThang.getSelectedItem().toString();
            String selectedYear = spNam.getSelectedItem().toString();
            String thangNam = selectedYear + "-" + selectedMonth;
            
            // Tổng số đơn nghỉ phép
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep WHERE strftime('%Y-%m', NgayBatDau) = ?", new String[]{thangNam});
            int tongDonNghiPhep = 0;
            if (cursor != null && cursor.moveToFirst()) {
                tongDonNghiPhep = cursor.getInt(0);
                cursor.close();
            }
            
            // Đơn chờ duyệt
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep WHERE TrangThai = 'Chờ duyệt' AND strftime('%Y-%m', NgayBatDau) = ?", new String[]{thangNam});
            int donChoDuyet = 0;
            if (cursor != null && cursor.moveToFirst()) {
                donChoDuyet = cursor.getInt(0);
                cursor.close();
            }
            
            // Đơn đã duyệt
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM NghiPhep WHERE TrangThai = 'Đã duyệt' AND strftime('%Y-%m', NgayBatDau) = ?", new String[]{thangNam});
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
            String selectedMonth = spThang.getSelectedItem().toString();
            String selectedYear = spNam.getSelectedItem().toString();
            String thangNam = selectedYear + "-" + selectedMonth;
            
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
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f đ", amount);
    }
}