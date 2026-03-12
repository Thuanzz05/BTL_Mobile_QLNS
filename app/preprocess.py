"""
Tiền xử lý dữ liệu cho hệ thống dự đoán lương nhân viên
"""
import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.model_selection import train_test_split
import joblib
import os

class DataPreprocessor:
    def __init__(self):
        self.label_encoders = {}
        self.scaler = StandardScaler()
        self.feature_columns = []
        
    def load_data(self, file_path: str) -> pd.DataFrame:
        """Tải dữ liệu từ file CSV"""
        try:
            df = pd.read_csv(file_path, encoding='utf-8')
            print(f"Đã tải {len(df)} bản ghi từ {file_path}")
            return df
        except Exception as e:
            print(f"Lỗi khi tải dữ liệu: {e}")
            return None
    
    def clean_data(self, df: pd.DataFrame) -> pd.DataFrame:
        """Làm sạch dữ liệu"""
        # Xử lý missing values
        df = df.dropna(subset=['Luong', 'KinhNghiem', 'ChucVu'])
        
        # Xử lý outliers cho lương (loại bỏ 1% cao nhất và thấp nhất)
        Q1 = df['Luong'].quantile(0.01)
        Q99 = df['Luong'].quantile(0.99)
        df = df[(df['Luong'] >= Q1) & (df['Luong'] <= Q99)]
        
        # Chuẩn hóa tên cột
        df.columns = df.columns.str.strip()
        
        print(f"Sau khi làm sạch: {len(df)} bản ghi")
        return df
    
    def feature_engineering(self, df: pd.DataFrame) -> pd.DataFrame:
        """Tạo các đặc trưng mới"""
        # Tính tuổi từ năm sinh
        if 'NamSinh' in df.columns:
            df['Tuoi'] = 2024 - df['NamSinh']
        
        # Tạo nhóm tuổi
        if 'Tuoi' in df.columns:
            df['NhomTuoi'] = pd.cut(df['Tuoi'], 
                                   bins=[0, 25, 35, 45, 100], 
                                   labels=['Trẻ', 'Trung bình', 'Trung niên', 'Cao tuổi'])
        
        # Tạo nhóm kinh nghiệm
        if 'KinhNghiem' in df.columns:
            df['NhomKinhNghiem'] = pd.cut(df['KinhNghiem'],
                                         bins=[0, 2, 5, 10, 100],
                                         labels=['Mới', 'Junior', 'Senior', 'Expert'])
        
        # Tạo đặc trưng tương tác
        if 'KinhNghiem' in df.columns and 'Tuoi' in df.columns:
            df['TiLe_KinhNghiem_Tuoi'] = df['KinhNghiem'] / df['Tuoi']
        
        return df
    
    def encode_categorical(self, df: pd.DataFrame, categorical_columns: list) -> pd.DataFrame:
        """Mã hóa các biến categorical"""
        df_encoded = df.copy()
        
        for col in categorical_columns:
            if col in df_encoded.columns:
                if col not in self.label_encoders:
                    self.label_encoders[col] = LabelEncoder()
                    df_encoded[col] = self.label_encoders[col].fit_transform(df_encoded[col].astype(str))
                else:
                    df_encoded[col] = self.label_encoders[col].transform(df_encoded[col].astype(str))
        
        return df_encoded
    
    def scale_features(self, X_train: pd.DataFrame, X_test: pd.DataFrame = None) -> tuple:
        """Chuẩn hóa đặc trưng"""
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_train_scaled = pd.DataFrame(X_train_scaled, columns=X_train.columns, index=X_train.index)
        
        if X_test is not None:
            X_test_scaled = self.scaler.transform(X_test)
            X_test_scaled = pd.DataFrame(X_test_scaled, columns=X_test.columns, index=X_test.index)
            return X_train_scaled, X_test_scaled
        
        return X_train_scaled
    
    def prepare_features(self, df: pd.DataFrame, target_column: str = 'Luong') -> tuple:
        """Chuẩn bị đặc trưng cho training"""
        # Định nghĩa các cột categorical và numerical
        categorical_cols = ['GioiTinh', 'PhongBan', 'ChucVu', 'TrinhDo', 'NhomTuoi', 'NhomKinhNghiem']
        numerical_cols = ['Tuoi', 'KinhNghiem', 'TiLe_KinhNghiem_Tuoi']
        
        # Lọc các cột tồn tại trong dataframe
        categorical_cols = [col for col in categorical_cols if col in df.columns]
        numerical_cols = [col for col in numerical_cols if col in df.columns]
        
        # Mã hóa categorical
        df_processed = self.encode_categorical(df, categorical_cols)
        
        # Chọn features
        feature_cols = categorical_cols + numerical_cols
        self.feature_columns = feature_cols
        
        X = df_processed[feature_cols]
        y = df_processed[target_column]
        
        return X, y
    
    def split_data(self, X: pd.DataFrame, y: pd.Series, test_size: float = 0.2, random_state: int = 42) -> tuple:
        """Chia dữ liệu train/test"""
        return train_test_split(X, y, test_size=test_size, random_state=random_state)
    
    def save_preprocessor(self, save_dir: str = 'models'):
        """Lưu preprocessor"""
        os.makedirs(save_dir, exist_ok=True)
        
        # Lưu label encoders
        joblib.dump(self.label_encoders, f'{save_dir}/label_encoders.pkl')
        
        # Lưu scaler
        joblib.dump(self.scaler, f'{save_dir}/scaler.pkl')
        
        # Lưu feature columns
        joblib.dump(self.feature_columns, f'{save_dir}/feature_columns.pkl')
        
        print(f"Đã lưu preprocessor vào {save_dir}/")
    
    def load_preprocessor(self, save_dir: str = 'models'):
        """Tải preprocessor"""
        try:
            self.label_encoders = joblib.load(f'{save_dir}/label_encoders.pkl')
            self.scaler = joblib.load(f'{save_dir}/scaler.pkl')
            self.feature_columns = joblib.load(f'{save_dir}/feature_columns.pkl')
            print(f"Đã tải preprocessor từ {save_dir}/")
        except Exception as e:
            print(f"Lỗi khi tải preprocessor: {e}")

