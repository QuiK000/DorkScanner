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
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyManager {
    private final List<String> proxies = new CopyOnWriteArrayList<>();
    private final AtomicInteger index = new AtomicInteger();

    public void load(String filePath) throws IOException {
        proxies.addAll(Files.readAllLines(Path.of(filePath)));
    }

    public WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--user-agent=" + UserAgentPool.random());

        String proxyAddr = nextProxy();
        if (proxyAddr != null) {
            Proxy proxy = new Proxy();

            proxy.setSocksProxy(proxyAddr);
            proxy.setSocksVersion(5);
            options.setProxy(proxy);
        }

        return new ChromeDriver(options);
    }

    private String nextProxy() {
        if (proxies.isEmpty()) return null;
        return proxies.get(Math.abs(index.getAndIncrement() % proxies.size()));
    }
}
