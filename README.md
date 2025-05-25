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
   mvn clean package
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
java -jar target/file-analysis-service-1.0-SNAPSHOT-exec.jar
```

После запуска микросервисы будут доступны по следующим адресам:
- API Gateway: `http://localhost:8080`
- File Storing Service: `http://localhost:5001`
- File Analysis Service: `http://localhost:5002`

Доступные API endpoints через API Gateway:
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

- **Важно!** Перед получением результатов анализа файла необходимо сначала выполнить анализ через POST-запрос:
  ```bash
  # Анализ файла по ID и сохранение результатов
  curl -X POST http://localhost:8080/analysis/$fileId > analysis_result.json
  
  # Просмотр результатов анализа в удобном формате
  cat analysis_result.json | jq
  ```
  
- **Получение статистики файла с сохранением**
  ```bash
  # Получение статистики и сохранение в текстовый файл
  curl -X GET http://localhost:8080/analysis/$fileId/statistics > statistics.json
  
  # Просмотр сохраненной статистики
  cat statistics.json | jq
  ```
  
- **Проверка на плагиат с сохранением результата**
  ```bash
  # Проверка на плагиат и вывод результата
  curl -X GET http://localhost:8080/analysis/$fileId/plagiarism > plagiarism_check.json
  
  # Просмотр результата проверки
  cat plagiarism_check.json | jq
  ```
  
- **Генерация облака слов и сохранение URL**
  ```bash
  # Получение URL облака слов
  curl -X GET http://localhost:8080/analysis/$fileId/wordcloud > wordcloud_response.json
  
  # Извлечение URL из JSON ответа
  wordcloud_url=$(cat wordcloud_response.json | grep -o 'https://quickchart.io/wordcloud?text=[^"]*')
  
  # Открытие URL в браузере (macOS)
  open $wordcloud_url
  
  # Открытие URL в браузере (Linux)
  xdg-open $wordcloud_url
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
curl -s -X GET http://localhost:8080/analysis/$fileId/statistics > $OUTPUT_DIR/statistics.json
echo "Статистика сохранена в $OUTPUT_DIR/statistics.json"
cat $OUTPUT_DIR/statistics.json | jq

echo "5. Проверка на плагиат..."
curl -s -X GET http://localhost:8080/analysis/$fileId/plagiarism > $OUTPUT_DIR/plagiarism.json
echo "Результат проверки сохранен в $OUTPUT_DIR/plagiarism.json"
cat $OUTPUT_DIR/plagiarism.json | jq

echo "6. Генерация облака слов..."
curl -s -X GET http://localhost:8080/analysis/$fileId/wordcloud > $OUTPUT_DIR/wordcloud_response.json
wordcloud_url=$(cat $OUTPUT_DIR/wordcloud_response.json | grep -o 'https://quickchart.io/wordcloud?text=[^"]*')
echo "$wordcloud_url" > $OUTPUT_DIR/wordcloud_url.txt
echo "URL облака слов сохранен в $OUTPUT_DIR/wordcloud_url.txt"
echo "URL: $wordcloud_url"

echo "Все результаты сохранены в директории $OUTPUT_DIR"
```

### Прямой доступ к микросервисам (для отладки)

Помимо использования API Gateway, вы также можете обращаться напрямую к микросервисам:

- File Storing Service: `http://localhost:5001/files/**`
- File Analysis Service: `http://localhost:5002/analysis/**`

Например:
```bash
# Получение файла напрямую из File Storing Service
curl -X GET http://localhost:5001/files/$fileId

# Анализ файла напрямую через File Analysis Service
curl -X POST http://localhost:5002/analysis/$fileId
```

### Swagger UI
Документация API доступна по адресам:
```
http://localhost:8080/swagger-ui.html  # Через API Gateway
http://localhost:5001/swagger-ui.html  # File Storing Service
http://localhost:5002/swagger-ui.html  # File Analysis Service
```

## Архитектура
Система состоит из трех микросервисов:

### 1. API Gateway
- **Функциональность**: Обрабатывает входящие HTTP-запросы и маршрутизирует их к соответствующим микросервисам.
- **Технологии**: Spring Boot, Spring Cloud Gateway
- **Взаимодействие**: Перенаправляет запросы к File Storing Service и File Analysis Service.
- **Порт**: 8080

### 2. File Storing Service
- **Функциональность**: Отвечает за загрузку, хранение и выдачу файлов.
- **Технологии**: Spring Boot, Spring Data
- **Взаимодействие**: Предоставляет REST API для работы с файлами.
- **Порт**: 5001

### 3. File Analysis Service
- **Функциональность**: Выполняет анализ текстовых файлов, включая:
  - Подсчет статистики (абзацы, слова, символы)
  - Сравнение файлов на плагиат
  - Генерацию облаков слов (с использованием QuickChart API)
- **Технологии**: Spring Boot, алгоритмы текстового анализа
- **Взаимодействие**: Получает файлы от File Storing Service, анализирует их и возвращает результаты.
- **Порт**: 5002

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
                   │   (8080)    │
                   └──────┬──────┘
                          │
                 ┌────────┴────────┐
                 │                 │
    ┌────────────▼─────┐    ┌──────▼───────────┐
    │                  │    │                  │
    │ File Storing     │◄───┤ File Analysis    │
    │ Service (5001)   │    │ Service (5002)   │
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
