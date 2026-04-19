package com.example.btl_mobile_qlns;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class TaiKhoanAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private DatabaseHelper dbHelper;

    public TaiKhoanAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tai_khoan, parent, false);
        }

        cursor.moveToPosition(position);

        TextView tvUsername = convertView.findViewById(R.id.tv_username);
        TextView tvFullName = convertView.findViewById(R.id.tv_full_name);
        TextView tvRole = convertView.findViewById(R.id.tv_role);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);
        Button btnDelete = convertView.findViewById(R.id.btn_delete_account);
        Button btnEdit = convertView.findViewById(R.id.btn_edit_account);

        String username = cursor.getString(cursor.getColumnIndexOrThrow("TenDangNhap"));
        String fullName = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"));
        String role = cursor.getString(cursor.getColumnIndexOrThrow("VaiTro"));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"));

        tvUsername.setText(username);
        tvFullName.setText(fullName != null ? fullName : "N/A");
        tvRole.setText("Vai trò: " + role);
        
        if (status == 1) {
            tvStatus.setText("Hoạt động");
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setText("Bị khóa");
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        if ("admin".equals(username.toLowerCase())) {
            btnDelete.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
        }

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản " + username + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (dbHelper.deleteAccount(username)) {
                        Toast.makeText(context, "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                        refreshData();
                    } else {
                        Toast.makeText(context, "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        // Sự kiện Sửa tài khoản
        btnEdit.setOnClickListener(v -> {
            showEditAccountDialog(username, fullName, role);
        });

        convertView.setOnClickListener(v -> {
            if ("admin".equals(username.toLowerCase())) return;
            
            String[] options = status == 1 ? new String[]{"Khóa tài khoản"} : new String[]{"Mở khóa tài khoản"};
            new AlertDialog.Builder(context)
                .setTitle("Tùy chọn cho " + username)
                .setItems(options, (dialog, which) -> {
                    int newStatus = status == 1 ? 0 : 1;
                    if (dbHelper.updateAccountStatus(username, newStatus)) {
                        Toast.makeText(context, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                        if (context instanceof QuanLyTaiKhoanActivity) {
                            ((QuanLyTaiKhoanActivity) context).loadAccounts();
                        }
                    }
                })
                .show();
        });

        return convertView;
    }

    private void showEditAccountDialog(String username, String currentName, String currentRole) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa tài khoản: " + username);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText etFullName = new EditText(context);
        etFullName.setHint("Họ tên");
        etFullName.setText(currentName);
        layout.addView(etFullName);

        EditText etRole = new EditText(context);
        etRole.setHint("Vai trò (Admin/HR/Manager/Employee)");
        etRole.setText(currentRole);
        layout.addView(etRole);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = etFullName.getText().toString().trim();
            String newRole = etRole.getText().toString().trim();

            // Thống nhất user thành Employee
            if (newRole.equalsIgnoreCase("user")) {
                newRole = "Employee";
            }

            if (newName.isEmpty() || newRole.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateAccountInfo(username, newName, newRole)) {
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                refreshData();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void refreshData() {
        if (context instanceof QuanLyTaiKhoanActivity) {
            ((QuanLyTaiKhoanActivity) context).loadAccounts();
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
