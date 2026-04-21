package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class ThemHopDongActivity extends AppCompatActivity {

    private EditText etMaHD, etNgayBD, etNgayKT, etMucLuong;
    private Spinner spNhanVien, spLoaiHD;
    private Button btnSave, btnCancel;
    private DatabaseHelper dbHelper;
    private String editMaHD = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_hop_dong);

        dbHelper = new DatabaseHelper(this);
        editMaHD = getIntent().getStringExtra("MaHopDong");

        initViews();
        setupSpinners();
        setupDatePickers();
        setupEvents();

        if (editMaHD != null) {
            loadEditData();
        }
    }

    private void initViews() {
        etMaHD = findViewById(R.id.et_ma_hd);
        etNgayBD = findViewById(R.id.et_ngay_bd);
        etNgayKT = findViewById(R.id.et_ngay_kt);
        etMucLuong = findViewById(R.id.et_muc_luong);
        spNhanVien = findViewById(R.id.sp_nhan_vien);
        spLoaiHD = findViewById(R.id.sp_loai_hd);
        btnSave = findViewById(R.id.btn_save_hd);
        btnCancel = findViewById(R.id.btn_cancel);
        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Spinner Nhân viên
        Cursor cursor = dbHelper.getEmployeeListForSpinner();
        String[] from = {"DisplayName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, from, to, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNhanVien.setAdapter(adapter);

        // Lắng nghe sự kiện chọn nhân viên để gợi ý lương
        spNhanVien.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                String maNV = c.getString(c.getColumnIndexOrThrow("_id"));
                double lươngChucVu = dbHelper.getSalaryByEmployee(maNV);
                etMucLuong.setText(String.valueOf((long)lươngChucVu));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Spinner Loại hợp đồng
        String[] loaiHDs = {"Thử việc", "Có thời hạn (1 năm)", "Có thời hạn (3 năm)", "Không thời hạn"};
        ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loaiHDs);
        loaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLoaiHD.setAdapter(loaiAdapter);
    }

    private void setupDatePickers() {
        etNgayBD.setOnClickListener(v -> showDatePicker(etNgayBD));
        etNgayKT.setOnClickListener(v -> showDatePicker(etNgayKT));
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupEvents() {
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String maHD = etMaHD.getText().toString().trim();
            String ngayBD = etNgayBD.getText().toString().trim();
            String ngayKT = etNgayKT.getText().toString().trim();
            String mucLuongStr = etMucLuong.getText().toString().trim();

            if (maHD.isEmpty() || ngayBD.isEmpty() || mucLuongStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy mã nhân viên từ Spinner
            Cursor cursor = (Cursor) spNhanVien.getSelectedItem();
            String maNV = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            String loaiHD = spLoaiHD.getSelectedItem().toString();
            double mucLuong = Double.parseDouble(mucLuongStr);

            boolean success;
            if (editMaHD != null) {
                success = dbHelper.updateHopDong(maHD, maNV, loaiHD, ngayBD, ngayKT, mucLuong, "Hiệu lực");
            } else {
                success = dbHelper.insertHopDong(maHD, maNV, loaiHD, ngayBD, ngayKT, mucLuong);
            }

            if (success) {
                Toast.makeText(this, editMaHD != null ? "Cập nhật thành công" : "Thêm hợp đồng thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEditData() {
        TextView tvTitle = findViewById(android.R.id.content).getRootView().findViewById(R.id.tv_title); // I need to add ID to title
        // Let's just find by type or ignore title change for now, or add ID to title.
        
        etMaHD.setText(editMaHD);
        etMaHD.setEnabled(false);
        btnSave.setText("CẬP NHẬT HỢP ĐỒNG");

        Cursor cursor = dbHelper.getHopDongById(editMaHD);
        if (cursor != null && cursor.moveToFirst()) {
            String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
            String loaiHD = cursor.getString(cursor.getColumnIndexOrThrow("LoaiHopDong"));
            String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("NgayBatDau"));
            String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("NgayKetThuc"));
            double mucLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("MucLuong"));

            etNgayBD.setText(ngayBD);
            etNgayKT.setText(ngayKT);
            etMucLuong.setText(String.valueOf((long)mucLuong));

            // Select Employee in Spinner
            for (int i = 0; i < spNhanVien.getCount(); i++) {
                Cursor c = (Cursor) spNhanVien.getItemAtPosition(i);
                if (c.getString(c.getColumnIndexOrThrow("_id")).equals(maNV)) {
                    spNhanVien.setSelection(i);
                    break;
                }
            }

            // Select Contract Type
            for (int i = 0; i < spLoaiHD.getCount(); i++) {
                if (spLoaiHD.getItemAtPosition(i).toString().equals(loaiHD)) {
                    spLoaiHD.setSelection(i);
                    break;
                }
            }
            cursor.close();
        }
    }
}
