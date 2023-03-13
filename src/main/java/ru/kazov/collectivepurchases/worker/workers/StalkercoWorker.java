package ru.kazov.collectivepurchases.worker.workers;

import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kazov.collectivepurchases.worker.models.ParserItem;
import ru.kazov.collectivepurchases.worker.models.ParserJob;
import ru.kazov.collectivepurchases.worker.services.GrpcClientService;

import java.util.List;

public class StalkercoWorker extends AbstractWorker {

    @Autowired
    private GrpcClientService grpcClientService;

    @Override
    protected boolean checkAuth(Page page) {
        return false;
    }

    @Override
    protected void authorize(Page page, String login, String password) {

    }

    @Override
    protected List<ParserItem> work(Page page, ParserJob job) {
        return null;
    }

    @Override
    public boolean supports(String baseUrl) {
        return baseUrl.contains("stalker-co.ru");
    }
}
