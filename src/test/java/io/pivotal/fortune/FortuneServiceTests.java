package io.pivotal.fortune;

import io.pivotal.GreetingUIApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingUIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.application.name=greeting-ui", "fortuneServiceURL=http://fortune-service", "spring.cloud.circuit.breaker.enabled=false", "hystrix.stream.queue.enabled=false"})
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:10-78119a9bbde61510640ffc93beed4697e07f4ef0"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:+"},
@AutoConfigureStubRunner(
        // ids = {"${STUBRUNNER_IDS}"}, // will automatically pick up env variable STUBRUNNER_IDS
        repositoryRoot = "${REPO_WITH_BINARIES}",
        stubsMode = StubRunnerProperties.StubsMode.REMOTE
        //workOffline = true
)

public class FortuneServiceTests {

    @Autowired FortuneService fortuneService;

    @Test
    public void shouldSendRequestToFortune() {
        // when
        String fortune = fortuneService.getFortune();
        // then
        BDDAssertions.then(fortune).isEqualTo("foo fortune");
    }

}

