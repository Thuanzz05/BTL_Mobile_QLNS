package com.example.btl_mobile_qlns;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

import java.text.NumberFormat;
import java.util.Locale;

public class HopDongAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private OnHopDongActionListener listener;

    public interface OnHopDongActionListener {
        void onDelete(String maHD);
    }

    public HopDongAdapter(Context context, Cursor cursor, OnHopDongActionListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hop_dong, parent, false);
        }

        if (cursor != null && cursor.moveToPosition(position)) {
            TextView tvMaHD = convertView.findViewById(R.id.tv_ma_hd);
            TextView tvTenNV = convertView.findViewById(R.id.tv_ten_nv);
            TextView tvLoaiHD = convertView.findViewById(R.id.tv_loai_hd);
            TextView tvThoiGian = convertView.findViewById(R.id.tv_thoi_gian);
            TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
            TextView tvMucLuong = convertView.findViewById(R.id.tv_muc_luong);
            android.widget.ImageButton btnDelete = convertView.findViewById(R.id.btn_delete_hd);

            String maHD = cursor.getString(cursor.getColumnIndexOrThrow("MaHopDong"));
            String maNV = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
            String loaiHD = cursor.getString(cursor.getColumnIndexOrThrow("LoaiHopDong"));
            String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("NgayBatDau"));
            String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("NgayKetThuc"));
            double mucLuong = cursor.getDouble(cursor.getColumnIndexOrThrow("MucLuong"));
            String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(maHD);
                }
            });

            // Lấy tên nhân viên
            String tenNV = dbHelper.getEmployeeNameByMa(maNV);

            tvMaHD.setText("Mã HD: " + maHD);
            tvTenNV.setText("Nhân viên: " + (tenNV != null ? tenNV : maNV));
            tvLoaiHD.setText("Loại: " + loaiHD);
            
            String thoiGian = "Từ " + ngayBD + (ngayKT != null && !ngayKT.isEmpty() ? " đến " + ngayKT : " - Không thời hạn");
            tvThoiGian.setText(thoiGian);
            
            tvTrangThai.setText(trangThai);
            
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvMucLuong.setText("Mức lương: " + formatter.format(mucLuong));
        }

        return convertView;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
