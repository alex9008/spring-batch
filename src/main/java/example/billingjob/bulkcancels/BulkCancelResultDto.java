package example.billingjob.bulkcancels;

public record BulkCancelResultDto(String merchantId, String transactionId, String status) {
}
