package com.digitalsmartclass.service;
import com.digitalsmartclass.entity.*;
import com.digitalsmartclass.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import com.digitalsmartclass.dto.Dtos.ProgressRes;

@Service
@RequiredArgsConstructor
public class ProgressService {
  private final LessonRepository lessons;
  private final LessonProgressRepository progress;
  private final AccessService access;
  private final SettingsService settings;

  @Transactional
  public ProgressRes record(User student, Long lessonId, int reportedSeconds) {
    Lesson lesson = lessons.findById(lessonId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found."));
    access.requireEnrollment(student, lesson.getCourse().getId());
    LessonProgress lp = progress.findByStudentIdAndLessonId(student.getId(), lessonId).orElseGet(() -> {
      LessonProgress n = new LessonProgress();
      n.setStudent(student);
      n.setLesson(lesson);
      return n;
    });
    int total = lesson.getVideoSeconds();
    int watched = Math.max(lp.getWatchedSeconds(), Math.min(reportedSeconds, total)); // progress never goes backwards or above the video length
    lp.setWatchedSeconds(watched);
    boolean done = total == 0 || (long) watched * 100 >= (long) total * settings.completionPercent();
    lp.setCompleted(lp.isCompleted() || done);
    lp.setUpdatedAt(LocalDateTime.now());
    progress.save(lp);
    return new ProgressRes(lp.getWatchedSeconds(), lp.isCompleted(), coursePercent(student.getId(), lesson.getCourse().getId()));
  }

  public int coursePercent(Long studentId, Long courseId) {
    long total = lessons.countByCourseId(courseId);
    if (total == 0) return 0;
    return (int) (progress.countByStudentIdAndLessonCourseIdAndCompletedTrue(studentId, courseId) * 100 / total);
  }
}
