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
import java.math.BigDecimal;

/** Payment review and revenue. Available to ADMIN and ACCOUNTANT only (see SecurityConfig). */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
  private final PaymentRepository payments;
  private final PaymentService paymentService;

  @GetMapping("/payments")
  public List<PaymentView> list(@RequestParam(required = false) ReviewStatus status) {
    return (status == null ? payments.findAllByOrderBySubmittedAtDesc() : payments.findByStatusOrderBySubmittedAtDesc(status))
        .stream().map(PaymentView::of).toList();
  }
  @PostMapping("/payments/{id}/approve")
  public PaymentView approve(@AuthenticationPrincipal User me, @PathVariable Long id) {
    return PaymentView.of(paymentService.approve(id, me));
  }
  @PostMapping("/payments/{id}/reject")
  public PaymentView reject(@AuthenticationPrincipal User me, @PathVariable Long id, @Valid @RequestBody ReasonReq r) {
    return PaymentView.of(paymentService.reject(id, r.reason(), me));
  }
  @GetMapping("/revenue")
  public Map<String, Object> revenue() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("totalRevenue", payments.totalRevenue());
    m.put("pending", payments.countByStatus(ReviewStatus.PENDING));
    m.put("approved", payments.countByStatus(ReviewStatus.APPROVED));
    m.put("rejected", payments.countByStatus(ReviewStatus.REJECTED));
    Map<String, BigDecimal> byCourse = new LinkedHashMap<>();
    for (Object[] row : payments.revenueByCourse()) byCourse.put((String) row[0], (BigDecimal) row[1]);
    m.put("byCourse", byCourse);
    return m;
  }
}
