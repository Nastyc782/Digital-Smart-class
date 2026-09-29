package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "courses") @Getter @Setter
public class Course {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false) private String title;
  @Column(columnDefinition = "TEXT") private String description;
  @Column(columnDefinition = "TEXT") private String outcomes;
  @Column(nullable = false) private BigDecimal price;
  private String durationLabel;
  private String thumbnail;
  private boolean published;
  @ManyToOne(optional = false) @JoinColumn(name = "teacher_id") private User teacher;
  @ManyToOne @JoinColumn(name = "category_id") private CourseCategory category;
  private LocalDateTime createdAt = LocalDateTime.now();
}
