package com.dev.quikkkk.parser.infrastructure.proxy;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class DriverPool {
    private final BlockingQueue<WebDriver> drivers;
    private final ProxyManager proxyManager;
    private final boolean manualCaptcha;
    private final Consumer<String> logger;

    public DriverPool(int poolSize, ProxyManager proxyManager, boolean manualCaptcha, Consumer<String> logger) {
        this.drivers = new LinkedBlockingQueue<>(poolSize);
        this.proxyManager = proxyManager;
        this.manualCaptcha = manualCaptcha;
        this.logger = logger;

        for (int i = 0; i < poolSize; i++) {
            try {
                WebDriver driver = proxyManager.createDriverWithRetry(manualCaptcha, logger);
                drivers.offer(driver);
            } catch (Exception e) {
                logger.accept("Критическая ошибка создания пула драйверов: " + e.getMessage());
            }
        }
    }

    public WebDriver borrow() throws InterruptedException {
        return drivers.take();
    }

    public void returnDriver(WebDriver driver) {
        if (driver != null) {
            drivers.offer(driver);
        }
    }

    public WebDriver restartDriver(WebDriver oldDriver) {
        if (oldDriver != null) {
            try {
                oldDriver.quit();
            } catch (Exception ignored) {}
        }

        logger.accept("Пересоздаю поток с новым прокси...");
        try {
            return proxyManager.createDriverWithRetry(manualCaptcha, logger);
        } catch (Exception e) {
            logger.accept("Не удалось пересоздать драйвер: " + e.getMessage());
            return null;
        }
    }
}
