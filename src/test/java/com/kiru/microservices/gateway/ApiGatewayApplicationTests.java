package com.kiru.microservices.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiGatewayApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Test
	void contextLoads() {
	}

	@Test
	void swaggerUiIsPublic() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void protectedApiRoutesRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/product"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void corsAllowsFrontendOrigin() {
		UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
		var corsConfig = source.getCorsConfigurations().get("/**");

		assertThat(corsConfig).isNotNull();
		assertThat(corsConfig.getAllowedOrigins()).contains("http://localhost:4200");
		assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST");
	}
}
