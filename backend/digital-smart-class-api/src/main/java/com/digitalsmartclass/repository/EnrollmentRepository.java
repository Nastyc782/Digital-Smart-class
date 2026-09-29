package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
  boolean existsByStudentIdAndCourseIdAndActiveTrue(Long studentId, Long courseId);
  Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
  List<Enrollment> findByStudentIdAndActiveTrue(Long studentId);
  List<Enrollment> findByCourseIdAndActiveTrue(Long courseId);
}
