package com.dev.quikkkk.parser.infrastructure.proxy;

import java.util.List;
import java.util.Random;

public class UserAgentPool {

    private static final List<String> AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/119",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/537.36"
    );

    private static final Random random = new Random();

    public static String random() {
        return AGENTS.get(random.nextInt(AGENTS.size()));
    }
}
