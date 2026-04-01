package com.example.btl_mobile_qlns.models;

public class ChamCong {
    private String ngayChamCong;
    private String gioVao;
    private String gioRa;
    private double soGioLam;
    private String trangThai;

    public ChamCong(String ngayChamCong, String gioVao, String gioRa, double soGioLam, String trangThai) {
        this.ngayChamCong = ngayChamCong;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.soGioLam = soGioLam;
        this.trangThai = trangThai;
    }

    // Getters
    public String getNgayChamCong() { return ngayChamCong; }
    public String getGioVao() { return gioVao; }
    public String getGioRa() { return gioRa; }
    public double getSoGioLam() { return soGioLam; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setNgayChamCong(String ngayChamCong) { this.ngayChamCong = ngayChamCong; }
    public void setGioVao(String gioVao) { this.gioVao = gioVao; }
    public void setGioRa(String gioRa) { this.gioRa = gioRa; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}