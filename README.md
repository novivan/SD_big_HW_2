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
java -jar target/api-gateway-1.0-SNAPSHOT.jar

# В отдельном терминале: запуск File Storing Service
cd ../file-storing-service
java -jar target/file-storing-service-1.0-SNAPSHOT.jar

# В отдельном терминале: запуск File Analysis Service
cd ../file-analysis-service
java -jar target/file-analysis-service-1.0-SNAPSHOT.jar
```

После запуска API Gateway будет доступен по адресу `http://localhost:8080`.

Доступные API endpoints:
- `http://localhost:8080/` - домашняя страница с информацией о системе
- `http://localhost:8080/files/**` - операции с файлами (загрузка, получение)
- `http://localhost:8080/analysis/**` - операции анализа файлов
- `http://localhost:8080/swagger-ui.html` - документация API

## Использование
### API Endpoints и сохранение результатов

#### 1. File Storing Service

- **Загрузка файла и сохранение ID**
  ```bash
  # Загрузка файла и сохранение ID в переменную
  fileId=$(curl -X POST -F "file=@path/to/your/file.txt" http://localhost:8080/files)
  echo "Файл загружен, ID: $fileId"
  
  # Или сохранение ID в файл
  curl -X POST -F "file=@path/to/your/file.txt" http://localhost:8080/files > file_id.txt
  cat file_id.txt  # Просмотр полученного ID
  ```
  
- **Получение файла и сохранение в JSON**
  ```bash
  # Получение файла по ID и сохранение ответа в JSON
  curl -X GET http://localhost:8080/files/$fileId > file_data.json
  
  # Форматированный вывод JSON
  cat file_data.json | jq
  ```
  
- **Получение всех файлов с форматированным выводом**
  ```bash
  # Получение списка файлов с форматированным JSON
  curl -X GET http://localhost:8080/files | jq
  
  # Сохранение списка файлов
  curl -X GET http://localhost:8080/files > all_files.json
  ```

#### 2. File Analysis Service

- **Анализ файла и сохранение результатов**
  ```bash
  # Анализ файла по ID и сохранение результатов
  curl -X POST http://localhost:8080/analysis/$fileId > analysis_result.json
  
  # Просмотр результатов анализа в удобном формате
  cat analysis_result.json | jq
  ```
  
- **Получение результатов анализа с форматированием**
  ```bash
  # Получение результатов анализа с форматированным выводом
  curl -X GET http://localhost:8080/analysis/$fileId | jq
  ```
  
- **Получение статистики файла с сохранением**
  ```bash
  # Получение статистики и сохранение в текстовый файл
  curl -X GET http://localhost:8080/analysis/$fileId/statistics > statistics.txt
  
  # Просмотр сохраненной статистики
  cat statistics.txt
  ```
  
- **Проверка на плагиат с сохранением результата**
  ```bash
  # Проверка на плагиат и вывод результата
  curl -X GET http://localhost:8080/analysis/$fileId/plagiarism > plagiarism_check.txt
  
  # Просмотр результата проверки
  cat plagiarism_check.txt
  ```
  
- **Генерация облака слов и сохранение URL**
  ```bash
  # Получение URL облака слов
  curl -X GET http://localhost:8080/analysis/$fileId/wordcloud > wordcloud_url.txt
  
  # Открытие URL в браузере (macOS)
  open $(cat wordcloud_url.txt | grep -o 'https://quickchart.io/wordcloud?text=[^"]*')
  
  # Открытие URL в браузере (Linux)
  xdg-open $(cat wordcloud_url.txt | grep -o 'https://quickchart.io/wordcloud?text=[^"]*')
  ```

### Полный пример рабочего процесса

Вот полный пример использования системы с сохранением всех результатов:

```bash
#!/bin/bash

# Путь к вашему текстовому файлу
FILE_PATH="/path/to/your/essay.txt"
OUTPUT_DIR="./results"

# Создание директории для результатов
mkdir -p $OUTPUT_DIR

echo "1. Загрузка файла..."
fileId=$(curl -s -X POST -F "file=@$FILE_PATH" http://localhost:8080/files)
echo "Файл загружен с ID: $fileId"
echo $fileId > $OUTPUT_DIR/file_id.txt

echo "2. Запуск анализа..."
curl -s -X POST http://localhost:8080/analysis/$fileId > $OUTPUT_DIR/analysis_raw.json
echo "Анализ выполнен"

echo "3. Получение результатов анализа..."
curl -s -X GET http://localhost:8080/analysis/$fileId | jq > $OUTPUT_DIR/analysis_result.json
echo "Результаты анализа сохранены в $OUTPUT_DIR/analysis_result.json"

echo "4. Получение статистики..."
curl -s -X GET http://localhost:8080/analysis/$fileId/statistics > $OUTPUT_DIR/statistics.txt
echo "Статистика сохранена в $OUTPUT_DIR/statistics.txt"
cat $OUTPUT_DIR/statistics.txt

echo "5. Проверка на плагиат..."
curl -s -X GET http://localhost:8080/analysis/$fileId/plagiarism > $OUTPUT_DIR/plagiarism.txt
echo "Результат проверки сохранен в $OUTPUT_DIR/plagiarism.txt"
cat $OUTPUT_DIR/plagiarism.txt

echo "6. Генерация облака слов..."
curl -s -X GET http://localhost:8080/analysis/$fileId/wordcloud > $OUTPUT_DIR/wordcloud_response.txt
wordcloud_url=$(grep -o 'https://quickchart.io/wordcloud?text=[^"]*' $OUTPUT_DIR/wordcloud_response.txt)
echo "$wordcloud_url" > $OUTPUT_DIR/wordcloud_url.txt
echo "URL облака слов сохранен в $OUTPUT_DIR/wordcloud_url.txt"
echo "URL: $wordcloud_url"

echo "Все результаты сохранены в директории $OUTPUT_DIR"
```

### Swagger UI
Документация API доступна по адресу:
```
http://localhost:8080/swagger-ui.html
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
