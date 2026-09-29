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
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
  private final PaymentService paymentService;
  private final PaymentRepository payments;
  private final EnrollmentRepository enrollments;
  private final LessonRepository lessons;
  private final LessonProgressRepository progress;
  private final MaterialRepository materials;
  private final ProgressService progressService;
  private final AccessService access;

  @PostMapping("/payments")
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentView pay(@AuthenticationPrincipal User me, @Valid @RequestBody PaymentReq r) {
    return PaymentView.of(paymentService.submit(me, r));
  }
  @GetMapping("/payments")
  public List<PaymentView> myPayments(@AuthenticationPrincipal User me) {
    return payments.findByStudentIdOrderBySubmittedAtDesc(me.getId()).stream().map(PaymentView::of).toList();
  }

  @GetMapping("/courses")
  public List<CourseView> myCourses(@AuthenticationPrincipal User me) {
    return enrollments.findByStudentIdAndActiveTrue(me.getId()).stream()
        .map(e -> CourseView.of(e.getCourse(), lessons.countByCourseId(e.getCourse().getId()))).toList();
  }

  /** Backend enforcement: no approved enrollment means 403, whatever the frontend shows. */
  @GetMapping("/courses/{courseId}/lessons")
  public List<LessonView> lessons(@AuthenticationPrincipal User me, @PathVariable Long courseId) {
    access.requireEnrollment(me, courseId);
    Map<Long, LessonProgress> done = progress.findByStudentIdAndLessonCourseId(me.getId(), courseId).stream()
        .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p));
    return lessons.findByCourseIdOrderByPositionAsc(courseId).stream().map(l -> {
      LessonProgress p = done.get(l.getId());
      return new LessonView(l.getId(), l.getModuleTitle(), l.getTitle(), l.getDescription(), l.getNotes(), l.getPosition(),
          l.getVideoUrl(), l.getVideoSeconds(), materials.findByLessonId(l.getId()).stream().map(MaterialView::of).toList(),
          p == null ? 0 : p.getWatchedSeconds(), p != null && p.isCompleted());
    }).toList();
  }

  @PostMapping("/lessons/{lessonId}/progress")
  public ProgressRes saveProgress(@AuthenticationPrincipal User me, @PathVariable Long lessonId, @Valid @RequestBody ProgressReq r) {
    return progressService.record(me, lessonId, r.watchedSeconds());
  }
}
