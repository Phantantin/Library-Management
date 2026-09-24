package com.zou;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.zou.service.PortalService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LibraryManagementApplicationTests {
	@Autowired
	private PortalService portalService;

	@Test
	void contextLoads() {
	}

	@Test
	void administrationStatisticsExecuteAgainstTheDatabase() {
		var statistics = portalService.statistics();
		assertThat((java.util.List<?>) statistics.get("operationsTrend")).hasSize(30);
		assertThat(statistics).containsKeys("revenueTrend", "popularBooks", "popularGenres");
	}

}
