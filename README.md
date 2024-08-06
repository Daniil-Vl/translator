# Translator

Многопоточный переводчик, использующий yandex translate api. \
Это реализация лабораторной работы, необходимой для поступления на курс Т-Образование Финтех Java-разработчик.

## Способы сборки и запуска

### 1) Докер + сборка проекта на машине хоста

Требования: Docker, JDK21 (для запуска gradle)

1. Установить переменные окружения в файл `.env` (см шаблон ниже)
2. Установить переменной окружения `DOCKERFILE` значение `Dockerfile-without-build`
3. Запустить сборку проекта
    ```bash
    ./gradlew build
    ```
4. Запустить сервисы
    ```bash
    docker compose up
    ```

### 2) Докер + сборка проекта в докер контейнере

Требования: Docker \
Замечания: в данном варианте не запускаются тесты перед стартом (т.к. для теста необходим докер, а в контейнере его
невозможно запустить)

1. Установить переменные окружения в файл `.env` (см шаблон ниже)
2. Установить переменной окружения `DOCKERFILE` значение `Dockerfile-with-build`
3. Запустить сервисы
    ```bash
    docker compose up
    ```
   
## Переменные окружения
Переменные окружения необходимо добавлять в файл `.env` \
Шаблон файла
```text
# Postgres
POSTGRES_DB=translator
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_HOST=postgresql
POSTGRES_PORT=5432

# Yandex api
YANDEX_TRANSLATE_URL=https://translate.api.cloud.yandex.net/translate/v2/translate
YANDEX_DETECT_LANGUAGE_URL=https://translate.api.cloud.yandex.net/translate/v2/detect
YANDEX_API_KEY=<YOUR_YANDEX_CLOUD_API_KEY>

# Dockerfile
DOCKERFILE=Dockerfile-without-build
```

#### Замечания
1. Нельзя менять значение переменной `POSTGRES_HOST`, т.к. в данной конфигурации она необходима для запуска сервисов в докер контейнере (это название контейнера с бд)
2. Файл будет полностью готов к использованию, когда вы вставите свой api ключ для api яндекса \
См подробнее - https://yandex.cloud/ru/docs/translate/operations/sa-api-key