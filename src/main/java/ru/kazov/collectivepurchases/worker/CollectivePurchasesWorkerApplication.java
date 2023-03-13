package ru.kazov.collectivepurchases.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@RequiredArgsConstructor
@SpringBootApplication
//@EnableConfigurationProperties(StorageProperties.class)
public class CollectivePurchasesWorkerApplication {

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CollectivePurchasesWorkerApplication.class);
        builder.headless(false);
        context = builder.run(args);
    }


}
