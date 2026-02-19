package com.dev.quikkkk.parser.infrastructure.search;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SearchUtils {
    public static void extractLinks(
            List<WebElement> links,
            Set<SearchResult> results,
            int limit,
            String dork,
            Predicate<String> filter
    ) {
        for (WebElement el : links) {
            if (results.size() >= limit) break;

            String href = el.getAttribute("href");
            if (href != null && !href.isEmpty() && filter.test(href)) {
                results.add(new SearchResult(href, dork));
            }
        }
    }

    public static void extractLinks(List<WebElement> links, Set<SearchResult> results, int limit, String dork) {
        extractLinks(links, results, limit, dork, _ -> true);
    }
}