// src/test/java/com/mpmt/backend/MpmtBackendApplicationTests.java
package com.mpmt.backend;

import com.mpmt.backend.security.JwtAuthFilter;
import com.mpmt.backend.security.JwtService;
import com.mpmt.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // évite les filtres sécu
class MpmtBackendApplicationTests {

	// Satisfait les dépendances du contexte
	@MockBean JwtService jwtService;
	@MockBean JwtAuthFilter jwtAuthFilter;
	@MockBean
	UserService userService;

	@Test
	void contextLoads() {}
}
