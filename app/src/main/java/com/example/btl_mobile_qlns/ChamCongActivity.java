package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.ChamCong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChamCongActivity extends AppCompatActivity {

    private TextView tvCurrentTime, tvCurrentDate, tvStatus, tvTitle;
    private Button btnChamCongVao, btnChamCongRa;
    private ListView lvLichSuChamCong;
    private Spinner spNhanVien;
    private View layoutChamCongCaNhan, layoutQuanLyChamCong;
    
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private String maNhanVien;
    private String currentRole;
    private SimpleDateFormat timeFormat, dateFormat;
    private ChamCongAdapter adapter;
    private List<ChamCong> listChamCong;
    private List<String> listMaNhanVien;
    private List<String> listTenNhanVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cham_cong);
        
        initViews();
        setupDatabase();
        setupFormats();
        setupUI();
        updateCurrentTime();
        loadTodayStatus();
        loadAttendanceHistory();
        setupButtons();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvCurrentDate = findViewById(R.id.tv_current_date);
        tvStatus = findViewById(R.id.tv_status);
        btnChamCongVao = findViewById(R.id.btn_cham_cong_vao);
        btnChamCongRa = findViewById(R.id.btn_cham_cong_ra);
        lvLichSuChamCong = findViewById(R.id.lv_lich_su_cham_cong);
        spNhanVien = findViewById(R.id.sp_nhan_vien);
        layoutChamCongCaNhan = findViewById(R.id.layout_cham_cong_ca_nhan);
        layoutQuanLyChamCong = findViewById(R.id.layout_quan_ly_cham_cong);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        currentRole = getIntent().getStringExtra("role");
        
        if (currentUsername != null) {
            maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
        }
    }
    
    private void setupFormats() {
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    private void setupUI() {
        // Tất cả role đều có thể chấm công cá nhân
        tvTitle.setText("CHẤM CÔNG");
        layoutChamCongCaNhan.setVisibility(View.VISIBLE);
        
        // Admin/HR/Manager có thêm chức năng quản lý
        if (!"Employee".equals(currentRole)) {
            layoutQuanLyChamCong.setVisibility(View.VISIBLE);
            setupEmployeeSpinner();
        } else {
            layoutQuanLyChamCong.setVisibility(View.GONE);
        }
    }
    
    private void setupEmployeeSpinner() {
        try {
            listMaNhanVien = new ArrayList<>();
            listTenNhanVien = new ArrayList<>();
            
            // Thêm option "Tất cả nhân viên"
            listMaNhanVien.add("ALL");
            listTenNhanVien.add("Tất cả nhân viên");
            
            Cursor cursor = dbHelper.getAllEmployees();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                    
                    if (maNV != null && hoTen != null) {
                        listMaNhanVien.add(maNV);
                        listTenNhanVien.add(maNV + " - " + hoTen);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            if (spNhanVien != null && listTenNhanVien != null && !listTenNhanVien.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_spinner_item, listTenNhanVien);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spNhanVien.setAdapter(adapter);
                
                // Listener để load dữ liệu khi chọn nhân viên
                spNhanVien.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (listMaNhanVien != null && position >= 0 && position < listMaNhanVien.size()) {
                                loadAttendanceHistory();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChamCongActivity.this, "Lỗi khi tải dữ liệu chấm công", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải danh sách nhân viên", Toast.LENGTH_SHORT).show();
        }
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

    public void loadAttendanceHistory() {
        try {
            listChamCong = new ArrayList<>();
            Cursor cursor = null;
            
            if ("Employee".equals(currentRole)) {
                // Employee: Chỉ xem lịch sử của mình
                if (maNhanVien == null) return;
                cursor = dbHelper.getAttendanceHistory(maNhanVien, 30);
            } else {
                // Admin/HR/Manager: Xem theo nhân viên được chọn
                if (spNhanVien == null || listMaNhanVien == null || listMaNhanVien.isEmpty()) {
                    // Fallback: load tất cả nếu spinner chưa sẵn sàng
                    cursor = dbHelper.getAllAttendanceHistory(30);
                } else {
                    int selectedPosition = spNhanVien.getSelectedItemPosition();
                    if (selectedPosition == 0) {
                        // Tất cả nhân viên
                        cursor = dbHelper.getAllAttendanceHistory(30);
                    } else if (selectedPosition > 0 && selectedPosition < listMaNhanVien.size()) {
                        String selectedMaNV = listMaNhanVien.get(selectedPosition);
                        if (selectedMaNV != null && !selectedMaNV.isEmpty()) {
                            cursor = dbHelper.getAttendanceHistory(selectedMaNV, 30);
                        } else {
                            cursor = dbHelper.getAllAttendanceHistory(30);
                        }
                    } else {
                        cursor = dbHelper.getAllAttendanceHistory(30);
                    }
                }
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String maNV = null;
                        String hoTen = null;
                        
                        // Lấy thông tin nhân viên nếu không phải Employee
                        if (!"Employee".equals(currentRole)) {
                            int maNVIndex = cursor.getColumnIndex("MaNhanVien");
                            if (maNVIndex >= 0) {
                                maNV = cursor.getString(maNVIndex);
                                if (maNV != null && !maNV.isEmpty()) {
                                    hoTen = dbHelper.getEmployeeNameByMa(maNV);
                                }
                            }
                        }
                        
                        int ngayIndex = cursor.getColumnIndex("NgayChamCong");
                        int gioVaoIndex = cursor.getColumnIndex("GioVao");
                        int gioRaIndex = cursor.getColumnIndex("GioRa");
                        int soGioIndex = cursor.getColumnIndex("SoGioLam");
                        int trangThaiIndex = cursor.getColumnIndex("TrangThai");
                        int ghiChuIndex = cursor.getColumnIndex("GhiChu");
                        
                        if (ngayIndex >= 0 && gioVaoIndex >= 0 && gioRaIndex >= 0 && 
                            soGioIndex >= 0 && trangThaiIndex >= 0) {
                            
                            String ngay = cursor.getString(ngayIndex);
                            String gioVao = cursor.getString(gioVaoIndex);
                            String gioRa = cursor.getString(gioRaIndex);
                            double soGio = cursor.getDouble(soGioIndex);
                            String trangThai = cursor.getString(trangThaiIndex);
                            String ghiChu = ghiChuIndex >= 0 ? cursor.getString(ghiChuIndex) : "";

                            ChamCong chamCong = new ChamCong(ngay, gioVao, gioRa, soGio, trangThai);
                            if (maNV != null) {
                                chamCong.setMaNhanVien(maNV);
                                chamCong.setHoTen(hoTen);
                            }
                            chamCong.setGhiChu(ghiChu);
                            
                            listChamCong.add(chamCong);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Tiếp tục với record tiếp theo
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (adapter == null) {
                adapter = new ChamCongAdapter(this, listChamCong, currentRole);
                lvLichSuChamCong.setAdapter(adapter);
            } else {
                adapter.updateData(listChamCong);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải lịch sử chấm công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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