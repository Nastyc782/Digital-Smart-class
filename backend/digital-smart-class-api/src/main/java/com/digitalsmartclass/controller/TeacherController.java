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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
  private final CourseRepository courses;
  private final CourseCategoryRepository categories;
  private final LessonRepository lessons;
  private final MaterialRepository materials;
  private final EnrollmentRepository enrollments;
  private final AccessService access;
  private final ProgressService progressService;
  private final FileStorageService storage;

  @GetMapping("/courses")
  public List<CourseView> mine(@AuthenticationPrincipal User me) {
    access.requireApprovedTeacher(me);
    return courses.findByTeacherId(me.getId()).stream().map(c -> CourseView.of(c, lessons.countByCourseId(c.getId()))).toList();
  }

  @PostMapping("/courses")
  @ResponseStatus(HttpStatus.CREATED)
  public CourseView create(@AuthenticationPrincipal User me, @Valid @RequestBody CourseReq r) {
    access.requireApprovedTeacher(me);
    Course c = new Course();
    c.setTeacher(me);
    apply(c, r);
    return CourseView.of(courses.save(c), 0);
  }

  @PutMapping("/courses/{id}")
  public CourseView update(@AuthenticationPrincipal User me, @PathVariable Long id, @Valid @RequestBody CourseReq r) {
    access.requireApprovedTeacher(me);
    Course c = owned(me, id);
    apply(c, r);
    return CourseView.of(courses.save(c), lessons.countByCourseId(id));
  }

  @PostMapping("/courses/{id}/lessons")
  @ResponseStatus(HttpStatus.CREATED)
  public Long addLesson(@AuthenticationPrincipal User me, @PathVariable Long id, @Valid @RequestBody LessonReq r) {
    access.requireApprovedTeacher(me);
    Lesson l = new Lesson();
    l.setCourse(owned(me, id));
    l.setTitle(r.title()); l.setModuleTitle(r.moduleTitle()); l.setDescription(r.description()); l.setNotes(r.notes());
    l.setPosition(r.position()); l.setVideoUrl(r.videoUrl()); l.setVideoSeconds(r.videoSeconds());
    return lessons.save(l).getId();
  }

  @PostMapping("/lessons/{lessonId}/materials")
  @ResponseStatus(HttpStatus.CREATED)
  public MaterialView upload(@AuthenticationPrincipal User me, @PathVariable Long lessonId,
                             @RequestParam MaterialType type, @RequestParam MultipartFile file) {
    access.requireApprovedTeacher(me);
    Lesson l = lessons.findById(lessonId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found."));
    access.requireOwner(me, l.getCourse());
    Material m = new Material();
    m.setLesson(l);
    m.setType(type);
    m.setOriginalName(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
    m.setContentType(file.getContentType());
    m.setStoragePath(storage.store(file));
    return MaterialView.of(materials.save(m));
  }

  /** Only students enrolled in THIS teacher's course are returned. */
  @GetMapping("/courses/{id}/students")
  public List<StudentProgressView> students(@AuthenticationPrincipal User me, @PathVariable Long id) {
    access.requireApprovedTeacher(me);
    owned(me, id);
    long total = lessons.countByCourseId(id);
    return enrollments.findByCourseIdAndActiveTrue(id).stream().map(e -> {
      int pct = progressService.coursePercent(e.getStudent().getId(), id);
      return new StudentProgressView(e.getStudent().getId(), e.getStudent().getFullName(), e.getStudent().getEmail(),
          pct, Math.round(pct * total / 100.0), total);
    }).toList();
  }

  private Course owned(User me, Long id) {
    Course c = courses.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));
    access.requireOwner(me, c);
    return c;
  }
  private void apply(Course c, CourseReq r) {
    c.setTitle(r.title()); c.setDescription(r.description()); c.setOutcomes(r.outcomes());
    c.setPrice(r.price()); c.setDurationLabel(r.durationLabel()); c.setPublished(r.published());
    c.setCategory(r.categoryId() == null ? null : categories.findById(r.categoryId()).orElse(null));
  }
}
