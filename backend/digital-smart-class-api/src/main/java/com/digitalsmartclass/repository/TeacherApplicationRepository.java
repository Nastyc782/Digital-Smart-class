package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface TeacherApplicationRepository extends JpaRepository<TeacherApplication, Long> {
  List<TeacherApplication> findByStatus(ReviewStatus status);
}
