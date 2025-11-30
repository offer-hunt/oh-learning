# oh-learning

## Запуск локально

1) Поднять PostgreSQL:
```bash
docker compose up -d postgres
```

2) Тестирование через Swagger

Соберите и запустите сервис под профилем local:

```bash
./gradlew clean build
java -jar build/libs/oh-course-0.0.1.jar --spring.profiles.active=local
```

Перейдите по ссылке:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

## Запуск тестов и линтеров

- Полная проверка (Spotless, Checkstyle, тесты):
```bash
./gradlew clean check
```

## Docker образ

После каждого успешного билда в ветке `main`, CI автоматически собирает и публикует Docker образ.

- Репозиторий на Docker Hub: https://hub.docker.com/r/offerhunt/oh-learning
