package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "enrollments") @Getter @Setter
public class Enrollment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "student_id") private User student;
  @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
  private boolean active = true;
  private LocalDateTime enrolledAt = LocalDateTime.now();
}
