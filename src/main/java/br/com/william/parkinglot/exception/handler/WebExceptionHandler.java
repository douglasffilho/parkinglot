package br.com.william.parkinglot.exception.handler;

import br.com.william.parkinglot.exception.WebException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(WebException.class)
    public ResponseEntity<Map<String, Object>> handleWebException(WebException ex) {
        final Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", ex.getStatus().value());
        response.put("logref", ex.getLogref());

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final Map<String, Object> response = new HashMap<>();
        response.put(
                "message",
                ex.getFieldErrors()
                        .stream()
                        .map(error -> "%s:%s".formatted(error.getField(), error.getDefaultMessage()))
                        .collect(Collectors.joining(","))
        );
        response.put("status", HttpStatus.BAD_REQUEST);
        response.put("logref", "bad-request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
