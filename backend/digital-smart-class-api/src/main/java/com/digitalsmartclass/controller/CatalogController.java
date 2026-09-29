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

/** Any logged-in user can browse. Lesson content is never returned here. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {
  private final CourseRepository courses;
  private final LessonRepository lessons;
  private final CourseCategoryRepository categories;
  private final NotificationRepository notifications;

  @GetMapping("/courses")
  public List<CourseView> list() {
    return courses.findByPublishedTrue().stream().map(c -> CourseView.of(c, lessons.countByCourseId(c.getId()))).toList();
  }
  @GetMapping("/courses/{id}")
  public CourseView detail(@PathVariable Long id) {
    Course c = courses.findById(id).filter(Course::isPublished)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));
    return CourseView.of(c, lessons.countByCourseId(id));
  }
  @GetMapping("/categories")
  public List<CourseCategory> categories() { return categories.findAll(); }

  @GetMapping("/notifications")
  public List<Notification> mine(@AuthenticationPrincipal User u) { return notifications.findByUserIdOrderByCreatedAtDesc(u.getId()); }

  @PostMapping("/notifications/{id}/read")
  public void read(@AuthenticationPrincipal User u, @PathVariable Long id) {
    Notification n = notifications.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));
    if (!n.getUser().getId().equals(u.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not yours.");
    n.setSeen(true);
    notifications.save(n);
  }
}
