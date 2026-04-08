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
import com.example.btl_mobile_qlns.models.ChucVu;

import java.util.ArrayList;
import java.util.List;

public class QuanLyChucVuActivity extends AppCompatActivity {

    private TextView tvTitle, tvTongSo;
    private EditText etTimKiem;
    private Button btnThemChucVu;
    private ListView lvChucVu;
    
    private DatabaseHelper dbHelper;
    private ChucVuAdapter adapter;
    private List<ChucVu> listChucVu;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_chuc_vu);
        
        initViews();
        setupDatabase();
        setupUI();
        loadPositions();
        setupSearch();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvTongSo = findViewById(R.id.tv_tong_so);
        etTimKiem = findViewById(R.id.et_tim_kiem);
        btnThemChucVu = findViewById(R.id.btn_them_chuc_vu);
        lvChucVu = findViewById(R.id.lv_chuc_vu);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
    }
    
    private void setupUI() {
        tvTitle.setText("QUẢN LÝ CHỨC VỤ");
        
        // Chỉ Admin và HR mới có thể thêm/sửa/xóa chức vụ
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            btnThemChucVu.setVisibility(View.VISIBLE);
            btnThemChucVu.setOnClickListener(v -> openAddPosition());
        } else {
            btnThemChucVu.setVisibility(View.GONE);
        }
    }
    
    private void setupSearch() {
        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPositions(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    public void loadPositions() {
        try {
            listChucVu = new ArrayList<>();
            Cursor cursor = dbHelper.getAllPositionsWithDetails();
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maChucVu = cursor.getString(cursor.getColumnIndexOrThrow("MaChucVu"));
                    String tenChucVu = cursor.getString(cursor.getColumnIndexOrThrow("TenChucVu"));
                    double mucLuongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("MucLuongCoBan"));
                    int trangThai = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"));
                    int soNhanVien = cursor.getInt(cursor.getColumnIndexOrThrow("SoNhanVien"));
                    
                    ChucVu chucVu = new ChucVu(maChucVu, tenChucVu, mucLuongCoBan, trangThai);
                    chucVu.setSoNhanVien(soNhanVien);
                    
                    listChucVu.add(chucVu);
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải danh sách chức vụ", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void searchPositions(String keyword) {
        try {
            listChucVu = new ArrayList<>();
            Cursor cursor;
            
            if (keyword.trim().isEmpty()) {
                cursor = dbHelper.getAllPositionsWithDetails();
            } else {
                cursor = dbHelper.searchPositions(keyword);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maChucVu = cursor.getString(cursor.getColumnIndexOrThrow("MaChucVu"));
                    String tenChucVu = cursor.getString(cursor.getColumnIndexOrThrow("TenChucVu"));
                    double mucLuongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("MucLuongCoBan"));
                    int trangThai = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"));
                    int soNhanVien = cursor.getInt(cursor.getColumnIndexOrThrow("SoNhanVien"));
                    
                    ChucVu chucVu = new ChucVu(maChucVu, tenChucVu, mucLuongCoBan, trangThai);
                    chucVu.setSoNhanVien(soNhanVien);
                    
                    listChucVu.add(chucVu);
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
            adapter = new ChucVuAdapter(this, listChucVu, currentRole);
            lvChucVu.setAdapter(adapter);
        } else {
            adapter.updateData(listChucVu);
        }
        
        tvTongSo.setText("Tổng số: " + listChucVu.size() + " chức vụ");
    }
    
    private void openAddPosition() {
        Intent intent = new Intent(this, ThemChucVuActivity.class);
        intent.putExtra("role", currentRole);
        startActivityForResult(intent, 1002);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            loadPositions(); // Refresh danh sách
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadPositions(); // Refresh khi quay lại
    }
}