package com.example.btl_mobile_qlns;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.Luong;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LuongAdapter extends BaseAdapter {
    
    private Context context;
    private List<Luong> listLuong;
    private String currentRole;
    private DatabaseHelper dbHelper;
    private NumberFormat currencyFormat;
    
    public LuongAdapter(Context context, List<Luong> listLuong, String currentRole) {
        this.context = context;
        this.listLuong = listLuong;
        this.currentRole = currentRole;
        this.dbHelper = new DatabaseHelper(context);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }
    
    @Override
    public int getCount() {
        return listLuong != null ? listLuong.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return listLuong.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_luong, parent, false);
        }
        
        Luong luong = listLuong.get(position);
        
        TextView tvMaNV = convertView.findViewById(R.id.tv_ma_nv);
        TextView tvHoTen = convertView.findViewById(R.id.tv_ho_ten);
        TextView tvThangNam = convertView.findViewById(R.id.tv_thang_nam);
        TextView tvLuongCoBan = convertView.findViewById(R.id.tv_luong_co_ban);
        TextView tvPhuCap = convertView.findViewById(R.id.tv_phu_cap);
        TextView tvSoGioLam = convertView.findViewById(R.id.tv_so_gio_lam);
        TextView tvSoNgayLam = convertView.findViewById(R.id.tv_so_ngay_lam);
        TextView tvGioTangCa = convertView.findViewById(R.id.tv_gio_tang_ca);
        TextView tvLuongTangCa = convertView.findViewById(R.id.tv_luong_tang_ca);
        TextView tvTongLuong = convertView.findViewById(R.id.tv_tong_luong);
        TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
        LinearLayout layoutButtons = convertView.findViewById(R.id.layout_buttons);
        Button btnThanhToan = convertView.findViewById(R.id.btn_thanh_toan);
        
        // Hiển thị thông tin nhân viên
        if (!"Employee".equals(currentRole)) {
            // Admin/HR/Manager: hiển thị mã NV và họ tên
            tvMaNV.setText("Mã NV: " + luong.getMaNhanVien());
            tvHoTen.setText("Họ tên: " + (luong.getHoTen() != null ? luong.getHoTen() : "N/A"));
            tvMaNV.setVisibility(View.VISIBLE);
            tvHoTen.setVisibility(View.VISIBLE);
        } else {
            // Employee: hiển thị tên của mình
            tvMaNV.setVisibility(View.GONE);
            tvHoTen.setText(luong.getHoTen() != null ? luong.getHoTen() : "");
            tvHoTen.setVisibility(View.VISIBLE);
        }
        
        // Hiển thị thông tin lương
        tvThangNam.setText("Tháng: " + luong.getThangNam());
        tvLuongCoBan.setText("Lương cơ bản: " + currencyFormat.format(luong.getLuongCoBan()));
        tvPhuCap.setText("Phụ cấp: " + currencyFormat.format(luong.getPhuCap()));
        tvSoGioLam.setText("Số giờ làm: " + String.format("%.1f", luong.getSoGioLam()) + " giờ");
        
        // Hiển thị thông tin chi tiết
        tvSoNgayLam.setText("Số ngày: " + luong.getSoNgayLam() + " ngày");
        tvGioTangCa.setText("Giờ tăng ca: " + String.format("%.1f", luong.getSoGioTangCa()) + " giờ");
        tvLuongTangCa.setText("Lương tăng ca: " + currencyFormat.format(luong.getLuongTangCa()));
        
        tvTongLuong.setText("Tổng lương: " + currencyFormat.format(luong.getTongLuong()));
        tvTrangThai.setText("Trạng thái: " + luong.getTrangThai());
        
        // Thiết lập màu sắc cho trạng thái
        if ("Đã thanh toán".equals(luong.getTrangThai())) {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }
        
        // Hiển thị nút thanh toán cho Admin/HR
        if (("Admin".equals(currentRole) || "HR".equals(currentRole)) && 
            "Chưa thanh toán".equals(luong.getTrangThai())) {
            layoutButtons.setVisibility(View.VISIBLE);
            btnThanhToan.setOnClickListener(v -> showPaymentDialog(luong, position));
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    private void showPaymentDialog(Luong luong, int position) {
        new AlertDialog.Builder(context)
            .setTitle("Xác nhận thanh toán")
            .setMessage("Xác nhận thanh toán lương cho nhân viên " + luong.getMaNhanVien() + 
                       " tháng " + luong.getThangNam() + "?\n\n" +
                       "Số tiền: " + currencyFormat.format(luong.getTongLuong()))
            .setPositiveButton("Thanh toán", (dialog, which) -> {
                boolean success = dbHelper.updateSalaryStatus(luong.getMaLuong(), "Đã thanh toán");
                if (success) {
                    Toast.makeText(context, "Đã thanh toán lương thành công", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(context, "Lỗi khi thanh toán lương", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void refreshData() {
        if (context instanceof QuanLyLuongActivity) {
            ((QuanLyLuongActivity) context).onResume();
        }
    }
    
    public void updateData(List<Luong> newData) {
        this.listLuong = newData;
        notifyDataSetChanged();
    }
}