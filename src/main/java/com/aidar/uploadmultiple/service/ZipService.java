package com.aidar.uploadmultiple.service;

import com.aidar.uploadmultiple.exception.EmptyRequestException;
import com.aidar.uploadmultiple.exception.FilesCompressionException;
import com.aidar.uploadmultiple.exception.FilesLimitExceededException;
import com.aidar.uploadmultiple.exception.MissingOriginalFileNameException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public class ZipService {

  private final int filesCountLimit;

  public ZipService(int filesCountLimit) {
    this.filesCountLimit = filesCountLimit;
  }

  public void zip(ServletResponse response, MultipartFile[] files) {
    validateFilesSize(files);
    try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
      for (MultipartFile file : files) {
        String originalFilename = file.getOriginalFilename();
        validateName(originalFilename);
        ZipEntry e = new ZipEntry(originalFilename);
        e.setSize(file.getSize());
        e.setTime(System.currentTimeMillis());
        zippedOut.putNextEntry(e);
        StreamUtils.copy(file.getInputStream(), zippedOut);
        zippedOut.closeEntry();
      }
      zippedOut.finish();
    } catch (IOException e) {
      throw new FilesCompressionException(e);
    }
  }

  private void validateFilesSize(MultipartFile[] files) {
    if (files == null || files.length == 0)
      throw new EmptyRequestException();
    if (files.length > filesCountLimit)
      throw new FilesLimitExceededException();
  }

  private void validateName(String originalFilename) {
    if (Strings.isBlank(originalFilename)) {
      throw new MissingOriginalFileNameException();
    }
  }
}
