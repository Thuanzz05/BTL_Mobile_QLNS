# Dữ liệu cho Hệ thống QLNS

## Mô tả Dataset

### Nguồn dữ liệu
- Dữ liệu nhân viên được tạo synthetic cho mục đích demo
- File chính: `nhan_vien_data.csv`

### Cấu trúc dữ liệu

| Cột | Mô tả | Kiểu dữ liệu |
|-----|-------|--------------|
| MaNV | Mã nhân viên | String |
| GioiTinh | Giới tính (Nam/Nữ) | String |
| NamSinh | Năm sinh | Integer |
| PhongBan | Phòng ban làm việc | String |
| ChucVu | Chức vụ | String |
| TrinhDo | Trình độ học vấn | String |
| KinhNghiem | Số năm kinh nghiệm | Integer |
| Luong | Mức lương (VNĐ) - Target variable | Integer |

### Hướng dẫn tải dữ liệu
```python
# Chạy script để tạo dữ liệu mẫu
python app/preprocess.py
```

### Thống kê cơ bản
- Tổng số mẫu: ~1000 records
- Phân bố giới tính: 60% Nam, 40% Nữ
- Phạm vi lương: 8-200 triệu VNĐ
- Phòng ban: IT, HR, Finance, Marketing, Sales