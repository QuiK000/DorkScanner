# DORK SCANNER

A JavaFX desktop application for automated Google Dork searching across multiple search engines simultaneously.

---

## Overview

DORK SCANNER automates crawling search engines (Google, Bing, DuckDuckGo, Yandex) using dork queries and saves all discovered URLs to `results.txt` and `results_N.csv`. Supports proxy rotation, multithreading, and manual CAPTCHA solving.

---

## Requirements

| Component     | Version                              |
|---------------|--------------------------------------|
| Java          | 22+                                  |
| Maven         | 3.8.5+                               |
| Google Chrome | Latest                               |
| ChromeDriver  | Compatible with your Chrome version  |

> `chromedriver.exe` must be placed in the same directory as the application.

---

## Running

### Via Maven Wrapper (Linux / macOS)
```bash
./mvnw clean javafx:run
```

### On Windows
```cmd
mvnw.cmd clean javafx:run
```

---

## Usage

1. **Dork source** — select a `.txt` file where each line is a separate dork query.
2. **Proxy server** — optional. Provide a `.txt` file with SOCKS5 proxies in `host:port` format.
3. **Limit** — maximum number of results per dork (default: 50).
4. **Threads** — number of parallel threads (default: 5).
5. **Manual CAPTCHA** — enable to solve CAPTCHAs manually in the browser window. Forces thread count to 1 when active.
6. Click **START** to begin scanning, **STOP** to abort.

---

## Dork File Format

One query per line:

```
inurl:login.php
site:example.com filetype:sql
intitle:"index of" "passwords"
```

## Proxy File Format

One proxy per line:

```
192.168.1.1:1080
10.0.0.1:9050
```

---

## Output

After scanning completes, the following files appear in the working directory:

- **`results.txt`** — unique URLs, one per line.
- **`results_1.csv`, `results_2.csv`, ...** — results split into files of 500 records each. Format:
  ```
  https://example.com/login.php,
  ```

---

## Project Structure

```
src/main/java/com/dev/quikkkk/parser/
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
    │   ├── DorkLoader.java              # Load dorks from file
    │   ├── ResultTxtWriter.java         # Save results to TXT
    │   └── ResultCsvWriter.java         # Save results to CSV
    ├── proxy/
    │   ├── DriverPool.java              # WebDriver pool (borrow / return / restart)
    │   ├── ProxyManager.java            # Driver creation with proxy rotation
    │   └── UserAgentPool.java           # User-Agent string pool
    └── search/
        ├── GoogleSearchEngine.java
        ├── BingSearchEngine.java
        ├── DuckDuckGoSearchEngine.java
        ├── YandexSearchEngine.java
        └── SearchUtils.java             # Shared link extraction utilities
```

---

## Tech Stack

- **JavaFX 22** — graphical user interface
- **Selenium 4** — browser automation
- **Jsoup 1.22** — HTML parsing
- **Apache POI 5** — Excel support (reserved)
- **Lombok** — boilerplate reduction

---

## Disclaimer

This tool is intended solely for educational purposes and legitimate security testing of infrastructure you own or have explicit permission to test. Using it against systems without the owner's authorization is illegal.
