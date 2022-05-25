import yandex.cloud.api.compute.v1.InstanceServiceGrpc;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass;
import yandex.cloud.api.operation.OperationOuterClass;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.util.function.Function;

public class Handler implements Function<String, String> {
    @Override
    public String apply(String instanceId) {
        if(instanceId.isEmpty()) {
            instanceId = System.getenv("INSTANCEID");
        }
        // Авторизация в SDK при помощи сервисного аккаунта
        CredentialProvider defaultComputeEngine = Auth.computeEngineBuilder().build();
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(defaultComputeEngine)
                .build();
        InstanceServiceGrpc.InstanceServiceBlockingStub instanceService = factory
                .create(InstanceServiceGrpc.InstanceServiceBlockingStub.class, InstanceServiceGrpc::newBlockingStub);
        // Создание запроса на старт инстанса
        InstanceServiceOuterClass.StartInstanceRequest startInstanceRequest = InstanceServiceOuterClass
                .StartInstanceRequest
                .newBuilder()
                .setInstanceId(instanceId)
                .build();
        // Запуск инстанса
        OperationOuterClass.Operation startInstanceResponse = instanceService.start(startInstanceRequest);

        if (!startInstanceResponse.hasError()) {
            System.out.printf("Started %s instance", instanceId);
            return String.format("Started %s instance", instanceId);
        }
        System.out.printf("Not started instance error: " + startInstanceResponse.getError());
        throw new RuntimeException(String.valueOf(startInstanceResponse.getError()));
    }
}