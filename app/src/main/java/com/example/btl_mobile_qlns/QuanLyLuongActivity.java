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
    private View layoutChonThang;
    
    private DatabaseHelper dbHelper;
    private LuongAdapter adapter;
    private List<Luong> listLuong;
    private String currentRole;
    private String currentUsername;
    private boolean isInitialLoad = true;

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
        // Hiển thị tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvThangHienTai.setText("Tháng hiện tại: " + sdf.format(calendar.getTime()));
        
        if ("Employee".equals(currentRole)) {
            // Employee: giao diện xem lương cá nhân
            tvTitle.setText("LƯƠNG CÁ NHÂN");
            btnTinhLuong.setVisibility(View.GONE);
            btnXemLuong.setVisibility(View.GONE);
        } else if ("Manager".equals(currentRole)) {
            // Manager: xem lương nhân viên, không tính lương
            tvTitle.setText("XEM LƯƠNG NHÂN VIÊN");
            btnTinhLuong.setVisibility(View.GONE);
            btnXemLuong.setVisibility(View.VISIBLE);
        } else {
            // Admin/HR: quản lý lương đầy đủ
            tvTitle.setText("QUẢN LÝ LƯƠNG");
            btnTinhLuong.setVisibility(View.VISIBLE);
            btnXemLuong.setVisibility(View.VISIBLE);
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
                if (!isInitialLoad) {
                    loadSalaryByMonth();
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        spNam.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (!isInitialLoad) {
                    loadSalaryByMonth();
                }
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
        
        loadSalary(thangNam);
        isInitialLoad = false;
    }
    
    private void loadSalaryByMonth() {
        String thang = spThang.getSelectedItem().toString();
        String nam = spNam.getSelectedItem().toString();
        
        // Đảm bảo định dạng tháng đúng (01, 02, ..., 12)
        if (thang.length() == 1) {
            thang = "0" + thang;
        }
        
        String thangNam = nam + "-" + thang;
        loadSalary(thangNam);
    }
    
    private void loadSalary(String thangNam) {
        try {
            listLuong = new ArrayList<>();
            Cursor cursor;
            
            if ("Employee".equals(currentRole)) {
                // Employee chỉ xem lương của mình
                String maNhanVien = dbHelper.getMaNhanVienByUsername(currentUsername);
                cursor = dbHelper.getSalaryByEmployee(maNhanVien, thangNam);
            } else {
                // Admin/HR/Manager xem tất cả
                cursor = dbHelper.getSalaryByMonth(thangNam);
            }
            
            if (cursor != null && cursor.moveToFirst()) {
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
                    
                    // Lấy tên nhân viên (luôn lấy để hiển thị cho cả Employee)
                    String hoTen = dbHelper.getEmployeeNameByMa(maNV);
                    luong.setHoTen(hoTen);
                    
                    listLuong.add(luong);
                } while (cursor.moveToNext());
                cursor.close();
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
        report.append("           CÔNG TY QUẢN LÝ NHÂN SỰ           \n");
        report.append("=============================================\n");
        report.append(String.format("          BÁO CÁO LƯƠNG THÁNG %s/%s          \n", thang, nam));
        report.append("=============================================\n\n");
        
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
                int daThanhToan = 0;
                int chuaThanhToan = 0;
                
                do {
                    String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                    double luongCoBan = cursor.getDouble(cursor.getColumnIndexOrThrow("LuongCoBan"));
                    double phuCap = cursor.getDouble(cursor.getColumnIndexOrThrow("PhuCap"));
                    double tongLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("TongLuong"));
                    String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));
                    
                    // Lấy thông tin chi tiết
                    DatabaseHelper.AttendanceStats stats = dbHelper.getAttendanceStatsForSalary(maNV, thangNam);
                    String hoTen = dbHelper.getEmployeeNameByMa(maNV);
                    if (hoTen == null) hoTen = "Không xác định";
                    
                    // Tính lương tăng ca
                    double luongCoBanGoc = getLuongCoBanGocByMaNV(maNV);
                    double luongGio = luongCoBanGoc / 208.0;
                    double luongTangCa = stats.soGioTangCa * luongGio * 1.5;
                    
                    report.append(String.format("NHÂN VIÊN: %s (%s)\n", hoTen.toUpperCase(), maNV));
                    report.append("---------------------------------------------\n");
                    report.append(String.format(" Ngày làm : %-5d | Tổng giờ : %.1f\n", stats.soNgayLam, stats.soGioLam));
                    report.append(String.format(" Tăng ca  : %-5.1f |\n", stats.soGioTangCa));
                    report.append("---------------------------------------------\n");
                    report.append(String.format(" Lương cơ bản  :%15s\n", formatCurrency(luongCoBan)));
                    report.append(String.format(" Lương tăng ca :%15s\n", formatCurrency(luongTangCa)));
                    report.append(String.format(" Phụ cấp       :%15s\n", formatCurrency(phuCap)));
                    report.append("---------------------------------------------\n");
                    report.append(String.format(" TỔNG NHẬN     :%15s\n", formatCurrency(tongLuong)));
                    report.append(String.format(" TRẠNG THÁI    : %s\n", trangThai.toUpperCase()));
                    report.append("=============================================\n\n");
                    
                    tongLuongCongTy += tongLuong;
                    soNhanVien++;
                    if ("Đã thanh toán".equals(trangThai)) daThanhToan++;
                    else chuaThanhToan++;
                    
                } while (cursor.moveToNext());
                cursor.close();
                
                // Thống kê tổng kết
                report.append("TỔNG KẾT DOANH NGHIỆP\n");
                report.append("---------------------------------------------\n");
                report.append(String.format(" Tổng NV         : %d người\n", soNhanVien));
                report.append(String.format(" Tổng chi lương  :%16s\n", formatCurrency(tongLuongCongTy)));
                if (soNhanVien > 0) {
                    report.append(String.format(" Lương trung bình:%16s\n", formatCurrency(tongLuongCongTy / soNhanVien)));
                }
                report.append(String.format(" Đã thanh toán   : %d/%d\n", daThanhToan, soNhanVien));
                report.append(String.format(" Chưa thanh toán : %d/%d\n", chuaThanhToan, soNhanVien));
                report.append("=============================================\n");
                
            } else {
                report.append("Không có dữ liệu lương cho tháng này!\n");
                report.append("Vui lòng tính lương trước khi xuất báo cáo.\n");
            }
            
        } catch (Exception e) {
            report.append("Lỗi khi tạo báo cáo: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\nNgày xuất: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
        
        // Hiển thị báo cáo trong dialog
        showReportDialog(report.toString(), thang, nam);
    }
    
    private void showReportDialog(String report, String thang, String nam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Báo cáo lương " + thang + "/" + nam);
        
        // Tạo ScrollView cho nội dung dài
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(report);
        textView.setTextSize(12);
        textView.setTypeface(android.graphics.Typeface.MONOSPACE);
        textView.setPadding(24, 24, 24, 24);
        textView.setTextIsSelectable(true);
        textView.setLineSpacing(4, 1);
        
        scrollView.addView(textView);
        builder.setView(scrollView);
        
        // Nút Copy
        builder.setPositiveButton("Copy", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Báo cáo lương", report);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã copy báo cáo vào clipboard!", Toast.LENGTH_SHORT).show();
        });
        
        // Nút Xuất PDF
        builder.setNeutralButton("Xuất PDF", (dialog, which) -> {
            exportToPdf(report, thang, nam);
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
    
    private void exportToPdf(String reportContent, String thang, String nam) {
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        int pageNumber = 1;
        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
        android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTypeface(android.graphics.Typeface.MONOSPACE);
        paint.setTextSize(12);
        paint.setColor(android.graphics.Color.BLACK);

        int x = 40, y = 50;
        for (String line : reportContent.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
            if (y > 800) {
                document.finishPage(page);
                pageNumber++;
                pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }
        document.finishPage(page);

        String fileName = "BaoCaoLuong_" + thang + "_" + nam + ".pdf";
        try {
            java.io.OutputStream fos;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(android.provider.MediaStore.Downloads.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS);
                android.net.Uri uri = getContentResolver().insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(uri);
            } else {
                java.io.File dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
                if (!dir.exists()) dir.mkdirs();
                java.io.File file = new java.io.File(dir, fileName);
                fos = new java.io.FileOutputStream(file);
            }
            document.writeTo(fos);
            document.close();
            if (fos != null) fos.close();
            Toast.makeText(this, "Đã lưu tệp PDF thành công vào thư mục Tải Xuống (Downloads)!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f đ", amount);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialLoad) {
            loadSalaryByMonth();
        }
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