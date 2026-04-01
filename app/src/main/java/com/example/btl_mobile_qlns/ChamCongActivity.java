package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.ChamCong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChamCongActivity extends AppCompatActivity {

    private TextView tvCurrentTime, tvCurrentDate, tvStatus;
    private Button btnChamCongVao, btnChamCongRa;
    private ListView lvLichSuChamCong;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String maNhanVien;
    private SimpleDateFormat timeFormat, dateFormat;
    private ChamCongAdapter adapter;
    private List<ChamCong> listChamCong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cham_cong);
        
        initViews();
        setupDatabase();
        setupFormats();
        updateCurrentTime();
        loadTodayStatus();
        loadAttendanceHistory();
        setupButtons();
    }
    
    private void initViews() {
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvCurrentDate = findViewById(R.id.tv_current_date);
        tvStatus = findViewById(R.id.tv_status);
        btnChamCongVao = findViewById(R.id.btn_cham_cong_vao);
        btnChamCongRa = findViewById(R.id.btn_cham_cong_ra);
        lvLichSuChamCong = findViewById(R.id.lv_lich_su_cham_cong);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername != null) {
            maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
        }
    }
    
    private void setupFormats() {
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    private void updateCurrentTime() {
        Date now = new Date();
        tvCurrentTime.setText(timeFormat.format(now));
        tvCurrentDate.setText(dateFormat.format(now));
        
        // Cập nhật thời gian mỗi giây
        tvCurrentTime.postDelayed(this::updateCurrentTime, 1000);
    }
    
    private void loadTodayStatus() {
        if (maNhanVien == null) return;
        
        String today = dateFormat.format(new Date());
        boolean[] status = dbHelper.getTodayAttendanceStatus(maNhanVien, today);
        
        boolean hasCheckedIn = status[0];
        boolean hasCheckedOut = status[1];
        
        if (!hasCheckedIn) {
            tvStatus.setText("Chưa chấm công vào");
            btnChamCongVao.setEnabled(true);
            btnChamCongRa.setEnabled(false);
        } else if (!hasCheckedOut) {
            tvStatus.setText("Đã chấm công vào - Chưa chấm công ra");
            btnChamCongVao.setEnabled(false);
            btnChamCongRa.setEnabled(true);
        } else {
            tvStatus.setText("Đã hoàn thành chấm công hôm nay");
            btnChamCongVao.setEnabled(false);
            btnChamCongRa.setEnabled(false);
        }
    }

    private void loadAttendanceHistory() {
        if (maNhanVien == null) return;

        listChamCong = new ArrayList<>();
        Cursor cursor = dbHelper.getAttendanceHistory(maNhanVien, 30); // Lấy 30 ngày gần nhất

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ngay = cursor.getString(cursor.getColumnIndexOrThrow("NgayChamCong"));
                String gioVao = cursor.getString(cursor.getColumnIndexOrThrow("GioVao"));
                String gioRa = cursor.getString(cursor.getColumnIndexOrThrow("GioRa"));
                double soGio = cursor.getDouble(cursor.getColumnIndexOrThrow("SoGioLam"));
                String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));

                listChamCong.add(new ChamCong(ngay, gioVao, gioRa, soGio, trangThai));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ChamCongAdapter(this, listChamCong);
        lvLichSuChamCong.setAdapter(adapter);
    }
    
    private void setupButtons() {
        btnChamCongVao.setOnClickListener(v -> chamCongVao());
        btnChamCongRa.setOnClickListener(v -> chamCongRa());
    }
    
    private void chamCongVao() {
        if (maNhanVien == null) {
            Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Date now = new Date();
        String today = dateFormat.format(now);
        String currentTime = timeFormat.format(now);
        
        boolean success = dbHelper.chamCongVao(maNhanVien, today, currentTime);
        
        if (success) {
            Toast.makeText(this, "Chấm công vào thành công: " + currentTime, Toast.LENGTH_SHORT).show();
            loadTodayStatus();
            loadAttendanceHistory(); // Refresh lịch sử
        } else {
            Toast.makeText(this, "Lỗi khi chấm công vào", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void chamCongRa() {
        if (maNhanVien == null) {
            Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Date now = new Date();
        String today = dateFormat.format(now);
        String currentTime = timeFormat.format(now);
        
        boolean success = dbHelper.chamCongRa(maNhanVien, today, currentTime);
        
        if (success) {
            Toast.makeText(this, "Chấm công ra thành công: " + currentTime, Toast.LENGTH_SHORT).show();
            loadTodayStatus();
            loadAttendanceHistory(); // Refresh lịch sử
        } else {
            Toast.makeText(this, "Lỗi khi chấm công ra", Toast.LENGTH_SHORT).show();
        }
    }
}