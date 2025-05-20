# SD_big_HW_2
## Система анализа студенческих отчетов

## Содержание
- [Описание проекта](#описание-проекта)
- [Требования](#требования)
- [Установка](#установка)
- [Запуск](#запуск)
- [Использование](#использование)
- [Архитектура](#архитектура)
- [API](#api)
- [Тестирование](#тестирование)

## Описание проекта
Данный проект представляет собой микросервисное веб-приложение для анализа студенческих отчетов. Система позволяет загружать текстовые файлы (.txt), анализировать их содержимое, включая подсчет статистики (абзацы, слова, символы), проверку на плагиат и визуализацию данных (облака слов).

## Требования
Для работы с проектом необходимы следующие компоненты:
- Java 11+ (JDK)
- Maven или Gradle
- Git

## Установка
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/novivan/SD_big_HW_2.git
   cd SD_big_HW_2
   ```

2. Соберите каждый микросервис:
   ```bash
   # Сборка API Gateway
   cd api-gateway
   mvn clean install
   
   # Сборка File Storing Service
   cd ../file-storing-service
   mvn clean install
   
   # Сборка File Analysis Service
   cd ../file-analysis-service
   mvn clean install
   ```

## Запуск
Запускаем каждый микросервис отдельно:

```bash
# Запуск API Gateway
cd api-gateway
java -jar target/api-gateway.jar

# Запуск File Storing Service
cd ../file-storing-service
java -jar target/file-storing-service.jar

# Запуск File Analysis Service
cd ../file-analysis-service
java -jar target/file-analysis-service.jar
```

После запуска API Gateway будет доступен по адресу `http://localhost:8080`.

## Использование
### Загрузка файла
```bash
curl -X POST -F "file=@path/to/your/file.txt" http://localhost:8080/api/files
```

### Получение статистики файла
```bash
curl -X GET http://localhost:8080/api/files/{fileId}/statistics
```

### Проверка на плагиат
```bash
curl -X GET http://localhost:8080/api/files/{fileId}/plagiarism
```

### Генерация облака слов
```bash
curl -X GET http://localhost:8080/api/files/{fileId}/wordcloud
```

## Архитектура
Система состоит из трех микросервисов:

### 1. API Gateway
- **Функциональность**: Обрабатывает входящие HTTP-запросы и маршрутизирует их к соответствующим микросервисам.
- **Технологии**: Spring Boot, Spring Cloud Gateway
- **Взаимодействие**: Перенаправляет запросы к File Storing Service и File Analysis Service.

### 2. File Storing Service
- **Функциональность**: Отвечает за загрузку, хранение и выдачу файлов.
- **Технологии**: Spring Boot, Spring Data
- **Взаимодействие**: Предоставляет REST API для работы с файлами.

### 3. File Analysis Service
- **Функциональность**: Выполняет анализ текстовых файлов, включая:
  - Подсчет статистики (абзацы, слова, символы)
  - Сравнение файлов на плагиат
  - Генерацию облаков слов (опционально, с использованием QuickChart API)
- **Технологии**: Spring Boot, алгоритмы текстового анализа
- **Взаимодействие**: Получает файлы от File Storing Service, анализирует их и возвращает результаты.

### Схема взаимодействия микросервисов

                  ┌─────────────┐
                  │             │
                  │   Клиент    │
                  │             │
                  └──────┬──────┘
                         │
                         │ HTTP-запросы
                         ▼
                   ┌─────────────┐
                   │             │
                   │ API Gateway │
                   │             │
                   └──────┬──────┘
                          │
                 ┌────────┴────────┐
                 │                 │
    ┌────────────▼─────┐    ┌──────▼───────────┐
    │                  │    │                  │
    │ File Storing     │◄───┤ File Analysis    │
    │ Service          │    │ Service          │
    │                  │───►│                  │
    └──────────────────┘    └──────────────────┘
             │
             ▼
        ┌──────────┐
        │          │
        │ Файловое │
        │ хранилище│
        │          │
        └──────────┘
