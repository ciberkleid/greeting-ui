package io.pivotal.fortune;

import io.pivotal.GreetingHystrixApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingHystrixApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.application.name=greeting-ui")
@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:1.0.0.M1-20171212_182912-VERSION"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:+:stubs:9876"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:0.0.1-SNAPSHOT:stubs:9876"},
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
