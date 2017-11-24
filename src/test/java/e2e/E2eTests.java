package smoke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = E2eTests.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration
public class E2eTests {

	//	hit /, check that 2xx and body does NOT contain “This fortune is no good. Try another.”
	// also does NOT contain “The fortuneteller will be back soon.”

	@Value("${application.url}") String applicationUrl;

	RestTemplate restTemplate = new RestTemplate();

	@Test
	public void should_return_a_foo_fortune() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://" + this.applicationUrl + "/", String.class);

		BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);
		BDDAssertions.then(response.getBody()).doesNotContain("The fortuneteller will be back soon.").doesNotContain("This fortune is no good. Try another.");
	}

}
