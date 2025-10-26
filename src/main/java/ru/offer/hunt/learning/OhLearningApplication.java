package ru.offer.hunt.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.offer.hunt.learning.model.repository")
public class OhLearningApplication {

  public static void main(String[] args) {
    SpringApplication.run(OhLearningApplication.class, args);
  }
}
