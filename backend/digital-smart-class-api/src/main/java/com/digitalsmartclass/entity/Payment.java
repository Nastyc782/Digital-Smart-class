package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "payments") @Getter @Setter
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "student_id") private User student;
  @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
  @Column(nullable = false) private BigDecimal amount;
  @Column(nullable = false) private String method;
  @Column(nullable = false, columnDefinition = "TEXT") private String message;
  @Column(nullable = false, unique = true) private String reference;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private ReviewStatus status = ReviewStatus.PENDING;
  private String rejectionReason;
  private LocalDateTime submittedAt = LocalDateTime.now();
  @ManyToOne @JoinColumn(name = "reviewed_by_id") private User reviewedBy;
  private LocalDateTime reviewedAt;
}
