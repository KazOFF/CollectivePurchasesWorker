package ru.kazov.collectivepurchases.worker.workers;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kazov.collectivepurchases.worker.models.ParserItem;
import ru.kazov.collectivepurchases.worker.models.ParserJob;
import ru.kazov.collectivepurchases.worker.services.PlaywrightService;

import java.util.List;

public abstract class AbstractWorker {
    @Autowired
    private PlaywrightService playwrightService;


    abstract protected boolean checkAuth(Page page);

    abstract protected void authorize(Page page, String login, String password);

    abstract protected List<ParserItem> work(Page page, ParserJob job);

    abstract public boolean supports(String url);


    public List<ParserItem> run(ParserJob job) {
        BrowserContext context = playwrightService.launch();
        Page page = context.newPage();
        if (job.getShop().isNeedLogin() && !checkAuth(page))
            authorize(page, job.getShop().getLogin(), job.getShop().getPassword());

        List<ParserItem> list = work(page, job);
        playwrightService.close();
        return list;
    }
}
