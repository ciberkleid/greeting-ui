package io.pivotal.fortune;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class FortuneService {

  Logger logger = LoggerFactory
      .getLogger(FortuneService.class);

  private String fortuneServiceUrl = "fortune-service";

  @Autowired
  public RestTemplate restTemplate;

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() {
    return new RestTemplate();
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

  void setFortuneServiceUrl(String url) {
    this.fortuneServiceUrl=url;
  }

  public FortuneService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

}
