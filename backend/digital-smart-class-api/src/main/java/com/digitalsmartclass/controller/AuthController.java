package com.digitalsmartclass.controller;
import com.digitalsmartclass.dto.Dtos.*;
import com.digitalsmartclass.entity.*;
import com.digitalsmartclass.repository.*;
import com.digitalsmartclass.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.digitalsmartclass.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserRepository users;
  private final TeacherApplicationRepository applications;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  @PostMapping("/register-student")
  @ResponseStatus(HttpStatus.CREATED)
  public UserView registerStudent(@Valid @RequestBody RegisterReq r) {
    return UserView.of(create(r.fullName(), r.email(), r.phone(), r.password(), Role.STUDENT, UserStatus.ACTIVE));
  }

  @PostMapping("/register-teacher")
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public UserView registerTeacher(@Valid @RequestBody TeacherRegisterReq r) {
    User u = create(r.fullName(), r.email(), r.phone(), r.password(), Role.TEACHER, UserStatus.PENDING);
    TeacherApplication a = new TeacherApplication();
    a.setUser(u);
    a.setQualification(r.qualification());
    a.setExperience(r.experience());
    a.setAddress(r.address());
    applications.save(a);
    return UserView.of(u);
  }

  @PostMapping("/login")
  public AuthRes login(@Valid @RequestBody LoginReq r) {
    User u = users.findByEmail(r.email().trim().toLowerCase()).orElse(null);
    if (u == null || !encoder.matches(r.password(), u.getPasswordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password.");
    if (u.getStatus() == UserStatus.DISABLED)
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is deactivated. Contact the administrator.");
    if (u.getStatus() == UserStatus.REJECTED)
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your teacher application was rejected. Contact the administrator.");
    // PENDING/REJECTED teachers may log in to see their approval status, but every teacher action is blocked by AccessService.
    return new AuthRes(jwt.generate(u), u.getRole().name(), u.getStatus().name(), u.getFullName());
  }

  private User create(String name, String email, String phone, String password, Role role, UserStatus status) {
    String e = email.trim().toLowerCase();
    if (users.existsByEmail(e)) throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
    User u = new User();
    u.setFullName(name.trim()); u.setEmail(e); u.setPhone(phone); u.setRole(role); u.setStatus(status);
    u.setPasswordHash(encoder.encode(password));
    return users.save(u);
  }
}
