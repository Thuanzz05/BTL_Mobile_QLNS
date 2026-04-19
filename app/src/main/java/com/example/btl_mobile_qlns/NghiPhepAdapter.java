package com.example.btl_mobile_qlns;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.btl_mobile_qlns.database.DatabaseHelper;

public class NghiPhepAdapter extends BaseAdapter {
    
    private Context context;
    private Cursor cursor;
    private String currentRole;
    private DatabaseHelper dbHelper;
    
    public NghiPhepAdapter(Context context, Cursor cursor, String currentRole) {
        this.context = context;
        this.cursor = cursor;
        this.currentRole = currentRole;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nghi_phep, parent, false);
        }
        
        if (cursor != null && cursor.moveToPosition(position)) {
            TextView tvMaNV = convertView.findViewById(R.id.tv_ma_nv);
            TextView tvHoTen = convertView.findViewById(R.id.tv_ho_ten);
            TextView tvNgayNghi = convertView.findViewById(R.id.tv_ngay_nghi);
            TextView tvSoNgay = convertView.findViewById(R.id.tv_so_ngay);
            TextView tvLyDo = convertView.findViewById(R.id.tv_ly_do);
            TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
            Button btnDuyet = convertView.findViewById(R.id.btn_duyet);
            Button btnTuChoi = convertView.findViewById(R.id.btn_tu_choi);
            ImageButton btnSua = convertView.findViewById(R.id.btn_sua_np);
            ImageButton btnXoa = convertView.findViewById(R.id.btn_xoa_np);
            
            int maNghiPhep = cursor.getInt(cursor.getColumnIndexOrThrow("MaNghiPhep"));
            String maNhanVien = cursor.getString(cursor.getColumnIndexOrThrow("MaNhanVien"));
            String ngayBatDau = cursor.getString(cursor.getColumnIndexOrThrow("NgayBatDau"));
            String ngayKetThuc = cursor.getString(cursor.getColumnIndexOrThrow("NgayKetThuc"));
            int soNgayNghi = cursor.getInt(cursor.getColumnIndexOrThrow("SoNgayNghi"));
            String lyDo = cursor.getString(cursor.getColumnIndexOrThrow("LyDo"));
            String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"));
            
            // Hiển thị thông tin cơ bản
            tvMaNV.setText("Mã NV: " + maNhanVien);
            tvNgayNghi.setText("Từ " + ngayBatDau + " đến " + ngayKetThuc);
            tvSoNgay.setText("Số ngày: " + soNgayNghi);
            tvLyDo.setText("Lý do: " + lyDo);
            tvTrangThai.setText("Trạng thái: " + trangThai);
            
            // Lấy tên nhân viên nếu không phải Employee
            if (!"Employee".equals(currentRole)) {
                String hoTen = dbHelper.getEmployeeNameByMa(maNhanVien);
                tvHoTen.setText("Họ tên: " + (hoTen != null ? hoTen : "N/A"));
                tvHoTen.setVisibility(View.VISIBLE);
            } else {
                tvHoTen.setVisibility(View.GONE);
            }
            
            // Thiết lập màu sắc cho trạng thái
            switch (trangThai) {
                case "Chờ duyệt":
                    tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "Đã duyệt":
                    tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "Từ chối":
                    tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    break;
            }
            
            // Logic cho các nút bấm
            if ("Chờ duyệt".equals(trangThai)) {
                // Sửa/Xóa đơn cho nhân viên (chính chủ) hoặc Admin
                btnSua.setVisibility(View.VISIBLE);
                btnXoa.setVisibility(View.VISIBLE);

                // Duyệt/Từ chối cho Quản lý
                if ("Admin".equals(currentRole) || "HR".equals(currentRole) || "Manager".equals(currentRole)) {
                    btnDuyet.setVisibility(View.VISIBLE);
                    btnTuChoi.setVisibility(View.VISIBLE);
                } else {
                    btnDuyet.setVisibility(View.GONE);
                    btnTuChoi.setVisibility(View.GONE);
                }
            } else {
                btnSua.setVisibility(View.GONE);
                btnXoa.setVisibility(View.GONE);
                btnDuyet.setVisibility(View.GONE);
                btnTuChoi.setVisibility(View.GONE);
            }

            // Sự kiện Xóa
            btnXoa.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                    .setTitle("Xác nhận hủy đơn")
                    .setMessage("Bạn có chắc chắn muốn hủy đơn nghỉ phép này?")
                    .setPositiveButton("Hủy đơn", (dialog, which) -> {
                        if (dbHelper.deleteLeaveRequest(maNghiPhep)) {
                            Toast.makeText(context, "Đã hủy đơn thành công", Toast.LENGTH_SHORT).show();
                            refreshData();
                        }
                    })
                    .setNegativeButton("Quay lại", null)
                    .show();
            });

            // Sự kiện Sửa (Mở Dialog nhập lại lý do)
            btnSua.setOnClickListener(v -> {
                if (context instanceof NghiPhepActivity) {
                    ((NghiPhepActivity) context).showEditLeaveDialog(maNghiPhep, ngayBatDau, ngayKetThuc, soNgayNghi, lyDo);
                }
            });

            // Sự kiện Duyệt/Từ chối
            btnDuyet.setOnClickListener(v -> {
                boolean success = dbHelper.approveLeaveRequest(maNghiPhep, "Đã duyệt");
                if (success) {
                    Toast.makeText(context, "Đã duyệt đơn nghỉ phép", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            });

            btnTuChoi.setOnClickListener(v -> {
                boolean success = dbHelper.approveLeaveRequest(maNghiPhep, "Từ chối");
                if (success) {
                    Toast.makeText(context, "Đã từ chối đơn nghỉ phép", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            });
        }
        
        return convertView;
    }
    
    private void refreshData() {
        if (context instanceof NghiPhepActivity) {
            ((NghiPhepActivity) context).refreshHistory();
        }
    }
    
    public void updateCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}