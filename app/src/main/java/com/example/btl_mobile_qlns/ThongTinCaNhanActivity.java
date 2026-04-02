package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.Calendar;

public class ThongTinCaNhanActivity extends AppCompatActivity {

    private TextView tvTitle, tvMaNV, tvVaiTro;
    private EditText etHoTen, etNgaySinh, etSoDienThoai, etEmail;
    private RadioGroup rgGioiTinh;
    private RadioButton rbNam, rbNu;
    private Button btnCapNhat, btnDoiMatKhau;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String maNhanVien;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_thong_tin_ca_nhan);
            
            initViews();
            setupDatabase();
            loadUserInfo();
            setupButtons();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvMaNV = findViewById(R.id.tv_ma_nv);
        tvVaiTro = findViewById(R.id.tv_vai_tro);
        etHoTen = findViewById(R.id.et_ho_ten);
        etNgaySinh = findViewById(R.id.et_ngay_sinh);
        etSoDienThoai = findViewById(R.id.et_so_dien_thoai);
        etEmail = findViewById(R.id.et_email);
        rgGioiTinh = findViewById(R.id.rg_gioi_tinh);
        rbNam = findViewById(R.id.rb_nam);
        rbNu = findViewById(R.id.rb_nu);
        btnCapNhat = findViewById(R.id.btn_cap_nhat);
        btnDoiMatKhau = findViewById(R.id.btn_doi_mat_khau);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername != null) {
            maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
        }
    }
    
    private void loadUserInfo() {
        if (currentUsername == null) return;
        
        // Lấy thông tin tài khoản
        Cursor cursorAccount = dbHelper.getUserInfo(currentUsername);
        if (cursorAccount != null && cursorAccount.moveToFirst()) {
            String hoTen = cursorAccount.getString(cursorAccount.getColumnIndexOrThrow("HoTen"));
            currentRole = cursorAccount.getString(cursorAccount.getColumnIndexOrThrow("VaiTro"));
            
            tvTitle.setText("THÔNG TIN CÁ NHÂN");
            tvVaiTro.setText("Vai trò: " + currentRole);
            
            cursorAccount.close();
        }
        
        // Nếu là Admin, chỉ hiển thị thông tin cơ bản
        if ("Admin".equals(currentRole)) {
            tvMaNV.setText("Mã: ADMIN");
            etHoTen.setText("Administrator");
            etHoTen.setEnabled(false);
            etNgaySinh.setEnabled(false);
            etSoDienThoai.setEnabled(false);
            etEmail.setEnabled(false);
            rgGioiTinh.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnCapNhat.setText("ADMIN - KHÔNG THỂ SỬA");
            return;
        }
        
        // Lấy thông tin nhân viên chi tiết
        if (maNhanVien != null) {
            Cursor cursorEmployee = dbHelper.getEmployeeByMa(maNhanVien);
            if (cursorEmployee != null && cursorEmployee.moveToFirst()) {
                tvMaNV.setText("Mã: " + maNhanVien);
                etHoTen.setText(cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("HoTen")));
                etNgaySinh.setText(cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("NgaySinh")));
                etSoDienThoai.setText(cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("SoDienThoai")));
                etEmail.setText(cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("Email")));
                
                String gioiTinh = cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("GioiTinh"));
                if ("Nam".equals(gioiTinh)) {
                    rbNam.setChecked(true);
                } else {
                    rbNu.setChecked(true);
                }
                
                cursorEmployee.close();
            }
        }
    }
    
    private void setupButtons() {
        // Date picker cho ngày sinh
        etNgaySinh.setOnClickListener(v -> showDatePicker());
        
        btnCapNhat.setOnClickListener(v -> capNhatThongTin());
        
        btnDoiMatKhau.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, 
            (view, year1, month1, dayOfMonth) -> {
                String date = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                etNgaySinh.setText(date);
            }, year, month, day);
        datePickerDialog.show();
    }
    
    private void capNhatThongTin() {
        if (maNhanVien == null || "Admin".equals(currentRole)) {
            Toast.makeText(this, "Không thể cập nhật thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String hoTen = etHoTen.getText().toString().trim();
        String ngaySinh = etNgaySinh.getText().toString().trim();
        String soDienThoai = etSoDienThoai.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gioiTinh = rbNam.isChecked() ? "Nam" : "Nữ";
        
        if (hoTen.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean success = dbHelper.updateEmployeePersonalInfo(maNhanVien, hoTen, ngaySinh, 
                                                             gioiTinh, soDienThoai, email);
        
        if (success) {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
        }
    }
}