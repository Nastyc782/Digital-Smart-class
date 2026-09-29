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
import org.springframework.core.io.Resource;
import org.springframework.http.*;

/** Every download passes through here: authentication, then enrollment/ownership, then the file. */
@RestController
@RequiredArgsConstructor
public class FileController {
  private final MaterialRepository materials;
  private final AccessService access;
  private final FileStorageService storage;

  @GetMapping("/api/files/{id}")
  public ResponseEntity<Resource> get(@AuthenticationPrincipal User me, @PathVariable Long id) {
    Material m = materials.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found."));
    Course course = m.getLesson().getCourse();
    switch (me.getRole()) {
      case ADMIN -> { }
      case TEACHER -> access.requireOwner(me, course);
      case STUDENT -> access.requireEnrollment(me, course.getId());
      default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to course files.");
    }
    MediaType type = MediaType.APPLICATION_OCTET_STREAM;
    try { if (m.getContentType() != null) type = MediaType.parseMediaType(m.getContentType()); } catch (Exception ignored) { }
    String safe = m.getOriginalName().replaceAll("[^A-Za-z0-9._-]", "_");
    return ResponseEntity.ok().contentType(type)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safe + "\"")
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .body(storage.load(m.getStoragePath()));
  }
}
