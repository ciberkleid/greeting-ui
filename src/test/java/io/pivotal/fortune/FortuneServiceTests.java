package io.pivotal.fortune;

import io.pivotal.GreetingHystrixApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.application.name=greeting-ui")
@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:1.0.0.M1-20171204_113308-VERSION:stubs:9876"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:+:stubs:9876"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:0.0.1-SNAPSHOT:stubs:9876"},
        repositoryRoot = "${REPO_WITH_BINARIES}"
        //workOffline = true,
        //stubsPerConsumer = true)
)

public class FortuneServiceTests {

    @Test
    public void shouldSendRequestToFortune() {
        // given
        FortuneService fortuneService = new FortuneService(new RestTemplate(), "localhost:9876");
        // when
        String fortune = fortuneService.getFortune();
        // then
        BDDAssertions.then(fortune).isEqualTo("foo fortune");
    }

}

@Configuration
@EnableAutoConfiguration
class TestConfig {

}

