package com.example.btl_mobile_qlns;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.Luong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class QuanLyLuongActivity extends AppCompatActivity {

    private TextView tvTitle, tvThangHienTai;
    private Spinner spThang, spNam;
    private Button btnTinhLuong, btnXemLuong;
    private ListView lvLuong;
    
    private DatabaseHelper dbHelper;
    private LuongAdapter adapter;
    private List<Luong> listLuong;
    private String currentRole;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_luong);
        
        initViews();
        setupDatabase();
        setupUI();
        setupSpinners();
        setupButtons();
        loadCurrentMonthSalary();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvThangHienTai = findViewById(R.id.tv_thang_hien_tai);
        spThang = findViewById(R.id.sp_thang);
        spNam = findViewById(R.id.sp_nam);
        btnTinhLuong = findViewById(R.id.btn_tinh_luong);
        btnXemLuong = findViewById(R.id.btn_xem_luong);
        lvLuong = findViewById(R.id.lv_luong);
    }
    
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        currentRole = getIntent().getStringExtra("role");
        currentUsername = getIntent().getStringExtra("username");
    }
    
    private void setupUI() {
        tvTitle.setText("QUẢN LÝ LƯƠNG");
        
        // Hiển thị tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvThangHienTai.setText("Tháng hiện tại: " + sdf.format(calendar.getTime()));
        
        // Chỉ Admin và HR mới có thể tính lương
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            btnTinhLuong.setVisibility(View.VISIBLE);
        } else {
            btnTinhLuong.setVisibility(View.GONE);
        }
    }
    
    private void setupSpinners() {
        // Setup spinner tháng
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(monthAdapter);
        
        // Setup spinner năm
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 1; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNam.setAdapter(yearAdapter);
        
        // Set tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        spThang.setSelection(calendar.get(Calendar.MONTH));
        spNam.setSelection(2); // Current year (index 2 in the list)
        
        // Auto load khi thay đổi spinner
        spThang.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadSalaryByMonth();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        spNam.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadSalaryByMonth();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    private void setupButtons() {
        btnTinhLuong.setOnClickListener(v -> showCalculateSalaryDialog());
        btnXemLuong.setOnClickListener(v -> exportSalaryReport());
    }
    
    private void showCalculateSalaryDialog() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        
        // Đảm bảo định dạng tháng đúng
        if (thang.length() == 1) {
            thang = "0" + thang;
        }
        
        String thangNam = nam + "-" + thang;
        
        // Validation: Không cho tính lương tháng tương lai
        Calendar current = Calendar.getInstance();
        Calendar selected = Calendar.getInstance();
        selected.set(Integer.parseInt(nam), Integer.parseInt(thang) - 1, 1);
        
        if (selected.after(current)) {
            Toast.makeText(this, "Không thể tính lương cho tháng tương lai!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Kiểm tra có dữ liệu chấm công không
        if (!hasAttendanceData(thangNam)) {
            Toast.makeText(this, "Chưa có dữ liệu chấm công cho tháng " + thang + "/" + nam, Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Tính lương tháng " + thang + "/" + nam)
            .setMessage("Bạn có chắc muốn tính lương cho tất cả nhân viên trong tháng này?\n\n" +
                       "Lưu ý: Nếu đã có dữ liệu lương tháng này, hệ thống sẽ cập nhật lại.")
            .setPositiveButton("Tính lương", (dialog, which) -> calculateSalary(thangNam))
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private boolean hasAttendanceData(String thangNam) {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM ChamCong WHERE strftime('%Y-%m', NgayChamCong) = ? AND SoGioLam > 0",
                new String[]{thangNam}
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }
    
    private void calculateSalary(String thangNam) {
        try {
            int count = dbHelper.calculateMonthlySalary(thangNam);
            if (count > 0) {
                Toast.makeText(this, "Đã tính lương cho " + count + " nhân viên", Toast.LENGTH_SHORT).show();
                loadSalaryByMonth();
            } else {
                Toast.makeText(this, "Không có nhân viên nào để tính lương", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tính lương: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadCurrentMonthSalary() {
        Calendar calendar = Calendar.getInstance();
        String thang = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String nam = String.valueOf(calendar.get(Calendar.YEAR));
        String thangNam = nam + "-" + thang;
        
        // Auto load dữ liệu khi mở app
        loadSalary(thangNam);
    }
    
    private void loadSalaryByMonth() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        
        // Đảm bảo định dạng tháng đúng (01, 02, ..., 12)
        if (thang.length() == 1) {
            thang = "0" + thang;
        }
        
        String thangNam = nam + "-" + thang;
        
        // Debug log
        android.util.Log.d("QuanLyLuong", "Xem lương cho tháng: " + thangNam);
        Toast.makeText(this, "Đang tải lương tháng " + thang + "/" + nam, Toast.LENGTH_SHORT).show();
        
        loadSalary(thangNam);
    }
    
    private void loadSalary(String thangNam) {
        try {
            listLuong = new ArrayList<>();
            Cursor cursor;
            
            // Debug log
            android.util.Log.d("QuanLyLuong", "Loading salary for: " + thangNam + ", Role: " + currentRole);
            
            if ("Employee".equals(currentRole)) {
                // Employee chỉ xem lương của mình
                String maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
                android.util.Log.d("QuanLyLuong", "Employee ID: " + maNhanVien);
                cursor = dbHelper.getSalaryByEmployee(maNhanVien, thangNam);
            } else {
                // Admin/HR xem tất cả
                cursor = dbHelper.getSalaryByMonth(thangNam);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
                android.util.Log.d("QuanLyLuong", "Found " + cursor.getCount() + " salary records");
                do {
                    int maLuong = cursor.getInt(cursor.getColumnIndexOrThrow("MaLuong"));
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    String thang = cursor.getString(cursor.getColumnIndexOrThrow("ThangNam"));
                    double luongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("LuongCoBan"));
                    double phuCap = cursor.getDouble(cursor.getColumnIndexOrThrow("PhuCap"));
                    double soGioLam = cursor.getDouble(cursor.getColumnIndexOrThrow("SoGioLam"));
                    double tongLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("TongLuong"));
                    String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));
                    String ngayTinhLuong = cursor.getString(cursor.getColumnIndexOrThrow("NgayTinhLuong"));
                    
                    Luong luong = new Luong(maLuong, maNV, thang, luongCoBan, phuCap, soGioLam, tongLuong, trangThai);
                    luong.setNgayTinhLuong(ngayTinhLuong);
                    
                    // Tính toán thông tin chi tiết
                    DatabaseHelper.AttendanceStats stats = dbHelper.getAttendanceStatsForSalary(maNV, thang);
                    luong.setSoGioTangCa(stats.soGioTangCa);
                    luong.setSoNgayLam(stats.soNgayLam);
                    
                    // Lấy lương cơ bản gốc từ chức vụ để tính lương tăng ca
                    double luongCoBanGoc = getLuongCoBanGocByMaNV(maNV);
                    double luongGio = luongCoBanGoc / 208.0; // 26 ngày × 8 giờ
                    double luongTangCa = stats.soGioTangCa * luongGio * 1.5;
                    luong.setLuongTangCa(luongTangCa);
                    
                    // Lấy tên nhân viên
                    if (!"Employee".equals(currentRole)) {
                        String hoTen = dbHelper.getEmployeeNameByMa(maNV);
                        luong.setHoTen(hoTen);
                    }
                    
                    listLuong.add(luong);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                android.util.Log.d("QuanLyLuong", "No salary records found for: " + thangNam);
                Toast.makeText(this, "Không tìm thấy dữ liệu lương cho tháng " + thangNam.substring(5) + "/" + thangNam.substring(0, 4), Toast.LENGTH_SHORT).show();
            }
            
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải dữ liệu lương: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateUI() {
        if (adapter == null) {
            adapter = new LuongAdapter(this, listLuong, currentRole);
            lvLuong.setAdapter(adapter);
        } else {
            adapter.updateData(listLuong);
        }
        
        if (listLuong.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu lương cho tháng này", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void exportSalaryReport() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        
        // Đảm bảo định dạng tháng đúng
        if (thang.length() == 1) {
            thang = "0" + thang;
        }
        
        String thangNam = nam + "-" + thang;
        
        // Tạo báo cáo chi tiết
        StringBuilder report = new StringBuilder();
        report.append("📊 BÁO CÁO LƯƠNG THÁNG ").append(thang).append("/").append(nam).append("\n");
        report.append("═══════════════════════════════════════\n\n");
        
        try {
            Cursor cursor;
            if ("Employee".equals(currentRole)) {
                String maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
                cursor = dbHelper.getSalaryByEmployee(maNhanVien, thangNam);
            } else {
                cursor = dbHelper.getSalaryByMonth(thangNam);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
                double tongLuongCongTy = 0;
                int soNhanVien = 0;
                
                do {
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    double luongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("LuongCoBan"));
                    double phuCap = cursor.getDouble(cursor.getColumnIndexOrThrow("PhuCap"));
                    double tongLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("TongLuong"));
                    String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));
                    
                    // Lấy thông tin chi tiết
                    DatabaseHelper.AttendanceStats stats = dbHelper.getAttendanceStatsForSalary(maNV, thangNam);
                    String hoTen = dbHelper.getEmployeeNameByMa(maNV);
                    
                    // Tính lương tăng ca
                    double luongCoBanGoc = getLuongCoBanGocByMaNV(maNV);
                    double luongGio = luongCoBanGoc / 208.0;
                    double luongTangCa = stats.soGioTangCa * luongGio * 1.5;
                    
                    report.append("👤 ").append(hoTen != null ? hoTen : "N/A").append(" (").append(maNV).append(")\n");
                    report.append("   • Số ngày làm: ").append(stats.soNgayLam).append(" ngày\n");
                    report.append("   • Tổng giờ làm: ").append(String.format("%.1f", stats.soGioLam)).append(" giờ\n");
                    report.append("   • Giờ tăng ca: ").append(String.format("%.1f", stats.soGioTangCa)).append(" giờ\n");
                    report.append("   • Lương cơ bản: ").append(formatCurrency(luongCoBan)).append("\n");
                    report.append("   • Phụ cấp: ").append(formatCurrency(phuCap)).append("\n");
                    report.append("   • Lương tăng ca: ").append(formatCurrency(luongTangCa)).append("\n");
                    report.append("   • Tổng lương: ").append(formatCurrency(tongLuong)).append("\n");
                    report.append("   • Trạng thái: ").append(trangThai).append("\n");
                    report.append("───────────────────────────────────────\n");
                    
                    tongLuongCongTy += tongLuong;
                    soNhanVien++;
                    
                } while (cursor.moveToNext());
                cursor.close();
                
                // Thống kê tổng kết
                report.append("\n📈 THỐNG KÊ TỔNG KẾT:\n");
                report.append("   • Số nhân viên: ").append(soNhanVien).append("\n");
                report.append("   • Tổng lương công ty: ").append(formatCurrency(tongLuongCongTy)).append("\n");
                report.append("   • Lương trung bình: ").append(formatCurrency(tongLuongCongTy / soNhanVien)).append("\n");
                
            } else {
                report.append("❌ Không có dữ liệu lương cho tháng này!\n");
                report.append("Vui lòng tính lương trước khi xuất báo cáo.\n");
            }
            
        } catch (Exception e) {
            report.append("❌ Lỗi khi tạo báo cáo: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n📅 Báo cáo được tạo lúc: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        
        // Hiển thị báo cáo trong dialog
        showReportDialog(report.toString(), thang, nam);
    }
    
    private void showReportDialog(String report, String thang, String nam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📊 Báo cáo lương " + thang + "/" + nam);
        
        // Tạo ScrollView cho nội dung dài
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(report);
        textView.setTextSize(12);
        textView.setTypeface(android.graphics.Typeface.MONOSPACE);
        textView.setPadding(20, 20, 20, 20);
        textView.setTextIsSelectable(true); // Cho phép copy text
        
        scrollView.addView(textView);
        builder.setView(scrollView);
        
        builder.setPositiveButton("📋 Copy", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Báo cáo lương", report);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã copy báo cáo vào clipboard!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Đóng", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Điều chỉnh kích thước dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.95),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.8)
            );
        }
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f đ", amount);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSalaryByMonth();
    }
    
    private double getLuongCoBanGocByMaNV(String maNhanVien) {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT cv.MucLuongCoBan FROM NhanVien nv " +
                "LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu " +
                "WHERE nv.MaNhanVien = ?",
                new String[]{maNhanVien}
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }
}