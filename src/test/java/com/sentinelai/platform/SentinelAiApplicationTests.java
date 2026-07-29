package com.sentinelai.platform;

import com.sentinelai.platform.testsupport.BasePostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class SentinelAiApplicationTests extends BasePostgresTest {

	@Test
	void contextLoads() {
	}

	@Test
	void CI_check() {
		assertEquals(1,1);
	}

}
