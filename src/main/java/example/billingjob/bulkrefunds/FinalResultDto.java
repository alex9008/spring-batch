package example.billingjob.bulkrefunds;

public record FinalResultDto(RefundData refundData, int statusCode, String statusDescription, String externalReference) {
}
