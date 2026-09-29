package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface MaterialRepository extends JpaRepository<Material, Long> {
  List<Material> findByLessonId(Long lessonId);
}
