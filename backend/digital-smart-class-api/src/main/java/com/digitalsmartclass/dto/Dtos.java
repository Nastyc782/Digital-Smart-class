package com.digitalsmartclass.dto;

import com.digitalsmartclass.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dtos {
  public record RegisterReq(@NotBlank String fullName, @Email @NotBlank String email, String phone,
                            @NotBlank @Size(min = 8, message = "Password must have at least 8 characters") String password) {}
  public record TeacherRegisterReq(@NotBlank String fullName, @Email @NotBlank String email, String phone,
                                   @NotBlank @Size(min = 8) String password, @NotBlank String qualification,
                                   String experience, String address) {}
  public record LoginReq(@NotBlank String email, @NotBlank String password) {}
  public record AuthRes(String token, String role, String status, String fullName) {}
  public record PaymentReq(@NotNull Long courseId, @NotBlank @Size(min = 10) String message,
                           @NotBlank @Size(min = 6, max = 60) String reference,
                           @Pattern(regexp = "MTN_MOBILE_MONEY|MOMO_PAY") String method) {}
  public record ReasonReq(@NotBlank String reason) {}
  public record ProgressReq(@Min(0) int watchedSeconds) {}
  public record ThresholdReq(@Min(50) @Max(100) int percent) {}
  public record CategoryReq(@NotBlank String name) {}
  public record CourseReq(@NotBlank String title, String description, String outcomes,
                          @NotNull @DecimalMin("0") BigDecimal price, String durationLabel,
                          Long categoryId, boolean published) {}
  public record LessonReq(@NotBlank String title, String moduleTitle, String description, String notes,
                          int position, String videoUrl, @Min(0) int videoSeconds) {}

  public record CourseView(Long id, String title, String description, String outcomes, BigDecimal price,
                           String durationLabel, String category, String teacherName, boolean published, long lessons) {
    public static CourseView of(Course c, long lessons) {
      return new CourseView(c.getId(), c.getTitle(), c.getDescription(), c.getOutcomes(), c.getPrice(),
          c.getDurationLabel(), c.getCategory() == null ? null : c.getCategory().getName(),
          c.getTeacher().getFullName(), c.isPublished(), lessons);
    }
  }
  public record PaymentView(Long id, String studentName, String studentEmail, Long courseId, String courseTitle,
                            BigDecimal amount, String method, String message, String reference, ReviewStatus status,
                            String rejectionReason, LocalDateTime submittedAt) {
    public static PaymentView of(Payment p) {
      return new PaymentView(p.getId(), p.getStudent().getFullName(), p.getStudent().getEmail(), p.getCourse().getId(),
          p.getCourse().getTitle(), p.getAmount(), p.getMethod(), p.getMessage(), p.getReference(), p.getStatus(),
          p.getRejectionReason(), p.getSubmittedAt());
    }
  }
  public record MaterialView(Long id, MaterialType type, String name) {
    public static MaterialView of(Material m) { return new MaterialView(m.getId(), m.getType(), m.getOriginalName()); }
  }
  public record LessonView(Long id, String moduleTitle, String title, String description, String notes, int position,
                           String videoUrl, int videoSeconds, List<MaterialView> materials, int watchedSeconds, boolean completed) {}
  public record ProgressRes(int watchedSeconds, boolean completed, int courseProgressPercent) {}
  public record StudentProgressView(Long studentId, String name, String email, int progressPercent, long completedLessons, long totalLessons) {}
  public record ApplicationView(Long id, Long userId, String name, String email, String phone, String qualification,
                                String experience, String address, ReviewStatus status, String rejectionReason) {
    public static ApplicationView of(TeacherApplication a) {
      User u = a.getUser();
      return new ApplicationView(a.getId(), u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), a.getQualification(),
          a.getExperience(), a.getAddress(), a.getStatus(), a.getRejectionReason());
    }
  }
  public record UserView(Long id, String fullName, String email, String phone, Role role, UserStatus status) {
    public static UserView of(User u) { return new UserView(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getRole(), u.getStatus()); }
  }
}
