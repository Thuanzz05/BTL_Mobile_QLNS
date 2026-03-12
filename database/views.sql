-- Views cho hệ thống QLNS

-- View thông tin nhân viên đầy đủ
CREATE VIEW v_NhanVienDayDu AS
SELECT 
    nv.MaNhanVien,
    nv.HoTen,
    nv.NgaySinh,
    nv.GioiTinh,
    nv.CCCD,
    nv.SoDienThoai,
    nv.Email,
    nv.DiaChi,
    nv.NgayVaoLam,
    pb.TenPhongBan,
    cv.TenChucVu,
    nv.TrangThaiLamViec,
    hd.MucLuong,
    hd.PhuCap,
    hd.LoaiHopDong,
    tk.VaiTro,
    tk.TrangThai as TrangThaiTaiKhoan
FROM NhanVien nv
LEFT JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan
LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu
LEFT JOIN HopDongLaoDong hd ON nv.MaNhanVien = hd.MaNhanVien AND hd.TrangThai = 'Hiệu lực'
LEFT JOIN TaiKhoan tk ON nv.MaNhanVien = tk.MaNhanVien;

-- View báo cáo chấm công tháng
CREATE VIEW v_BaoCaoChamCongThang AS
SELECT 
    cc.MaNhanVien,
    nv.HoTen,
    pb.TenPhongBan,
    DATE_FORMAT(cc.NgayChamCong, '%Y-%m') as ThangNam,
    COUNT(*) as SoNgayLamViec,
    SUM(cc.SoGioLam) as TongGioLam,
    SUM(cc.SoGioTangCa) as TongGioTangCa,
    COUNT(CASE WHEN cc.TrangThai = 'Đi muộn' THEN 1 END) as SoLanDiMuon,
    COUNT(CASE WHEN cc.TrangThai = 'Vắng mặt' THEN 1 END) as SoNgayVangMat
FROM ChamCong cc
JOIN NhanVien nv ON cc.MaNhanVien = nv.MaNhanVien
JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan
GROUP BY cc.MaNhanVien, nv.HoTen, pb.TenPhongBan, DATE_FORMAT(cc.NgayChamCong, '%Y-%m');

-- View báo cáo lương tháng
CREATE VIEW v_BaoCaoLuongThang AS
SELECT 
    l.MaNhanVien,
    nv.HoTen,
    pb.TenPhongBan,
    cv.TenChucVu,
    l.ThangNam,
    l.LuongCoBan,
    l.PhuCap,
    l.ThuongHieuSuat,
    l.TienTangCa,
    l.KhauTru,
    l.TongLuong,
    l.TrangThai as TrangThaiThanhToan,
    l.NgayThanhToan
FROM Luong l
JOIN NhanVien nv ON l.MaNhanVien = nv.MaNhanVien
JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan
JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu;

-- View thống kê nghỉ phép
CREATE VIEW v_ThongKeNghiPhep AS
SELECT 
    np.MaNhanVien,
    nv.HoTen,
    pb.TenPhongBan,
    YEAR(np.NgayBatDau) as Nam,
    np.LoaiNghiPhep,
    COUNT(*) as SoLanNghi,
    SUM(np.SoNgayNghi) as TongSoNgayNghi,
    COUNT(CASE WHEN np.TrangThai = 'Đã duyệt' THEN 1 END) as SoLanDuocDuyet,
    COUNT(CASE WHEN np.TrangThai = 'Từ chối' THEN 1 END) as SoLanTuChoi
FROM NghiPhep np
JOIN NhanVien nv ON np.MaNhanVien = nv.MaNhanVien
JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan
GROUP BY np.MaNhanVien, nv.HoTen, pb.TenPhongBan, YEAR(np.NgayBatDau), np.LoaiNghiPhep;

-- View dashboard tổng quan
CREATE VIEW v_DashboardTongQuan AS
SELECT 
    (SELECT COUNT(*) FROM NhanVien WHERE TrangThaiLamViec = 'Đang làm việc') as TongNhanVien,
    (SELECT COUNT(*) FROM PhongBan WHERE TrangThai = 1) as TongPhongBan,
    (SELECT COUNT(*) FROM NghiPhep WHERE TrangThai = 'Chờ duyệt') as DonNghiPhepChoDuyet,
    (SELECT COUNT(*) FROM Luong WHERE TrangThai = 'Chưa thanh toán' AND ThangNam = DATE_FORMAT(CURDATE(), '%Y-%m')) as LuongChuaThanhToan,
    (SELECT AVG(TongLuong) FROM Luong WHERE ThangNam = DATE_FORMAT(CURDATE(), '%Y-%m')) as LuongTrungBinhThang,
    (SELECT SUM(TongLuong) FROM Luong WHERE ThangNam = DATE_FORMAT(CURDATE(), '%Y-%m')) as TongQuiLuongThang;

-- View nhân viên sắp hết hạn hợp đồng
CREATE VIEW v_NhanVienSapHetHanHopDong AS
SELECT 
    nv.MaNhanVien,
    nv.HoTen,
    pb.TenPhongBan,
    hd.LoaiHopDong,
    hd.NgayKetThuc,
    DATEDIFF(hd.NgayKetThuc, CURDATE()) as SoNgayConLai
FROM NhanVien nv
JOIN HopDongLaoDong hd ON nv.MaNhanVien = hd.MaNhanVien
JOIN PhongBan pb ON nv.MaPhongBan = pb.MaPhongBan
WHERE hd.TrangThai = 'Hiệu lực'
AND hd.NgayKetThuc IS NOT NULL
AND hd.NgayKetThuc BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 90 DAY)
ORDER BY hd.NgayKetThuc ASC;