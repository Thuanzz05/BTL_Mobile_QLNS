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
    private Button btnQuanLyNV, btnQuanLyPB, btnQuanLyCV, btnChamCong, btnNghiPhep, btnLuong, btnThongKe, btnThongTin, btnDangXuat;
    private androidx.cardview.widget.CardView cardQuanLyNV, cardQuanLyPB, cardQuanLyCV, cardLuong, cardThongKe;
    
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
        
        // Buttons
        btnQuanLyNV = findViewById(R.id.btn_quan_ly_nv);
        btnQuanLyPB = findViewById(R.id.btn_quan_ly_pb);
        btnQuanLyCV = findViewById(R.id.btn_quan_ly_cv);
        btnChamCong = findViewById(R.id.btn_cham_cong);
        btnNghiPhep = findViewById(R.id.btn_nghi_phep);
        btnLuong = findViewById(R.id.btn_luong);
        btnThongKe = findViewById(R.id.btn_thong_ke);
        btnThongTin = findViewById(R.id.btn_thong_tin);
        btnDangXuat = findViewById(R.id.btn_dang_xuat);
        
        // CardViews
        cardQuanLyNV = findViewById(R.id.card_quan_ly_nv);
        cardQuanLyPB = findViewById(R.id.card_quan_ly_pb);
        cardQuanLyCV = findViewById(R.id.card_quan_ly_cv);
        cardLuong = findViewById(R.id.card_luong);
        cardThongKe = findViewById(R.id.card_thong_ke);
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
        // Mặc định ẩn tất cả các CardView quản lý
        cardQuanLyNV.setVisibility(android.view.View.GONE);
        cardQuanLyPB.setVisibility(android.view.View.GONE);
        cardQuanLyCV.setVisibility(android.view.View.GONE);
        cardLuong.setVisibility(android.view.View.GONE);
        cardThongKe.setVisibility(android.view.View.GONE);
        
        // Admin: Full quyền tất cả chức năng
        if ("Admin".equals(currentRole)) {
            cardQuanLyNV.setVisibility(android.view.View.VISIBLE);
            cardQuanLyPB.setVisibility(android.view.View.VISIBLE);
            cardQuanLyCV.setVisibility(android.view.View.VISIBLE);
            cardLuong.setVisibility(android.view.View.VISIBLE);
            cardThongKe.setVisibility(android.view.View.VISIBLE);
        }
        // HR: Chuyên về nhân sự - quản lý nhân viên, lương, nghỉ phép
        else if ("HR".equals(currentRole)) {
            cardQuanLyNV.setVisibility(android.view.View.VISIBLE);
            cardQuanLyPB.setVisibility(android.view.View.VISIBLE);
            cardQuanLyCV.setVisibility(android.view.View.VISIBLE);
            cardLuong.setVisibility(android.view.View.VISIBLE);
            cardThongKe.setVisibility(android.view.View.VISIBLE);
        }
        // Manager: Quản lý cấp trung - chỉ quản lý nhân viên, không quản lý lương
        else if ("Manager".equals(currentRole)) {
            cardQuanLyNV.setVisibility(android.view.View.VISIBLE);
            cardThongKe.setVisibility(android.view.View.VISIBLE);
        }
        // Employee: Chỉ chấm công, nghỉ phép và thông tin cá nhân
        // Các card khác đã được ẩn ở trên
        
        // Các nút luôn hiển thị cho tất cả vai trò
        btnChamCong.setVisibility(android.view.View.VISIBLE);
        btnNghiPhep.setVisibility(android.view.View.VISIBLE);
        btnThongTin.setVisibility(android.view.View.VISIBLE);
        btnDangXuat.setVisibility(android.view.View.VISIBLE);
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
        
        btnQuanLyPB.setOnClickListener(v -> {
            if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
                Intent intent = new Intent(DashboardActivity.this, QuanLyPhongBanActivity.class);
                intent.putExtra("role", currentRole);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn không có quyền truy cập chức năng này", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnQuanLyCV.setOnClickListener(v -> {
            if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
                Intent intent = new Intent(DashboardActivity.this, QuanLyChucVuActivity.class);
                intent.putExtra("role", currentRole);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn không có quyền truy cập chức năng này", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnChamCong.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ChamCongActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("role", currentRole);
            startActivity(intent);
        });
        
        btnNghiPhep.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, NghiPhepActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("role", currentRole);
            startActivity(intent);
        });
        
        btnLuong.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, QuanLyLuongActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("role", currentRole);
            startActivity(intent);
        });
        
        btnThongKe.setOnClickListener(v -> {
            if ("Admin".equals(currentRole) || "HR".equals(currentRole) || "Manager".equals(currentRole)) {
                Intent intent = new Intent(DashboardActivity.this, ThongKeActivity.class);
                intent.putExtra("username", currentUsername);
                intent.putExtra("role", currentRole);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn không có quyền truy cập chức năng này", Toast.LENGTH_SHORT).show();
            }
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