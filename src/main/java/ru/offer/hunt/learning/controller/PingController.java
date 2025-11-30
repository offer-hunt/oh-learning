package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ping", description = "Технические пинги сервиса")
public class PingController {
  @GetMapping("/api/ping")
  @Operation(summary = "Пинг")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<String> ping() {
    return ResponseEntity.ok("pong");
  }

  @GetMapping("/api/secure/ping")
  @Operation(summary = "Пинг (защищённый)")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<String> securePing() {
    return ResponseEntity.ok("pong-secure");
  }
}
