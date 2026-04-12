package com.example.btl_mobile_qlns;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_mobile_qlns.database.DatabaseHelper;
import com.example.btl_mobile_qlns.models.ChamCong;

import java.util.Calendar;
import java.util.List;

public class ChamCongAdapter extends BaseAdapter {
    
    private Context context;
    private List<ChamCong> listChamCong;
    private String currentRole;
    private DatabaseHelper dbHelper;
    
    public ChamCongAdapter(Context context, List<ChamCong> listChamCong, String currentRole) {
        this.context = context;
        this.listChamCong = listChamCong;
        this.currentRole = currentRole;
        this.dbHelper = new DatabaseHelper(context);
    }
    
    // Constructor cũ để tương thích
    public ChamCongAdapter(Context context, List<ChamCong> listChamCong) {
        this.context = context;
        this.listChamCong = listChamCong;
        this.currentRole = "Employee";
        this.dbHelper = new DatabaseHelper(context);
    }
    
    @Override
    public int getCount() {
        return listChamCong != null ? listChamCong.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return listChamCong.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cham_cong, parent, false);
        }
        
        ChamCong chamCong = listChamCong.get(position);
        
        TextView tvMaNV = convertView.findViewById(R.id.tv_ma_nv);
        TextView tvHoTen = convertView.findViewById(R.id.tv_ho_ten);
        TextView tvNgay = convertView.findViewById(R.id.tv_ngay);
        TextView tvGioVao = convertView.findViewById(R.id.tv_gio_vao);
        TextView tvGioRa = convertView.findViewById(R.id.tv_gio_ra);
        TextView tvSoGio = convertView.findViewById(R.id.tv_so_gio);
        TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);
        TextView tvGhiChu = convertView.findViewById(R.id.tv_ghi_chu);
        LinearLayout layoutButtons = convertView.findViewById(R.id.layout_buttons);
        Button btnSua = convertView.findViewById(R.id.btn_sua);
        Button btnXoa = convertView.findViewById(R.id.btn_xoa);
        
        // Hiển thị thông tin nhân viên nếu không phải Employee
        if (!"Employee".equals(currentRole) && chamCong.getMaNhanVien() != null) {
            tvMaNV.setText("Mã NV: " + chamCong.getMaNhanVien());
            tvHoTen.setText("Họ tên: " + (chamCong.getHoTen() != null ? chamCong.getHoTen() : "N/A"));
            tvMaNV.setVisibility(View.VISIBLE);
            tvHoTen.setVisibility(View.VISIBLE);
        } else {
            tvMaNV.setVisibility(View.GONE);
            tvHoTen.setVisibility(View.GONE);
        }
        
        tvNgay.setText("Ngày: " + chamCong.getNgayChamCong());
        tvGioVao.setText("Giờ vào: " + (chamCong.getGioVao() != null ? chamCong.getGioVao() : "Chưa chấm"));
        tvGioRa.setText("Giờ ra: " + (chamCong.getGioRa() != null ? chamCong.getGioRa() : "Chưa chấm"));
        
        // Tính giờ tăng ca
        double soGioLam = chamCong.getSoGioLam();
        double gioTangCa = dbHelper.tinhGioTangCa(soGioLam);
        
        if (gioTangCa > 0) {
            tvSoGio.setText(String.format("Số giờ: %.1f (Tăng ca: %.1f)", soGioLam, gioTangCa));
        } else {
            tvSoGio.setText("Số giờ: " + String.format("%.1f", soGioLam));
        }
        
        tvTrangThai.setText("Trạng thái: " + chamCong.getTrangThai());
        
        // Hiển thị ghi chú nếu có
        if (chamCong.getGhiChu() != null && !chamCong.getGhiChu().trim().isEmpty()) {
            tvGhiChu.setText("Ghi chú: " + chamCong.getGhiChu());
            tvGhiChu.setVisibility(View.VISIBLE);
        } else {
            tvGhiChu.setVisibility(View.GONE);
        }
        
        // Thiết lập màu sắc cho trạng thái
        switch (chamCong.getTrangThai()) {
            case "Có mặt":
                tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Vắng mặt":
                tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
        
        // Hiển thị nút sửa/xóa cho Admin/HR/Manager
        if (("Admin".equals(currentRole) || "HR".equals(currentRole) || "Manager".equals(currentRole)) 
            && chamCong.getMaNhanVien() != null) {
            layoutButtons.setVisibility(View.VISIBLE);
            
            btnSua.setOnClickListener(v -> showEditDialog(chamCong, position));
            btnXoa.setOnClickListener(v -> showDeleteDialog(chamCong, position));
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    private void showEditDialog(ChamCong chamCong, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_cham_cong, null);
        
        EditText etGioVao = dialogView.findViewById(R.id.et_gio_vao);
        EditText etGioRa = dialogView.findViewById(R.id.et_gio_ra);
        EditText etGhiChu = dialogView.findViewById(R.id.et_ghi_chu);
        
        etGioVao.setText(chamCong.getGioVao());
        etGioRa.setText(chamCong.getGioRa());
        etGhiChu.setText(chamCong.getGhiChu() != null ? chamCong.getGhiChu() : "");
        
        // Time picker cho giờ vào
        etGioVao.setOnClickListener(v -> showTimePicker(etGioVao));
        etGioRa.setOnClickListener(v -> showTimePicker(etGioRa));
        
        builder.setView(dialogView)
               .setTitle("Sửa chấm công - " + chamCong.getNgayChamCong())
               .setPositiveButton("Lưu", (dialog, which) -> {
                   String gioVao = etGioVao.getText().toString().trim();
                   String gioRa = etGioRa.getText().toString().trim();
                   String ghiChu = etGhiChu.getText().toString().trim();
                   
                   boolean success = dbHelper.updateAttendance(chamCong.getMaNhanVien(), 
                                                             chamCong.getNgayChamCong(), 
                                                             gioVao, gioRa, ghiChu);
                   if (success) {
                       Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                       refreshData();
                   } else {
                       Toast.makeText(context, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Hủy", null)
               .show();
    }
    
    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
            (view, hourOfDay, minute1) -> {
                String time = String.format("%02d:%02d:00", hourOfDay, minute1);
                editText.setText(time);
            }, hour, minute, true);
        timePickerDialog.show();
    }
    
    private void showDeleteDialog(ChamCong chamCong, int position) {
        new AlertDialog.Builder(context)
            .setTitle("Xóa dữ liệu chấm công")
            .setMessage("Bạn có chắc muốn xóa dữ liệu chấm công ngày " + chamCong.getNgayChamCong() + "?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                boolean success = dbHelper.deleteAttendance(chamCong.getMaNhanVien(), 
                                                          chamCong.getNgayChamCong());
                if (success) {
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(context, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void refreshData() {
        if (context instanceof ChamCongActivity) {
            ((ChamCongActivity) context).loadAttendanceHistory();
        }
    }
    
    public void updateData(List<ChamCong> newData) {
        this.listChamCong = newData;
        notifyDataSetChanged();
    }
}