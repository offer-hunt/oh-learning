package ru.offer.hunt.learning.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CourseServiceProperties.class)
public class ClientsConfig {

  @Bean
  public RestClient courseRestClient(CourseServiceProperties props) {
    return RestClient.builder().baseUrl(props.baseUrl()).build();
  }
}
