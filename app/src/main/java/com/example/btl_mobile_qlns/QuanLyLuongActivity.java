package com.example.btl_mobile_qlns;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.Luong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class QuanLyLuongActivity extends AppCompatActivity {

    private TextView tvTitle, tvThangHienTai;
    private Spinner spThang, spNam;
    private Button btnTinhLuong, btnXemLuong;
    private ListView lvLuong;
    
    private DatabaseHelper dbHelper;
    private LuongAdapter adapter;
    private List<Luong> listLuong;
    private String currentRole;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_luong);
        
        initViews();
        setupDatabase();
        setupUI();
        setupSpinners();
        setupButtons();
        loadCurrentMonthSalary();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvThangHienTai = findViewById(R.id.tv_thang_hien_tai);
        spThang = findViewById(R.id.sp_thang);
        spNam = findViewById(R.id.sp_nam);
        btnTinhLuong = findViewById(R.id.btn_tinh_luong);
        btnXemLuong = findViewById(R.id.btn_xem_luong);
        lvLuong = findViewById(R.id.lv_luong);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
        currentUsername = getIntent().getStringExtra("username");
    }
    
    private void setupUI() {
        tvTitle.setText("QUẢN LÝ LƯƠNG");
        
        // Hiển thị tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvThangHienTai.setText("Tháng hiện tại: " + sdf.format(calendar.getTime()));
        
        // Chỉ Admin và HR mới có thể tính lương
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            btnTinhLuong.setVisibility(View.VISIBLE);
        } else {
            btnTinhLuong.setVisibility(View.GONE);
        }
    }
    
    private void setupSpinners() {
        // Setup spinner tháng
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(monthAdapter);
        
        // Setup spinner năm
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 1; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNam.setAdapter(yearAdapter);
        
        // Set tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        spThang.setSelection(calendar.get(Calendar.MONTH));
        spNam.setSelection(2); // Current year (index 2 in the list)
    }
    
    private void setupButtons() {
        btnTinhLuong.setOnClickListener(v -> showCalculateSalaryDialog());
        btnXemLuong.setOnClickListener(v -> loadSalaryByMonth());
    }
    
    private void showCalculateSalaryDialog() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        String thangNam = nam + "-" + thang;
        
        new AlertDialog.Builder(this)
            .setTitle("Tính lương tháng " + thang + "/" + nam)
            .setMessage("Bạn có chắc muốn tính lương cho tất cả nhân viên trong tháng này?\n\n" +
                       "Lưu ý: Nếu đã có dữ liệu lương tháng này, hệ thống sẽ cập nhật lại.")
            .setPositiveButton("Tính lương", (dialog, which) -> calculateSalary(thangNam))
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void calculateSalary(String thangNam) {
        try {
            int count = dbHelper.calculateMonthlySalary(thangNam);
            if (count > 0) {
                Toast.makeText(this, "Đã tính lương cho " + count + " nhân viên", Toast.LENGTH_SHORT).show();
                loadSalaryByMonth();
            } else {
                Toast.makeText(this, "Không có nhân viên nào để tính lương", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tính lương: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadCurrentMonthSalary() {
        Calendar calendar = Calendar.getInstance();
        String thang = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String nam = String.valueOf(calendar.get(Calendar.YEAR));
        String thangNam = nam + "-" + thang;
        
        loadSalary(thangNam);
    }
    
    private void loadSalaryByMonth() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        String thangNam = nam + "-" + thang;
        
        loadSalary(thangNam);
    }
    
    private void loadSalary(String thangNam) {
        try {
            listLuong = new ArrayList<>();
            Cursor cursor;
            
            if ("Employee".equals(currentRole)) {
                // Employee chỉ xem lương của mình
                String maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
                cursor = dbHelper.getSalaryByEmployee(maNhanVien, thangNam);
            } else {
                // Admin/HR xem tất cả
                cursor = dbHelper.getSalaryByMonth(thangNam);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int maLuong = cursor.getInt(cursor.getColumnIndexOrThrow("MaLuong"));
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    String thang = cursor.getString(cursor.getColumnIndexOrThrow("ThangNam"));
                    double luongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("LuongCoBan"));
                    double phuCap = cursor.getDouble(cursor.getColumnIndexOrThrow("PhuCap"));
                    double soGioLam = cursor.getDouble(cursor.getColumnIndexOrThrow("SoGioLam"));
                    double tongLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("TongLuong"));
                    String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));
                    String ngayTinhLuong = cursor.getString(cursor.getColumnIndexOrThrow("NgayTinhLuong"));
                    
                    Luong luong = new Luong(maLuong, maNV, thang, luongCoBan, phuCap, soGioLam, tongLuong, trangThai);
                    luong.setNgayTinhLuong(ngayTinhLuong);
                    
                    // Lấy tên nhân viên
                    if (!"Employee".equals(currentRole)) {
                        String hoTen = dbHelper.getEmployeeNameByMa(maNV);
                        luong.setHoTen(hoTen);
                    }
                    
                    listLuong.add(luong);
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải dữ liệu lương", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateUI() {
        if (adapter == null) {
            adapter = new LuongAdapter(this, listLuong, currentRole);
            lvLuong.setAdapter(adapter);
        } else {
            adapter.updateData(listLuong);
        }
        
        if (listLuong.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu lương cho tháng này", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSalaryByMonth();
    }
}