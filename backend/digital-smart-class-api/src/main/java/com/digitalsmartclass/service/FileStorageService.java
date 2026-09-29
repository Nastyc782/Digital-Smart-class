package com.digitalsmartclass.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

/** Files live outside any public web folder and are only returned through FileController after an access check. */
@Service
public class FileStorageService {
  private static final Set<String> ALLOWED = Set.of("pdf", "doc", "docx", "ppt", "pptx", "zip", "txt", "mp4", "webm");
  private final Path root;

  public FileStorageService(@Value("${app.upload-dir}") String dir) throws IOException {
    this.root = Paths.get(dir).toAbsolutePath().normalize();
    Files.createDirectories(root);
  }
  public String store(MultipartFile f) {
    if (f.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is empty.");
    String name = StringUtils.cleanPath(f.getOriginalFilename() == null ? "file" : f.getOriginalFilename());
    String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase() : "";
    if (!ALLOWED.contains(ext)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type not allowed. Allowed: " + ALLOWED);
    String stored = UUID.randomUUID() + "." + ext;
    try {
      Files.copy(f.getInputStream(), root.resolve(stored));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the file.");
    }
    return stored;
  }
  public Resource load(String stored) {
    try {
      Path p = root.resolve(stored).normalize();
      if (!p.startsWith(root) || !Files.exists(p)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found.");
      return new UrlResource(p.toUri());
    } catch (java.net.MalformedURLException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found.");
    }
  }
}
