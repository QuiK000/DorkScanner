package infrastructure.io;

import com.dev.quikkkk.parser.domain.model.SearchResult;
import com.dev.quikkkk.parser.infrastructure.io.ResultCsvWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultCsvWriterTest {
    @Test
    void save_singleFile_whenResultsBelowLimit() throws IOException {
        List<SearchResult> results = List.of(
                new SearchResult("https://a.com", "dork1"),
                new SearchResult("https://b.com", "dork2")
        );

        ResultCsvWriter.save(results, 500);
        Path file = Path.of("results_1.csv");

        assertTrue(Files.exists(file), "results_1.csv must exist");
        List<String> lines = Files.readAllLines(file);

        assertEquals(2, lines.size());
        assertTrue(lines.getFirst().startsWith("https://a.com,") || lines.getFirst().startsWith("https://b.com,"));

        Files.deleteIfExists(file);
    }

    @Test
    void save_splitsIntoMultipleFiles_whenLimitExceeded() throws IOException {
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            results.add(
                    new SearchResult("https://site" + i + ".com", "dork")
            );
        }

        ResultCsvWriter.save(results, 3);

        Path f1 = Path.of("results_1.csv");
        Path f2 = Path.of("results_2.csv");
        Path f3 = Path.of("results_3.csv");

        assertTrue(Files.exists(f1), "results_1.csv must exist");
        assertTrue(Files.exists(f2), "results_2.csv must exist");
        assertTrue(Files.exists(f3), "results_3.csv must exist");

        assertEquals(3, Files.readAllLines(f1).size());
        assertEquals(3, Files.readAllLines(f2).size());
        assertEquals(1, Files.readAllLines(f3).size());

        Files.deleteIfExists(f1);
        Files.deleteIfExists(f2);
        Files.deleteIfExists(f3);
    }

    @Test
    void save_eachLineEndsWithComma() throws IOException {
        List<SearchResult> results = List.of(
                new SearchResult("https://example.com/page", "inurl:page")
        );

        ResultCsvWriter.save(results, 500);

        Path file = Path.of("results_1.csv");
        List<String> lines = Files.readAllLines(file);

        assertFalse(lines.isEmpty());
        assertTrue(lines.getFirst().endsWith(","), "Each CSV row must end with a comma");

        Files.deleteIfExists(file);
    }

    @Test
    void save_emptyCollection_createsNoFiles() throws IOException {
        Path path = Path.of("results_1.csv");
        Files.deleteIfExists(path);

        ResultCsvWriter.save(List.of(), 500);
        assertFalse(Files.exists(path), "There must be no CSV files when the collection is empty");
    }

    @Test
    void save_exactlyLimitResults_producesOneFile() throws IOException {
        List<SearchResult> results = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            results.add(
                    new SearchResult("https://x" + i + ".com", "dork")
            );
        }

        ResultCsvWriter.save(results, 5);

        Path f1 = Path.of("results_1.csv");
        Path f2 = Path.of("results_2.csv");

        assertTrue(Files.exists(f1));
        assertFalse(Files.exists(f2), "The second file must not be created when the limit is matched exactly");

        assertEquals(5, Files.readAllLines(f1).size());
        Files.deleteIfExists(f1);
    }
}
