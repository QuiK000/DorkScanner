package com.dev.quikkkk.parser.application;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.domain.search.ISearchEngine;
import com.dev.quikkkk.parser.infrastructure.io.DorkLoader;
import com.dev.quikkkk.parser.infrastructure.io.ResultCsvWriter;
import com.dev.quikkkk.parser.infrastructure.io.ResultTxtWriter;
import com.dev.quikkkk.parser.infrastructure.proxy.ProxyManager;
import com.dev.quikkkk.parser.infrastructure.search.BingSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.DuckDuckGoSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.GoogleSearchEngine;
import com.dev.quikkkk.parser.infrastructure.search.YandexSearchEngine;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.ArrayList;
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

    public static void run(
            String dorkPath,
            String proxyPath,
            boolean manualCaptcha,
            int limit,
            int threadsCount,
            TextArea log,
            ProgressBar progressBar
    ) throws IOException {
        stopped = false;
        log(log, "Loading dorks...");
        List<String> dorks = DorkLoader.load(dorkPath);

        ProxyManager proxyManager = new ProxyManager();
        if (proxyPath != null && !proxyPath.isEmpty()) {
            proxyManager.load(proxyPath);
            log(log, "Proxy loaded");
        }

        List<ISearchEngine> engines = List.of(
                new GoogleSearchEngine(proxyManager, manualCaptcha),
                new BingSearchEngine(proxyManager, manualCaptcha),
                new DuckDuckGoSearchEngine(proxyManager, manualCaptcha),
                new YandexSearchEngine(proxyManager, manualCaptcha)
        );

        Set<SearchResult> allResults = ConcurrentHashMap.newKeySet();
        AtomicInteger done = new AtomicInteger();

        int total = Math.max(dorks.size(), 1);
        if (manualCaptcha && threadsCount > 1) {
            log(log, "Manual captcha is ONN. Forcing 1 thread to avoid multiple windows.");
            threadsCount = 1;
        }

        executor = Executors.newFixedThreadPool(threadsCount);
        List<Future<?>> futures = new ArrayList<>();

        for (String dork : dorks) {
            log(log, "Processing dork: " + dork);
            futures.add(executor.submit(() -> {
                try {
                    if (stopped) return;
                    for (ISearchEngine engine : engines) {
                        if (stopped) return;
                        log(log, "Engine: " + engine.getName());
                        allResults.addAll(engine.search(dork, limit));
                    }
                } catch (Exception e) {
                    log(log, "Error (dork " + dork + "): " + e.getMessage());
                } finally {
                    int current = done.incrementAndGet();
                    double progress = (double) current / total;
                    Platform.runLater(() -> progressBar.setProgress(progress));
                }
            }));
        }

        for (Future<?> f : futures) {
            if (stopped) break;
            try {
                f.get();
            } catch (Exception ignored) {
            }
        }

        executor.shutdownNow();
        if (!stopped) {
            log(log, "Saving TXT...");
            ResultTxtWriter.save("results.txt", allResults);

            log(log, "Saving CSV...");
            ResultCsvWriter.save(allResults, 500);

            log(log, "Done");
        } else {
            log(log, "Stopped");
        }
    }

    public static void stop() {
        stopped = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private static void log(TextArea area, String msg) {
        Platform.runLater(() -> area.appendText(msg + "\n"));
    }
}
