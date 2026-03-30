package com.example.btl_mobile_qlns;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String gioiTinh;
    private String tenChucVu;
    private String tenPhongBan;
    private String hinhAnh;

    public NhanVien(String maNhanVien, String hoTen, String gioiTinh, String tenChucVu, String tenPhongBan, String hinhAnh) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.tenChucVu = tenChucVu;
        this.tenPhongBan = tenPhongBan;
        this.hinhAnh = hinhAnh;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public String getGioiTinh() { return gioiTinh; }
    public String getTenChucVu() { return tenChucVu; }
    public String getTenPhongBan() { return tenPhongBan; }
    public String getHinhAnh() { return hinhAnh; }
}