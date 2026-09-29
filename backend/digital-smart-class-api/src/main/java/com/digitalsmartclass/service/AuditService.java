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
public class AuditService {
  private final AuditLogRepository repo;
  public void log(User actor, String action, String detail) {
    AuditLog a = new AuditLog();
    a.setUser(actor);
    a.setAction(action);
    a.setDetail(detail);
    repo.save(a);
  }
}
