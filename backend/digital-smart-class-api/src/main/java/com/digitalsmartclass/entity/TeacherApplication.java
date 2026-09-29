package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "teacher_applications") @Getter @Setter
public class TeacherApplication {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @OneToOne(optional = false) @JoinColumn(name = "user_id") private User user;
  @Column(nullable = false) private String qualification;
  private String experience;
  private String address;
  private String cvPath;
  private String certificatePath;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private ReviewStatus status = ReviewStatus.PENDING;
  private String rejectionReason;
  private LocalDateTime reviewedAt;
}
