package com.aidar.uploadmultiple.service;

import com.aidar.uploadmultiple.exception.EmptyRequestException;
import com.aidar.uploadmultiple.exception.FilesLimitExceededException;
import com.aidar.uploadmultiple.util.InputStreamUtil;
import com.aidar.uploadmultiple.util.UnzipUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZipServiceTest {

  private MockHttpServletResponse mockHttpServletResponse;
  private static final MultipartFile MULTIPART_FILE_1 = new MockMultipartFile(
      "files",
      "MultipartFile1.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "MultipartFile1".getBytes());
  private static final MultipartFile MULTIPART_FILE_2 = new MockMultipartFile(
      "files",
      "MultipartFile2.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "MultipartFile2".getBytes());
  private static final MultipartFile MULTIPART_FILE_EMPTY = new MockMultipartFile(
      "files",
      "MultipartFile3.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "".getBytes());

  @BeforeEach
  public void setUp() {
    mockHttpServletResponse = new MockHttpServletResponse();
  }

  @ParameterizedTest
  @MethodSource("provideMultipartFiles")
  void testSimilarForContentWhenZipped(MultipartFile[] multipartFiles) throws IOException {
    // given
    ZipService zipService = new ZipService(2);

    // when
    zipService.zip(mockHttpServletResponse, multipartFiles);

    // then
    byte[] contentAsByteArray = mockHttpServletResponse.getContentAsByteArray();
    List<UnzipUtil.InternalTestFile> internalTestFiles = UnzipUtil.unzipToInternalTestFiles(contentAsByteArray);

    for (int i = 0; i < internalTestFiles.size(); i++) {
      Assertions.assertEquals(InputStreamUtil.inputStreamToString(multipartFiles[i].getInputStream()), internalTestFiles.get(i).getContent());
      assertEquals(multipartFiles[i].getOriginalFilename(), internalTestFiles.get(i).getName());
    }
  }

  private static Stream<Arguments> provideMultipartFiles() {
    return Stream.of(
        Arguments.of((Object) new MultipartFile[]{MULTIPART_FILE_EMPTY}),
        Arguments.of((Object) new MultipartFile[]{MULTIPART_FILE_1}),
        Arguments.of((Object) new MultipartFile[]{MULTIPART_FILE_1, MULTIPART_FILE_2}),
        Arguments.of((Object) new MultipartFile[]{MULTIPART_FILE_1, MULTIPART_FILE_EMPTY})
    );
  }

  @ParameterizedTest
  @MethodSource("provideInvalidMultipartFiles")
  void testThrowsExceptionWhenMultipartFilesParameterIsEmpty(MultipartFile[] multipartFiles, Class<RuntimeException> exception) {
    // given
    ZipService zipService = new ZipService(2);

    // when
    Assertions.assertThrows(exception, () -> {
      zipService.zip(mockHttpServletResponse, multipartFiles);
    });
  }

  private static Stream<Arguments> provideInvalidMultipartFiles() {
    return Stream.of(
        Arguments.of(new MultipartFile[0], EmptyRequestException.class),
        Arguments.of(new MultipartFile[3], FilesLimitExceededException.class)
    );
  }
}