package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "lessons") @Getter @Setter
public class Lesson {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
  private String moduleTitle;
  @Column(nullable = false) private String title;
  @Column(columnDefinition = "TEXT") private String description;
  @Column(columnDefinition = "MEDIUMTEXT") private String notes;
  private int position;
  private String videoUrl;
  private int videoSeconds;
}
