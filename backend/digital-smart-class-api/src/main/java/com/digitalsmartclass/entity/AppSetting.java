package com.digitalsmartclass.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name = "app_settings") @Getter @Setter
public class AppSetting {
  @Id @Column(name = "setting_key") private String name;
  @Column(name = "setting_value", nullable = false) private String value;
}
