package com.example.loanmodule.exception;


import com.example.loanmodule.util.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
@RestController
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
        public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
                return ResponseEntity.of(Optional.of(DataResponse.errorResponse(e.getMessage(), HttpStatus.NOT_FOUND)));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
                return ResponseEntity.of(Optional.of(DataResponse.errorResponse(e.getMessage())));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
                Map<String, String> errorResponse = new HashMap<>();

                String errorMessage = ex.getMostSpecificCause().getMessage(); // Get detailed database error message
                String fieldName = "Unknown Field"; // Default if the field can't be determined
                String message = "Data integrity violation";

                if (errorMessage != null) {
                        // Handle unique constraint violations
                        if (errorMessage.contains("duplicate key value")) {
                                fieldName = extractFieldName(errorMessage, "Key (", ")");
                                message = fieldName + " already exists";
                        }
                        // Handle NOT NULL constraint violations
                        else if (errorMessage.contains("null value in column")) {
                                fieldName = extractFieldName(errorMessage, "column \"", "\"");
                                message = fieldName + " cannot be null";
                        }
                        // Handle other constraint violations (e.g., foreign key)
                        else if (errorMessage.contains("violates foreign key constraint")) {
                                fieldName = extractFieldName(errorMessage, "constraint \"", "\"");
                                message = "Foreign key violation: " + fieldName;
                        }
                }

                errorResponse.put("error", "Data Integrity Violation");
                errorResponse.put("field", fieldName);
                errorResponse.put("message", message);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Utility method to extract a substring between two delimiters.
         *
         * @param source the source string
         * @param start  the start delimiter
         * @param end    the end delimiter
         * @return the extracted substring or "Unknown Field" if not found
         */
        private String extractFieldName(String source, String start, String end) {
                int startIndex = source.indexOf(start);
                int endIndex = source.indexOf(end, startIndex + start.length());
                if (startIndex != -1 && endIndex != -1) {
                        return source.substring(startIndex + start.length(), endIndex).trim();
                }
                return "Unknown Field";
        }

}
