package ru.kazov.collectivepurchases.worker.services;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kazov.collectivepurchases.worker.common.AppProperties;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class PlaywrightService {
    private Playwright playwright;
    private final Path userDataDir = Paths.get("context");
    private final AppProperties properties;

    public BrowserContext launch() {
        playwright = Playwright.create();
        BrowserType chromium = playwright.chromium();
        BrowserContext context = chromium.launchPersistentContext(userDataDir,
                new BrowserType.LaunchPersistentContextOptions().setHeadless(properties.isHeadless())
                        .setViewportSize(null));
        context.setDefaultTimeout(60000);
        return context;
    }

    public void launchHeaded() {
        playwright = Playwright.create();
        BrowserType chromium = playwright.chromium();
        BrowserContext context = chromium.launchPersistentContext(userDataDir,
                new BrowserType.LaunchPersistentContextOptions().setHeadless(properties.isHeadless())
                        .setViewportSize(null));
        context.setDefaultTimeout(60000);
    }

    public void close() {
        if (playwright != null)
            playwright.close();
    }


    @PreDestroy
    private void onDestroy() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
