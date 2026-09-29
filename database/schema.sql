-- Digital Smart Class - MySQL 8 schema
CREATE DATABASE IF NOT EXISTS digital_smart_class CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE digital_smart_class;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  phone VARCHAR(30),
  password_hash VARCHAR(100) NOT NULL,
  role ENUM('STUDENT','TEACHER','ADMIN','ACCOUNTANT') NOT NULL,
  status ENUM('ACTIVE','PENDING','REJECTED','DISABLED') NOT NULL DEFAULT 'ACTIVE',
  profile_photo VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_users_role (role)
);

CREATE TABLE teacher_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  qualification VARCHAR(200) NOT NULL,
  experience VARCHAR(200),
  address VARCHAR(200),
  cv_path VARCHAR(255),
  certificate_path VARCHAR(255),
  status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  rejection_reason VARCHAR(500),
  reviewed_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE course_categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE courses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  description TEXT,
  outcomes TEXT,
  price DECIMAL(12,2) NOT NULL,
  duration_label VARCHAR(40),
  thumbnail VARCHAR(255),
  published BOOLEAN NOT NULL DEFAULT FALSE,
  teacher_id BIGINT NOT NULL,
  category_id BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (teacher_id) REFERENCES users(id),
  FOREIGN KEY (category_id) REFERENCES course_categories(id),
  INDEX idx_courses_teacher (teacher_id)
);

CREATE TABLE lessons (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  course_id BIGINT NOT NULL,
  module_title VARCHAR(160),
  title VARCHAR(160) NOT NULL,
  description TEXT,
  notes MEDIUMTEXT,
  position INT NOT NULL DEFAULT 0,
  video_url VARCHAR(500),
  video_seconds INT NOT NULL DEFAULT 0,
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  INDEX idx_lessons_course (course_id, position)
);

CREATE TABLE materials (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  lesson_id BIGINT NOT NULL,
  type ENUM('FILE','PRESENTATION','VIDEO') NOT NULL,
  original_name VARCHAR(255) NOT NULL,
  storage_path VARCHAR(255) NOT NULL,
  content_type VARCHAR(120),
  FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);

CREATE TABLE payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method VARCHAR(30) NOT NULL,
  message TEXT NOT NULL,
  reference VARCHAR(60) NOT NULL UNIQUE,
  status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  rejection_reason VARCHAR(500),
  submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_by_id BIGINT,
  reviewed_at DATETIME,
  FOREIGN KEY (student_id) REFERENCES users(id),
  FOREIGN KEY (course_id) REFERENCES courses(id),
  FOREIGN KEY (reviewed_by_id) REFERENCES users(id),
  INDEX idx_pay_status (status)
);

CREATE TABLE enrollments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_enrollment (student_id, course_id),
  FOREIGN KEY (student_id) REFERENCES users(id),
  FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE lesson_progress (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  lesson_id BIGINT NOT NULL,
  watched_seconds INT NOT NULL DEFAULT 0,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_progress (student_id, lesson_id),
  FOREIGN KEY (student_id) REFERENCES users(id),
  FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  message VARCHAR(600) NOT NULL,
  seen BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_notif_user (user_id, created_at)
);

CREATE TABLE app_settings (
  setting_key VARCHAR(60) PRIMARY KEY,
  setting_value VARCHAR(200) NOT NULL
);

CREATE TABLE audit_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  action VARCHAR(60) NOT NULL,
  detail VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
