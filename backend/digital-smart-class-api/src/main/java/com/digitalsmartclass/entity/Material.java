package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "materials") @Getter @Setter
public class Material {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "lesson_id") private Lesson lesson;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private MaterialType type;
  @Column(nullable = false) private String originalName;
  @JsonIgnore @Column(nullable = false) private String storagePath;
  private String contentType;
}
