package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "lesson_progress") @Getter @Setter
public class LessonProgress {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "student_id") private User student;
  @ManyToOne(optional = false) @JoinColumn(name = "lesson_id") private Lesson lesson;
  private int watchedSeconds;
  private boolean completed;
  private LocalDateTime updatedAt = LocalDateTime.now();
}
