package com.example.btl_mobile_qlns;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.PhongBan;

import java.util.ArrayList;
import java.util.List;

public class QuanLyPhongBanActivity extends AppCompatActivity {

    private TextView tvTitle, tvTongSo;
    private EditText etTimKiem;
    private Button btnThemPhongBan;
    private ListView lvPhongBan;
    
    private DatabaseHelper dbHelper;
    private PhongBanAdapter adapter;
    private List<PhongBan> listPhongBan;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_phong_ban);
        
        initViews();
        setupDatabase();
        setupUI();
        loadDepartments();
        setupSearch();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvTongSo = findViewById(R.id.tv_tong_so);
        etTimKiem = findViewById(R.id.et_tim_kiem);
        btnThemPhongBan = findViewById(R.id.btn_them_phong_ban);
        lvPhongBan = findViewById(R.id.lv_phong_ban);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
    }
    
    private void setupUI() {
        tvTitle.setText("QUẢN LÝ PHÒNG BAN");
        
        // Chỉ Admin và HR mới có thể thêm/sửa/xóa phòng ban
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            btnThemPhongBan.setVisibility(View.VISIBLE);
            btnThemPhongBan.setOnClickListener(v -> openAddDepartment());
        } else {
            btnThemPhongBan.setVisibility(View.GONE);
        }
    }
    
    private void setupSearch() {
        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDepartments(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    public void loadDepartments() {
        try {
            listPhongBan = new ArrayList<>();
            Cursor cursor = dbHelper.getAllDepartmentsWithDetails();
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("MaPhongBan"));
                    String tenPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("TenPhongBan"));
                    String truongPhong = cursor.getString(cursor.getColumnIndexOrThrow("TruongPhong"));
                    int trangThai = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"));
                    int soNhanVien = cursor.getInt(cursor.getColumnIndexOrThrow("SoNhanVien"));
                    String tenTruongPhong = cursor.getString(cursor.getColumnIndexOrThrow("TenTruongPhong"));
                    
                    PhongBan phongBan = new PhongBan(maPhongBan, tenPhongBan, truongPhong, trangThai);
                    phongBan.setSoNhanVien(soNhanVien);
                    phongBan.setTenTruongPhong(tenTruongPhong);
                    
                    listPhongBan.add(phongBan);
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải danh sách phòng ban", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void searchDepartments(String keyword) {
        try {
            listPhongBan = new ArrayList<>();
            Cursor cursor;
            
            if (keyword.trim().isEmpty()) {
                cursor = dbHelper.getAllDepartmentsWithDetails();
            } else {
                cursor = dbHelper.searchDepartments(keyword);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("MaPhongBan"));
                    String tenPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("TenPhongBan"));
                    String truongPhong = cursor.getString(cursor.getColumnIndexOrThrow("TruongPhong"));
                    int trangThai = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"));
                    int soNhanVien = cursor.getInt(cursor.getColumnIndexOrThrow("SoNhanVien"));
                    String tenTruongPhong = cursor.getString(cursor.getColumnIndexOrThrow("TenTruongPhong"));
                    
                    PhongBan phongBan = new PhongBan(maPhongBan, tenPhongBan, truongPhong, trangThai);
                    phongBan.setSoNhanVien(soNhanVien);
                    phongBan.setTenTruongPhong(tenTruongPhong);
                    
                    listPhongBan.add(phongBan);
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateUI() {
        if (adapter == null) {
            adapter = new PhongBanAdapter(this, listPhongBan, currentRole);
            lvPhongBan.setAdapter(adapter);
        } else {
            adapter.updateData(listPhongBan);
        }
        
        tvTongSo.setText("Tổng số: " + listPhongBan.size() + " phòng ban");
    }
    
    private void openAddDepartment() {
        Intent intent = new Intent(this, ThemPhongBanActivity.class);
        intent.putExtra("role", currentRole);
        startActivityForResult(intent, 1001);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadDepartments(); // Refresh danh sách
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadDepartments(); // Refresh khi quay lại
    }
}