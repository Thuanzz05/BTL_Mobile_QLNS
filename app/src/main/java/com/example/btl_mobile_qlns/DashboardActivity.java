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
    private Button btnQuanLyNV, btnChamCong, btnNghiPhep, btnLuong, btnDangXuat;
    
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
    }
    
    private void setupButtons() {
        btnQuanLyNV.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnChamCong.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnNghiPhep.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnLuong.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
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