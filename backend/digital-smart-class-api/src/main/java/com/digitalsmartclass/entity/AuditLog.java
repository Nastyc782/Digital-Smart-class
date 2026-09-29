package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "audit_logs") @Getter @Setter
public class AuditLog {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne @JoinColumn(name = "user_id") private User user;
  @Column(nullable = false) private String action;
  private String detail;
  private LocalDateTime createdAt = LocalDateTime.now();
}
