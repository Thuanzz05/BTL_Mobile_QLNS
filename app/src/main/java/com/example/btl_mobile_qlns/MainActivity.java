package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private DatabaseHelper dbHelper;

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
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void displayUserInfo() {
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            Cursor cursor = dbHelper.getUserInfo(username);
            if (cursor.moveToFirst()) {
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                String vaiTro = cursor.getString(cursor.getColumnIndexOrThrow("VaiTro"));
                tvWelcome.setText("Chào mừng, " + hoTen + "\nVai trò: " + vaiTro);
            }
            cursor.close();
        } else {
            tvWelcome.setText("Chào mừng đến với hệ thống QLNS!");
        }
    }
}