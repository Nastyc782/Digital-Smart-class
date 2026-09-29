package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  List<Payment> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
  List<Payment> findByStatusOrderBySubmittedAtDesc(ReviewStatus status);
  List<Payment> findAllByOrderBySubmittedAtDesc();
  boolean existsByReference(String reference);
  boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, ReviewStatus status);
  long countByStatus(ReviewStatus status);
  @Query("select coalesce(sum(p.amount),0) from Payment p where p.status = com.digitalsmartclass.entity.ReviewStatus.APPROVED")
  BigDecimal totalRevenue();
  @Query("select p.course.title, sum(p.amount) from Payment p where p.status = com.digitalsmartclass.entity.ReviewStatus.APPROVED group by p.course.title")
  List<Object[]> revenueByCourse();
}
