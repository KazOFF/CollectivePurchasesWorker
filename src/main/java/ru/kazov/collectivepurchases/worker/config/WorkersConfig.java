package ru.kazov.collectivepurchases.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kazov.collectivepurchases.worker.workers.ShowroompriveWorker;
import ru.kazov.collectivepurchases.worker.workers.StalkercoWorker;

@Configuration
public class WorkersConfig {

    @Bean
    public ShowroompriveWorker showroompriveWorker() {
        return new ShowroompriveWorker();
    }

    @Bean
    public StalkercoWorker stalkercoWorker() {
        return new StalkercoWorker();
    }

}
