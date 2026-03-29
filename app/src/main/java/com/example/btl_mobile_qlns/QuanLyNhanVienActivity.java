package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class QuanLyNhanVienActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_nhan_vien);
        
        khoiTaoViews();
        thieLapDatabase();
        taiDanhSachNhanVien();
    }
    
    private void khoiTaoViews() {
        listView = findViewById(R.id.lv_nhan_vien);
    }
    
    private void thieLapDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void taiDanhSachNhanVien() {
        // Tạm thời hiển thị toast, sẽ làm ListView sau
        Toast.makeText(this, "Danh sách nhân viên đang được tải...", Toast.LENGTH_SHORT).show();
    }
}