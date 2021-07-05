package com.aidar.uploadmultiple.exception;

import com.aidar.uploadmultiple.exception.message.ResponseErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
  Logger logger = LogManager.getLogger(ExceptionAdvice.class);

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ResponseErrorMessage> handleMaxSizeException(MaxUploadSizeExceededException e) {
    logger.warn("One or more files exceeds size limit.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ResponseErrorMessage("One or more files exceeds size limit."));
  }

  @ExceptionHandler(MissingOriginalFileNameException.class)
  public ResponseEntity<ResponseErrorMessage> handleMissingOriginalFileNameException(MissingOriginalFileNameException e) {
    logger.warn("One or more files don't have originalFileName.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ResponseErrorMessage("One or more files don't have originalFileName."));
  }

  @ExceptionHandler(FilesCompressionException.class)
  public ResponseEntity<ResponseErrorMessage> handleFilesCompressionException(FilesCompressionException e) {
    logger.error("Compression process failed.", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ResponseErrorMessage("Compression process failed."));
  }

  @ExceptionHandler(EmptyRequestException.class)
  public ResponseEntity<ResponseErrorMessage> handleEmptyRequestException(EmptyRequestException e) {
    logger.warn("No files provided.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ResponseErrorMessage("No files provided."));
  }

  @ExceptionHandler(FilesLimitExceededException.class)
  public ResponseEntity<ResponseErrorMessage> handleFilesLimitExceededException(FilesLimitExceededException e) {
    logger.warn("Files limit exceeded.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ResponseErrorMessage("Files limit exceeded."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseErrorMessage> handleGenericException(Exception e) {
    logger.error("Server error.", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ResponseErrorMessage("Server error."));
  }
}