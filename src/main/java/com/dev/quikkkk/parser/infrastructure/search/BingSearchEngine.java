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
import java.util.Set;

public class BingSearchEngine implements ISearchEngine {
    @Override
    public String getName() {
        return BingSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit, WebDriver driver) throws InterruptedException {
        Set<SearchResult> results = new HashSet<>();
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

        return results;
    }

    private void waitForCaptcha(WebDriver driver) throws InterruptedException {
        String url = driver.getCurrentUrl();
        while (url != null && (url.contains("challenge") || url.contains("verify"))) {
            System.out.println("Waiting to resolve Bing captcha");
            Thread.sleep(3000);
            url = driver.getCurrentUrl();
        }
    }
}
