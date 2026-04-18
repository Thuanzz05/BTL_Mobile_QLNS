package com.example.btl_mobile_qlns;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QuanLyNhanVienActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvEmpty;
    private SearchView searchView;
    private android.widget.ImageButton btnExportPdf;
    private FloatingActionButton fabAdd;
    private DatabaseHelper dbHelper;
    private NhanVienAdapter adapter;
    private List<NhanVien> listNhanVien;

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    taiDanhSachNhanVien();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_nhan_vien);
        
        dbHelper = new DatabaseHelper(this);
        khoiTaoViews();
        taiDanhSachNhanVien();
        thietLapTimKiem();
    }
    
    private void khoiTaoViews() {
        listView = findViewById(R.id.lv_nhan_vien);
        tvEmpty = findViewById(R.id.tv_empty);
        searchView = findViewById(R.id.search_view);
        btnExportPdf = findViewById(R.id.btn_export_pdf);
        fabAdd = findViewById(R.id.fab_add_nhan_vien);

        btnExportPdf.setOnClickListener(v -> exportToPdf());
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThemNhanVienActivity.class);
            startForResult.launch(intent);
        });
    }
    
    private void taiDanhSachNhanVien() {
        Cursor cursor = dbHelper.getAllEmployees();
        hienThiDanhSach(cursor);
    }

    private void hienThiDanhSach(Cursor cursor) {
        if (listNhanVien == null) {
            listNhanVien = new ArrayList<>();
        } else {
            listNhanVien.clear();
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
                String gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"));
                String tenChucVu = cursor.getString(cursor.getColumnIndexOrThrow("TenChucVu"));
                String tenPhongBan = cursor.getString(cursor.getColumnIndexOrThrow("TenPhongBan"));
                String hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
                
                listNhanVien.add(new NhanVien(maNV, hoTen, gioiTinh, tenChucVu, tenPhongBan, hinhAnh));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (listNhanVien.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new NhanVienAdapter(this, listNhanVien, new NhanVienAdapter.OnNhanVienActionListener() {
                @Override
                public void onDelete(NhanVien nhanVien) {
                    xacNhanXoaNhanVien(nhanVien);
                }

                @Override
                public void onEdit(NhanVien nhanVien) {
                    Intent intent = new Intent(QuanLyNhanVienActivity.this, ThemNhanVienActivity.class);
                    intent.putExtra("ma_nv", nhanVien.getMaNhanVien());
                    startForResult.launch(intent);
                }
            });
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void xacNhanXoaNhanVien(NhanVien nv) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nhân viên " + nv.getHoTen() + " (" + nv.getMaNhanVien() + ")?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (dbHelper.deleteEmployee(nv.getMaNhanVien())) {
                        Toast.makeText(this, "Đã xóa nhân viên", Toast.LENGTH_SHORT).show();
                        taiDanhSachNhanVien();
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa nhân viên", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void thietLapTimKiem() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                thucHienTimKiem(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                thucHienTimKiem(newText);
                return true;
            }
        });
    }

    private void thucHienTimKiem(String keyword) {
        Cursor cursor;
        if (keyword.isEmpty()) {
            cursor = dbHelper.getAllEmployees();
        } else {
            cursor = dbHelper.searchEmployees(keyword);
        }
        hienThiDanhSach(cursor);
    }

    private void exportToPdf() {
        if (listNhanVien == null || listNhanVien.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder report = new StringBuilder();
        String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
        
        report.append("DANH SÁCH NHÂN VIÊN\n");
        report.append("Ngày xuất: ").append(dateStr).append("\n");
        report.append("=============================================\n");
        report.append(String.format("%-8s %-20s %-15s\n", "Mã NV", "Họ tên", "Phòng ban"));
        report.append("---------------------------------------------\n");
        
        for (NhanVien nv : listNhanVien) {
            String hoTen = nv.getHoTen();
            if (hoTen.length() > 18) hoTen = hoTen.substring(0, 15) + "...";
            
            String pb = nv.getTenPhongBan();
            if (pb.length() > 13) pb = pb.substring(0, 11) + "...";

            report.append(String.format("%-8s %-20s %-15s\n", 
                nv.getMaNhanVien(), 
                hoTen, 
                pb));
        }
        report.append("=============================================\n");
        report.append("Tổng số: ").append(listNhanVien.size()).append(" nhân viên\n");
        
        savePdfFile(report.toString());
    }

    private void savePdfFile(String content) {
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
        for (String line : content.split("\n")) {
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

        String fileName = "DanhSachNhanVien_" + System.currentTimeMillis() + ".pdf";
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
            Toast.makeText(this, "Đã xuất danh sách nhân viên ra PDF thành công!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xuất PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
