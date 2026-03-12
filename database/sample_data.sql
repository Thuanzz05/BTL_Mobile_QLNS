-- Dữ liệu mẫu bổ sung cho hệ thống QLNS

-- Thêm dữ liệu Hợp đồng lao động
INSERT INTO HopDongLaoDong (MaHopDong, MaNhanVien, LoaiHopDong, NgayBatDau, NgayKetThuc, MucLuong, PhuCap, NoiDung, TrangThai) VALUES
('HD001', 'NV001', 'Không thời hạn', '2020-01-20', NULL, 50000000, 5000000, N'Hợp đồng lao động Giám đốc', 'Hiệu lực'),
('HD002', 'NV002', 'Không thời hạn', '2020-02-01', NULL, 25000000, 3000000, N'Hợp đồng lao động Trưởng phòng NS', 'Hiệu lực'),
('HD003', 'NV003', 'Có thời hạn', '2020-03-15', '2023-03-14', 25000000, 2500000, N'Hợp đồng lao động Trưởng phòng KT', 'Hiệu lực'),
('HD004', 'NV004', 'Có thời hạn', '2020-04-01', '2022-03-31', 15000000, 1500000, N'Hợp đồng lao động Nhân viên KD', 'Hiệu lực'),
('HD005', 'NV005', 'Thử việc', '2020-05-10', '2020-07-09', 12000000, 1000000, N'Hợp đồng thử việc', 'Hiệu lực');

-- Thêm dữ liệu Chấm công (tháng hiện tại)
INSERT INTO ChamCong (MaNhanVien, NgayChamCong, GioVao, GioRa, SoGioLam, SoGioTangCa, TrangThai, GhiChu) VALUES
-- Dữ liệu cho NV001
('NV001', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV001', '2024-03-02', '08:15:00', '17:30:00', 8.25, 0, 'Đi muộn', N'Đi muộn 15 phút'),
('NV001', '2024-03-03', '08:00:00', '18:00:00', 8.5, 1.5, 'Có mặt', N'Tăng ca'),
-- Dữ liệu cho NV002
('NV002', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV002', '2024-03-02', NULL, NULL, 0, 0, 'Nghỉ phép', N'Nghỉ phép năm'),
('NV002', '2024-03-03', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
-- Dữ liệu cho NV003
('NV003', '2024-03-01', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV003', '2024-03-02', '08:00:00', '17:30:00', 8.5, 0, 'Có mặt', NULL),
('NV003', '2024-03-03', '08:00:00', '19:00:00', 8.5, 2.5, 'Có mặt', N'Tăng ca cuối tháng');

-- Thêm dữ liệu Nghỉ phép
INSERT INTO NghiPhep (MaNhanVien, LoaiNghiPhep, NgayBatDau, NgayKetThuc, SoNgayNghi, LyDo, TrangThai, NguoiDuyet, NgayDuyet) VALUES
('NV002', 'Nghỉ phép năm', '2024-03-02', '2024-03-02', 1, N'Nghỉ phép cá nhân', 'Đã duyệt', 'NV001', '2024-02-28'),
('NV004', 'Nghỉ ốm', '2024-02-15', '2024-02-16', 2, N'Bị cảm cúm', 'Đã duyệt', 'NV002', '2024-02-14'),
('NV005', 'Nghỉ phép năm', '2024-03-10', '2024-03-12', 3, N'Về quê thăm gia đình', 'Chờ duyệt', NULL, NULL);

-- Thêm dữ liệu Lương (tháng 2/2024)
INSERT INTO Luong (MaNhanVien, ThangNam, LuongCoBan, PhuCap, ThuongHieuSuat, SoGioLam, SoGioTangCa, TienTangCa, KhauTru, TongLuong, TrangThai, NgayTinhLuong, NgayThanhToan) VALUES
('NV001', '2024-02', 50000000, 5000000, 2000000, 176, 8, 2000000, 5700000, 53300000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV002', '2024-02', 25000000, 3000000, 1000000, 168, 4, 800000, 2880000, 26920000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV003', '2024-02', 25000000, 2500000, 800000, 176, 12, 2400000, 3030000, 27670000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV004', '2024-02', 15000000, 1500000, 500000, 160, 0, 0, 1700000, 15300000, 'Đã thanh toán', '2024-02-28', '2024-03-05'),
('NV005', '2024-02', 12000000, 1000000, 0, 176, 2, 300000, 1330000, 11970000, 'Chưa thanh toán', '2024-02-28', NULL);