package io.pivotal.fortune;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class FortuneService {

  Logger logger = LoggerFactory
      .getLogger(FortuneService.class);

  private final String fortuneServiceUrl;
  private final RestTemplate restTemplate;

  public FortuneService(RestTemplate restTemplate, @Value("${fortune.service.url:fortune-service}") String fortuneServiceUrl) {
    this.restTemplate = restTemplate;
    this.fortuneServiceUrl = fortuneServiceUrl;
  }

  @HystrixCommand(fallbackMethod = "defaultFortune")
  public String getFortune() {
    String fortune = restTemplate.getForObject("http://".concat(fortuneServiceUrl), String.class);
    return fortune;
  }

  public String defaultFortune(){
    logger.debug("Default fortune used.");
    return "This fortune is no good. Try another.";
  }

}
