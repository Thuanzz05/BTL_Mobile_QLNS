package com.example.btl_mobile_qlns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhanVienAdapter extends BaseAdapter {
    private Context context;
    private List<NhanVien> listNhanVien;
    private List<NhanVien> listNhanVienFull;
    private OnNhanVienActionListener listener;

    public interface OnNhanVienActionListener {
        void onDelete(NhanVien nhanVien);
        void onEdit(NhanVien nhanVien);
    }

    public NhanVienAdapter(Context context, List<NhanVien> listNhanVien, OnNhanVienActionListener listener) {
        this.context = context;
        this.listNhanVien = listNhanVien;
        this.listNhanVienFull = new ArrayList<>(listNhanVien);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listNhanVien.size();
    }

    @Override
    public Object getItem(int position) {
        return listNhanVien.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nhan_vien, parent, false);
        }

        NhanVien nv = listNhanVien.get(position);

        TextView tvTen = convertView.findViewById(R.id.tv_ten_nv);
        TextView tvMa = convertView.findViewById(R.id.tv_ma_nv);
        TextView tvChucVuPB = convertView.findViewById(R.id.tv_chuc_vu_phong_ban);
        ImageButton btnDelete = convertView.findViewById(R.id.btn_delete_nv);

        tvTen.setText(nv.getHoTen());
        tvMa.setText("Mã: " + nv.getMaNhanVien());
        tvChucVuPB.setText(nv.getTenChucVu() + " - " + nv.getTenPhongBan());

        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(nv);
            }
        });

        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(nv);
            }
        });

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        listNhanVien.clear();
        if (charText.length() == 0) {
            listNhanVien.addAll(listNhanVienFull);
        } else {
            for (NhanVien nv : listNhanVienFull) {
                if (nv.getHoTen().toLowerCase(Locale.getDefault()).contains(charText) ||
                    nv.getMaNhanVien().toLowerCase(Locale.getDefault()).contains(charText)) {
                    listNhanVien.add(nv);
                }
            }
        }
        notifyDataSetChanged();
    }
}