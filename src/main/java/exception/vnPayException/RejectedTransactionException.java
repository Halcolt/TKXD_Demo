package exception.vnPayException;

import exception.PaymentException;

public class RejectedTransactionException extends PaymentException {
    public RejectedTransactionException() {
        super("ERROR: GD Hoàn trả bị từ chối");
    }
}
