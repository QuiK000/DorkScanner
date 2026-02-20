package com.dev.quikkkk.parser.infrastructure.search;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.domain.search.ISearchEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DuckDuckGoSearchEngine implements ISearchEngine {
    private static final long CAPTCHA_TIMEOUT_MS = 60000;

    @Override
    public String getName() {
        return DuckDuckGoSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit, WebDriver driver) throws InterruptedException {
        Set<SearchResult> results = new HashSet<>();

        driver.get("https://duckduckgo.com/?q=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));
        while (results.size() < limit) {
            if (!waitForCaptcha(driver)) break;
            Thread.sleep(2500);

            List<WebElement> links = driver.findElements(By.cssSelector("a[data-testid='result-title-a']"));
            if (links.isEmpty())
                links = driver.findElements(By.cssSelector("div.result__extras__url a.result__url"));

            int initialSize = results.size();
            SearchUtils.extractLinks(links, results, limit, dork);

            if (results.size() == initialSize) break;
            List<WebElement> next = driver.findElements(By.id("more-results"));
            if (next.isEmpty()) break;

            try {
                next.getFirst().click();
            } catch (Exception e) {
                break;
            }
        }

        return results;
    }

    private boolean waitForCaptcha(WebDriver driver) throws InterruptedException {
        long start = System.currentTimeMillis();

        while (Objects.requireNonNull(driver.getPageSource()).contains("If this error persists")
                || Objects.requireNonNull(driver.getCurrentUrl()).contains("lite.duckduckgo.com")
        ) {
            if (System.currentTimeMillis() - start > CAPTCHA_TIMEOUT_MS) {
                System.out.println("Timeout waiting for DUckDuckGo block. Skipping...");
                return false;
            }

            System.out.println("Waiting to resolve DuckDuckGo block");
            Thread.sleep(3000);
        }

        return true;
    }
}
