package com.example.btl_mobile_qlns;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class QuanLyHopDongActivity extends AppCompatActivity {

    private ListView lvHopDong;
    private EditText etSearch;
    private ImageButton btnAdd;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private HopDongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_hop_dong);

        initViews();
        setupEvents();
        loadData();
    }

    private void initViews() {
        lvHopDong = findViewById(R.id.lv_hop_dong);
        etSearch = findViewById(R.id.et_search_hop_dong);
        btnAdd = findViewById(R.id.btn_add_hop_dong);
        tvEmpty = findViewById(R.id.tv_empty);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupEvents() {
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyHopDongActivity.this, ThemHopDongActivity.class);
            startActivity(intent);
        });

        lvHopDong.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) adapter.getItem(position);
            String maHD = cursor.getString(cursor.getColumnIndexOrThrow("MaHopDong"));
            Intent intent = new Intent(QuanLyHopDongActivity.this, ThemHopDongActivity.class);
            intent.putExtra("MaHopDong", maHD);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHopDong(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllHopDong();
        if (cursor != null && cursor.getCount() > 0) {
            adapter = new HopDongAdapter(this, cursor, this::confirmDelete);
            lvHopDong.setAdapter(adapter);
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void confirmDelete(String maHD) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa hợp đồng " + maHD + " không?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                if (dbHelper.deleteHopDong(maHD)) {
                    android.widget.Toast.makeText(this, "Đã xóa hợp đồng", android.widget.Toast.LENGTH_SHORT).show();
                    loadData();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void searchHopDong(String query) {
        Cursor cursor = dbHelper.searchHopDong(query);
        if (adapter != null) {
            adapter.swapCursor(cursor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
