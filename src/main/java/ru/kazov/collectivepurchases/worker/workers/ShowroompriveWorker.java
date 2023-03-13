package ru.kazov.collectivepurchases.worker.workers;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kazov.collectivepurchases.worker.models.ParserItem;
import ru.kazov.collectivepurchases.worker.models.ParserJob;

import java.util.ArrayList;
import java.util.List;


public class ShowroompriveWorker extends AbstractWorker {
    Logger logger = LoggerFactory.getLogger(ShowroompriveWorker.class);
    private final static String BASE_URL = "https://www.showroomprive.es";

    @Override
    protected boolean checkAuth(Page page) {
        logger.info("ShowroomPrive.es: Check authentication");
        page.navigate("https://showroomprive.es", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(0));
        return page.querySelector("a[href*=deconnect]") != null;
    }

    @Override
    protected void authorize(Page page, String login, String password) {
        logger.info("ShowroomPrive.es: Trying to authenticate");

        if (page.locator("#popinCookies > div.fixed-bottom.card.panelSimple").isVisible()) {
            page.locator("div#agree_button").click();
        }

        page.locator("input[name=login]").fill(login);
        page.locator("input[name=password]").fill(password);
        page.locator("srp-login > form > button").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @Override
    protected List<ParserItem> work(Page page, ParserJob job) {
        logger.info("ShowroomPrive.es: Start parsing url - " + job.getUrl());
        page.navigate(job.getUrl(), new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(0));
        //page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        List<ElementHandle> productElementList = page.querySelectorAll("div[class*=bloc_product][id*=bloc_product_]");
        List<String> urlList = new ArrayList<>(productElementList.size());
        List<ParserItem> parserItems = new ArrayList<>(productElementList.size());

        for (ElementHandle element : productElementList) {
            if (element.textContent().contains("Agotado"))
                continue;
            urlList.add(BASE_URL + element.querySelector("a").getAttribute("href"));
        }

        int counter = 1;
        for (String url : urlList) {
            logger.info("ShowroomPrive.es: Parsing product [" + (counter++) + ":" + urlList.size() + "]");
            //page.navigate(url);
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(0));
            //page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            ParserItem item = new ParserItem();
            item.setUrl(url);

            //Product Name
            ElementHandle elementHandle = page.querySelector("div.fp__info_article > div.fp__nom_article");
            if (elementHandle != null)
                item.setName(elementHandle.textContent().trim().replaceAll("\\s{2,}", " "));
            else
                logger.error("ShowroomPrive.es: Name value is null");

            //Product description
            elementHandle = page.querySelector("div#tabs-1");
            if (elementHandle != null)
                item.setDescription(elementHandle.textContent().trim().replaceAll("\\s{2,}", " "));
            else
                logger.error("ShowroomPrive.es: Description value is null");

            //Product Sizes-Prices
            double price = 0;
            elementHandle = page.querySelector("div.fp__info_article div.fp__prix-montant");
            if (elementHandle != null)
                price = Double.parseDouble(elementHandle.textContent().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
            else
                logger.error("ShowroomPrive.es: Price value is null");

            //Product Prices
            elementHandle = page.querySelector("div.ui-selectmenu-menu > ul.ui-menu");
            if (elementHandle != null) {
                double finalPrice = price;
                elementHandle.querySelectorAll("li").stream().filter((el -> !el.textContent().toLowerCase().contains("agotado")))
                        .forEachOrdered((e -> item.getPrices().put(e.textContent().replaceAll("\\(Disponible\\)", "").trim(), finalPrice)));
            } else
                logger.error("ShowroomPrive.es: Sizes value is null");

            //Product Pictures
            elementHandle = page.querySelector("ul#divCarousel");
            if (elementHandle != null)
                item.setPictures(elementHandle.querySelectorAll("li>a").stream().map((e) -> "https:" + e.getAttribute("href")).toList());
            else
                logger.error("ShowroomPrive.es: Pictures value is null");

            parserItems.add(item);
        }
        logger.info("ShowroomPrive.es: Job done");
        return parserItems;
    }

    @Override
    public boolean supports(String url) {
        return url.contains("showroomprive.es");
    }
}
