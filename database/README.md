# Cơ sở dữ liệu Quản lý Nhân sự (QLNS)

## Mô tả
Hệ thống cơ sở dữ liệu cho ứng dụng Quản lý Nhân sự bao gồm các chức năng:
- Quản lý thông tin nhân viên
- Quản lý phòng ban và chức vụ
- Chấm công và tính lương
- Quản lý nghỉ phép
- Hệ thống đăng nhập và phân quyền

## Cấu trúc Database

### Bảng chính:
1. **PhongBan** - Quản lý phòng ban
2. **ChucVu** - Quản lý chức vụ
3. **NhanVien** - Thông tin nhân viên
4. **HopDongLaoDong** - Hợp đồng lao động
5. **ChamCong** - Chấm công hàng ngày
6. **NghiPhep** - Quản lý nghỉ phép
7. **Luong** - Tính lương hàng tháng
8. **TaiKhoan** - Tài khoản đăng nhập

### Views:
- `v_NhanVienDayDu` - Thông tin nhân viên đầy đủ
- `v_BaoCaoChamCongThang` - Báo cáo chấm công theo tháng
- `v_BaoCaoLuongThang` - Báo cáo lương theo tháng
- `v_ThongKeNghiPhep` - Thống kê nghỉ phép
- `v_DashboardTongQuan` - Dashboard tổng quan
- `v_NhanVienSapHetHanHopDong` - Nhân viên sắp hết hạn hợp đồng

### Stored Procedures:
- `TinhLuongThang()` - Tính lương tháng cho nhân viên
- `ChamCongTuDong()` - Chấm công tự động
- `BaoCaoNhanSuTheoPhongBan()` - Báo cáo nhân sự theo phòng ban

### Functions:
- `GetSoNgayPhepConLai()` - Lấy số ngày phép còn lại

## Cách sử dụng

### 1. Tạo database:
```sql
mysql -u root -p < qlns_database.sql
```

### 2. Thêm dữ liệu mẫu:
```sql
mysql -u root -p QLNS_DB < sample_data.sql
```

### 3. Tạo stored procedures:
```sql
mysql -u root -p QLNS_DB < stored_procedures.sql
```

### 4. Tạo views:
```sql
mysql -u root -p QLNS_DB < views.sql
```

## Tài khoản mặc định
- **Admin**: `admin` / `password`
- **HR Manager**: `hr_manager` / `password`
- **Manager**: `acc_manager` / `password`
- **Employee**: `employee1` / `password`

## Lưu ý bảo mật
- Mật khẩu được mã hóa bằng bcrypt
- Phân quyền theo vai trò: Admin, HR, Manager, Employee
- Sử dụng prepared statements để tránh SQL injection

## Backup và Restore
```bash
# Backup
mysqldump -u root -p QLNS_DB > backup_qlns.sql

# Restore
mysql -u root -p QLNS_DB < backup_qlns.sql
```