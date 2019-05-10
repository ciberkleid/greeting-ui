package io.pivotal.fortune;

import io.pivotal.GreetingUIApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingUIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(repositoryRoot = "${repo.with.binaries}")
        // Use mvnw with STUBS env var and -Drepo.with.binaries to download stubs from remote repo
        // Use mvnw with STUBS env var and -Dstubrunner.stubs-mode=LOCAL to use stubs in local M2 repo
public class FortuneServiceTests {

    // Expects:
    //  env var Stubr STUBS with all x versions to test
    //  format groupid:name:version,groupid2:name2:version2

    Logger logger = LoggerFactory.getLogger(FortuneServiceTests.class);

    static final Map<String, Integer> stubsMap = new HashMap<String, Integer>();

    @BeforeClass
    public static void setup() {
        String stubs = System.getenv("STUBS");
        String stubrunnerIds = Arrays.stream(stubs.split(","))
                .map(s -> {
                    s = s.trim();
                    int port = SocketUtils.findAvailableTcpPort(10000, 15000);
                    String stubId =  s + ":stubs:" + port;
                    stubsMap.put(stubId, port);
                    return stubId;
                }).collect(Collectors.joining(","));

        System.out.println("\n\n\nStubs map: " + stubsMap.toString() + "\n\n\n ");

        // Set system property so that it is detected by StbRunner auto-configuration
        System.setProperty("stubrunner.ids", stubrunnerIds);

    }

    FortuneService fortuneService = new FortuneService(new RestTemplate());

//    @Autowired
//    StubFinder stubFinder;

    @Test
    public void shouldSendRequestToFortune() {

        logger.info("\n\n\nGot stubrunner.ids: [{}]\n\n\n", System.getProperty("stubrunner.ids"));

        List<AbstractMap.SimpleEntry> error = stubsMap.entrySet()
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
                    List<String> fortunes = new ArrayList<>();
                    try {
                        fortunes.add(fortuneService.getFortune());
                    } catch (Exception e1) {
                        logger.warn("getFortune() failed with exception [{}]", e1.getMessage());
                    }
                    try {
                        fortunes.add(fortuneService.fallbackFortuneAPI(new Throwable()));
                    }
                    catch (Exception e2) {
                        logger.warn("fallbackFortuneAPI() failed with exception [{}]", e2.getMessage());
                    }

                    // then
                    try {
                        BDDAssertions.then(fortunes).contains("foo fortune");
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

        // Print summary of all errors
        if (!error.isEmpty()) {
            logger.error("{} of {} stub(s) resulted in failure", error.size(), stubsMap.size());
            error.stream().forEach((e) -> logger.error("Stub [{}], Error: {}", e.getKey(), ((AssertionError)e.getValue()).getMessage()));
        }

        // Fail if any errors occurred
        BDDAssertions.then(error).isEmpty();
    }

}
