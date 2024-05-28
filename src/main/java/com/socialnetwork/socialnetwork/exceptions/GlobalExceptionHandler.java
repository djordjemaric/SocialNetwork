package com.socialnetwork.socialnetwork.exceptions;

import com.socialnetwork.socialnetwork.exceptions.*;
import com.socialnetwork.socialnetwork.exceptions.IAMProviderException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BusinessLogicException.class, IAMProviderException.class})
    public ResponseEntity<ExceptionResponse> businessLogicAndIamExceptionHandler(SocialNetworkException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getErrorCode(), exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationExceptionHandler(ConstraintViolationException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.VALIDATION_ERROR, exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(MethodArgumentNotValidException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.VALIDATION_ERROR, exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedExceptionHandler(AccessDeniedException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.ERROR_ACCESS_DENIED, exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getErrorCode(), exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> genericExceptionHandler(Exception exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.SERVER_ERROR, exception.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<ExceptionResponse> genericErrorHandler(Error error){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.SERVER_ERROR, error.getMessage(), getCurrentTimestamp());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    private String getCurrentTimestamp(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}
