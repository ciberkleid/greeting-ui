package io.pivotal.fortune;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class FortuneService {

  Logger logger = LoggerFactory
          .getLogger(FortuneService.class);

  private final RestTemplate restTemplate;

  @Value("${fortuneServiceURLV1:https://fortune-service}")
  String fortuneServiceURL;

  public FortuneService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

//  @HystrixCommand(fallbackMethod = "defaultFortune")
//  public String getFortune() {
//    String fortune = "";
//    try {
//      logger.debug("Using fortuneServiceURL=[{}]", fortuneServiceURL + "/fortune");
//      fortune = restTemplate.getForObject(fortuneServiceURL + "/fortune", String.class);
//    } catch (HttpClientErrorException e) {
//      logger.debug("Got error: [{}]", e.getMessage());
//      logger.debug("Trying fortuneServiceURL=[{}]", fortuneServiceURL);
//      fortune = restTemplate.getForObject(fortuneServiceURL, String.class);
//    }
//    logger.debug("Got fortune=[{}]", fortune);
//    return fortune;
//  }

  @HystrixCommand(fallbackMethod = "fallbackFortuneAPI")
  public String getFortune() {
    logger.debug("Using fortuneServiceURL=[{}]", fortuneServiceURL + "/fortune");
    String fortune = restTemplate.getForObject(fortuneServiceURL + "/fortune", String.class);
    logger.debug("Got fortune=[{}]", fortune);
    return fortune;
  }

  // for tests
  void setFortuneServiceURL(String url) {
    this.fortuneServiceURL = url;
  }

  @HystrixCommand(fallbackMethod = "defaultFortune")
  public String fallbackFortuneAPI(Throwable throwable){
    logger.debug("Got error: [{}]", throwable.getMessage());
    logger.debug("Trying fortuneServiceURL=[{}]", fortuneServiceURL);
    String fortune = restTemplate.getForObject(fortuneServiceURL, String.class);
    logger.debug("Got fortune=[{}]", fortune);
    return fortune;
  }

  public String defaultFortune(Throwable throwable){
    logger.debug("Returning fallback fortune. Error: {}", throwable.getMessage());
    return "This fortune is no good. Try another.";
  }

}
