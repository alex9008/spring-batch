package example.billingjob.bulkrefunds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;


@Service
public class BulkRefundsService implements ItemProcessor<RefundData, FinalResultDto> {

    private static final Logger log = LoggerFactory.getLogger(BulkRefundsService.class);

    private final RestClient bulkRefundClient;

    public BulkRefundsService(RestClient bulkRefundClient) {
        this.bulkRefundClient = bulkRefundClient;
    }

    @Override
    public FinalResultDto process(RefundData item) {
        log.info("Executing bulk refund request");
        // execute bulk refund request
        ProviderRefundRequestDto requestDto = buildRefundRequestDto(item.amount(), item.id());
        ProviderRefundResponseDto responseDto = bulkRefundClient.sendPostRequest(requestDto);
        return new FinalResultDto(item, responseDto.getStatusCode(), responseDto.getStatusDescription(), responseDto.getExternalReference());
    }


    private ProviderRefundRequestDto buildRefundRequestDto(String amount, String id) {

        ProviderRefundRequestDto refundRequestDto = new ProviderRefundRequestDto();
                refundRequestDto.setMerchantCode("code");
                refundRequestDto.setReferenceNumber(id);
                refundRequestDto.setRefundAmount(amount);
                refundRequestDto.setReason("Fawry refund");
                refundRequestDto.setSignature(buildRefundSignature(refundRequestDto));

        return refundRequestDto;
    }

    public static String buildRefundSignature(ProviderRefundRequestDto request) {
        String stringBuilder = request.getMerchantCode() +
                request.getReferenceNumber() +
                request.getRefundAmount() +
                request.getReason() +
                "key";
        return getSHA256(stringBuilder);
    }

    public static String getSHA256(String value) throws SecurityException {
        String hashValue = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hashValue = byteArray2Hex(messageDigest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage(), e);
        }
        return hashValue;
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        byte[] arrayOfByte = hash;
        int j = hash.length;
        for (int i = 0; i < j; i++) {
            byte b = arrayOfByte[i];

            formatter.format("%02x", new Object[]{Byte.valueOf(b)});
        }
        return formatter.toString();
    }
}
