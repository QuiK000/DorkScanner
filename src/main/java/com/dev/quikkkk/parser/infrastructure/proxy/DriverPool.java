package com.dev.quikkkk.parser.infrastructure.proxy;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DriverPool {
    private final BlockingQueue<WebDriver> pool;

    public DriverPool(int size, ProxyManager proxyManager, boolean manualCaptcha) {
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.offer(proxyManager.createDriver(manualCaptcha));
        }
    }

    public WebDriver borrow() throws InterruptedException {
        return pool.take();
    }

    public void returnDriver(WebDriver driver) {
        pool.offer(driver);
    }

    public void shutdown() {
        WebDriver driver;
        while ((driver = pool.poll()) != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
        }
    }
}
