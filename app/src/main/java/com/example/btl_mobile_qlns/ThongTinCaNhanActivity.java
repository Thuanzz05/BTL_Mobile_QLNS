package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.Calendar;

public class ThongTinCaNhanActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView tvTitle, tvMaNV, tvVaiTro;
    private EditText etHoTen, etNgaySinh, etSoDienThoai, etEmail;
    private RadioGroup rgGioiTinh;
    private RadioButton rbNam, rbNu;
    private Button btnCapNhat, btnDoiMatKhau;
    private ImageView ivAvatar;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String maNhanVien;
    private String currentRole;
    private String imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ca_nhan);
        
        initViews();
        setupDatabase();
        loadUserInfo();
        setupButtons();
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
        ivAvatar = findViewById(R.id.iv_avatar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
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
                
                // Hiển thị ảnh đại diện
                imageUri = cursorEmployee.getString(cursorEmployee.getColumnIndexOrThrow("HinhAnh"));
                if (imageUri != null && !imageUri.isEmpty()) {
                    try {
                        ivAvatar.setImageURI(Uri.parse(imageUri));
                    } catch (Exception e) {
                        ivAvatar.setImageResource(R.drawable.ic_person);
                    }
                }
                
                cursorEmployee.close();
            }
        }
    }
    
    private void setupButtons() {
        // Date picker cho ngày sinh
        etNgaySinh.setOnClickListener(v -> showDatePicker());
        
        btnCapNhat.setOnClickListener(v -> capNhatThongTin());
        
        btnDoiMatKhau.setOnClickListener(v -> showDoiMatKhauDialog());

        ivAvatar.setOnClickListener(v -> openGallery());
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = uri.toString();
            ivAvatar.setImageURI(uri);
        }
    }
    
    private void showDoiMatKhauDialog() {
        // Inflate layout dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_doi_mat_khau, null);
        
        EditText etMatKhauCu = dialogView.findViewById(R.id.et_mat_khau_cu);
        EditText etMatKhauMoi = dialogView.findViewById(R.id.et_mat_khau_moi);
        EditText etXacNhanMatKhau = dialogView.findViewById(R.id.et_xac_nhan_mat_khau);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Xác nhận", null) // Set null để tự xử lý dismiss
                .setNegativeButton("Hủy", null)
                .create();
        
        dialog.show();
        
        // Override nút Xác nhận để kiểm tra validation trước khi đóng dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String matKhauCu = etMatKhauCu.getText().toString().trim();
            String matKhauMoi = etMatKhauMoi.getText().toString().trim();
            String xacNhanMatKhau = etXacNhanMatKhau.getText().toString().trim();
            
            // Validate mật khẩu hiện tại
            if (matKhauCu.isEmpty()) {
                etMatKhauCu.setError("Vui lòng nhập mật khẩu hiện tại");
                etMatKhauCu.requestFocus();
                return;
            }
            
            // Validate mật khẩu mới
            if (matKhauMoi.isEmpty()) {
                etMatKhauMoi.setError("Vui lòng nhập mật khẩu mới");
                etMatKhauMoi.requestFocus();
                return;
            }
            
            if (matKhauMoi.length() < 6) {
                etMatKhauMoi.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
                etMatKhauMoi.requestFocus();
                return;
            }
            
            // Validate xác nhận mật khẩu
            if (xacNhanMatKhau.isEmpty()) {
                etXacNhanMatKhau.setError("Vui lòng xác nhận mật khẩu mới");
                etXacNhanMatKhau.requestFocus();
                return;
            }
            
            if (!matKhauMoi.equals(xacNhanMatKhau)) {
                etXacNhanMatKhau.setError("Mật khẩu xác nhận không khớp");
                etXacNhanMatKhau.requestFocus();
                return;
            }
            
            // Kiểm tra mật khẩu mới không trùng mật khẩu cũ
            if (matKhauCu.equals(matKhauMoi)) {
                etMatKhauMoi.setError("Mật khẩu mới không được trùng mật khẩu cũ");
                etMatKhauMoi.requestFocus();
                return;
            }
            
            // Kiểm tra mật khẩu hiện tại có đúng không
            if (!dbHelper.checkCurrentPassword(currentUsername, matKhauCu)) {
                etMatKhauCu.setError("Mật khẩu hiện tại không đúng");
                etMatKhauCu.requestFocus();
                return;
            }
            
            // Thực hiện đổi mật khẩu
            boolean success = dbHelper.changePassword(currentUsername, matKhauMoi);
            if (success) {
                Toast.makeText(ThongTinCaNhanActivity.this, 
                        "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(ThongTinCaNhanActivity.this, 
                        "Lỗi khi đổi mật khẩu. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
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
            etHoTen.setError("Vui lòng nhập họ tên");
            etHoTen.requestFocus();
            return;
        }

        if (ngaySinh.isEmpty()) {
            etNgaySinh.setError("Vui lòng chọn ngày sinh");
            etNgaySinh.requestFocus();
            return;
        }

        if (soDienThoai.length() != 10) {
            etSoDienThoai.setError("Số điện thoại phải có đúng 10 chữ số");
            etSoDienThoai.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Định dạng Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }
        
        boolean success = dbHelper.updateEmployeePersonalInfo(maNhanVien, hoTen, ngaySinh, 
                                                               gioiTinh, soDienThoai, email, imageUri);
        
        if (success) {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
        }
    }
}