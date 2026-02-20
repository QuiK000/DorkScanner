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

public class YandexSearchEngine implements ISearchEngine {
    @Override
    public String getName() {
        return YandexSearchEngine.class.getSimpleName();
    }

    @Override
    public Set<SearchResult> search(String dork, int limit, WebDriver driver) throws InterruptedException {
        Set<SearchResult> results = new HashSet<>();

        driver.get("https://yandex.com/search/?text=" + URLEncoder.encode(dork, StandardCharsets.UTF_8));

        while (results.size() < limit) {
            waitForCaptcha(driver);
            Thread.sleep(2500);

            List<WebElement> links = driver.findElements(By.cssSelector("li.serp-item a.organic__url"));
            if (links.isEmpty()) {
                links = driver.findElements(By.cssSelector("a.Link.Link_theme_normal"));
            }

            int initialSize = results.size();
            SearchUtils.extractLinks(links, results, limit, dork, href ->
                    !href.contains("yandex.ru") && !href.contains("yandex.com") && !href.contains("yastatic.net")
            );

            if (results.size() == initialSize) break;
            List<WebElement> next = driver.findElements(By.cssSelector("a.pager__item_kind_next"));
            if (next.isEmpty()) break;

            try {
                next.getFirst().click();
            } catch (Exception e) {
                break;
            }
        }

        return results;
    }

    private void waitForCaptcha(WebDriver driver) throws InterruptedException {
        String url = driver.getCurrentUrl();
        while (url != null && (url.contains("showcaptcha") || url.contains("captcha"))) {
            System.out.println("Waiting to resolve Yandex captcha");
            Thread.sleep(3000);
            url = driver.getCurrentUrl();
        }
    }
}