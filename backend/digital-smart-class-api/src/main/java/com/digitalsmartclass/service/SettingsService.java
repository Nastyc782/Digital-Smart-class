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
public class SettingsService {
  private final AppSettingRepository repo;
  public int completionPercent() {
    return repo.findById("completion_percent").map(s -> Integer.parseInt(s.getValue())).orElse(90);
  }
  public void setCompletionPercent(int percent) {
    AppSetting s = repo.findById("completion_percent").orElseGet(AppSetting::new);
    s.setName("completion_percent");
    s.setValue(String.valueOf(percent));
    repo.save(s);
  }
}
