# Xây dựng ứng dụng Quản lý Nhân sự 

---

## 1. Giới thiệu đề tài
*   **Bài toán:** Quản lý nhân sự trong các doanh nghiệp vừa và nhỏ gặp nhiều khó khăn trong việc theo dõi thông tin, tính lương, quản lý nghỉ phép và dự báo biến động nhân sự.
*   **Mục tiêu:** Xây dựng một ứng dụng Android hoàn chỉnh giúp tự động hóa quy trình quản trị nhân sự, tích hợp khả năng xuất báo cáo chuyên nghiệp và phân tích dữ liệu nhân viên.

## 2. Dataset
*   **Nguồn dữ liệu:** Dữ liệu được thu thập và mô phỏng từ quy trình quản lý thực tế của doanh nghiệp, lưu trữ dưới dạng SQLite Database.
*   **Mô tả các bảng chính:**
    *   `NhanVien`: Lưu thông tin cá nhân (Mã NV, Họ tên, Ngày sinh, Giới tính, SĐT, Email, Hình ảnh).
    *   `PhongBan`: Thông tin các phòng ban trong công ty.
    *   `ChucVu`: Thông tin các chức vụ và mức lương cơ bản.
    *   `HopDongLaoDong`: Quản lý các loại hợp đồng, ngày bắt đầu/kết thúc và mức lương thỏa thuận.
    *   `ChamCong`: Theo dõi thời gian vào/ra, số giờ làm việc thực tế hàng ngày của nhân viên.
    *   `NghiPhep`: Quản lý đơn xin nghỉ phép, lý do và quy trình duyệt từ Admin.
    *   `Luong`: Tính toán tổng lương hàng tháng dựa trên lương cơ bản, phụ cấp và số công thực tế.
    *   `TaiKhoan`: Quản lý thông tin đăng nhập và phân quyền (Admin, Employee, HR...).

## 3. Pipeline & Luồng xử lý
Hệ thống được thiết kế theo mô hình kiến trúc Android tiêu chuẩn kết hợp xử lý dữ liệu:
1.  **Thu thập dữ liệu:** Người dùng nhập liệu qua giao diện Android -> Lưu trữ vào SQLite.
2.  **Tiền xử lý:** Kiểm tra định dạng (Validation) Email, Số điện thoại, Ràng buộc dữ liệu (Constraint).
3.  **Xử lý Logic:** Tính toán lương tự động dựa trên ngày công và mức lương chức vụ.
4.  **Phân tích & Xuất bản:** Trích xuất dữ liệu ra định dạng PDF và phân tích lịch sử nghỉ phép/lương.

## 4. Mô hình & Công nghệ sử dụng
*   **Ngôn ngữ:** Java (Android), SQL.
*   **Cơ sở dữ liệu:** SQLite (Đảm bảo tính gọn nhẹ, truy xuất nhanh trên di động).
*   **Thư viện hỗ trợ:**
    *   `PdfDocument`: Dùng để khởi tạo và xuất báo cáo PDF chuyên nghiệp.
    *   `Android UI Components`: SearchView, RecyclerView, CardView... để tối ưu trải nghiệm.
*   **Lý do chọn:** Sự kết hợp giữa SQLite và Java giúp ứng dụng hoạt động ổn định Offline, bảo mật dữ liệu nhân sự tuyệt đối.

## 5. Kết quả đạt được
*   **Tính năng:**
    *   Quản lý danh mục Nhân viên, Phòng ban, Chức vụ.
    *   Tính lương tự động và quản lý trạng thái lương.
    *   Hệ thống xin nghỉ phép và duyệt đơn (Workflow).
    *   **Xuất danh sách nhân viên và bảng lương ra tệp PDF.**
    *   Tìm kiếm và lọc dữ liệu thông minh.
*   **Hiệu năng:** Ứng dụng chạy mượt mà trên các thiết bị Android từ API 24 trở lên.

## 6. Hướng dẫn chạy dự án
### Cài đặt môi trường
1.  Tải và cài đặt **Android Studio**
2.  Cài đặt JDK 17 hoặc mới hơn.

### Chạy ứng dụng
1.  Clone dự án: `git clone https://github.com/Thuanzz05/BTL_Mobile_QLNS.git`
2.  Mở dự án bằng Android Studio.
3.  Chờ Gradle đồng bộ (Sync).
4.  Chọn thiết bị ảo (Emulator) hoặc thiết bị thật và nhấn **Run (Shift + F10)**.

## 7. Cấu trúc thư mục
```text
BTL_Mobile_QLNS/
├── app/
│   ├── src/main/java/com/example/btl_mobile_qlns/  # Source code Java
│   ├── src/main/res/layout/                       # Giao diện XML
│   └── AndroidManifest.xml                        # Cấu hình ứng dụng
├── database/                                      # File SQLite và Script SQL
├── slides/                                        # Slide thuyết trình
├── reports/                                       # Tài liệu báo cáo (Word/PDF)
└── README.md                                      # Hướng dẫn dự án
```

---

## 8. Tác giả
*   **Họ tên:** Nguyễn Duy Thuấn
*   **Mã SV:** 10123308
*   **Lớp:** 12523W.1

*   **Họ tên:** Đào Duy Huy
*   **Mã SV:** 10123156
*   **Lớp:** 12523W.1
