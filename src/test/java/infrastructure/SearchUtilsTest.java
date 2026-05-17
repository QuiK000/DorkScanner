package infrastructure;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.infrastructure.search.SearchUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchUtilsTest {
    private static WebElement stubLink(String href) {
        return new WebElement() {
            @Override
            public String getAttribute(@NonNull String name) {
                return "href".equals(name) ? href : null;
            }

            @Override
            public void click() {
            }

            @Override
            public void submit() {
            }

            @Override
            public void sendKeys(CharSequence @NonNull ... keysToSend) {
            }

            @Override
            public void clear() {
            }

            @Override
            @NullMarked
            public String getTagName() {
                return "a";
            }

            @Override
            @NullMarked
            public String getText() {
                return href;
            }

            @Override
            public boolean isDisplayed() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            @NullMarked
            public Point getLocation() {
                return new Point(0, 0);
            }

            @Override
            @NullMarked
            public Dimension getSize() {
                return new Dimension(0, 0);
            }

            @Override
            @NullMarked
            public Rectangle getRect() {
                return new Rectangle(0, 0, 0, 0);
            }

            @Override
            @NullMarked
            public String getCssValue(String p) {
                return "";
            }

            @Override
            @NullMarked
            public <X> X getScreenshotAs(OutputType<X> t) {
                return null;
            }

            @Override
            public WebElement findElement(@NonNull By by) {
                return null;
            }

            @Override
            @NullMarked
            public List<WebElement> findElements(By by) {
                return List.of();
            }

            @Override
            public SearchContext getShadowRoot() {
                return null;
            }

            @Override
            public String getDomProperty(@NonNull String name) {
                return null;
            }

            @Override
            public String getDomAttribute(@NonNull String name) {
                return null;
            }

            @Override
            public String getAriaRole() {
                return "";
            }

            @Override
            public String getAccessibleName() {
                return "";
            }
        };
    }

    @Test
    void extractLinks_addsMatchingUrls() {
        List<WebElement> links = List.of(
                stubLink("https://example.com/login"),
                stubLink("https://target.org/admin")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 10, "inurl:login");

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.address().equals("https://example.com/login")));
        assertTrue(results.stream().anyMatch(r -> r.address().equals("https://target.org/admin")));
    }

    @Test
    void extractLinks_respectsLimit() {
        List<WebElement> links = List.of(
                stubLink("https://a.com"),
                stubLink("https://b.com"),
                stubLink("https://c.com")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 2, "dork");
        assertEquals(2, results.size());
    }

    @Test
    void extractLinks_skipsIgnoredDomains() {
        List<WebElement> links = List.of(
                stubLink("https://www.google.com/search?q=test"),
                stubLink("https://www.bing.com/search"),
                stubLink("https://duckduckgo.com/?q=test"),
                stubLink("https://www.youtube.com/watch"),
                stubLink("https://msn.com/news"),
                stubLink("https://legit-site.com/page")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 100, "dork");

        assertEquals(1, results.size());
        assertEquals("https://legit-site.com/page", results.iterator().next().address());
    }

    @Test
    void extractLinks_skipsNullAndEmptyHrefs() {
        List<WebElement> links = List.of(
                stubLink(null),
                stubLink(""),
                stubLink("https://valid.com/page")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 10, "dork");
        assertEquals(1, results.size());
    }

    @Test
    void extractLinks_withCustomFilter_appliesFilter() {
        List<WebElement> links = List.of(
                stubLink("https://allowed.com/page"),
                stubLink("https://blocked.com/page")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 10, "dork", href -> href.contains("allowed"));

        assertEquals(1, results.size());
        assertEquals("https://allowed.com/page", results.iterator().next().address());
    }

    @Test
    void extractLinks_storesCorrectDorkInResult() {
        List<WebElement> links = List.of(stubLink("https://example.com"));
        Set<SearchResult> results = new HashSet<>();

        SearchUtils.extractLinks(links, results, 10, "inurl:config.php");
        SearchResult result = results.iterator().next();

        assertEquals("inurl:config.php", result.description());
    }

    @Test
    void extractLinks_doesNotAddDuplicates() {
        List<WebElement> links = List.of(
                stubLink("https://example.com"),
                stubLink("https://example.com")
        );

        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(links, results, 10, "dork");

        assertEquals(1, results.size());
    }

    @Test
    void extractLinks_emptyLinkList_returnsEmptyResults() {
        Set<SearchResult> results = new HashSet<>();
        SearchUtils.extractLinks(List.of(), results, 10, "dork");
        assertTrue(results.isEmpty());
    }

    @Test
    void extractLinks_limitAlreadyReached_addsNothing() {
        List<WebElement> links = List.of(stubLink("https://new-site.com"));
        Set<SearchResult> results = new HashSet<>();

        results.add(new SearchResult("https://existing.com", "dork"));
        SearchUtils.extractLinks(links, results, 1, "dork");

        assertEquals(1, results.size());
    }
}
