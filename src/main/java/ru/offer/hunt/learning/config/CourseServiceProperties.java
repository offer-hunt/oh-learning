package ru.offer.hunt.learning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.course")
public record CourseServiceProperties(String baseUrl) {}
