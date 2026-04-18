package com.example.btl_mobile_qlns;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.ChucVu;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ChucVuAdapter extends BaseAdapter {
    
    private Context context;
    private List<ChucVu> listChucVu;
    private String currentRole;
    private DatabaseHelper dbHelper;
    private NumberFormat currencyFormat;
    
    public ChucVuAdapter(Context context, List<ChucVu> listChucVu, String currentRole) {
        this.context = context;
        this.listChucVu = listChucVu;
        this.currentRole = currentRole;
        this.dbHelper = new DatabaseHelper(context);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }
    
    @Override
    public int getCount() {
        return listChucVu != null ? listChucVu.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return listChucVu.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chuc_vu, parent, false);
        }
        
        ChucVu chucVu = listChucVu.get(position);
        
        TextView tvMaChucVu = convertView.findViewById(R.id.tv_ma_chuc_vu);
        TextView tvTenChucVu = convertView.findViewById(R.id.tv_ten_chuc_vu);
        TextView tvMucLuong = convertView.findViewById(R.id.tv_muc_luong);
        TextView tvSoNhanVien = convertView.findViewById(R.id.tv_so_nhan_vien);
        TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
        LinearLayout layoutButtons = convertView.findViewById(R.id.layout_buttons);
        Button btnSua = convertView.findViewById(R.id.btn_sua);
        Button btnXoa = convertView.findViewById(R.id.btn_xoa);
        
        // Hiển thị thông tin chức vụ
        tvMaChucVu.setText("Mã: " + chucVu.getMaChucVu());
        tvTenChucVu.setText(chucVu.getTenChucVu());
        tvMucLuong.setText("Mức lương cơ bản: " + currencyFormat.format(chucVu.getMucLuongCoBan()));
        tvSoNhanVien.setText("Số nhân viên: " + chucVu.getSoNhanVien());
        
        // Hiển thị trạng thái
        String trangThaiText = chucVu.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động";
        tvTrangThai.setText("Trạng thái: " + trangThaiText);
        
        // Thiết lập màu sắc cho trạng thái
        if (chucVu.getTrangThai() == 1) {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        
        // Hiển thị nút sửa/xóa cho Admin và HR
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            layoutButtons.setVisibility(View.VISIBLE);
            
            btnSua.setOnClickListener(v -> editPosition(chucVu));
            btnXoa.setOnClickListener(v -> showDeleteDialog(chucVu, position));
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    private void editPosition(ChucVu chucVu) {
        Intent intent = new Intent(context, ThemChucVuActivity.class);
        intent.putExtra("role", currentRole);
        intent.putExtra("edit_mode", true);
        intent.putExtra("ma_chuc_vu", chucVu.getMaChucVu());
        intent.putExtra("ten_chuc_vu", chucVu.getTenChucVu());
        intent.putExtra("muc_luong_co_ban", chucVu.getMucLuongCoBan());
        intent.putExtra("trang_thai", chucVu.getTrangThai());
        
        if (context instanceof QuanLyChucVuActivity) {
            ((QuanLyChucVuActivity) context).startActivityForResult(intent, 1002);
        }
    }
    
    private void showDeleteDialog(ChucVu chucVu, int position) {
        // Kiểm tra ràng buộc: Nếu còn nhân viên thì không cho xóa
        if (chucVu.getSoNhanVien() > 0) {
            new AlertDialog.Builder(context)
                .setTitle("Không thể xóa")
                .setMessage("Chức vụ \"" + chucVu.getTenChucVu() + "\" hiện đang có " + 
                           chucVu.getSoNhanVien() + " nhân viên đang đảm nhiệm.\n\n" +
                           "Vui lòng thay đổi chức vụ cho các nhân viên này trước khi thực hiện xóa.")
                .setPositiveButton("Đã hiểu", null)
                .show();
            return;
        }

        new AlertDialog.Builder(context)
            .setTitle("Xóa chức vụ")
            .setMessage("Bạn có chắc muốn xóa chức vụ \"" + chucVu.getTenChucVu() + "\"?\n\n" +
                       "Lưu ý: Chức vụ sẽ được đánh dấu là ngừng hoạt động thay vì xóa hoàn toàn.")
            .setPositiveButton("Xóa", (dialog, which) -> {
                boolean success = dbHelper.deletePosition(chucVu.getMaChucVu());
                if (success) {
                    Toast.makeText(context, "Đã xóa chức vụ thành công", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(context, "Lỗi khi xóa chức vụ", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void refreshData() {
        if (context instanceof QuanLyChucVuActivity) {
            ((QuanLyChucVuActivity) context).loadPositions();
        }
    }
    
    public void updateData(List<ChucVu> newData) {
        this.listChucVu = newData;
        notifyDataSetChanged();
    }
}