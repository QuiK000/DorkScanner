package com.dev.quikkkk.parser.infrastructure.proxy;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class ProxyManager {
    private final List<String> proxies = new CopyOnWriteArrayList<>();

    public void load(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                proxies.add(line.trim());
            }
        }
    }

    public WebDriver createDriverWithRetry(boolean isManualCaptcha, Consumer<String> logger) {
        if (proxies.isEmpty()) return buildDriver(null, isManualCaptcha);

        int attempts = 0;
        int maxAttempts = proxies.size() * 2;

        while (attempts < maxAttempts) {
            String proxyAddr = getRandomProxy();
            try {
                return buildDriver(proxyAddr, isManualCaptcha);
            } catch (Exception e) {
                logger.accept("Прокси не рабочий: " + proxyAddr + " -> Пробую другой...");
                attempts++;
            }
        }

        throw new RuntimeException("Не удалось найти рабочий прокси после " + attempts + " попыток.");
    }

    private WebDriver buildDriver(String proxyAddr, boolean isManualCaptcha) {
        ChromeOptions options = new ChromeOptions();

        if (!isManualCaptcha) options.addArguments("--headless=new");

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--user-agent=" + UserAgentPool.random());
        options.addArguments("--remote-allow-origins=*");

        if (proxyAddr != null) {
            Proxy proxy = new Proxy();

            proxy.setSocksProxy(proxyAddr);
            proxy.setSocksVersion(5);
            options.setProxy(proxy);
        }

        return new ChromeDriver(options);
    }

    private String getRandomProxy() {
        if (proxies.isEmpty()) return null;
        int randomIndex = ThreadLocalRandom.current().nextInt(proxies.size());
        return proxies.get(randomIndex);
    }
}
