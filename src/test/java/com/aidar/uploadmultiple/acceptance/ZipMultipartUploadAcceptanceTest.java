package com.aidar.uploadmultiple.acceptance;

import com.aidar.uploadmultiple.util.InputStreamUtil;
import com.aidar.uploadmultiple.util.UnzipUtil;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZipMultipartUploadAcceptanceTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void testUpload() throws IOException {
    // given
    String expectedFileName1 = "MultipartFile1.txt";
    String expectedFileName2 = "MultipartFile2.txt";
    LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
    ClassPathResource expectedFile1 = new ClassPathResource(expectedFileName1);
    parameters.add("files", expectedFile1);
    ClassPathResource expectedFile2 = new ClassPathResource(expectedFileName2);
    parameters.add("files", expectedFile2);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
    String url = "http://localhost:" + port + "/compress";

    // when
    ResponseEntity<byte[]> response = testRestTemplate.exchange(url, HttpMethod.POST, entity, byte[].class, "");

    // then
    assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("archive.zip"));

    // and
    List<UnzipUtil.InternalTestFile> internalTestFiles = UnzipUtil.unzipToInternalTestFiles(response.getBody());

    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals(internalTestFiles.get(0).getName(), expectedFileName1);
    Assertions.assertEquals(internalTestFiles.get(0).getContent(), InputStreamUtil.inputStreamToString(expectedFile1.getInputStream()));
    assertEquals(internalTestFiles.get(1).getName(), expectedFileName2);
    Assertions.assertEquals(internalTestFiles.get(1).getContent(), InputStreamUtil.inputStreamToString(expectedFile2.getInputStream()));
  }
}