def create_sample_data():
    """Tạo dữ liệu mẫu cho training"""
    np.random.seed(42)
    n_samples = 1000
    
    # Tạo dữ liệu synthetic
    data = {
        'MaNV': [f'NV{i:04d}' for i in range(1, n_samples + 1)],
        'GioiTinh': np.random.choice(['Nam', 'Nữ'], n_samples, p=[0.6, 0.4]),
        'NamSinh': np.random.randint(1970, 2000, n_samples),
        'PhongBan': np.random.choice(['IT', 'HR', 'Finance', 'Marketing', 'Sales'], n_samples),
        'ChucVu': np.random.choice(['Nhân viên', 'Trưởng nhóm', 'Trưởng phòng', 'Phó giám đốc', 'Giám đốc'], 
                                  n_samples, p=[0.5, 0.25, 0.15, 0.07, 0.03]),
        'TrinhDo': np.random.choice(['Cao đẳng', 'Đại học', 'Thạc sĩ', 'Tiến sĩ'], 
                                   n_samples, p=[0.2, 0.6, 0.18, 0.02]),
        'KinhNghiem': np.random.exponential(3, n_samples).astype(int)
    }
    
    df = pd.DataFrame(data)
    df['Tuoi'] = 2024 - df['NamSinh']
    
    # Tạo lương dựa trên các yếu tố
    base_salary = 10000000  # 10 triệu
    
    # Hệ số theo chức vụ
    position_multiplier = {
        'Nhân viên': 1.0,
        'Trưởng nhóm': 1.5,
        'Trưởng phòng': 2.0,
        'Phó giám đốc': 3.0,
        'Giám đốc': 4.0
    }
    
    # Hệ số theo trình độ
    education_multiplier = {
        'Cao đẳng': 1.0,
        'Đại học': 1.2,
        'Thạc sĩ': 1.4,
        'Tiến sĩ': 1.6
    }
    
    # Tính lương
    df['Luong'] = base_salary
    df['Luong'] *= df['ChucVu'].map(position_multiplier)
    df['Luong'] *= df['TrinhDo'].map(education_multiplier)
    df['Luong'] *= (1 + df['KinhNghiem'] * 0.05)  # 5% mỗi năm kinh nghiệm
    
    # Thêm noise
    df['Luong'] *= np.random.normal(1, 0.1, n_samples)
    df['Luong'] = df['Luong'].round().astype(int)
    
    return df

if __name__ == "__main__":
    # Tạo và lưu dữ liệu mẫu
    df = create_sample_data()
    os.makedirs('data', exist_ok=True)
    df.to_csv('data/nhan_vien_data.csv', index=False, encoding='utf-8')
    print("Đã tạo dữ liệu mẫu tại data/nhan_vien_data.csv")