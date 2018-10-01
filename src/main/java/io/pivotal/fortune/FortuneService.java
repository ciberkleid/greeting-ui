package io.pivotal.fortune;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FortuneService {

  Logger logger = LoggerFactory
          .getLogger(FortuneService.class);

  private final RestTemplate restTemplate;

  @Value("${fortuneServiceURL:http://fortune-service}")
  String fortuneServiceURL;

  public FortuneService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @HystrixCommand(fallbackMethod = "defaultFortune")
  public String getFortune() {
    logger.debug("Using fortuneServiceURL=[{}]", fortuneServiceURL);
    String fortune = restTemplate.getForObject(fortuneServiceURL, String.class);
    return fortune;
  }

  public String defaultFortune(Throwable throwable){
    logger.debug("Returning fallback fortune. Error: {}", throwable.getMessage());
    return "This fortune is no good. Try another.";
  }

}
