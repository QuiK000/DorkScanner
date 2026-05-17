package infrastructure.proxy;

import com.dev.quikkkk.parser.infrastructure.proxy.ProxyManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProxyManagerTest {
    @TempDir
    Path tempDir;

    @Test
    void load_validFile_doesNotThrow() throws IOException {
        Path file = tempDir.resolve("proxies.txt");
        Files.writeString(file, "192.168.1.1:1080\n10.0.0.1:9050\n");

        ProxyManager manager = new ProxyManager();
        assertDoesNotThrow(() -> manager.load(file.toString()));
    }

    @Test
    void load_emptyFile_doesNotThrow() throws IOException {
        Path file = tempDir.resolve("proxies.txt");
        Files.writeString(file, "");

        ProxyManager manager = new ProxyManager();
        assertDoesNotThrow(() -> manager.load(file.toString()));
    }

    @Test
    void load_skipsBlankLines() throws IOException {
        Path file = tempDir.resolve("proxies.txt");
        Files.writeString(file, "192.168.1.1:1080\n\n   \n10.0.0.1:9050\n");

        ProxyManager manager = new ProxyManager();
        assertDoesNotThrow(() -> manager.load(file.toString()));
    }

    @Test
    void load_multipleEntries_allAccepted() throws IOException {
        Path file = tempDir.resolve("proxies.txt");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            sb.append("10.0.0.").append(i % 265).append(":").append(1000 + i).append("\n");
        }

        Files.writeString(file, sb.toString());
        ProxyManager manager = new ProxyManager();

        assertDoesNotThrow(() -> manager.load(file.toString()));
    }

    @Test
    void createDriverWithRetry_withoutProxies_attemptsDirectDriver() {
        ProxyManager manager = new ProxyManager();
        assertThrows(Exception.class, () -> manager.createDriverWithRetry(false, msg -> {}));
    }

    @Test
    void createDriverWithRetry_withProxies_exhaustsAttemptsAndThrows() throws IOException {
        Path file = tempDir.resolve("proxies.txt");
        Files.writeString(file, "127.0.0.1:19999\n");

        ProxyManager manager = new ProxyManager();
        manager.load(file.toString());

        assertThrows(Exception.class, () -> manager.createDriverWithRetry(false, msg -> {}));
    }
}
