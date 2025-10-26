package ru.offer.hunt.learning.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
    properties = {
      "spring.flyway.enabled=true",
      "spring.flyway.schemas=learning",
      "spring.flyway.baseline-on-migrate=true"
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbSmokeTest {

  static final PostgreSQLContainer<?> PG =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("oh_learning")
          .withUsername("learning_user")
          .withPassword("learning_user");

  static {
    PG.start();
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", PG::getJdbcUrl);
    r.add("spring.datasource.username", PG::getUsername);
    r.add("spring.datasource.password", PG::getPassword);
    r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    r.add("spring.flyway.enabled", () -> true);
    r.add("spring.flyway.schemas", () -> "learning");
  }

  @Autowired private JdbcTemplate jdbc;

  @Test
  void schemaAndInsert() {
    Integer cnt =
        jdbc.queryForObject(
            "select count(*) from information_schema.tables where table_schema = 'learning' and table_name='learning_ratings'",
            Integer.class);
    assertThat(cnt).isNotNull().isGreaterThanOrEqualTo(1);

    UUID userId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    int ins =
        jdbc.update(
            "insert into learning.learning_ratings(user_id, course_id, value, comment) values (?,?,?,?)",
            userId,
            courseId,
            5,
            "ok");
    assertThat(ins).isEqualTo(1);

    Integer value =
        jdbc.queryForObject(
            "select value from learning.learning_ratings where user_id = ? and course_id = ?",
            Integer.class,
            userId,
            courseId);
    assertThat(value).isNotNull().isEqualTo(5);
  }
}
