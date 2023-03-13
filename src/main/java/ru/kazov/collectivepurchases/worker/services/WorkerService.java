package ru.kazov.collectivepurchases.worker.services;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kazov.collectivepurchases.protomodels.*;
import ru.kazov.collectivepurchases.worker.common.AppProperties;
import ru.kazov.collectivepurchases.worker.models.ParserItem;
import ru.kazov.collectivepurchases.worker.models.ParserJob;
import ru.kazov.collectivepurchases.worker.models.ParserShop;
import ru.kazov.collectivepurchases.worker.workers.AbstractWorker;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WorkerService {
    Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final AppProperties appProperties;
    private final GrpcClientService grpcClientService;

    private final List<? extends AbstractWorker> workerList;


    @Scheduled(fixedDelayString = "${app.timeout.poll}")
    private void fetchWork() {
        if (!appProperties.isRunning())
            return;

        ParserJobResponseProto parserJobProto = null;
        try {
            logger.info("Fetching new Job");

            parserJobProto = grpcClientService.fetchJob();
            if (parserJobProto.getId() == 0) {
                logger.info("No new job");
                return;
            }
            logger.info("Job received");
            ParserJob parserJob = convertToParserJob(parserJobProto);
            AbstractWorker worker = getWorker(parserJob);
            List<ParserItem> list = worker.run(parserJob);
            grpcClientService.sendItems(generateRequest(parserJob, list));

        } catch (StatusRuntimeException ex) {
            logger.error("GRPC ERROR - " + ex.getStatus().getCode().name());
        } catch (Exception ex) {
            if (parserJobProto != null) {
                ErrorRequestProto request = ErrorRequestProto.newBuilder()
                        .setParserJobId(parserJobProto.getId())
                        .setMessage(ex.getMessage())
                        .build();
                grpcClientService.sendError(request);
            }
        }
    }

    private AbstractWorker getWorker(ParserJob job) {
        return workerList.stream()
                .filter(e -> e.supports(job.getUrl()))
                .findFirst()
                .orElseThrow(); //TODO изменить исключение
    }

    private ParserJob convertToParserJob(ParserJobResponseProto proto) {
        return ParserJob.builder()
                .id(proto.getId())
                .url(proto.getUrl())
                .shop(convertToParserShop(proto.getShop()))
                .build();
    }

    private ParserShop convertToParserShop(ParserShopProto proto) {
        return ParserShop.builder()
                .needLogin(proto.getNeedLogin())
                .login(proto.getLogin())
                .password(proto.getPassword())
                .build();
    }

    private ParserItemProto covertToParserItemProto(ParserItem item) {
        return ParserItemProto.newBuilder()
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setUrl(item.getUrl())
                .addAllPictures(item.getPictures())
                .putAllProperties(item.getProperties())
                .putAllPrices(item.getPrices())
                .build();
    }

    private AddItemsRequestProto generateRequest(ParserJob job, List<ParserItem> items) {
        return AddItemsRequestProto.newBuilder()
                .setParserJobId(job.getId())
                .addAllItems(items.stream()
                        .filter(e -> !e.getPictures().isEmpty())
                        .map(this::covertToParserItemProto)
                        .toList())
                .build();
    }
}
