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
On WindowsDOSmvnw.cmd clean javafx:run
UsageDorks Source — select a .txt file where each line is a separate dork query.Proxy Server — optional. Specify a .txt file with a list of SOCKS5 proxies in host:port format.Limit — maximum number of results per dork (default: 50).Threads — number of parallel threads (default: 5).Manual CAPTCHA solving — enable if you want to manually solve CAPTCHAs in the opened browser. When enabled, threads are forcibly limited to 1.Click START to begin scanning, STOP — to halt.Dorks File Structureinurl:login.php
site:example.com filetype:sql
intitle:"index of" "passwords"
📁 Proxy File Structure192.168.1.1:1080
10.0.0.1:9050
ResultsAfter scanning is complete, the following will appear in the working directory:results.txt — a list of unique URLs, one per line.results_1.csv, results_2.csv, ... — results split into files of 500 records each. Format:[https://example.com/login.php](https://example.com/login.php),
🏗️ Project Architecturesrc/main/java/com/dev/quikkkk/parser/
├── Launcher.java                        # Entry point
├── app/
│   ├── App.java                         # JavaFX Application
│   └── AppController.java               # FXML UI controller
├── application/
│   └── ParserService.java               # Search orchestration
├── domain/
│   ├── model/SearchResult.java          # Result model
│   └── search/ISearchEngine.java        # Search engine interface
└── infrastructure/
    ├── io/
    │   ├── DorkLoader.java              # Loading dorks from file
    │   ├── ResultTxtWriter.java         # Saving to TXT
    │   └── ResultCsvWriter.java         # Saving to CSV
    ├── proxy/
    │   ├── DriverPool.java              # WebDriver pool (borrow / return / restart)
    │   ├── ProxyManager.java            # Creating drivers with proxies and rotation
    │   └── UserAgentPool.java           # User-Agent string pool
    └── search/
        ├── GoogleSearchEngine.java
        ├── BingSearchEngine.java
        ├── DuckDuckGoSearchEngine.java
        ├── YandexSearchEngine.java
        └── SearchUtils.java             # Shared utilities for link extraction
TechnologiesJavaFX 22 — Graphical User InterfaceSelenium 4 — Browser automationJsoup 1.22 — HTML parsingApache POI 5 — Working with Excel (fallback)Lombok — Boilerplate code reductionDisclaimerThis tool is intended exclusively for educational purposes and legitimate security testing of your own infrastructure. Using it against systems without the explicit permission of the owner is illegal.DORK СКАНЕРДесктопное приложение на JavaFX для автоматизированного поиска по Google Dorks через несколько поисковых систем одновременно.ОписаниеDORK СКАНЕР автоматически обходит поисковые системы (Google, Bing, DuckDuckGo, Yandex) по заданным дорк-запросам и сохраняет найденные ссылки в файлы results.txt и results_N.csv. Поддерживает работу с прокси, многопоточность и ручное прохождение капчи.ТребованияКомпонентВерсияJava22+Maven3.8.5+Google ChromeПоследняяChromeDriverСовместимый с установленным ChromeChromeDriver (chromedriver.exe) должен находиться в рабочей директории рядом с программой.🚀 ЗапускЧерез Maven WrapperBash./mvnw clean javafx:run
На WindowsDOSmvnw.cmd clean javafx:run
ИспользованиеИсточник дорков — выберите .txt файл, где каждая строка — отдельный дорк-запрос.Прокси сервер — опционально. Укажите .txt файл со списком SOCKS5 прокси в формате host:port.Лимит — максимальное количество результатов на один дорк (по умолчанию: 50).Потоки — количество параллельных потоков (по умолчанию: 5).Ручной ввод капчи — включите, если хотите вручную решать капчу в открывшемся браузере. При включении потоки принудительно ограничиваются до 1.Нажмите ЗАПУСК для начала сканирования, СТОП — для остановки.Структура файлов дорковinurl:login.php
site:example.com filetype:sql
intitle:"index of" "passwords"
📁 Структура файла прокси192.168.1.1:1080
10.0.0.1:9050
РезультатыПосле завершения сканирования в рабочей директории появятся:results.txt — список уникальных URL, по одному на строку.results_1.csv, results_2.csv, ... — результаты, разбитые на файлы по 500 записей. Формат:[https://example.com/login.php](https://example.com/login.php),
🏗️ Архитектура проектаsrc/main/java/com/dev/quikkkk/parser/
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
ТехнологииJavaFX 22 — графический интерфейсSelenium 4 — автоматизация браузераJsoup 1.22 — парсинг HTMLApache POI 5 — работа с Excel (резерв)Lombok — сокращение boilerplate-кодаДисклеймерИнструмент предназначен исключительно для образовательных целей и легитимного тестирования безопасности собственной инфраструктуры. Использование против систем без явного разрешения владельца является незаконным.
