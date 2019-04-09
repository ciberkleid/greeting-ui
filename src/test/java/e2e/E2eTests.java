package e2e;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = E2eTests.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration
public class E2eTests {

	@Value("${application.url}") String applicationUrl;

	RestTemplate restTemplate = new RestTemplate();

	@Test
	public void should_return_a_fortune() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://" + this.applicationUrl + "/", String.class);

		BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);

		// Filter out the known Hystrix fallback response
		BDDAssertions.then(response.getBody()).doesNotContain("This fortune is no good. Try another.");
	}

}