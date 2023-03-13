package ru.kazov.collectivepurchases.worker.services;

import com.google.protobuf.Empty;
import io.grpc.CallCredentials;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kazov.collectivepurchases.protomodels.AddItemsRequestProto;
import ru.kazov.collectivepurchases.protomodels.CPGrpcServiceGrpc;
import ru.kazov.collectivepurchases.protomodels.ErrorRequestProto;
import ru.kazov.collectivepurchases.protomodels.ParserJobResponseProto;
import ru.kazov.collectivepurchases.worker.common.AppProperties;

@RequiredArgsConstructor
@Service
public class GrpcClientService {
    Logger logger = LoggerFactory.getLogger(GrpcClientService.class);
    private final AppProperties appProperties;
    @GrpcClient("CPGrpcService")
    CPGrpcServiceGrpc.CPGrpcServiceBlockingStub grpcService;

    private CallCredentials authCredentials;


    @PostConstruct
    private void onInit() {
        authCredentials = CallCredentialsHelper.authorizationHeader("Bearer " + appProperties.getToken());
    }

    public ParserJobResponseProto fetchJob() throws StatusRuntimeException {
        return grpcService.withCallCredentials(authCredentials).fetchJob(Empty.getDefaultInstance());
    }

    public void sendItems(AddItemsRequestProto requestProto) {
        grpcService.withCallCredentials(authCredentials).addItems(requestProto);
    }

    public void sendError(ErrorRequestProto requestProto) {
        grpcService.withCallCredentials(authCredentials).handleError(requestProto);

    }
}

