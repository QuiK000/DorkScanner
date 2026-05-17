package domain.model;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchResultTest {
    @Test
    void constructor_storesAddressAndDescription() {
        SearchResult result = new SearchResult("https://example.com", "inurl:login");
        assertEquals("https://example.com", result.address());
        assertEquals("inurl:login", result.description());
    }

    @Test
    void equality_sameValues_areEqual() {
        SearchResult a = new SearchResult("https://example.com", "dork");
        SearchResult b = new SearchResult("https://example.com", "dork");
        assertEquals(a, b);
    }

    @Test
    void equality_differentAddress_areNotEqual() {
        SearchResult a = new SearchResult("https://a.com", "dork");
        SearchResult b = new SearchResult("https://b.com", "dork");
        assertNotEquals(a, b);
    }

    @Test
    void equality_differentDescription_areNotEqual() {
        SearchResult a = new SearchResult("https://example.com", "dork1");
        SearchResult b = new SearchResult("https://example.com", "dork2");
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_sameValues_areSame() {
        SearchResult a = new SearchResult("https://example.com", "dork");
        SearchResult b = new SearchResult("https://example.com", "dork");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsAddressAndDescription() {
        SearchResult result = new SearchResult("https://example.com", "inurl:admin");
        String str = result.toString();
        assertTrue(str.contains("https://example.com"));
        assertTrue(str.contains("inurl:admin"));
    }

    @Test
    void nullAddress_isAllowed() {
        SearchResult result = new SearchResult(null, "dork");
        assertNull(result.address());
    }
}
