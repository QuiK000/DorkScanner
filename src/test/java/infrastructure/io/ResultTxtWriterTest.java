package infrastructure.io;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.infrastructure.io.ResultTxtWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultTxtWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void save_writesUrlsOnePerLine() throws IOException {
        Path file = tempDir.resolve("results.txt");
        List<SearchResult> results = List.of(
                new SearchResult("https://example.com/login", "inurl:login"),
                new SearchResult("https://test.org/admin", "inurl:admin")
        );

        ResultTxtWriter.save(file.toString(), results);
        List<String> lines = Files.readAllLines(file);

        assertEquals(2, lines.size());
        assertTrue(lines.contains("https://example.com/login"));
        assertTrue(lines.contains("https://test.org/admin"));
    }

    @Test
    void save_deduplicatesAddress() throws IOException {
        Path file = tempDir.resolve("results.txt");
        List<SearchResult> results = List.of(
                new SearchResult("https://example.com/login", "inurl:login"),
                new SearchResult("https://example.com/login", "site:example.com")
        );

        ResultTxtWriter.save(file.toString(), results);
        List<String> lines = Files.readAllLines(file);

        assertEquals(1, lines.size());
        assertEquals("https://example.com/login", lines.getFirst());
    }

    @Test
    void save_emptyCollection_createsEmptyFile() throws IOException {
        Path file = tempDir.resolve("results.txt");
        ResultTxtWriter.save(file.toString(), List.of());

        assertTrue(Files.exists(file));
        assertEquals(0, Files.readAllLines(file).size());
    }

    @Test
    void save_overwritesExistingFile() throws IOException {
        Path file = tempDir.resolve("results.txt");
        Files.writeString(file, "stale content\n");

        ResultTxtWriter.save(file.toString(), List.of(
                new SearchResult("https://new.com", "dork")
        ));

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("https://new.com", lines.getFirst());
    }

    @Test
    void save_largeResultSet_writesAllEntries() throws IOException {
        Path file = tempDir.resolve("results.txt");
        Set<SearchResult> results = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            results.add(
                    new SearchResult("https://site" + i + ".com", "dork")
            );
        }

        ResultTxtWriter.save(file.toString(), results);
        List<String> lines = Files.readAllLines(file);

        assertEquals(1000, lines.size());
    }
}
