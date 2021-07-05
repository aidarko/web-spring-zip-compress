package com.aidar.uploadmultiple.exception.message;

public class ResponseErrorMessage {
  private String message;

  public ResponseErrorMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
