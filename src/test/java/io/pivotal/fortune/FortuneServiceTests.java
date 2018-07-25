package io.pivotal.fortune;

import io.pivotal.GreetingUIApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingUIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.application.name=greeting-ui", "spring.cloud.circuit.breaker.enabled=false", "hystrix.stream.queue.enabled=false"})
@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:1.0.0.M1-20180725_221358-VERSION"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:+"},
        repositoryRoot = "${REPO_WITH_BINARIES}"
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

