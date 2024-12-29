package controller;

import common.exception.*;
import entity.cart.Cart;
import entity.order.Order;
import entity.payment.PaymentTransaction;
import subsystem.VnPayInterface;
import subsystem.vnPay.VnPaySubsystemController;
import common.exception.vnPayException.TransactionExceptionHolder;
import utils.enums.OrderStatus;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class PaymentController extends BaseController {

    private final VnPayInterface vnPayService;
    public PaymentController() {
        this.vnPayService = new VnPaySubsystemController();
    }

    public Map<String, String> makePayment(Map<String, String> res, int orderId) {
        Map<String, String> result = new HashMap<>();
        PaymentTransaction transaction = null;

        try {
            transaction = this.vnPayService.makePaymentTransaction(res);

            if (transaction != null) {
                transaction.save(orderId);
            }

            handleTransactionResult(transaction, orderId, result);

        } catch (UnrecognizedException e) {
            result.put("RESULT", "PAYMENT FAILED!");
            result.put("MESSAGE", "A failure occurred. Please contact AIMS support.");
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing transaction data.", e);
        }

        return result;
    }

    private void handleTransactionResult(PaymentTransaction transaction, int orderId, Map<String, String> result) {
        Order order = new Order();

        if (transaction == null) {
            result.put("RESULT", "PAYMENT FAILED!");
            result.put("MESSAGE", "Transaction failed due to null response.");
            order.updateStatus(OrderStatus.Rejected, orderId);
            return;
        }

        if ("00".equals(transaction.getErrorCode())) {
            result.put("RESULT", "PAYMENT SUCCESSFUL!");
            result.put("MESSAGE", "You have successfully paid for the order.");
            order.updateStatus(OrderStatus.Paid, orderId);
        } else {
            handleTransactionError(transaction, orderId, result, order);
        }
    }

    private void handleTransactionError(PaymentTransaction transaction, int orderId, Map<String, String> result, Order order) {
        var exception = TransactionExceptionHolder.getInstance().getException(transaction.getErrorCode());

        if (exception != null) {
            result.put("MESSAGE", exception.getMessage());
        } else {
            result.put("MESSAGE", "Unknown error occurred. Please contact AIMS support.");
        }

        result.put("RESULT", "PAYMENT FAILED!");
        order.updateStatus(OrderStatus.Rejected, orderId);
    }

    public String getUrlPay(int amount, String content) {
        try {
            return this.vnPayService.generatePayUrl(amount, content);
        } catch (IOException e) {
            throw new RuntimeException("Error generating payment URL.", e);
        }
    }

    public void emptyCart() {
        Cart.getCart().emptyCart();
    }
}
