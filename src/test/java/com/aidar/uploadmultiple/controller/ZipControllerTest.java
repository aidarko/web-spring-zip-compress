package com.aidar.uploadmultiple.controller;

import com.aidar.uploadmultiple.exception.EmptyRequestException;
import com.aidar.uploadmultiple.exception.ExceptionAdvice;
import com.aidar.uploadmultiple.exception.FilesCompressionException;
import com.aidar.uploadmultiple.exception.FilesLimitExceededException;
import com.aidar.uploadmultiple.exception.MissingOriginalFileNameException;
import com.aidar.uploadmultiple.service.ZipService;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {ZipController.class, ExceptionAdvice.class})
@WebMvcTest
class ZipControllerTest {

  @MockBean
  private ZipService zipService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private static final MockMultipartFile FIRST_FILE = new MockMultipartFile(
      "data",
      "filename.txt",
      "text/plain",
      "some xml".getBytes());
  private static final MockMultipartFile SECOND_FILE = new MockMultipartFile(
      "data",
      "other-file-name.data",
      "text/plain",
      "some other type".getBytes());

  @Test
  public void testSuccess() throws Exception {
    String expectedFileName = "test";
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockMvc.perform(MockMvcRequestBuilders.multipart("/compress")
        .file(FIRST_FILE)
        .file(SECOND_FILE)
        .param("archiveName", expectedFileName))
        .andExpect(status().is(200))
        .andExpect(header().stringValues("Content-Disposition", "attachment; filename=" + expectedFileName));
  }

  @ParameterizedTest
  @MethodSource("provideParametersForErrors")
  public void tesFailure(Exception exceptionToBeThrown, String expectedMessage, int expectedStatus) throws Exception {

    doThrow(exceptionToBeThrown).when(zipService).zip(Mockito.any(), Mockito.any());

    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockMvc.perform(MockMvcRequestBuilders.multipart("/compress")
        .file(FIRST_FILE)
        .file(SECOND_FILE))
        .andExpect(status().is(expectedStatus))
        .andExpect(content().string(expectedMessage));
  }

  private static Stream<Arguments> provideParametersForErrors() {
    return Stream.of(
        Arguments.of(new RuntimeException(), "{\"message\":\"Server error.\"}", 500),
        Arguments.of(new EmptyRequestException(), "{\"message\":\"No files provided.\"}", 400),
        Arguments.of(new FilesCompressionException(new IOException()), "{\"message\":\"Compression process failed.\"}", 500),
        Arguments.of(new FilesLimitExceededException(), "{\"message\":\"Files limit exceeded.\"}", 400),
        Arguments.of(new MissingOriginalFileNameException(), "{\"message\":\"One or more files don't have originalFileName.\"}", 400)
    );
  }
}