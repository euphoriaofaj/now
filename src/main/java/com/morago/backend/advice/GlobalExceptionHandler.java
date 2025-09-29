package com.morago.backend.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.morago.backend.dto.common.ErrorResponse;
import com.morago.backend.exception.ApiException;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.exception.UserNotFoundException;
import com.morago.backend.exception.token.ExpireJwtTokenException;
import com.morago.backend.exception.token.InvalidJwtTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ---------- 400: malformed JSON / wrong field types ---------- */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String msg = "Malformed JSON request";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            msg = "Invalid value for field: " + ife.getPathReference();
        }
        return build(HttpStatus.BAD_REQUEST, msg, req, null);
    }

    /* ---------- 400: @Valid on request body (DTO) ---------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(err -> errors.put(err.getObjectName(), err.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
    }

    /* ---------- 400: binding query/path parameters ---------- */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Binding failed", req, errors);
    }

    /* ---------- 400: method parameter validation (@Validated) ---------- */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "Constraint violation", req, errors);
    }

    /* ---------- 400: parameter type mismatch ---------- */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = "Parameter '%s' has invalid value '%s'".formatted(ex.getName(), ex.getValue());
        return build(HttpStatus.BAD_REQUEST, msg, req, null);
    }

    /* ---------- 401 ---------- */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", req, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Invalid username or password", req, null, "AUTH_INVALID_CREDENTIALS");
    }

    /* ---------- 403 ---------- */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", req, null);
    }

    /* ---------- 404: your custom not-found exceptions ---------- */
    @ExceptionHandler({UserNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    /* ---------- 404: no handler (enable spring.mvc.throw-exception-if-no-handler-found=true) ---------- */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "No handler for " + ex.getRequestURL(), req, null);
    }

    /* ---------- 409: unique constraints, foreign keys, etc. ---------- */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        Throwable root = ex.getMostSpecificCause();

        String text = (root.getMessage() != null) ? root.getMessage() : ex.getMessage();
        String lower = (text != null) ? text.toLowerCase() : "";

        String constraint = "";
        Throwable cause = ex.getCause();
        if (cause instanceof org.hibernate.exception.ConstraintViolationException cve
                && cve.getConstraintName() != null) {
            constraint = cve.getConstraintName().toLowerCase();
        }

        if ((lower.contains("duplicate") || lower.contains("unique"))
                && (lower.contains("phone") || lower.contains("phone_number") || lower.contains("username")
                || constraint.contains("phone") || constraint.contains("username"))) {
            return build(HttpStatus.CONFLICT, "Phone is already registered", req, null, "PHONE_TAKEN");
        }

        String msg = resolveDataIntegrityMessage(ex);
        return build(HttpStatus.CONFLICT, msg, req, null);
    }

    /* ---------- 400: business validation ---------- */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    /* ---------- 401: JWT-related ---------- */
    @ExceptionHandler({ExpireJwtTokenException.class, InvalidJwtTokenException.class})
    public ResponseEntity<ErrorResponse> handleJwt(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    /* ---------- 400: missing parameter/header ---------- */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            org.springframework.web.bind.MissingServletRequestParameterException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing parameter: " + ex.getParameterName(), req, null);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            org.springframework.web.bind.MissingRequestHeaderException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing header: " + ex.getHeaderName(), req, null);
    }

    /* ---------- 405 / 415 / 413 ---------- */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", req, null);
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            org.springframework.web.HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", req, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "Payload too large", req, null);
    }

    /* ---------- Custom API exception base ---------- */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest req) {
        return build(ex.getStatus(), ex.getMessage(), req, ex.getErrors(), ex.getCode());
    }

    /* ---------- 500: everything else ---------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req, null);
    }

    /* ---------- 409: business conflicts (e.g., insufficient funds) ---------- */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        // Можно тоньше распознать текст и выдать code, например INSF_FUNDS
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null, "BUSINESS_CONFLICT");
    }

    /* ---------- 409: optimistic locking (concurrent updates) ---------- */
    @ExceptionHandler({
            org.springframework.dao.OptimisticLockingFailureException.class,
            jakarta.persistence.OptimisticLockException.class
    })
    public ResponseEntity<ErrorResponse> handleOptimisticLock(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Concurrent update, please retry", req, null, "CONCURRENT_MODIFICATION");
    }

    /* ---------- 404: JPA not found fallbacks ---------- */
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex,
                                                              HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(java.util.NoSuchElementException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Resource not found", req, null);
    }

    /* ================= helpers ================= */

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest req, Map<String, String> errors) {
        return build(status, message, req, errors, null);
    }

    /** Try to identify the cause of the conflict (unique/fk) across different DBs/drivers */
    private String resolveDataIntegrityMessage(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof org.hibernate.exception.ConstraintViolationException h) {
            String name = h.getConstraintName();
            if (name != null && name.toLowerCase().contains("unique")) {
                return "Duplicate value (unique constraint)";
            }
        }

        Throwable root = ex.getMostSpecificCause();

        if (root instanceof java.sql.SQLException sql) {
            String state = sql.getSQLState();
            // Postgres: 23505 | MySQL: 23000 + errorCode 1062
            if ("23505".equals(state) || "23000".equals(state) || sql.getErrorCode() == 1062) {
                return "Duplicate value (unique constraint)";
            }
            // FK: Postgres 23503 | MySQL errorCode 1452
            if ("23503".equals(state) || sql.getErrorCode() == 1452) {
                return "Referential integrity violation (foreign key)";
            }
        }

        String text = (root.getMessage() != null) ? root.getMessage() : ex.getMessage();
        if (text != null) {
            String l = text.toLowerCase();
            if (l.contains("unique") || l.contains("duplicate")) {
                return "Duplicate value (unique constraint)";
            }
            if (l.contains("foreign key")) {
                return "Referential integrity violation (foreign key)";
            }
        }
        return "Conflict";
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest req, Map<String, String> errors, String code) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                code,
                req.getRequestURI(),
                Instant.now(),
                errors
        );
        return ResponseEntity.status(status).body(body);
    }
}
