package com.example.btl_mobile_qlns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.btl_mobile_qlns.models.ChamCong;

import java.text.DecimalFormat;
import java.util.List;

public class ChamCongAdapter extends BaseAdapter {
    private Context context;
    private List<ChamCong> listChamCong;
    private DecimalFormat decimalFormat;

    public ChamCongAdapter(Context context, List<ChamCong> listChamCong) {
        this.context = context;
        this.listChamCong = listChamCong;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    @Override
    public int getCount() {
        return listChamCong.size();
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

        TextView tvNgay = convertView.findViewById(R.id.tv_ngay_cham_cong);
        TextView tvGioVao = convertView.findViewById(R.id.tv_gio_vao);
        TextView tvGioRa = convertView.findViewById(R.id.tv_gio_ra);
        TextView tvSoGio = convertView.findViewById(R.id.tv_so_gio);
        TextView tvTrangThai = convertView.findViewById(R.id.tv_trang_thai);

        tvNgay.setText(formatDate(chamCong.getNgayChamCong()));
        tvGioVao.setText(chamCong.getGioVao() != null ? chamCong.getGioVao() : "--:--");
        tvGioRa.setText(chamCong.getGioRa() != null ? chamCong.getGioRa() : "--:--");
        tvSoGio.setText(decimalFormat.format(chamCong.getSoGioLam()) + "h");
        tvTrangThai.setText(chamCong.getTrangThai());

        // Đổi màu trạng thái
        if ("Có mặt".equals(chamCong.getTrangThai())) {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        return convertView;
    }

    private String formatDate(String date) {
        try {
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return date;
        }
    }
}