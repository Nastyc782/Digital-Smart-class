package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
  Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
  List<LessonProgress> findByStudentIdAndLessonCourseId(Long studentId, Long courseId);
  long countByStudentIdAndLessonCourseIdAndCompletedTrue(Long studentId, Long courseId);
}
