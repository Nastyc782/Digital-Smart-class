package com.digitalsmartclass.controller;
import com.digitalsmartclass.dto.Dtos.*;
import com.digitalsmartclass.entity.*;
import com.digitalsmartclass.repository.*;
import com.digitalsmartclass.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
  private final UserRepository users;
  private final TeacherApplicationRepository applications;
  private final CourseRepository courses;
  private final CourseCategoryRepository categories;
  private final PaymentRepository payments;
  private final SettingsService settings;
  private final NotificationService notifications;
  private final AuditService audit;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  @GetMapping("/stats")
  public Map<String, Object> stats() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("students", users.countByRole(Role.STUDENT));
    m.put("teachers", users.countByRole(Role.TEACHER));
    m.put("courses", courses.count());
    m.put("pendingTeachers", applications.findByStatus(ReviewStatus.PENDING).size());
    m.put("pendingPayments", payments.countByStatus(ReviewStatus.PENDING));
    m.put("totalRevenue", payments.totalRevenue());
    return m;
  }

  @GetMapping("/users")
  public List<UserView> users(@RequestParam(required = false) Role role) {
    return (role == null ? users.findAll() : users.findByRole(role)).stream().map(UserView::of).toList();
  }
  @PatchMapping("/users/{id}/status")
  public UserView setStatus(@AuthenticationPrincipal User me, @PathVariable Long id, @RequestParam UserStatus value) {
    if (me.getId().equals(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot change your own status.");
    User u = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    u.setStatus(value);
    audit.log(me, "USER_STATUS", "user=" + id + " status=" + value);
    return UserView.of(users.save(u));
  }

  @GetMapping("/teacher-applications")
  public List<ApplicationView> apps(@RequestParam(required = false) ReviewStatus status) {
    return (status == null ? applications.findAll() : applications.findByStatus(status)).stream().map(ApplicationView::of).toList();
  }
  @PostMapping("/teacher-applications/{id}/approve")
  @Transactional
  public ApplicationView approve(@AuthenticationPrincipal User me, @PathVariable Long id) {
    return review(me, id, ReviewStatus.APPROVED, null);
  }
  @PostMapping("/teacher-applications/{id}/reject")
  @Transactional
  public ApplicationView reject(@AuthenticationPrincipal User me, @PathVariable Long id, @Valid @RequestBody ReasonReq r) {
    return review(me, id, ReviewStatus.REJECTED, r.reason());
  }
  private ApplicationView review(User admin, Long id, ReviewStatus status, String reason) {
    TeacherApplication a = applications.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found."));
    if (a.getStatus() != ReviewStatus.PENDING) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reviewed.");
    a.setStatus(status); a.setRejectionReason(reason); a.setReviewedAt(LocalDateTime.now());
    User t = a.getUser();
    t.setStatus(status == ReviewStatus.APPROVED ? UserStatus.ACTIVE : UserStatus.REJECTED);
    users.save(t);
    notifications.send(t, status == ReviewStatus.APPROVED ? "Your teacher application has been approved."
        : "Your teacher application has been rejected. Reason: " + reason);
    audit.log(admin, "TEACHER_" + status, "user=" + t.getId());
    return ApplicationView.of(applications.save(a));
  }

  @PutMapping("/settings/completion-percent")
  public Map<String, Integer> setThreshold(@AuthenticationPrincipal User me, @Valid @RequestBody ThresholdReq r) {
    settings.setCompletionPercent(r.percent());
    audit.log(me, "SETTING_COMPLETION", String.valueOf(r.percent()));
    return Map.of("completionPercent", r.percent());
  }
  @GetMapping("/settings/completion-percent")
  public Map<String, Integer> threshold() { return Map.of("completionPercent", settings.completionPercent()); }

  @PostMapping("/accountants")
  @ResponseStatus(HttpStatus.CREATED)
  public UserView createAccountant(@AuthenticationPrincipal User me, @Valid @RequestBody RegisterReq r) {
    String e = r.email().trim().toLowerCase();
    if (users.existsByEmail(e)) throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
    User u = new User();
    u.setFullName(r.fullName().trim()); u.setEmail(e); u.setPhone(r.phone());
    u.setRole(Role.ACCOUNTANT); u.setStatus(UserStatus.ACTIVE);
    u.setPasswordHash(encoder.encode(r.password()));
    audit.log(me, "ACCOUNTANT_CREATED", "email=" + e);
    return UserView.of(users.save(u));
  }

  @PostMapping("/categories")
  @ResponseStatus(HttpStatus.CREATED)
  public CourseCategory addCategory(@Valid @RequestBody CategoryReq r) {
    CourseCategory c = new CourseCategory();
    c.setName(r.name().trim());
    return categories.save(c);
  }
}
