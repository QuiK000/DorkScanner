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
import java.util.Set;

@AllArgsConstructor
public class BingSearchEngine implements ISearchEngine {
    private final ProxyManager proxyManager;
    private final boolean manualCaptcha;

    @Override
    public String getName() {
        return BingSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit) throws InterruptedException {
        WebDriver driver = proxyManager.createDriver(manualCaptcha);
        Set<SearchResult> results = new HashSet<>();

        try {
            driver.get("https://www.bing.com/search?q=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));
            while (results.size() < limit) {
                waitForCaptcha(driver);
                Thread.sleep(2500);

                List<WebElement> links = driver.findElements(By.cssSelector("li.b_algo h2 a"));
                SearchUtils.extractLinks(links, results, limit, dork);

                List<WebElement> next = driver.findElements(By.cssSelector("a.sb_pagN"));
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
            String url = driver.getCurrentUrl();
            while (url != null && (url.contains("challenge") || url.contains("verify"))) {
                System.out.println("Waiting to resolve Bing captcha");
                Thread.sleep(3000);
                url = driver.getCurrentUrl();
            }
        }
    }
}
