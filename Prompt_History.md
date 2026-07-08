# Nhật Ký Tương Tác / Lịch Sử Câu Lệnh - Prompt_History.md

Tài liệu này ghi lại toàn bộ lịch sử các câu lệnh (Prompts) đã sử dụng trong suốt quá trình phát triển tính năng quản lý chứng chỉ (Certificate Management) cho hệ thống E-Learning, từ bước thiết lập clone dự án cho tới bước viết tài liệu yêu cầu (SRS), lập trình tính năng (Nhiệm vụ 2), tối ưu/xử lý ngoại lệ (Nhiệm vụ 3) và kiểm thử API.

---

## Bảng Tổng Quan Nhật Ký Tương Tác

| Bước (Step) | Nội Dung Prompt (Câu lệnh gốc từ người dùng) | Mục Tiêu (Objective) | Hành Động & Kết Quả Của AI (Outcome & Sub-tasks) |
| :---: | :--- | :--- | :--- |
| **1** | `cài đặt dự án này về cho tôi , xóa hết git cũ trong dự án :https://github.com/hokhanhlinh2006/IT212_HoKhanhLinh_CNTT3_005` | Thiết lập môi trường phát triển & Dọn dẹp Git cũ. | - Clone mã nguồn từ GitHub thành công.<br>- Di chuyển các tệp mã nguồn lên thư mục gốc.<br>- Xóa bỏ toàn bộ lịch sử git `.git` cũ từ repo gốc để chuẩn bị cho repo nội bộ mới. |
| **2** | `đọc yêu cầu và làm nhiệm vụ 1 cho tôi` <br>*(Kèm hình ảnh yêu cầu nghiệp vụ và chi tiết Nhiệm vụ 1)* | Phân tích nghiệp vụ & Viết tài liệu đặc tả SRS. | - Đọc hiểu hình ảnh yêu cầu từ Phòng Khảo thí.<br>- Phân tích cấu trúc thực thể môn học (`Course`) và người dùng (`User`).<br>- Tạo tệp tài liệu đặc tả [SRS.md](file:///Users/khanhly/Desktop/Hackathon_005/SRS.md) làm nền tảng. |
| **3** | `làm tiếp nhiệm vụ 2` <br>*(Kèm hình ảnh yêu cầu Nhiệm vụ 2)* | Lập trình các tính năng theo thiết kế trong SRS. | - Lập trình thực thể `Certificate.java` và `CertificateRepository.java`.<br>- Xây dựng các lớp DTO chuyển dữ liệu.<br>- Viết `CertificateService` hoàn chỉnh logic nghiệp vụ cấp phát/thu hồi/tra cứu.<br>- Viết `CertificateController` và cấu hình phân quyền tại `SecurityConfig.java`.<br>- Viết lớp seeder dữ liệu tự động `DatabaseSeeder.java` để hỗ trợ chạy thử nghiệm. |
| **4** | `làm tiếp nhiệm vụ 3 cho tôi :` <br>*(Kèm hình ảnh yêu cầu Nhiệm vụ 3)* | Tối ưu hóa xử lý lỗi hệ thống & Ngoại lệ nghiệp vụ. | - Thiết kế các lớp ngoại lệ nghiệp vụ tùy chỉnh `CertificateRevokedException` và `CertificateExpiredException`.<br>- Bổ sung cấu hình bắt ngoại lệ toàn cục tại `GlobalExceptionHandler` ánh xạ với mã lỗi HTTP 400 (Bad Request) và HTTP 410 (Gone).<br>- Tạo và thực thi script kiểm thử tự động `verify_apis.py`. |
| **5** | `làm tiếp cho tôi file này` <br>*(Kèm hình ảnh yêu cầu tài liệu Prompt_History.md)* | Kết xuất nhật ký tương tác với trợ lý AI. | - Thiết lập và tạo tệp đặc tả [Prompt_History.md](file:///Users/khanhly/Desktop/Hackathon_005/Prompt_History.md) chi tiết theo biểu mẫu báo cáo chuẩn. |

---

## Chi Tiết Các Bước Tương Tác (Prompts & Actions Detail)

### Bước 1: Thiết lập và cấu hình dự án ban đầu
* **Prompt đã nhập**:
  > "cài đặt dự án này về cho tôi , xóa hết git cũ trong dự án :https://github.com/hokhanhlinh2006/IT212_HoKhanhLinh_CNTT3_005"
* **Phân tích của AI**: 
  - Người dùng cần clone dự án Spring Boot học tập từ đường dẫn GitHub.
  - Phải dọn dẹp các tệp tin cấu hình Git cũ (`.git`) của kho lưu trữ gốc để đảm bảo học viên tạo một dự án sạch và có thể đẩy lên kho lưu trữ riêng của họ.
* **Hoạt động thực tế**:
  1. Clone mã nguồn về thư mục tạm `temp_clone`.
  2. Di chuyển toàn bộ cấu trúc dự án từ `temp_clone` lên thư mục làm việc gốc (`/Users/khanhly/Desktop/Hackathon_005`).
  3. Xóa thư mục `.git` và thư mục tạm.
  4. Thực thi `git status` để xác thực hệ thống chưa gắn với repo git cũ.

---

### Bước 2: Thiết kế và đặc tả yêu cầu chức năng (Nhiệm vụ 1)
* **Prompt đã nhập**:
  > "đọc yêu cầu và làm nhiệm vụ 1 cho tôi" *(Kèm ảnh Section II & III)*
* **Phân tích của AI**:
  - Nhận diện yêu cầu nghiệp vụ: Cấp chứng chỉ nghề nghiệp thời hạn 1 năm. Nếu chứng chỉ hết hạn thì hiển thị trạng thái "Đã hết hạn". Cho phép Admin thu hồi tức thì và hiển thị lỗi: "Chứng chỉ không hợp lệ do vi phạm!".
  - Tạo cấu trúc của file `SRS.md` bao gồm: cấu trúc dữ liệu thực thể chứng chỉ, thuật toán / mã giả tra cứu thông tin chứng chỉ bằng mã code, phân quyền API truy cập.
* **Hoạt động thực tế**:
  1. Khảo sát các thực thể có sẵn trong dự án (`User.java` và `Course.java`).
  2. Xác định mối quan hệ: Certificate có quan hệ `ManyToOne` đến cả User và Course.
  3. Phác thảo chi tiết cấu trúc dữ liệu cho thực thể `Certificate`.
  4. Viết mã giả mô tả thuật toán kiểm tra tuần tự: kiểm tra tồn tại -> kiểm tra thu hồi (`isRevoked`) -> kiểm tra thời hạn phát hành (1 năm).
  5. Thiết kế ma trận phân quyền dựa trên 3 nhóm vai trò: PUBLIC (tra cứu công khai), STUDENT (xem chứng chỉ cá nhân), ADMIN (quản trị, cấp phát/thu hồi).
  6. Tạo file `SRS.md`.

---

### Bước 3: Lập trình tính năng lõi (Nhiệm vụ 2)
* **Prompt đã nhập**:
  > "làm tiếp nhiệm vụ 2" *(Kèm ảnh yêu cầu Nhiệm vụ 2)*
* **Phân tích của AI**:
  - Triển khai toàn bộ các định nghĩa trong `SRS.md` vào mã nguồn Java.
  - Cần viết đầy đủ Entity, Repository, Service, Controller, cập nhật Security và chuẩn bị kịch bản chạy thử.
* **Hoạt động thực tế**:
  1. **Entity**: Code thực thể `Certificate.java` kèm JPA Annotations (`@Entity`, `@ManyToOne`, `@JoinColumn`).
  2. **Repository**: Viết `CertificateRepository.java` khai báo các hàm truy vấn theo mã code và email.
  3. **DTOs**: Viết các lớp request/response (`IssueCertificateRequest`, `RevokeCertificateRequest`, `CertificateLookupResponse`).
  4. **Service**: Hiện thực hóa các logic nghiệp vụ trong `CertificateService.java` bao gồm sinh mã ngẫu nhiên UUID dạng `CERT-XXXXXXXX`, lưu trữ ngày giờ hiện tại, chuyển cờ `isRevoked` và gán lý do vi phạm.
  5. **Controller**: Viết REST Controller ánh xạ các đường dẫn API và tiêm quyền bảo mật tại `SecurityConfig.java`.
  6. **Data Seeder**: Code lớp `DatabaseSeeder.java` tự động chạy khi khởi động Spring Boot để tạo sẵn dữ liệu thử nghiệm.
  7. **Kiểm thử**: Tạo script Python `verify_apis.py` để test nhanh.

---

### Bước 4: Tối ưu và Quản trị ngoại lệ (Nhiệm vụ 3)
* **Prompt đã nhập**:
  > "làm tiếp nhiệm vụ 3 cho tôi :" *(Kèm ảnh yêu cầu Nhiệm vụ 3)*
* **Phân tích của AI**:
  - Nghiệp vụ của APIs cần trả về mã lỗi và mã trạng thái HTTP chính xác khi gặp ngoại lệ để Front-end xử lý thay vì trả về 200 kèm tin nhắn hoặc văng lỗi Stack-trace thô.
  - Cần tạo hai Custom Exceptions tương ứng và đăng ký xử lý tập trung trong Bộ bắt Exception toàn cục (`GlobalExceptionHandler`).
* **Hoạt động thực tế**:
  1. Lập trình lớp ngoại lệ `CertificateRevokedException` (gán mã lỗi mặc định HTTP 400).
  2. Lập trình lớp ngoại lệ `CertificateExpiredException` (gán mã lỗi mặc định HTTP 410 Gone).
  3. Cấu hình `@ExceptionHandler` cho hai lớp ngoại lệ vừa tạo trong lớp `GlobalExceptionHandler.java`.
  4. Cấu trúc lại phương thức `lookupCertificate` của `CertificateService.java` để ném ra các ngoại lệ tùy chỉnh này thay vì trả về DTO khi phát hiện chứng chỉ vi phạm hoặc hết hạn.
  5. Cập nhật các khẳng định kiểm thử trong `verify_apis.py` và chạy script thành công.

---

### Bước 5: Tạo lịch sử Prompts (Yêu cầu bổ sung)
* **Prompt đã nhập**:
  > "làm tiếp cho tôi file này" *(Kèm ảnh yêu cầu file Prompt_History.md)*
* **Hoạt động thực tế**:
  1. Phân tích lịch sử trò chuyện và các yêu cầu hình ảnh của người dùng trong các phiên trò chuyện liên tục.
  2. Tổng hợp các Prompt và mục tiêu/kết quả thực tế dưới dạng bảng biểu và giải trình chi tiết.
  3. Xuất bản tệp tin [Prompt_History.md](file:///Users/khanhly/Desktop/Hackathon_005/Prompt_History.md) trực tiếp tại thư mục làm việc của người dùng.
