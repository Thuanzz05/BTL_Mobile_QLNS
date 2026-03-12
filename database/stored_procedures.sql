-- Stored Procedures và Functions cho hệ thống QLNS

DELIMITER //

-- Procedure tính lương tháng cho nhân viên
CREATE PROCEDURE TinhLuongThang(
    IN p_MaNhanVien VARCHAR(10),
    IN p_ThangNam VARCHAR(7)
)
BEGIN
    DECLARE v_LuongCoBan DECIMAL(15,2);
    DECLARE v_PhuCap DECIMAL(15,2);
    DECLARE v_SoGioLam DECIMAL(6,2);
    DECLARE v_SoGioTangCa DECIMAL(6,2);
    DECLARE v_TienTangCa DECIMAL(15,2);
    DECLARE v_KhauTru DECIMAL(15,2);
    DECLARE v_TongLuong DECIMAL(15,2);
    
    -- Lấy thông tin lương cơ bản và phụ cấp từ hợp đồng
    SELECT hd.MucLuong, hd.PhuCap 
    INTO v_LuongCoBan, v_PhuCap
    FROM HopDongLaoDong hd 
    WHERE hd.MaNhanVien = p_MaNhanVien 
    AND hd.TrangThai = 'Hiệu lực'
    ORDER BY hd.NgayBatDau DESC 
    LIMIT 1;
    
    -- Tính tổng số giờ làm và giờ tăng ca trong tháng
    SELECT 
        COALESCE(SUM(SoGioLam), 0),
        COALESCE(SUM(SoGioTangCa), 0)
    INTO v_SoGioLam, v_SoGioTangCa
    FROM ChamCong 
    WHERE MaNhanVien = p_MaNhanVien 
    AND DATE_FORMAT(NgayChamCong, '%Y-%m') = p_ThangNam;
    
    -- Tính tiền tăng ca (1.5 lần lương giờ)
    SET v_TienTangCa = (v_LuongCoBan / 176) * 1.5 * v_SoGioTangCa;
    
    -- Tính khấu trừ (BHXH 8%, BHYT 1.5%, BHTN 1%, Thuế TNCN ước tính)
    SET v_KhauTru = (v_LuongCoBan + v_PhuCap) * 0.105; -- 10.5% tổng khấu trừ
    
    -- Tính tổng lương
    SET v_TongLuong = v_LuongCoBan + v_PhuCap + v_TienTangCa - v_KhauTru;
    
    -- Insert hoặc update bảng lương
    INSERT INTO Luong (MaNhanVien, ThangNam, LuongCoBan, PhuCap, SoGioLam, SoGioTangCa, TienTangCa, KhauTru, TongLuong, NgayTinhLuong)
    VALUES (p_MaNhanVien, p_ThangNam, v_LuongCoBan, v_PhuCap, v_SoGioLam, v_SoGioTangCa, v_TienTangCa, v_KhauTru, v_TongLuong, CURDATE())
    ON DUPLICATE KEY UPDATE
        LuongCoBan = v_LuongCoBan,
        PhuCap = v_PhuCap,
        SoGioLam = v_SoGioLam,
        SoGioTangCa = v_SoGioTangCa,
        TienTangCa = v_TienTangCa,
        KhauTru = v_KhauTru,
        TongLuong = v_TongLuong,
        NgayTinhLuong = CURDATE();
        
END //

-- Procedure chấm công tự động
CREATE PROCEDURE ChamCongTuDong(
    IN p_MaNhanVien VARCHAR(10),
    IN p_NgayChamCong DATE,
    IN p_GioVao TIME,
    IN p_GioRa TIME
)
BEGIN
    DECLARE v_SoGioLam DECIMAL(4,2);
    DECLARE v_SoGioTangCa DECIMAL(4,2);
    DECLARE v_TrangThai VARCHAR(20);
    
    -- Tính số giờ làm việc
    SET v_SoGioLam = TIMESTAMPDIFF(MINUTE, p_GioVao, p_GioRa) / 60.0;
    
    -- Tính giờ tăng ca (trên 8 giờ)
    IF v_SoGioLam > 8 THEN
        SET v_SoGioTangCa = v_SoGioLam - 8;
        SET v_SoGioLam = 8;
    ELSE
        SET v_SoGioTangCa = 0;
    END IF;
    
    -- Xác định trạng thái
    IF p_GioVao > '08:15:00' THEN
        SET v_TrangThai = 'Đi muộn';
    ELSE
        SET v_TrangThai = 'Có mặt';
    END IF;
    
    -- Insert hoặc update chấm công
    INSERT INTO ChamCong (MaNhanVien, NgayChamCong, GioVao, GioRa, SoGioLam, SoGioTangCa, TrangThai)
    VALUES (p_MaNhanVien, p_NgayChamCong, p_GioVao, p_GioRa, v_SoGioLam, v_SoGioTangCa, v_TrangThai)
    ON DUPLICATE KEY UPDATE
        GioVao = p_GioVao,
        GioRa = p_GioRa,
        SoGioLam = v_SoGioLam,
        SoGioTangCa = v_SoGioTangCa,
        TrangThai = v_TrangThai;
        
END //

-- Function lấy số ngày nghỉ phép còn lại
CREATE FUNCTION GetSoNgayPhepConLai(p_MaNhanVien VARCHAR(10), p_Nam INT)
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_SoNgayPhepNam INT DEFAULT 12; -- 12 ngày phép/năm
    DECLARE v_SoNgayDaSuDung INT DEFAULT 0;
    
    -- Tính số ngày phép đã sử dụng trong năm
    SELECT COALESCE(SUM(SoNgayNghi), 0)
    INTO v_SoNgayDaSuDung
    FROM NghiPhep
    WHERE MaNhanVien = p_MaNhanVien
    AND YEAR(NgayBatDau) = p_Nam
    AND LoaiNghiPhep = 'Nghỉ phép năm'
    AND TrangThai = 'Đã duyệt';
    
    RETURN v_SoNgayPhepNam - v_SoNgayDaSuDung;
END //

-- Procedure báo cáo tổng hợp nhân sự theo phòng ban
CREATE PROCEDURE BaoCaoNhanSuTheoPhongBan()
BEGIN
    SELECT 
        pb.MaPhongBan,
        pb.TenPhongBan,
        COUNT(nv.MaNhanVien) as TongSoNhanVien,
        COUNT(CASE WHEN nv.TrangThaiLamViec = 'Đang làm việc' THEN 1 END) as SoNVDangLamViec,
        COUNT(CASE WHEN nv.TrangThaiLamViec = 'Nghỉ việc' THEN 1 END) as SoNVNghiViec,
        CONCAT(tp.HoTen) as TruongPhong
    FROM PhongBan pb
    LEFT JOIN NhanVien nv ON pb.MaPhongBan = nv.MaPhongBan
    LEFT JOIN NhanVien tp ON pb.TruongPhong = tp.MaNhanVien
    WHERE pb.TrangThai = 1
    GROUP BY pb.MaPhongBan, pb.TenPhongBan, tp.HoTen
    ORDER BY pb.MaPhongBan;
END //

DELIMITER ;