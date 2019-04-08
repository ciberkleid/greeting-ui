package io.pivotal.fortune;

import io.pivotal.GreetingUIApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingUIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
//        properties = {"spring.application.name=greeting-ui", "fortuneServiceURL=http://fortune-service", "spring.cloud.circuit.breaker.enabled=false", "hystrix.stream.queue.enabled=false"})
        properties = {"spring.application.name=greeting-ui", "spring.cloud.discovery.enabled=false", "spring.cloud.service-registry.auto-registration.enabled=false", "eureka.client.enabled=false", "eureka.client.serviceUrl.registerWithEureka=false", "eureka.client.registerWithEureka=false", "eureka.client.fetchRegistry=false", "spring.cloud.circuit.breaker.enabled=false", "hystrix.stream.queue.enabled=false"})
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:10-78119a9bbde61510640ffc93beed4697e07f4ef0"},
//@AutoConfigureStubRunner(ids = {"io.pivotal:fortune-service:+"},
@AutoConfigureStubRunner(
        // ids = {"${STUBRUNNER_IDS}"}, // set through system prop or env var
        repositoryRoot = "${REPO_WITH_BINARIES}",
        stubsMode = StubRunnerProperties.StubsMode.REMOTE
        //workOffline = true
)

public class FortuneServiceTests {

    // Expects:
    //  env var Stubr ALL_STUBRUNNER_IDS with all x versions to test

    Logger logger = LoggerFactory
            .getLogger(FortuneService.class);

    static final Map<String, Integer> stubs = new HashMap<String, Integer>();

    @BeforeClass
    public static void setup() {
        // name:version,name2:version2
        String allStubs = System.getenv("ALL_STUBRUNNER_IDS");
        String stubrunnerIds = Arrays.stream(allStubs.split(","))
                .map(s -> {
                    String[] id = s.split(":");
                    String name = id[0];
                    String version = id[1];
                    int port = SocketUtils.findAvailableTcpPort(10000);
                    String stubId =  "io.pivotal:" + name + ":" + version + ":stubs:" + port;
                    stubs.put(stubId, port);
                    return stubId;
                }).collect(Collectors.joining(","));

        System.out.println("\n\n\nStubs map: " + stubs.toString() + "\n\n\n ");

        System.setProperty("stubrunner.ids", stubrunnerIds);

    }

    FortuneService fortuneService = new FortuneService(new RestTemplate());

//    @Autowired
//    StubFinder stubFinder;

    @Test
    public void shouldSendRequestToFortune() {

        logger.info("\n\n\nGot stubrunner.ids: [{}]\n\n\n", System.getProperty("stubrunner.ids"));

        List<AbstractMap.SimpleEntry> error = stubs.entrySet()
                .stream()
                .filter(e -> e.getKey().contains("fortune-service"))
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())
                .stream()
                .map(e -> {
                    logger.info("\n\n\nRunning contract test for [{}]\n\n\n", e.getKey());
                    int port = e.getValue();
                    fortuneService.setFortuneServiceURL("http://localhost:" + port);

                    // when
                    String fortune = fortuneService.getFortune();
                    // then
                    try {
                        BDDAssertions.then(fortune).isEqualTo("foo fortune");
                        return null;
                    } catch (AssertionError er) {
                        logger.error("\n\n\nContract test failed. Stub: [{}], Error: [{}]\n\n\n", e.getKey(), er.getMessage());
                        return new AbstractMap.SimpleEntry<String, AssertionError>(e.getKey(), er);
                        //return er;
                    }
                })
        .filter(Objects::nonNull)
        .collect(Collectors.toList())
        ;

        // TODO: add info about group id artifact id version and error
        BDDAssertions.then(error).isEmpty();
    }

}

