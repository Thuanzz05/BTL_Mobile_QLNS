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
import com.example.btl_mobile_qlns.models.PhongBan;

import java.util.List;

public class PhongBanAdapter extends BaseAdapter {
    
    private Context context;
    private List<PhongBan> listPhongBan;
    private String currentRole;
    private DatabaseHelper dbHelper;
    
    public PhongBanAdapter(Context context, List<PhongBan> listPhongBan, String currentRole) {
        this.context = context;
        this.listPhongBan = listPhongBan;
        this.currentRole = currentRole;
        this.dbHelper = new DatabaseHelper(context);
    }
    
    @Override
    public int getCount() {
        return listPhongBan != null ? listPhongBan.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return listPhongBan.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_phong_ban, parent, false);
        }
        
        PhongBan phongBan = listPhongBan.get(position);
        
        TextView tvMaPhongBan = convertView.findViewById(R.id.tv_ma_phong_ban);
        TextView tvTenPhongBan = convertView.findViewById(R.id.tv_ten_phong_ban);
        TextView tvTruongPhong = convertView.findViewById(R.id.tv_truong_phong);
        TextView tvSoNhanVien = convertView.findViewById(R.id.tv_so_nhan_vien);
        TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
        LinearLayout layoutButtons = convertView.findViewById(R.id.layout_buttons);
        Button btnSua = convertView.findViewById(R.id.btn_sua);
        Button btnXoa = convertView.findViewById(R.id.btn_xoa);
        
        // Hiển thị thông tin phòng ban
        tvMaPhongBan.setText("Mã: " + phongBan.getMaPhongBan());
        tvTenPhongBan.setText(phongBan.getTenPhongBan());
        
        String truongPhongText = "Trưởng phòng: ";
        if (phongBan.getTenTruongPhong() != null && !phongBan.getTenTruongPhong().isEmpty()) {
            truongPhongText += phongBan.getTenTruongPhong();
        } else {
            truongPhongText += "Chưa có";
        }
        tvTruongPhong.setText(truongPhongText);
        
        tvSoNhanVien.setText("Số nhân viên: " + phongBan.getSoNhanVien());
        
        // Hiển thị trạng thái
        String trangThaiText = phongBan.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động";
        tvTrangThai.setText("Trạng thái: " + trangThaiText);
        
        // Thiết lập màu sắc cho trạng thái
        if (phongBan.getTrangThai() == 1) {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        
        // Hiển thị nút sửa/xóa cho Admin và HR
        if ("Admin".equals(currentRole) || "HR".equals(currentRole)) {
            layoutButtons.setVisibility(View.VISIBLE);
            
            btnSua.setOnClickListener(v -> editDepartment(phongBan));
            btnXoa.setOnClickListener(v -> showDeleteDialog(phongBan, position));
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    private void editDepartment(PhongBan phongBan) {
        Intent intent = new Intent(context, ThemPhongBanActivity.class);
        intent.putExtra("role", currentRole);
        intent.putExtra("edit_mode", true);
        intent.putExtra("ma_phong_ban", phongBan.getMaPhongBan());
        intent.putExtra("ten_phong_ban", phongBan.getTenPhongBan());
        intent.putExtra("truong_phong", phongBan.getTruongPhong());
        intent.putExtra("trang_thai", phongBan.getTrangThai());
        
        if (context instanceof QuanLyPhongBanActivity) {
            ((QuanLyPhongBanActivity) context).startActivityForResult(intent, 1001);
        }
    }
    
    private void showDeleteDialog(PhongBan phongBan, int position) {
        new AlertDialog.Builder(context)
            .setTitle("Xóa phòng ban")
            .setMessage("Bạn có chắc muốn xóa phòng ban \"" + phongBan.getTenPhongBan() + "\"?\n\n" +
                       "Lưu ý: Phòng ban sẽ được đánh dấu là ngừng hoạt động thay vì xóa hoàn toàn.")
            .setPositiveButton("Xóa", (dialog, which) -> {
                boolean success = dbHelper.deleteDepartment(phongBan.getMaPhongBan());
                if (success) {
                    Toast.makeText(context, "Đã xóa phòng ban thành công", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(context, "Lỗi khi xóa phòng ban", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void refreshData() {
        if (context instanceof QuanLyPhongBanActivity) {
            ((QuanLyPhongBanActivity) context).loadDepartments();
        }
    }
    
    public void updateData(List<PhongBan> newData) {
        this.listPhongBan = newData;
        notifyDataSetChanged();
    }
}