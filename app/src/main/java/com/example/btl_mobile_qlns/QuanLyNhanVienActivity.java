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
        android.util.Log.d("QuanLyNV", "Bắt đầu tải danh sách nhân viên");
        
        Cursor cursor = dbHelper.getAllEmployees();
        android.util.Log.d("QuanLyNV", "Cursor count: " + (cursor != null ? cursor.getCount() : "null"));
        
        if (cursor != null && cursor.getCount() > 0) {
            String[] nhanVienList = new String[cursor.getCount()];
            int index = 0;
            
            while (cursor.moveToNext()) {
                String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                String tenChucVu = cursor.getString(cursor.getColumnIndexOrThrow("TenChucVu"));
                String tenPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("TenPhongBan"));
                
                nhanVienList[index] = maNV + " - " + hoTen + "\n" + tenChucVu + " - " + tenPhongBan;
                android.util.Log.d("QuanLyNV", "Nhân viên " + index + ": " + nhanVienList[index]);
                index++;
            }
            
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, nhanVienList);
            listView.setAdapter(adapter);
            
            cursor.close();
            Toast.makeText(this, "Đã tải " + index + " nhân viên", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không có nhân viên nào trong hệ thống", Toast.LENGTH_LONG).show();
            android.util.Log.d("QuanLyNV", "Không có dữ liệu nhân viên");
        }
    }
}