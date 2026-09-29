package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "users") @Getter @Setter
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false) private String fullName;
  @Column(nullable = false, unique = true) private String email;
  private String phone;
  @JsonIgnore @Column(nullable = false) private String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private UserStatus status = UserStatus.ACTIVE;
  private String profilePhoto;
  private LocalDateTime createdAt = LocalDateTime.now();
}
