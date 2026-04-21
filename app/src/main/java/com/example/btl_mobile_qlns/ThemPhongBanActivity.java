package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ThemPhongBanActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etMaPhongBan, etTenPhongBan;
    private Spinner spTruongPhong;
    private Switch swTrangThai;
    private Button btnLuu, btnHuy;
    
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private String originalMaPhongBan;
    private List<String> listMaNhanVien;
    private List<String> listTenNhanVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_phong_ban);
        
        initViews();
        setupDatabase();
        checkEditMode();
        setupManagerSpinner();
        setupButtons();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        etMaPhongBan = findViewById(R.id.et_ma_phong_ban);
        etTenPhongBan = findViewById(R.id.et_ten_phong_ban);
        spTruongPhong = findViewById(R.id.sp_truong_phong);
        swTrangThai = findViewById(R.id.sw_trang_thai);
        btnLuu = findViewById(R.id.btn_luu);
        btnHuy = findViewById(R.id.btn_huy);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void checkEditMode() {
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        
        if (isEditMode) {
            tvTitle.setText("SỬA PHÒNG BAN");
            
            originalMaPhongBan = getIntent().getStringExtra("ma_phong_ban");
            String tenPhongBan = getIntent().getStringExtra("ten_phong_ban");
            String truongPhong = getIntent().getStringExtra("truong_phong");
            int trangThai = getIntent().getIntExtra("trang_thai", 1);
            
            etMaPhongBan.setText(originalMaPhongBan);
            etMaPhongBan.setEnabled(false); // Không cho sửa mã phòng ban
            etTenPhongBan.setText(tenPhongBan);
            swTrangThai.setChecked(trangThai == 1);
            
            btnLuu.setText("CẬP NHẬT");
        } else {
            tvTitle.setText("THÊM PHÒNG BAN MỚI");
            
            // Tự động tạo mã phòng ban
            String nextCode = dbHelper.getNextDepartmentCode();
            etMaPhongBan.setText(nextCode);
            etMaPhongBan.setEnabled(false); // Không cho sửa mã tự động
            
            swTrangThai.setChecked(true); // Mặc định hoạt động
            btnLuu.setText("THÊM PHÒNG BAN");
        }
    }
    
    private void setupManagerSpinner() {
        try {
            listMaNhanVien = new ArrayList<>();
            listTenNhanVien = new ArrayList<>();
            
            // Thêm option "Không có trưởng phòng"
            listMaNhanVien.add("");
            listTenNhanVien.add("Không có trưởng phòng");
            
            // Lấy danh sách nhân viên có thể làm trưởng phòng (chức vụ Trưởng phòng hoặc Giám đốc)
            Cursor cursor = dbHelper.getManagerCandidates();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                    String tenChucVu = cursor.getString(cursor.getColumnIndexOrThrow("TenChucVu"));
                    
                    listMaNhanVien.add(maNV);
                    listTenNhanVien.add(maNV + " - " + hoTen + " (" + tenChucVu + ")");
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, listTenNhanVien);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTruongPhong.setAdapter(adapter);
            
            // Nếu là edit mode, chọn trưởng phòng hiện tại
            if (isEditMode) {
                String currentTruongPhong = getIntent().getStringExtra("truong_phong");
                if (currentTruongPhong != null) {
                    int position = listMaNhanVien.indexOf(currentTruongPhong);
                    if (position >= 0) {
                        spTruongPhong.setSelection(position);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải danh sách nhân viên", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupButtons() {
        btnLuu.setOnClickListener(v -> saveDepartment());
        btnHuy.setOnClickListener(v -> finish());
    }
    
    private void saveDepartment() {
        String maPhongBan = etMaPhongBan.getText().toString().trim();
        String tenPhongBan = etTenPhongBan.getText().toString().trim();
        int truongPhongPosition = spTruongPhong.getSelectedItemPosition();
        String truongPhong = truongPhongPosition > 0 ? listMaNhanVien.get(truongPhongPosition) : null;
        int trangThai = swTrangThai.isChecked() ? 1 : 0;
        
        // Validate
        if (tenPhongBan.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên phòng ban", Toast.LENGTH_SHORT).show();
            etTenPhongBan.requestFocus();
            return;
        }
        
        boolean success;
        if (isEditMode) {
            success = dbHelper.updateDepartment(originalMaPhongBan, tenPhongBan, truongPhong, trangThai);
        } else {
            success = dbHelper.addDepartment(maPhongBan, tenPhongBan, truongPhong, trangThai);
        }
        
        if (success) {
            String message = isEditMode ? "Cập nhật phòng ban thành công" : "Thêm phòng ban thành công";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            String message = isEditMode ? "Lỗi khi cập nhật phòng ban" : "Lỗi khi thêm phòng ban";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}