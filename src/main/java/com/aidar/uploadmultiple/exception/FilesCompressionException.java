package com.aidar.uploadmultiple.exception;

import java.io.IOException;

public class FilesCompressionException extends RuntimeException {
  public FilesCompressionException(IOException e) {
    super(e);
  }
}
