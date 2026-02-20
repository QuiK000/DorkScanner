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

public class GoogleSearchEngine implements ISearchEngine {
    private static final long CAPTCHA_TIMEOUT_MS = 60000;

    @Override
    public String getName() {
        return GoogleSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit, WebDriver driver) throws InterruptedException {
        Set<SearchResult> results = new HashSet<>();
        driver.get("https://www.google.com/search?q=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));

        while (results.size() < limit) {
            if (!waitForCaptcha(driver)) break;
            Thread.sleep(2500);

            List<WebElement> links = driver.findElements(By.cssSelector("div.yuRUbf a"));
            SearchUtils.extractLinks(links, results, limit, dork);

            List<WebElement> next = driver.findElements(By.id("pnnext"));
            if (next.isEmpty()) break;

            next.getFirst().click();
        }

        return results;
    }

    private boolean waitForCaptcha(WebDriver driver) throws InterruptedException {
        long start = System.currentTimeMillis();

        while (Objects.requireNonNull(driver.getCurrentUrl()).contains("sorry/index")) {
            if (System.currentTimeMillis() - start > CAPTCHA_TIMEOUT_MS) {
                System.out.println("Timeout waiting for Google captcha. Skipping...");
                return false;
            }

            System.out.println("Waiting to resolve captcha");
            Thread.sleep(3000);
        }

        return true;
    }
}
