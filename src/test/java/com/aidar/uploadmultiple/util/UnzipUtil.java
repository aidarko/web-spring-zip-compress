package com.aidar.uploadmultiple.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.aidar.uploadmultiple.util.InputStreamUtil.inputStreamToString;

public final class UnzipUtil {

  private UnzipUtil() {
    // util class
  }

  public static List<InternalTestFile> unzipToInternalTestFiles(byte[] contentAsByteArray) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(contentAsByteArray);
    ZipInputStream zipInputStream = new ZipInputStream(inputStream);
    List<InternalTestFile> internalTestFiles = new ArrayList<>();

    ZipEntry entry = zipInputStream.getNextEntry();
    while (entry != null) {
      // Skipping directories
      if (!entry.isDirectory()) {
        String content = inputStreamToString(zipInputStream);
        internalTestFiles.add(new InternalTestFile(
            content,
            entry.getName()
        ));
      }
      zipInputStream.closeEntry();
      entry = zipInputStream.getNextEntry();
    }
    zipInputStream.close();

    return internalTestFiles;
  }

  public static class InternalTestFile {
    private final String name;
    private final String content;

    public InternalTestFile(String content, String name) {
      this.content = content;
      this.name = name;
    }

    public String getContent() {
      return content;
    }

    public String getName() {
      return name;
    }
  }
}
