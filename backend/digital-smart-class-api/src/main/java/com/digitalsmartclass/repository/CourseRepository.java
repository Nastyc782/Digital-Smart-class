package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface CourseRepository extends JpaRepository<Course, Long> {
  List<Course> findByPublishedTrue();
  List<Course> findByTeacherId(Long teacherId);
}
