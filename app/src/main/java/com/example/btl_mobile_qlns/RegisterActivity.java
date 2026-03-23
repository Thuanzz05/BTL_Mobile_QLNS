package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etMaNhanVien, etHoTen, etNgaySinh, etSoDienThoai, etEmail, etNgayVaoLam;
    private EditText etTenDangNhap, etMatKhau, etXacNhanMatKhau;
    private RadioGroup rgGioiTinh;
    private Spinner spPhongBan;
    private Button btnDangKy, btnHuy;
    
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        setupDatabase();
        setupSpinners();
        setupDatePickers();
        setupButtons();
    }
    
    private void initViews() {
        etMaNhanVien = findViewById(R.id.et_ma_nhan_vien);
        etHoTen = findViewById(R.id.et_ho_ten);
        etNgaySinh = findViewById(R.id.et_ngay_sinh);
        etSoDienThoai = findViewById(R.id.et_so_dien_thoai);
        etEmail = findViewById(R.id.et_email);
        etNgayVaoLam = findViewById(R.id.et_ngay_vao_lam);
        etTenDangNhap = findViewById(R.id.et_ten_dang_nhap);
        etMatKhau = findViewById(R.id.et_mat_khau);
        etXacNhanMatKhau = findViewById(R.id.et_xac_nhan_mat_khau);
        
        rgGioiTinh = findViewById(R.id.rg_gioi_tinh);
        spPhongBan = findViewById(R.id.sp_phong_ban);
        
        btnDangKy = findViewById(R.id.btn_dang_ky);
        btnHuy = findViewById(R.id.btn_huy);
        
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void setupSpinners() {
        // Setup Phòng ban spinner
        List<String> phongBanList = new ArrayList<>();
        List<String> phongBanIds = new ArrayList<>();
        
        Cursor cursor = dbHelper.getAllDepartments();
        if (cursor.moveToFirst()) {
            do {
                String maPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("MaPhongBan"));
                String tenPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("TenPhongBan"));
                phongBanList.add(tenPhongBan);
                phongBanIds.add(maPhongBan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        ArrayAdapter<String> phongBanAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, phongBanList);
        phongBanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPhongBan.setAdapter(phongBanAdapter);
        spPhongBan.setTag(phongBanIds);
    }
    
    private void setupDatePickers() {
        etNgaySinh.setOnClickListener(v -> showDatePicker(etNgaySinh));
        etNgayVaoLam.setOnClickListener(v -> showDatePicker(etNgayVaoLam));
        
        // Set ngày vào làm mặc định là hôm nay
        etNgayVaoLam.setText(dateFormat.format(calendar.getTime()));
    }
    
    private void showDatePicker(EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void setupButtons() {
        btnDangKy.setOnClickListener(v -> registerUser());
        btnHuy.setOnClickListener(v -> finish());
    }
    
    private void registerUser() {
        // Lấy dữ liệu từ form
        String maNhanVien = etMaNhanVien.getText().toString().trim();
        String hoTen = etHoTen.getText().toString().trim();
        String ngaySinh = etNgaySinh.getText().toString().trim();
        String soDienThoai = etSoDienThoai.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ngayVaoLam = etNgayVaoLam.getText().toString().trim();
        String tenDangNhap = etTenDangNhap.getText().toString().trim();
        String matKhau = etMatKhau.getText().toString().trim();
        String xacNhanMatKhau = etXacNhanMatKhau.getText().toString().trim();
        
        // Lấy giới tính
        String gioiTinh = "Nam";
        int selectedGender = rgGioiTinh.getCheckedRadioButtonId();
        if (selectedGender != -1) {
            RadioButton rbSelected = findViewById(selectedGender);
            gioiTinh = rbSelected.getText().toString();
        }
        
        // Lấy phòng ban
        List<String> phongBanIds = (List<String>) spPhongBan.getTag();
        String maPhongBan = phongBanIds.get(spPhongBan.getSelectedItemPosition());
        
        // Validate dữ liệu
        if (!validateInput(maNhanVien, hoTen, ngaySinh, soDienThoai, email, 
                          ngayVaoLam, tenDangNhap, matKhau, xacNhanMatKhau)) {
            return;
        }
        
        // Kiểm tra mã nhân viên đã tồn tại
        if (dbHelper.checkEmployeeExists(maNhanVien)) {
            Toast.makeText(this, "Mã nhân viên đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Kiểm tra username đã tồn tại
        if (dbHelper.checkUsernameExists(tenDangNhap)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Đăng ký user
        boolean success = dbHelper.registerUser(maNhanVien, hoTen, ngaySinh, gioiTinh,
                soDienThoai, email, ngayVaoLam, maPhongBan, "CV003", tenDangNhap, matKhau); // CV003 = Nhân viên
        
        if (success) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateInput(String maNhanVien, String hoTen, String ngaySinh,
                                 String soDienThoai, String email, String ngayVaoLam,
                                 String tenDangNhap, String matKhau, String xacNhanMatKhau) {
        
        if (maNhanVien.isEmpty()) {
            etMaNhanVien.setError("Vui lòng nhập mã nhân viên");
            etMaNhanVien.requestFocus();
            return false;
        }
        
        if (hoTen.isEmpty()) {
            etHoTen.setError("Vui lòng nhập họ tên");
            etHoTen.requestFocus();
            return false;
        }
        
        if (ngaySinh.isEmpty()) {
            etNgaySinh.setError("Vui lòng chọn ngày sinh");
            etNgaySinh.requestFocus();
            return false;
        }
        
        if (soDienThoai.isEmpty()) {
            etSoDienThoai.setError("Vui lòng nhập số điện thoại");
            etSoDienThoai.requestFocus();
            return false;
        }
        
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }
        
        if (!email.contains("@")) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }
        
        if (ngayVaoLam.isEmpty()) {
            etNgayVaoLam.setError("Vui lòng chọn ngày vào làm");
            etNgayVaoLam.requestFocus();
            return false;
        }
        
        if (tenDangNhap.isEmpty()) {
            etTenDangNhap.setError("Vui lòng nhập tên đăng nhập");
            etTenDangNhap.requestFocus();
            return false;
        }
        
        if (matKhau.isEmpty()) {
            etMatKhau.setError("Vui lòng nhập mật khẩu");
            etMatKhau.requestFocus();
            return false;
        }
        
        if (matKhau.length() < 6) {
            etMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etMatKhau.requestFocus();
            return false;
        }
        
        if (!matKhau.equals(xacNhanMatKhau)) {
            etXacNhanMatKhau.setError("Mật khẩu xác nhận không khớp");
            etXacNhanMatKhau.requestFocus();
            return false;
        }
        
        return true;
    }
}