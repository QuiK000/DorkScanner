package com.dev.quikkkk.parser.application;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.domain.search.ISearchEngine;
import com.dev.quikkkk.parser.infrastructure.io.DorkLoader;
import com.dev.quikkkk.parser.infrastructure.io.ResultCsvWriter;
import com.dev.quikkkk.parser.infrastructure.io.ResultTxtWriter;
import com.dev.quikkkk.parser.infrastructure.proxy.ProxyManager;
import com.dev.quikkkk.parser.infrastructure.search.GoogleSearchEngine;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ParserService {
    public static void run(
            String dorkPath,
            String proxyPath,
            TextArea log,
            ProgressBar progressBar
    ) throws IOException, InterruptedException, ExecutionException {
        log(log, "Loading dorks...");
        List<String> dorks = DorkLoader.load(dorkPath);

        ProxyManager proxyManager = new ProxyManager();
        if (proxyPath != null && !proxyPath.isEmpty()) {
            proxyManager.load(proxyPath);
            log(log, "Proxy loaded");
        }

        List<ISearchEngine> engines = List.of(new GoogleSearchEngine(proxyManager));
        Set<SearchResult> allResults = ConcurrentHashMap.newKeySet();
        AtomicInteger done = new AtomicInteger();

        int total = dorks.size();
        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            List<Future<?>> futures = new ArrayList<>();

            for (String dork : dorks) {
                log(log, "Processing dork: " + dork);
                futures.add(executor.submit(() -> {
                    try {
                        for (ISearchEngine engine : engines) {
                            log(log, "Engine: " + engine.getName());
                            allResults.addAll(engine.search(dork, 50));
                        }

                        int current = done.incrementAndGet();
                        double progress = (double) current / total;

                        Platform.runLater(() -> progressBar.setProgress(progress));
                    } catch (Exception ignored) {}
                }));
            }

            for (Future<?> f : futures) f.get();
        }

        log(log, "Saving TXT...");
        ResultTxtWriter.save("results.txt", allResults);

        log(log, "Saving CSV...");
        ResultCsvWriter.save(allResults, 500);

        log(log, "Done");
    }

    private static void log(TextArea area, String msg) {
        Platform.runLater(() -> area.appendText(msg + "\n"));
    }
}
