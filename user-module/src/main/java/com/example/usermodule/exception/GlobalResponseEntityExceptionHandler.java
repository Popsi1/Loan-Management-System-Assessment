package com.example.usermodule.exception;


import com.example.usermodule.util.DataResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalResponseEntityExceptionHandler.class);

        @Nullable
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                BindingResult bindingResult = ex.getBindingResult();

                Map<String, String> fieldErrors = new HashMap<>();
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        String fieldName = fieldError.getField();
                        String errorMessage = fieldError.getDefaultMessage();
                        fieldErrors.put(fieldName, errorMessage);
                }

                return ResponseEntity.badRequest().body(fieldErrors);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException e) {
                return ResponseEntity.of(Optional.of(DataResponseUtils.errorResponse(e.getMessage(), HttpStatus.NOT_FOUND)));
        }

}
