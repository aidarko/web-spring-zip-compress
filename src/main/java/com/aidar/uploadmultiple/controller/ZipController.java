package com.aidar.uploadmultiple.controller;

import com.aidar.uploadmultiple.service.ZipService;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ZipController {
  Logger logger = LogManager.getLogger(ZipController.class);

  @Autowired
  ZipService zipService;

  @PostMapping(value = "/compress", produces = "application/zip")
  public void zipFiles(
      @RequestParam("files") MultipartFile[] files,
      @RequestParam(value = "archiveName", defaultValue = "archive.zip") String archiveName,
      HttpServletResponse response
  ) {
    response.setHeader("Content-Disposition", "attachment; filename=" + archiveName);
    zipService.zip(response, files);
    logger.info("Files archived.");
  }
}
