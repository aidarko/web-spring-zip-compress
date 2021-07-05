package com.aidar.uploadmultiple;

import com.aidar.uploadmultiple.service.ZipService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Value("${upload.files.limit}")
  private int filesCountLimit;

  @Bean
  ZipService createZipService(){
    return new ZipService(filesCountLimit);
  }
}
