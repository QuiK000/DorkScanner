package com.dev.quikkkk.parser.application;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.domain.search.ISearchEngine;
import com.dev.quikkkk.parser.infrastructure.io.DorkLoader;
import com.dev.quikkkk.parser.infrastructure.io.ResultCsvWriter;
import com.dev.quikkkk.parser.infrastructure.io.ResultTxtWriter;
import com.dev.quikkkk.parser.infrastructure.proxy.DriverPool;
import com.dev.quikkkk.parser.infrastructure.proxy.ProxyManager;
import com.dev.quikkkk.parser.infrastructure.search.BingSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.DuckDuckGoSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.GoogleSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.YandexSearchEngine;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ParserService {
    private static volatile boolean stopped = false;
    private static ExecutorService executor;
    private static DriverPool driverPool;

    public static void run(
            String dorkPath,
            String proxyPath,
            boolean manualCaptcha,
            int limit,
            int threadsCount,
            TextArea log,
            ProgressBar progressBar,
            Label statusLabel
    ) throws IOException {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        File f = new File("chromedriver.exe");

        if (!f.exists()) log(log, "ОШИБКА: Файл chromedriver.exe не найден рядом с программой!");

        stopped = false;
        updateStatus(statusLabel, "Статус: Выполняется...");

        log(log, "Загрузка дорков...");
        List<String> dorks = DorkLoader.load(dorkPath);

        ProxyManager proxyManager = new ProxyManager();
        if (proxyPath != null && !proxyPath.isEmpty()) {
            proxyManager.load(proxyPath);
            log(log, "Прокси загружены. Режим: Рандом + Авто-замена нерабочих.");
        }

        log(log, "Запуск браузеров...");
        driverPool = new DriverPool(threadsCount, proxyManager, manualCaptcha, msg -> log(log, msg));

        List<ISearchEngine> engines = List.of(
                new GoogleSearchEngine(),
                new BingSearchEngine(),
                new DuckDuckGoSearchEngine(),
                new YandexSearchEngine()
        );

        Set<SearchResult> allResults = ConcurrentHashMap.newKeySet();
        AtomicInteger done = new AtomicInteger();

        int total = Math.max(dorks.size(), 1);
        if (manualCaptcha && threadsCount > 1) {
            log(log, "Ручная капча ВКЛ. Принудительно 1 поток во избежание множества окон.");
            threadsCount = 1;
        }

        executor = Executors.newFixedThreadPool(threadsCount);
        List<Future<?>> futures = new ArrayList<>();

        for (String dork : dorks) {
            log(log, "Обработка дорков: " + dork);
            futures.add(executor.submit(() -> {
                WebDriver driver = null;
                try {
                    if (stopped) return;
                    driver = driverPool.borrow();

                    List<ISearchEngine> randomEngines = new ArrayList<>(engines);
                    Collections.shuffle(randomEngines);

                    for (ISearchEngine engine : randomEngines) {
                        if (stopped) return;
                        try {
                            log(log, "Поисковик: " + engine.getName() + " | Дорк: " + dork);
                            allResults.addAll(engine.search(dork, limit, driver));
                        } catch (Exception e) {
                            log(log, "Ошибка поиска ( " + engine.getName() + "): " + e.getMessage());
                            WebDriver freshDriver = driverPool.restartDriver(driver);
                            if (freshDriver != null) {
                                driver = freshDriver;
                            }
                        }
                    }
                } catch (Throwable e) {
                    log(log, "КРИТИЧЕСКАЯ ОШИБКА ПОТОКА: " + e);
                } finally {
                    if (driver != null) driverPool.returnDriver(driver);

                    int current = done.incrementAndGet();
                    double progress = (double) current / total;

                    Platform.runLater(() -> progressBar.setProgress(progress));
                }
            }));
        }

        for (Future<?> fut : futures) {
            if (stopped) break;
            try {
                fut.get();
            } catch (Throwable e) {
                log(log, "ОШИБКА ПРИ ОЖИДАНИИ: " + e);
            }
        }

        executor.shutdownNow();

        if (!stopped) {
            log(log, "Сохранение TXT...");
            ResultTxtWriter.save("results.txt", allResults);

            log(log, "Сохранение CSV...");
            ResultCsvWriter.save(allResults, 500);

            log(log, "Готово");
            updateStatus(statusLabel, "Статус: Завершено");
        } else {
            log(log, "Остановлено пользователем");
            updateStatus(statusLabel, "Статус: Остановленно");
        }
    }

    public static void stop() {
        stopped = true;
        if (executor != null) executor.shutdownNow();
    }

    private static void log(TextArea area, String msg) {
        Platform.runLater(() -> area.appendText(msg + "\n"));
    }

    private static void updateStatus(Label label, String text) {
        Platform.runLater(() -> label.setText(text));
    }
}
