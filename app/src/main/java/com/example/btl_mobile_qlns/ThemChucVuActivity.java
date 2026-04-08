package com.example.btl_mobile_qlns;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class ThemChucVuActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etMaChucVu, etTenChucVu, etMucLuong;
    private Switch swTrangThai;
    private Button btnLuu, btnHuy;
    
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private String originalMaChucVu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_chuc_vu);
        
        initViews();
        setupDatabase();
        checkEditMode();
        setupButtons();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        etMaChucVu = findViewById(R.id.et_ma_chuc_vu);
        etTenChucVu = findViewById(R.id.et_ten_chuc_vu);
        etMucLuong = findViewById(R.id.et_muc_luong);
        swTrangThai = findViewById(R.id.sw_trang_thai);
        btnLuu = findViewById(R.id.btn_luu);
        btnHuy = findViewById(R.id.btn_huy);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void checkEditMode() {
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        
        if (isEditMode) {
            tvTitle.setText("SỬA CHỨC VỤ");
            
            originalMaChucVu = getIntent().getStringExtra("ma_chuc_vu");
            String tenChucVu = getIntent().getStringExtra("ten_chuc_vu");
            double mucLuong = getIntent().getDoubleExtra("muc_luong_co_ban", 0);
            int trangThai = getIntent().getIntExtra("trang_thai", 1);
            
            etMaChucVu.setText(originalMaChucVu);
            etMaChucVu.setEnabled(false); // Không cho sửa mã chức vụ
            etTenChucVu.setText(tenChucVu);
            etMucLuong.setText(String.valueOf((long)mucLuong));
            swTrangThai.setChecked(trangThai == 1);
            
            btnLuu.setText("CẬP NHẬT");
        } else {
            tvTitle.setText("THÊM CHỨC VỤ MỚI");
            
            // Tự động tạo mã chức vụ
            String nextCode = dbHelper.getNextPositionCode();
            etMaChucVu.setText(nextCode);
            etMaChucVu.setEnabled(false); // Không cho sửa mã tự động
            
            swTrangThai.setChecked(true); // Mặc định hoạt động
            btnLuu.setText("THÊM CHỨC VỤ");
        }
    }
    
    private void setupButtons() {
        btnLuu.setOnClickListener(v -> savePosition());
        btnHuy.setOnClickListener(v -> finish());
    }
    
    private void savePosition() {
        String maChucVu = etMaChucVu.getText().toString().trim();
        String tenChucVu = etTenChucVu.getText().toString().trim();
        String mucLuongStr = etMucLuong.getText().toString().trim();
        int trangThai = swTrangThai.isChecked() ? 1 : 0;
        
        // Validate
        if (tenChucVu.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên chức vụ", Toast.LENGTH_SHORT).show();
            etTenChucVu.requestFocus();
            return;
        }
        
        if (mucLuongStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mức lương cơ bản", Toast.LENGTH_SHORT).show();
            etMucLuong.requestFocus();
            return;
        }
        
        double mucLuong;
        try {
            mucLuong = Double.parseDouble(mucLuongStr);
            if (mucLuong < 0) {
                Toast.makeText(this, "Mức lương phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                etMucLuong.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Mức lương không hợp lệ", Toast.LENGTH_SHORT).show();
            etMucLuong.requestFocus();
            return;
        }
        
        boolean success;
        if (isEditMode) {
            success = dbHelper.updatePosition(originalMaChucVu, tenChucVu, mucLuong, trangThai);
        } else {
            success = dbHelper.addPosition(maChucVu, tenChucVu, mucLuong, trangThai);
        }
        
        if (success) {
            String message = isEditMode ? "Cập nhật chức vụ thành công" : "Thêm chức vụ thành công";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            String message = isEditMode ? "Lỗi khi cập nhật chức vụ" : "Lỗi khi thêm chức vụ";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}