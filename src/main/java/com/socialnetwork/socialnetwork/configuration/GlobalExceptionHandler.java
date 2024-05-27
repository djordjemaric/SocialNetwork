package com.socialnetwork.socialnetwork.configuration;

import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.IAMProviderException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BusinessLogicException.class})
    public ResponseEntity<Object> businessLogicExceptionHandler(BusinessLogicException exception){
        switch(exception.getErrorCode()){
            case ERROR_REGISTERING_USER: return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler({IAMProviderException.class})
    public ResponseEntity<Object> iamProviderExceptionHandler(IAMProviderException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> resourceNotFoundExceptionHandler(ResourceNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

}
