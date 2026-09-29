package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "notifications") @Getter @Setter
public class Notification {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "user_id") @JsonIgnore private User user;
  @Column(nullable = false) private String message;
  private boolean seen;
  private LocalDateTime createdAt = LocalDateTime.now();
}
