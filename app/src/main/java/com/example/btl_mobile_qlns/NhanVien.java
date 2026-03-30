package com.example.btl_mobile_qlns;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String gioiTinh;
    private String tenChucVu;
    private String tenPhongBan;

    public NhanVien(String maNhanVien, String hoTen, String gioiTinh, String tenChucVu, String tenPhongBan) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.tenChucVu = tenChucVu;
        this.tenPhongBan = tenPhongBan;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public String getGioiTinh() { return gioiTinh; }
    public String getTenChucVu() { return tenChucVu; }
    public String getTenPhongBan() { return tenPhongBan; }
}