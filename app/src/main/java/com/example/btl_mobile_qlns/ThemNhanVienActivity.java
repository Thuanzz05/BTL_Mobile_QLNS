package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ThemNhanVienActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etMaNV, etHoTen, etNgaySinh, etSDT, etEmail;
    private Spinner spGioiTinh, spPhongBan, spChucVu;
    private Button btnSave;
    private TextView tvTitle;
    private ImageView ivAvatar;
    private DatabaseHelper dbHelper;
    private String currentMaNV = null;
    private String imageUri = "";

    private List<String> listMaPB = new ArrayList<>();
    private List<String> listTenPB = new ArrayList<>();
    private List<String> listMaCV = new ArrayList<>();
    private List<String> listTenCV = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan_vien);

        dbHelper = new DatabaseHelper(this);
        khoiTaoViews();
        taiDanhMuc();
        thietLapNgaySinh();

        ivAvatar.setOnClickListener(v -> openGallery());

        if (getIntent().hasExtra("ma_nv")) {
            currentMaNV = getIntent().getStringExtra("ma_nv");
            tvTitle.setText("CẬP NHẬT NHÂN VIÊN");
            etMaNV.setText(currentMaNV);
            etMaNV.setEnabled(false);
            dienThongTinNhanVien(currentMaNV);
        } else {
            etMaNV.setText(dbHelper.getNextEmployeeCode());
            etMaNV.setEnabled(false);
        }

        btnSave.setOnClickListener(v -> saveNhanVien());
    }

    private void khoiTaoViews() {
        tvTitle = findViewById(R.id.tv_title);
        ivAvatar = findViewById(R.id.iv_avatar_setup);
        etMaNV = findViewById(R.id.et_ma_nv);
        etHoTen = findViewById(R.id.et_ho_ten);
        etNgaySinh = findViewById(R.id.et_ngay_sinh);
        etSDT = findViewById(R.id.et_sdt);
        etEmail = findViewById(R.id.et_email);
        spGioiTinh = findViewById(R.id.sp_gioi_tinh);
        spPhongBan = findViewById(R.id.sp_phong_ban);
        spChucVu = findViewById(R.id.sp_chuc_vu);
        btnSave = findViewById(R.id.btn_save);
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
            // Xin quyền truy cập lâu dài cho URI này
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = uri.toString();
            ivAvatar.setImageURI(uri);
            ivAvatar.setPadding(0, 0, 0, 0);
            ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void taiDanhMuc() {
        Cursor cursorPB = dbHelper.getAllDepartments();
        if (cursorPB != null && cursorPB.moveToFirst()) {
            do {
                listMaPB.add(cursorPB.getString(cursorPB.getColumnIndexOrThrow("MaPhongBan")));
                listTenPB.add(cursorPB.getString(cursorPB.getColumnIndexOrThrow("TenPhongBan")));
            } while (cursorPB.moveToNext());
            cursorPB.close();
        }
        ArrayAdapter<String> adapterPB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTenPB);
        adapterPB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPhongBan.setAdapter(adapterPB);

        Cursor cursorCV = dbHelper.getAllPositions();
        if (cursorCV != null && cursorCV.moveToFirst()) {
            do {
                listMaCV.add(cursorCV.getString(cursorCV.getColumnIndexOrThrow("MaChucVu")));
                listTenCV.add(cursorCV.getString(cursorCV.getColumnIndexOrThrow("TenChucVu")));
            } while (cursorCV.moveToNext());
            cursorCV.close();
        }
        ArrayAdapter<String> adapterCV = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTenCV);
        adapterCV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChucVu.setAdapter(adapterCV);
    }

    private void thietLapNgaySinh() {
        etNgaySinh.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etNgaySinh.setText(String.format("%d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void dienThongTinNhanVien(String maNV) {
        Cursor cursor = dbHelper.getAllEmployees();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien")).equals(maNV)) {
                    etHoTen.setText(cursor.getString(cursor.getColumnIndexOrThrow("HoTen")));
                    etNgaySinh.setText(cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh")));
                    etSDT.setText(cursor.getString(cursor.getColumnIndexOrThrow("SoDienThoai")));
                    etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("Email")));
                    
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
                    if (imageUri != null && !imageUri.isEmpty()) {
                        ivAvatar.setImageURI(Uri.parse(imageUri));
                        ivAvatar.setPadding(0, 0, 0, 0);
                        ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    String gt = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"));
                    ArrayAdapter adapterGT = (ArrayAdapter) spGioiTinh.getAdapter();
                    if (adapterGT != null) spGioiTinh.setSelection(adapterGT.getPosition(gt));

                    String maPB = cursor.getString(cursor.getColumnIndexOrThrow("MaPhongBan"));
                    spPhongBan.setSelection(listMaPB.indexOf(maPB));

                    String maCV = cursor.getString(cursor.getColumnIndexOrThrow("MaChucVu"));
                    spChucVu.setSelection(listMaCV.indexOf(maCV));
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void saveNhanVien() {
        String maNV = etMaNV.getText().toString().trim();
        String hoTen = etHoTen.getText().toString().trim();
        String ngaySinh = etNgaySinh.getText().toString().trim();
        String gioiTinh = spGioiTinh.getSelectedItem().toString();
        String sdt = etSDT.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String maPB = listMaPB.get(spPhongBan.getSelectedItemPosition());
        String maCV = listMaCV.get(spChucVu.getSelectedItemPosition());

        if (hoTen.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (currentMaNV == null) {
            success = dbHelper.addEmployee(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, maPB, maCV, imageUri);
        } else {
            success = dbHelper.updateEmployee(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, maPB, maCV, imageUri);
        }

        if (success) {
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}