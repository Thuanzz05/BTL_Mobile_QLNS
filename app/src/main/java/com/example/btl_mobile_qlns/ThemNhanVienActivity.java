package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ThemNhanVienActivity extends AppCompatActivity {

    private EditText etMaNV, etHoTen, etNgaySinh, etSDT, etEmail;
    private Spinner spGioiTinh, spPhongBan, spChucVu;
    private Button btnSave;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private String currentMaNV = null; // null means adding, not null means editing

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

        // Kiểm tra xem là thêm mới hay sửa
        if (getIntent().hasExtra("ma_nv")) {
            currentMaNV = getIntent().getStringExtra("ma_nv");
            tvTitle.setText("CẬP NHẬT NHÂN VIÊN");
            etMaNV.setText(currentMaNV);
            etMaNV.setEnabled(false); // Không cho sửa mã
            dienThongTinNhanVien(currentMaNV);
        }

        btnSave.setOnClickListener(v -> saveNhanVien());
    }

    private void khoiTaoViews() {
        tvTitle = findViewById(R.id.tv_title);
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

    private void taiDanhMuc() {
        // Tải phòng ban
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

        // Tải chức vụ
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
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String date = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                etNgaySinh.setText(date);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void dienThongTinNhanVien(String maNV) {
        // Trong thực tế nên viết 1 hàm getEmployeeByMa trong DatabaseHelper
        // Ở đây ta dùng getAll và filter cho nhanh hoặc query trực tiếp
        Cursor cursor = dbHelper.getAllEmployees();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien")).equals(maNV)) {
                    etHoTen.setText(cursor.getString(cursor.getColumnIndexOrThrow("HoTen")));
                    etNgaySinh.setText(cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh")));
                    etSDT.setText(cursor.getString(cursor.getColumnIndexOrThrow("SoDienThoai")));
                    etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("Email")));
                    
                    String gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"));
                    ArrayAdapter adapterGT = (ArrayAdapter) spGioiTinh.getAdapter();
                    int posGT = adapterGT.getPosition(gioiTinh);
                    if (posGT >= 0) spGioiTinh.setSelection(posGT);

                    String maPB = cursor.getString(cursor.getColumnIndexOrThrow("MaPhongBan"));
                    int posPB = listMaPB.indexOf(maPB);
                    if (posPB >= 0) spPhongBan.setSelection(posPB);

                    String maCV = cursor.getString(cursor.getColumnIndexOrThrow("MaChucVu"));
                    int posCV = listMaCV.indexOf(maCV);
                    if (posCV >= 0) spChucVu.setSelection(posCV);
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
        
        if (maNV.isEmpty() || hoTen.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Mã và Họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        String maPB = listMaPB.get(spPhongBan.getSelectedItemPosition());
        String maCV = listMaCV.get(spChucVu.getSelectedItemPosition());

        boolean success;
        if (currentMaNV == null) {
            // Kiểm tra trùng mã
            if (dbHelper.checkEmployeeExists(maNV)) {
                Toast.makeText(this, "Mã nhân viên đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }
            success = dbHelper.addEmployee(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, maPB, maCV);
        } else {
            success = dbHelper.updateEmployee(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, maPB, maCV);
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