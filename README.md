# oh-learning

## Запуск локально

1) Поднять PostgreSQL:
```bash
docker compose up -d postgres
```

2) Запустить приложение с нужными переменными окружения:
```bash
export AUTH_ISSUER=http://localhost:8999
export AUTH_JWKS_URL=http://localhost:8999/.well-known/jwks.json
export AUTH_AUDIENCE=oh-learning

./mvnw spring-boot:run
```

## Запуск тестов

- Все тесты с линтерами:
```bash
./mvnw verify
```

## Docker образ

После каждого успешного билда в ветке `main`, CI автоматически собирает и публикует Docker образ.

- Репозиторий на Docker Hub: https://hub.docker.com/r/omnomdom/oh-learning
