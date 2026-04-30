package com.massemiso.supermarket_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SupermarketApplicationTests {
  // Checks if app connects to test container database
	@Test
	void contextLoads() {
	}

}
