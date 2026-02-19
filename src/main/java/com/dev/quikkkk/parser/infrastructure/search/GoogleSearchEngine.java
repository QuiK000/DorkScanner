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
public class GoogleSearchEngine implements ISearchEngine {
    private final ProxyManager proxyManager;
    private final boolean manualCaptcha;

    @Override
    public String getName() {
        return GoogleSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit) throws InterruptedException {
        WebDriver driver = proxyManager.createDriver(manualCaptcha);
        Set<SearchResult> results = new HashSet<>();

        try {
            driver.get("https://www.google.com/search?q=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));
            while (results.size() < limit) {
                waitForCaptcha(driver);
                Thread.sleep(2500);
                List<WebElement> links = driver.findElements(By.cssSelector("div.yuRUbf a"));
                for (WebElement el : links) {
                    if (results.size() >= limit) break;
                    results.add(new SearchResult(el.getAttribute("href"), dork));
                }

                List<WebElement> next = driver.findElements(By.id("pnnext"));
                if (next.isEmpty()) break;
                next.getFirst().click();
            }
        } finally {
            driver.quit();
        }

        return results;
    }

    private void waitForCaptcha(WebDriver driver) throws InterruptedException {
        if (manualCaptcha) {
            while (Objects.requireNonNull(driver.getCurrentUrl()).contains("sorry/index")) {
                System.out.println("Waiting to resolve captcha");
                Thread.sleep(3000);
            }
        }
    }
}
