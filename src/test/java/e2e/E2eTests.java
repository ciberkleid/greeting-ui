package e2e;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = E2eTests.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration
public class E2eTests {


	// The app is running in CF but the tests are executed from Concourse worker,
	// so the tests cannot retrieve the greeting-ui URL
	// We will assume same host and replace app name
	// (not compatible with c2c)

	Logger logger = LoggerFactory
			.getLogger(E2eTests.class);

	@Value("${application.url}") String applicationUrl;

	RestTemplate restTemplate = new RestTemplate();

	@Test
	public void should_return_a_fortune() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://" + this.applicationUrl.replace("fortune-service", "greeting-ui") + "/", String.class);

		logger.info("Response: [{}]", response);
		BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);

		// Filter out the known Hystrix fallback responses from both fortune and greeting
		BDDAssertions.then(response.getBody()).doesNotContain("This fortune is no good. Try another.");
	}

}



