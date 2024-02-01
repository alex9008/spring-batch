package example.billingjob.bulkcancels;

public class DirectCardResponseDto {

    private TransactionStatusEnum status;

    public TransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TransactionStatusEnum status) {
        this.status = status;
    }
}
