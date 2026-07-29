package com.sentinelai.platform;

import com.sentinelai.platform.testsupport.BasePostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SentinelAiApplicationTests extends BasePostgresTest {

	@Test
	void contextLoads() {
	}

}
