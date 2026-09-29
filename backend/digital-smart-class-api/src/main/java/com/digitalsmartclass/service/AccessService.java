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

/** Single place for every authorization rule that is not just a role check. */
@Service
@RequiredArgsConstructor
public class AccessService {
  private final EnrollmentRepository enrollments;

  public void requireEnrollment(User student, Long courseId) {
    if (!enrollments.existsByStudentIdAndCourseIdAndActiveTrue(student.getId(), courseId))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Course locked: no approved enrollment.");
  }
  public void requireApprovedTeacher(User teacher) {
    if (teacher.getStatus() != UserStatus.ACTIVE)
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your teacher account is not approved yet.");
  }
  public void requireOwner(User teacher, Course course) {
    if (!course.getTeacher().getId().equals(teacher.getId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This course belongs to another teacher.");
  }
}
