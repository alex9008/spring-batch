package example.billingjob.bulkcancels;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
public class BulkCancelService implements ItemProcessor<BulkCancelIdsDto, BulkCancelResultDto> {

    private final BulkCancelRestClient bulkCancelRestClient;

    public BulkCancelService(BulkCancelRestClient bulkCancelRestClient) {
        this.bulkCancelRestClient = bulkCancelRestClient;
    }


    @Override
    public BulkCancelResultDto process(BulkCancelIdsDto item) throws Exception {

        DirectCardResponseDto response = bulkCancelRestClient.sendPostRequest(item.merchantId(), item.transactionId());

        return new BulkCancelResultDto(item.merchantId(), item.transactionId(), response.getStatus().name());
    }
}
