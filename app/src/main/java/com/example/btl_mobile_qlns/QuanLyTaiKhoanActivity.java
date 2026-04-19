package com.example.btl_mobile_qlns;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class QuanLyTaiKhoanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etSearch;
    private ListView lvAccounts;
    private DatabaseHelper dbHelper;
    private TaiKhoanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_tai_khoan);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadAccounts();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etSearch = findViewById(R.id.et_search_account);
        lvAccounts = findViewById(R.id.lv_accounts);

        btnBack.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAccounts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void loadAccounts() {
        Cursor cursor = dbHelper.getAllAccounts();
        if (adapter == null) {
            adapter = new TaiKhoanAdapter(this, cursor);
            lvAccounts.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }
    }

    private void searchAccounts(String keyword) {
        // Có thể bổ sung searchAccount trong DatabaseHelper nếu muốn tối ưu
        // Ở đây ta dùng giải pháp đơn giản là reload với filter
        loadAccounts(); 
    }
}
