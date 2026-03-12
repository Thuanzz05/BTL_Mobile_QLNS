# Hệ thống Dự đoán Lương Nhân viên sử dụng Machine Learning

## 📋 Giới thiệu đề tài

### Bài toán
Xây dựng hệ thống dự đoán mức lương phù hợp cho nhân viên dựa trên các yếu tố như:
- Kinh nghiệm làm việc
- Trình độ học vấn  
- Chức vụ hiện tại
- Phòng ban làm việc
- Giới tính và độ tuổi

### Mục tiêu
- Dự đoán mức lương chính xác cho nhân viên mới
- Hỗ trợ HR trong việc đưa ra mức lương cạnh tranh
- Phân tích các yếu tố ảnh hưởng đến mức lương
- Tối ưu hóa chi phí nhân sự cho doanh nghiệp

## 📊 Dataset

### Nguồn dữ liệu
- **Dữ liệu**: Synthetic dataset được tạo cho mục đích demo
- **Kích thước**: ~1000 records
- **File chính**: `data/nhan_vien_data.csv`

### Mô tả các cột
| Cột | Mô tả | Kiểu dữ liệu | Ví dụ |
|-----|-------|--------------|-------|
| MaNV | Mã nhân viên | String | NV0001 |
| GioiTinh | Giới tính | String | Nam, Nữ |
| NamSinh | Năm sinh | Integer | 1990 |
| PhongBan | Phòng ban | String | IT, HR, Finance |
| ChucVu | Chức vụ | String | Nhân viên, Trưởng phòng |
| TrinhDo | Trình độ học vấn | String | Đại học, Thạc sĩ |
| KinhNghiem | Số năm kinh nghiệm | Integer | 5 |
| **Luong** | **Mức lương (VNĐ)** | **Integer** | **15000000** |

## 🔄 Pipeline

### 1. Tiền xử lý (Preprocessing)
```
Raw Data → Data Cleaning → Feature Engineering → Encoding → Scaling → Ready Data
```

### 2. Training
```
Ready Data → Train/Test Split → Model Training → Model Evaluation → Best Model Selection
```

### 3. Inference
```
New Data → Preprocessing → Trained Model → Salary Prediction
```

## 🤖 Mô hình sử dụng

### Các mô hình được thử nghiệm:
1. **Linear Regression** - Baseline model
2. **Ridge Regression** - Regularized linear model  
3. **Decision Tree** - Tree-based model
4. **Random Forest** - Ensemble method ⭐ (Model tốt nhất)
5. **Gradient Boosting** - Advanced ensemble

### Lý do chọn Random Forest:
- Xử lý tốt dữ liệu categorical và numerical
- Không bị overfitting như Decision Tree
- Cung cấp feature importance
- Performance ổn định trên nhiều loại dữ liệu

## 📈 Kết quả

### Performance Model tốt nhất (Random Forest):
- **MAE (Mean Absolute Error)**: [Sẽ cập nhật sau training]
- **RMSE (Root Mean Square Error)**: [Sẽ cập nhật sau training]  
- **R² Score**: [Sẽ cập nhật sau training]
- **MAPE (Mean Absolute Percentage Error)**: [Sẽ cập nhật sau training]

### Feature Importance:
[Sẽ cập nhật sau training]

## 🚀 Hướng dẫn chạy

### 1. Cài đặt môi trường
```bash
# Clone repository
git clone https://github.com/Thuanzz05/BTL_Mobile_QLNS.git
cd BTL_Mobile_QLNS

# Tạo virtual environment (khuyến nghị)
python -m venv venv
source venv/bin/activate  # Linux/Mac
# hoặc
venv\Scripts\activate     # Windows

# Cài đặt dependencies
pip install -r requirements.txt
```

### 2. Chạy Training
```bash
# Tạo dữ liệu mẫu và training model
python app/train.py
```

### 3. Chạy Demo/Inference
```bash
# Chạy demo script
python demo/demo_inference.py

# Hoặc sử dụng Jupyter notebook
jupyter notebook demo/demo.ipynb
```

## 📁 Cấu trúc thư mục dự án

```
BTL_Mobile_QLNS/
├── app/                          # Source code chính
│   ├── __init__.py
│   ├── preprocess.py            # Tiền xử lý dữ liệu
│   ├── train.py                 # Training models
│   ├── predict.py               # Inference/Prediction
│   └── utils.py                 # Utility functions
├── demo/                        # Demo scripts
│   ├── demo_inference.py        # Script demo nhanh
│   └── demo.ipynb              # Jupyter notebook demo
├── data/                        # Dữ liệu
│   ├── README.md               # Mô tả dataset
│   └── nhan_vien_data.csv      # Dataset chính (tạo tự động)
├── models/                      # Models đã train (tạo tự động)
│   ├── best_model.pkl
│   ├── scaler.pkl
│   └── label_encoders.pkl
├── reports/                     # Báo cáo
│   ├── bao_cao_BTL_QLNS.md     # Báo cáo chính
│   ├── model_evaluation.png     # Biểu đồ đánh giá (tạo tự động)
│   └── feature_importance.png   # Feature importance (tạo tự động)
├── slides/                      # Slide thuyết trình
│   └── README.md
├── database/                    # Database SQL (legacy)
├── requirements.txt             # Dependencies
├── README.md                   # File này
└── .gitignore                  # Git ignore rules
```

## 👥 Tác giả

### Thông tin nhóm:
- **Tên đề tài**: Hệ thống Dự đoán Lương Nhân viên sử dụng Machine Learning
- **Môn học**: [Tên môn học]
- **Lớp**: [Tên lớp]  
- **Học kỳ**: [Học kỳ năm học]

### Thành viên:
1. **[Tên sinh viên 1]**
   - **MSSV**: [Mã số sinh viên]
   - **Email**: [Email]
   - **Vai trò**: Team Leader, Data Scientist

2. **[Tên sinh viên 2]**  
   - **MSSV**: [Mã số sinh viên]
   - **Email**: [Email]
   - **Vai trò**: ML Engineer, Backend Developer

## 📞 Liên hệ

- **Repository**: https://github.com/Thuanzz05/BTL_Mobile_QLNS
- **Issues**: https://github.com/Thuanzz05/BTL_Mobile_QLNS/issues

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.