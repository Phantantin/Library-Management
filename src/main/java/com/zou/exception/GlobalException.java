package com.zou.exception;
import com.zou.payload.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(f -> f.getField()+": "+f.getDefaultMessage()).distinct().collect(java.util.stream.Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiResponse(message,false));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> denied(AccessDeniedException e) {return ResponseEntity.status(403).body(new ApiResponse("Access denied",false));}
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> auth(AuthenticationException e) {return ResponseEntity.status(401).body(new ApiResponse("Invalid email or password",false));}
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> conflict(DataIntegrityViolationException e) {return ResponseEntity.status(409).body(new ApiResponse("This record conflicts with existing data or is still in use.",false));}
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> status(ResponseStatusException e) {return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getReason(),false));}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> failure(Exception e) {
        boolean business = e.getClass()==Exception.class || e instanceof BookException || e instanceof GenreException || e instanceof SubscriptionException || e instanceof UserException || e instanceof IllegalArgumentException;
        return ResponseEntity.status(business ? 400 : 500).body(new ApiResponse(business ? e.getMessage() : "Unable to complete the request. Please try again.",false));
    }
}
