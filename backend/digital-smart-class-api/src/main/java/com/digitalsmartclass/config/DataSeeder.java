package com.digitalsmartclass.config;

import com.digitalsmartclass.entity.*;
import com.digitalsmartclass.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
  private final UserRepository users;
  private final AppSettingRepository settings;
  private final PasswordEncoder encoder;
  @Value("${app.admin.email}") String adminEmail;
  @Value("${app.admin.password}") String adminPassword;
  @Value("${app.accountant.email}") String accEmail;
  @Value("${app.accountant.password}") String accPassword;

  @Override
  public void run(String... args) {
    create("Platform Admin", adminEmail, adminPassword, Role.ADMIN);
    create("Platform Accountant", accEmail, accPassword, Role.ACCOUNTANT);
    if (!settings.existsById("completion_percent")) {
      AppSetting s = new AppSetting();
      s.setName("completion_percent");
      s.setValue("90");
      settings.save(s);
    }
  }
  private void create(String name, String email, String password, Role role) {
    email = email.trim().toLowerCase();
    if (users.existsByEmail(email)) return;
    User u = new User();
    u.setFullName(name); u.setEmail(email); u.setRole(role); u.setStatus(UserStatus.ACTIVE);
    u.setPasswordHash(encoder.encode(password));
    users.save(u);
  }
}
