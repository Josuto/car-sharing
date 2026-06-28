package com.example.cs.psp;

import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class PspController {

  private final BalanceStore store;

  @PostMapping("/process")
  ResponseEntity<Void> process(@RequestBody ProcessRequest request) {
    if (!store.exists(request.bankAccount())) {
      return ResponseEntity.notFound().build();
    }
    boolean charged = store.deduct(request.bankAccount(), request.amount());
    return charged
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @GetMapping("/balances")
  Map<String, BigDecimal> balances() {
    return store.all();
  }

  record ProcessRequest(String bankAccount, BigDecimal amount, String currency) {}
}
