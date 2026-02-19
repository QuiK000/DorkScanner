package com.dev.quikkkk.parser.infrastructure.search;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.domain.search.ISearchEngine;
import com.dev.quikkkk.parser.infrastructure.proxy.ProxyManager;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
public class DuckDuckGoSearchEngine implements ISearchEngine {
    private final ProxyManager proxyManager;
    private final boolean manualCaptcha;

    @Override
    public String getName() {
        return DuckDuckGoSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit) throws InterruptedException {
        WebDriver driver = proxyManager.createDriver(manualCaptcha);
        Set<SearchResult> results = new HashSet<>();

        try {
            driver.get("https://duckduckgo.com/?q=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));
            while (results.size() < limit) {
                waitForCaptcha(driver);
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
        } finally {
            driver.quit();
        }

        return results;
    }

    private void waitForCaptcha(WebDriver driver) throws InterruptedException {
        if (manualCaptcha) {
            while (Objects.requireNonNull(driver.getPageSource()).contains("If this error persists")
                    || Objects.requireNonNull(driver.getCurrentUrl()).contains("lite.duckduckgo.com")
            ) {
                System.out.println("Waiting to resolve DuckDuckGo block");
                Thread.sleep(3000);
            }
        }
    }
}
