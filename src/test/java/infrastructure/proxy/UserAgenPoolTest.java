package infrastructure.proxy;

import com.dev.quikkkk.parser.infrastructure.proxy.UserAgentPool;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAgenPoolTest {
    @Test
    void random_returnsNonNull() {
        assertNotNull(UserAgentPool.random());
    }

    @Test
    void random_returnsNonEmptyString() {
        assertFalse(UserAgentPool.random().isBlank());
    }

    @Test
    void random_containsMozillaPrefix() {
        String agent = UserAgentPool.random();
        assertTrue(agent.startsWith("Mozilla/"), "The User-Agent must start with 'Mozilla/': " + agent);
    }

    @RepeatedTest(30)
    void random_alwaysReturnsKnownAgent() {
        Set<String> known = Set.of(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/119",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/537.36"
        );

        assertTrue(known.contains(UserAgentPool.random()), "random() returned an unexpected user agent");
    }

    @Test
    void random_returnsVariousAgents_overManyCallsV() {
        Set<String> observed = new HashSet<>();
        for (int i = 0; i < 50; i++) observed.add(UserAgentPool.random());
        assertTrue(observed.size() >= 2, "At least 2 different user agents must appear within 50 calls");
    }
}
