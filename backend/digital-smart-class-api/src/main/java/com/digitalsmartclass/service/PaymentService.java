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
import com.digitalsmartclass.dto.Dtos.PaymentReq;

@Service
@RequiredArgsConstructor
public class PaymentService {
  private final PaymentRepository payments;
  private final CourseRepository courses;
  private final EnrollmentRepository enrollments;
  private final NotificationService notifications;
  private final AuditService audit;

  @Transactional
  public Payment submit(User student, PaymentReq req) {
    Course course = courses.findById(req.courseId())
        .filter(Course::isPublished)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));
    if (enrollments.existsByStudentIdAndCourseIdAndActiveTrue(student.getId(), course.getId()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You already own this course.");
    if (payments.existsByStudentIdAndCourseIdAndStatus(student.getId(), course.getId(), ReviewStatus.PENDING))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a payment waiting for review for this course.");
    if (payments.existsByReference(req.reference().trim()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This transaction reference was already submitted.");
    Payment p = new Payment();
    p.setStudent(student);
    p.setCourse(course);
    p.setAmount(course.getPrice());              // price always comes from the server, never from the client
    p.setMethod(req.method() == null ? "MTN_MOBILE_MONEY" : req.method());
    p.setMessage(req.message().trim());
    p.setReference(req.reference().trim());
    return payments.save(p);
  }

  @Transactional
  public Payment approve(Long id, User reviewer) {
    Payment p = pending(id);
    p.setStatus(ReviewStatus.APPROVED);
    p.setReviewedBy(reviewer);
    p.setReviewedAt(LocalDateTime.now());
    Enrollment e = enrollments.findByStudentIdAndCourseId(p.getStudent().getId(), p.getCourse().getId()).orElseGet(Enrollment::new);
    e.setStudent(p.getStudent());
    e.setCourse(p.getCourse());
    e.setActive(true);
    enrollments.save(e);
    notifications.send(p.getStudent(), "Your payment for " + p.getCourse().getTitle() + " has been approved. Your course is now available.");
    audit.log(reviewer, "PAYMENT_APPROVED", "payment=" + id + " ref=" + p.getReference());
    return p;
  }

  @Transactional
  public Payment reject(Long id, String reason, User reviewer) {
    Payment p = pending(id);
    p.setStatus(ReviewStatus.REJECTED);
    p.setRejectionReason(reason);
    p.setReviewedBy(reviewer);
    p.setReviewedAt(LocalDateTime.now());
    notifications.send(p.getStudent(), "Your payment for " + p.getCourse().getTitle() + " was rejected. Reason: " + reason);
    audit.log(reviewer, "PAYMENT_REJECTED", "payment=" + id + " reason=" + reason);
    return p;
  }

  private Payment pending(Long id) {
    Payment p = payments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found."));
    if (p.getStatus() != ReviewStatus.PENDING)
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This payment was already reviewed.");
    return p;
  }
}
