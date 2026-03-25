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

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnVaoUngDung, btnDangXuat;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        btnVaoUngDung = findViewById(R.id.btn_vao_ung_dung);
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
                String vaiTro = cursor.getString(cursor.getColumnIndexOrThrow("VaiTro"));
                tvWelcome.setText("Chào " + hoTen + " (" + vaiTro + ")!\nKhám phá các tính năng quản lý nhân sự hiện đại");
            }
            cursor.close();
        } else {
            tvWelcome.setText("Chào mừng bạn đến với ứng dụng QLNS hiện đại");
        }
    }
    
    private void setupButtons() {
        btnVaoUngDung.setOnClickListener(v -> {
            // Hiện tại chỉ hiển thị thông báo, sau này sẽ tạo DashboardActivity
            Toast.makeText(this, "Chức năng Dashboard đang được phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnDangXuat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}