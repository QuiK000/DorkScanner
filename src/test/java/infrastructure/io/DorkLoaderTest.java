package infrastructure.io;

import com.dev.quikkkk.parser.infrastructure.io.DorkLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DorkLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    void load_returnsAllNonEmptyLines() throws IOException {
        Path file = tempDir.resolve("dorks.txt");
        Files.writeString(file, "inurl:login.php\nsite:example.com filetype:sql\nintitle:\"index of\"\n");

        List<String> dorks = DorkLoader.load(file.toString());

        assertEquals(3, dorks.size());
        assertEquals("inurl:login.php", dorks.getFirst());
        assertEquals("site:example.com filetype:sql", dorks.get(1));
        assertEquals("intitle:\"index of\"", dorks.get(2));
    }

    @Test
    void load_skipsBlankLines() throws IOException {
        Path file = tempDir.resolve("dorks.txt");
        Files.writeString(file, "inurl:admin\n\n  \nsite:example.com\n");

        List<String> dorks = DorkLoader.load(file.toString());
        assertEquals(2, dorks.size());
    }

    @Test
    void load_trimsWhitespace() throws IOException {
        Path file = tempDir.resolve("dorks.txt");
        Files.writeString(file, "  inurl:admin  \n  site:test.com  \n");

        List<String> dorks = DorkLoader.load(file.toString());

        assertEquals("inurl:admin", dorks.getFirst());
        assertEquals("site:test.com", dorks.getLast());
    }

    @Test
    void load_emptyFile_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("dorks.txt");
        Files.writeString(file, "");

        List<String> dorks = DorkLoader.load(file.toString());
        assertTrue(dorks.isEmpty());
    }

    @Test
    void load_throwsIOException_forMissingFile() {
        assertThrows(IOException.class, () -> DorkLoader.load("/nonexistent/path/dorks.txt"));
    }

    @Test
    void load_singLine_withoutNewline() throws IOException {
        Path file = tempDir.resolve("dorks.txt");
        Files.writeString(file, "inurl:config.php");

        List<String> dorks = DorkLoader.load(file.toString());

        assertEquals(1, dorks.size());
        assertEquals("inurl:config.php", dorks.getFirst());
    }
}
