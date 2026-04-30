# DORK SCANNER

A desktop JavaFX application for automated Google Dorks searching across multiple search engines simultaneously.

---

## Description

DORK SCANNER automatically crawls search engines (Google, Bing, DuckDuckGo, Yandex) based on provided dork queries and saves the found links to `results.txt` and `results_N.csv` files. It supports proxies, multithreading, and manual CAPTCHA solving.

---

## Requirements

| Component     | Version    |
|---------------|------------|
| Java          | 22+        |
| Maven         | 3.8.5+     |
| Google Chrome | Latest     |
| ChromeDriver  | Compatible with installed Chrome |

> ChromeDriver (`chromedriver.exe`) must be located in the working directory next to the application.

---

## 🚀 Running

### Via Maven Wrapper
```bash
./mvnw clean javafx:run

# DORK СКАНЕР

Десктопное приложение на JavaFX для автоматизированного поиска по Google Dorks через несколько поисковых систем одновременно.

---

## Описание

DORK СКАНЕР автоматически обходит поисковые системы (Google, Bing, DuckDuckGo, Yandex) по заданным дорк-запросам и сохраняет найденные ссылки в файлы `results.txt` и `results_N.csv`. Поддерживает работу с прокси, многопоточность и ручное прохождение капчи.

---

## Требования

| Компонент     | Версия     |
|---------------|------------|
| Java          | 22+        |
| Maven         | 3.8.5+     |
| Google Chrome | Последняя  |
| ChromeDriver  | Совместимый с установленным Chrome |

> ChromeDriver (`chromedriver.exe`) должен находиться в рабочей директории рядом с программой.

---

## 🚀 Запуск

### Через Maven Wrapper
```bash
./mvnw clean javafx:run
```

### На Windows
```cmd
mvnw.cmd clean javafx:run
```

---

## Использование

1. **Источник дорков** — выберите `.txt` файл, где каждая строка — отдельный дорк-запрос.
2. **Прокси сервер** — опционально. Укажите `.txt` файл со списком SOCKS5 прокси в формате `host:port`.
3. **Лимит** — максимальное количество результатов на один дорк (по умолчанию: 50).
4. **Потоки** — количество параллельных потоков (по умолчанию: 5).
5. **Ручной ввод капчи** — включите, если хотите вручную решать капчу в открывшемся браузере. При включении потоки принудительно ограничиваются до 1.
6. Нажмите **ЗАПУСК** для начала сканирования, **СТОП** — для остановки.

---

## Структура файлов дорков

```
inurl:login.php
site:example.com filetype:sql
intitle:"index of" "passwords"
```

## 📁 Структура файла прокси

```
192.168.1.1:1080
10.0.0.1:9050
```

---

## Результаты

После завершения сканирования в рабочей директории появятся:

- **`results.txt`** — список уникальных URL, по одному на строку.
- **`results_1.csv`, `results_2.csv`, ...** — результаты, разбитые на файлы по 500 записей. Формат:
  ```
  https://example.com/login.php,
  ```

---

## 🏗️ Архитектура проекта

```
src/main/java/com/dev/quikkkk/parser/
├── Launcher.java                        # Точка входа
├── app/
│   ├── App.java                         # JavaFX Application
│   └── AppController.java               # FXML контроллер UI
├── application/
│   └── ParserService.java               # Оркестрация поиска
├── domain/
│   ├── model/SearchResult.java          # Модель результата
│   └── search/ISearchEngine.java        # Интерфейс поисковика
└── infrastructure/
    ├── io/
    │   ├── DorkLoader.java              # Загрузка дорков из файла
    │   ├── ResultTxtWriter.java         # Сохранение в TXT
    │   └── ResultCsvWriter.java         # Сохранение в CSV
    ├── proxy/
    │   ├── DriverPool.java              # Пул WebDriver-ов (borrow / return / restart)
    │   ├── ProxyManager.java            # Создание драйверов с прокси и ротацией
    │   └── UserAgentPool.java           # Пул User-Agent строк
    └── search/
        ├── GoogleSearchEngine.java
        ├── BingSearchEngine.java
        ├── DuckDuckGoSearchEngine.java
        ├── YandexSearchEngine.java
        └── SearchUtils.java             # Общие утилиты извлечения ссылок
```

---

## Технологии

- **JavaFX 22** — графический интерфейс
- **Selenium 4** — автоматизация браузера
- **Jsoup 1.22** — парсинг HTML
- **Apache POI 5** — работа с Excel (резерв)
- **Lombok** — сокращение boilerplate-кода

---

## Дисклеймер

Инструмент предназначен исключительно для образовательных целей и легитимного тестирования безопасности собственной инфраструктуры. Использование против систем без явного разрешения владельца является незаконным.
