package com.example.btl_mobile_qlns;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NghiPhepActivity extends AppCompatActivity {

    private TextView tvTitle, tvSoNgayNghi;
    private EditText etNgayBatDau, etNgayKetThuc, etLyDo;
    private Button btnXinNghiPhep;
    private ListView lvLichSuNghiPhep;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String maNhanVien;
    private String currentRole;
    private NghiPhepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghi_phep);
        
        initViews();
        setupDatabase();
        setupDatePickers();
        setupButtons();
        loadLeaveHistory();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvSoNgayNghi = findViewById(R.id.tv_so_ngay_nghi);
        etNgayBatDau = findViewById(R.id.et_ngay_bat_dau);
        etNgayKetThuc = findViewById(R.id.et_ngay_ket_thuc);
        etLyDo = findViewById(R.id.et_ly_do);
        btnXinNghiPhep = findViewById(R.id.btn_xin_nghi_phep);
        lvLichSuNghiPhep = findViewById(R.id.lv_lich_su_nghi_phep);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        currentRole = getIntent().getStringExtra("role");
        
        if (currentUsername != null) {
            maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
        }
        
        // Thiết lập title theo role
        if ("Employee".equals(currentRole)) {
            tvTitle.setText("XIN NGHỈ PHÉP");
        } else {
            tvTitle.setText("QUẢN LÝ NGHỈ PHÉP");
        }
    }
    
    private void setupDatePickers() {
        etNgayBatDau.setOnClickListener(v -> showDatePicker(etNgayBatDau));
        etNgayKetThuc.setOnClickListener(v -> showDatePicker(etNgayKetThuc));
        
        // Tính số ngày nghỉ khi thay đổi ngày
        etNgayBatDau.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateLeaveDays();
        });
        etNgayKetThuc.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateLeaveDays();
        });
    }
    
    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, 
            (view, year1, month1, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                editText.setText(date);
                calculateLeaveDays();
            }, year, month, day);
        
        // Không cho chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
    
    private void calculateLeaveDays() {
        String startDate = etNgayBatDau.getText().toString().trim();
        String endDate = etNgayKetThuc.getText().toString().trim();
        
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                
                if (start != null && end != null) {
                    long diffInMillies = Math.abs(end.getTime() - start.getTime());
                    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
                    
                    if (end.before(start)) {
                        tvSoNgayNghi.setText("Ngày kết thúc phải sau ngày bắt đầu");
                        tvSoNgayNghi.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        tvSoNgayNghi.setText("Số ngày nghỉ: " + diffInDays + " ngày");
                        tvSoNgayNghi.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    }
                }
            } catch (ParseException e) {
                tvSoNgayNghi.setText("Định dạng ngày không hợp lệ");
                tvSoNgayNghi.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        } else {
            tvSoNgayNghi.setText("Số ngày nghỉ: 0 ngày");
            tvSoNgayNghi.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
    
    private void setupButtons() {
        btnXinNghiPhep.setOnClickListener(v -> submitLeaveRequest());
    }
    
    private void submitLeaveRequest() {
        String startDate = etNgayBatDau.getText().toString().trim();
        String endDate = etNgayKetThuc.getText().toString().trim();
        String reason = etLyDo.getText().toString().trim();
        
        if (startDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (reason.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập lý do nghỉ phép", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Tính số ngày nghỉ
        int soNgayNghi = calculateDaysBetween(startDate, endDate);
        if (soNgayNghi <= 0) {
            Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean success = dbHelper.submitLeaveRequest(maNhanVien, startDate, endDate, soNgayNghi, reason);
        
        if (success) {
            Toast.makeText(this, "Gửi đơn xin nghỉ phép thành công", Toast.LENGTH_SHORT).show();
            clearForm();
            loadLeaveHistory();
        } else {
            Toast.makeText(this, "Lỗi khi gửi đơn xin nghỉ phép", Toast.LENGTH_SHORT).show();
        }
    }
    
    private int calculateDaysBetween(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            
            if (start != null && end != null && !end.before(start)) {
                long diffInMillies = Math.abs(end.getTime() - start.getTime());
                return (int) (TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private void clearForm() {
        etNgayBatDau.setText("");
        etNgayKetThuc.setText("");
        etLyDo.setText("");
        tvSoNgayNghi.setText("Số ngày nghỉ: 0 ngày");
        tvSoNgayNghi.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }
    
    private void loadLeaveHistory() {
        Cursor cursor;
        if ("Employee".equals(currentRole)) {
            // Employee chỉ xem lịch sử của mình
            cursor = dbHelper.getLeaveHistory(maNhanVien);
        } else {
            // Admin/HR/Manager xem tất cả đơn nghỉ phép
            cursor = dbHelper.getAllLeaveRequests();
        }
        
        adapter = new NghiPhepAdapter(this, cursor, currentRole);
        lvLichSuNghiPhep.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadLeaveHistory();
    }
}