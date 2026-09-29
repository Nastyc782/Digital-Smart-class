package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface LessonRepository extends JpaRepository<Lesson, Long> {
  List<Lesson> findByCourseIdOrderByPositionAsc(Long courseId);
  long countByCourseId(Long courseId);
}
