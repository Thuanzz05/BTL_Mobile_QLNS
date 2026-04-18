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
        fabAdd = findViewById(R.id.fab_add_nhan_vien);

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
}
