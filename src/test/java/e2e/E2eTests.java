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

	Logger logger = LoggerFactory
			.getLogger(E2eTests.class);

	//	hit /, check that 2xx and body does NOT contain “This fortune is no good. Try another.”
	// also does NOT contain “The fortuneteller will be back soon.”

	@Value("${application.url}") String applicationUrl;

	RestTemplate restTemplate = new RestTemplate();

	@Test
	public void should_return_a_fortune() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://" + this.applicationUrl + "/", String.class);

		logger.info("Repsonse: [{}]", response);
		BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);
		BDDAssertions.then(response.getBody()).doesNotContain("The fortuneteller will be back soon.").doesNotContain("This fortune is no good. Try another.");
	}

}
