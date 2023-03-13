package ru.kazov.collectivepurchases.worker.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import ru.kazov.collectivepurchases.worker.services.PlaywrightService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
@Component
public class CpTray {
    private final ApplicationContext applicationContext;
    private final AppProperties appProperties;
    private final PlaywrightService playwrightService;
    private JFrame consoleFrame;
    private MenuItem labelItem;

    private Image image_color;
    private Image image_gray;

    @PostConstruct
    public void onInit() {
        if (SystemTray.isSupported()) {
            image_color = createImage("shop.png");
            image_gray = createImage("shop_grey.png");

            createSystemTray();
            //consoleFrame = createConsoleFrame();
        }
    }

    private void createSystemTray() {
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image_color);
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        labelItem = new MenuItem("Worker is running");
        MenuItem startItem = new MenuItem("Start");
        MenuItem stopItem = new MenuItem("Stop");
        MenuItem browserItem = new MenuItem("Open Chrome");
        CheckboxMenuItem cb_headless = new CheckboxMenuItem("Headless", appProperties.isHeadless());
        MenuItem propertiesItem = new MenuItem("Properties");
        MenuItem exitItem = new MenuItem("Exit");


        browserItem.addActionListener(e -> {
            trayIcon.setImage(image_gray);
            appProperties.setRunning(false);
            playwrightService.launchHeaded();
            labelItem.setLabel("Worker is stopped");
        });
        cb_headless.addItemListener(e -> appProperties.setHeadless(e.getStateChange() == ItemEvent.SELECTED));

        startItem.addActionListener(e -> {
            trayIcon.setImage(image_color);
            appProperties.setRunning(true);
            labelItem.setLabel("Worker is running");
        });

        stopItem.addActionListener(e -> {
            trayIcon.setImage(image_gray);
            appProperties.setRunning(false);
            labelItem.setLabel("Worker is stopped");
        });

        propertiesItem.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File("conf.properties"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exitItem.addActionListener(e -> {
            System.exit(SpringApplication.exit(applicationContext, () -> 0));
        });

        popup.add(labelItem);
        popup.addSeparator();
        popup.add(startItem);
        popup.add(stopItem);
        popup.addSeparator();
        popup.add(cb_headless);
        popup.add(browserItem);
        popup.addSeparator();
        popup.add(propertiesItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    protected Image createImage(String path) {
        try {
            URL imageURL = ResourceUtils.getURL("classpath:images/" + path);
            return (new ImageIcon(imageURL)).getImage();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
