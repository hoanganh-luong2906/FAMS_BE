package com.example.FAMS;

import com.example.FAMS.repositories.SyllabusDAO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
@Log4j2
class FamsApplicationTests {

	@SpyBean
	SyllabusDAO syllabusDAO;

	@Test
	void contextLoads() {
		log.info(syllabusDAO.findTop1000ByOrderByCreatedDateDesc());
	}

}
