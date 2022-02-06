package dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

/**
 * DTO representing an application error of any kind.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationError {
  private ErrorCode errorCode;
  private Instant timeStamp;
  private String message;

  public ResponseEntity<ApplicationError> toResponse() {
    return ResponseEntity.status(errorCode.status()).body(this);
  }
}
