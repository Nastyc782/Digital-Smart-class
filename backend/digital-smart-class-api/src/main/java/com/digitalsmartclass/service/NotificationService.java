package com.digitalsmartclass.service;
import com.digitalsmartclass.entity.*;
import com.digitalsmartclass.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository repo;
  public void send(User user, String message) {
    Notification n = new Notification();
    n.setUser(user);
    n.setMessage(message);
    repo.save(n);
  }
}
