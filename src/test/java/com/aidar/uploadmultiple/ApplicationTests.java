package com.aidar.uploadmultiple;

import com.aidar.uploadmultiple.controller.ZipController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private ZipController zipController;

	@Test
	public void contextLoads() throws Exception {
		assertNotNull(zipController);
	}

}
