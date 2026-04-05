package com.example.btl_mobile_qlns;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnQuanLyNV, btnChamCong, btnNghiPhep, btnLuong, btnThongTin, btnDangXuat;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupDatabase();
        displayUserInfo();
        setupButtons();
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnQuanLyNV = findViewById(R.id.btn_quan_ly_nv);
        btnChamCong = findViewById(R.id.btn_cham_cong);
        btnNghiPhep = findViewById(R.id.btn_nghi_phep);
        btnLuong = findViewById(R.id.btn_luong);
        btnThongTin = findViewById(R.id.btn_thong_tin);
        btnDangXuat = findViewById(R.id.btn_dang_xuat);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void displayUserInfo() {
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername != null) {
            Cursor cursor = dbHelper.getUserInfo(currentUsername);
            if (cursor.moveToFirst()) {
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                currentRole = cursor.getString(cursor.getColumnIndexOrThrow("VaiTro"));
                tvWelcome.setText("Chào " + hoTen + " (" + currentRole + ")");
            }
            cursor.close();
        } else {
            tvWelcome.setText("Chào mừng đến với hệ thống QLNS!");
            currentRole = "Employee";
        }
        
        // Áp dụng phân quyền
        applyPermissions();
    }
    
    private void applyPermissions() {
        // Admin: Full quyền tất cả chức năng
        if ("Admin".equals(currentRole)) {
            btnQuanLyNV.setVisibility(android.view.View.VISIBLE);
            btnChamCong.setVisibility(android.view.View.VISIBLE); // Admin có thể xem chấm công của tất cả
            btnNghiPhep.setVisibility(android.view.View.VISIBLE);
            btnLuong.setVisibility(android.view.View.VISIBLE);
            btnThongTin.setVisibility(android.view.View.VISIBLE);
        }
        // HR: Chuyên về nhân sự - quản lý nhân viên, lương, nghỉ phép
        else if ("HR".equals(currentRole)) {
            btnQuanLyNV.setVisibility(android.view.View.VISIBLE);
            btnChamCong.setVisibility(android.view.View.VISIBLE);
            btnNghiPhep.setVisibility(android.view.View.VISIBLE);
            btnLuong.setVisibility(android.view.View.VISIBLE); // HR quản lý lương
            btnThongTin.setVisibility(android.view.View.VISIBLE);
        }
        // Manager: Quản lý cấp trung - chỉ quản lý nhân viên, không quản lý lương
        else if ("Manager".equals(currentRole)) {
            btnQuanLyNV.setVisibility(android.view.View.VISIBLE);
            btnChamCong.setVisibility(android.view.View.VISIBLE);
            btnNghiPhep.setVisibility(android.view.View.VISIBLE);
            btnLuong.setVisibility(android.view.View.GONE); // Manager không quản lý lương
            btnThongTin.setVisibility(android.view.View.VISIBLE);
        }
        // Employee: Chỉ chấm công và nghỉ phép cá nhân
        else {
            btnQuanLyNV.setVisibility(android.view.View.GONE);
            btnChamCong.setVisibility(android.view.View.VISIBLE);
            btnNghiPhep.setVisibility(android.view.View.VISIBLE);
            btnLuong.setVisibility(android.view.View.GONE);
            btnThongTin.setVisibility(android.view.View.VISIBLE);
        }
    }
    
    private void setupButtons() {
        btnQuanLyNV.setOnClickListener(v -> {
            if (isAdminOrHR()) {
                Intent intent = new Intent(DashboardActivity.this, QuanLyNhanVienActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn không có quyền truy cập chức năng này", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnChamCong.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ChamCongActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });
        
        btnNghiPhep.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, NghiPhepActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("role", currentRole);
            startActivity(intent);
        });
        
        btnLuong.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnThongTin.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ThongTinCaNhanActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });
        
        btnDangXuat.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private boolean isAdminOrHR() {
        return "Admin".equals(currentRole) || "HR".equals(currentRole) || "Manager".equals(currentRole);
    }
}