package controller.order;

import controller.common.BaseScreenController;
import controller.payment.ResultScreenController;
import entity.order.Order;
import entity.order.entities.RefundTransaction;
import entity.payment.TransactionResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import subsystem.vnPay.VnPaySubsystemController;
import utils.Configs;
import utils.enums.OrderStatus;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class OrderScreenController extends BaseScreenController {

    @FXML
    private ImageView aimsImage;

    @FXML
    private Label pageTitle;

    @FXML
    private VBox vboxOrder;

    public OrderScreenController(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
        File file = new File("assets/images/Logo.png");
        Image im = new Image(file.toURI().toString());
        aimsImage.setImage(im);

        aimsImage.setOnMouseClicked(e -> {
            homeScreenHandler.show();
        });
    }

    public void show(BaseScreenController prevScreen) throws SQLException {
        setPreviousScreen(prevScreen);
        setScreenTitle("Order Screen");

        displayOrder();
        show();
    }

    private void displayOrder() {
        vboxOrder.getChildren().clear();
        Order orderInstance = new Order(); // Create an instance
        var listOrders = orderInstance.getListOrders(); // Use instance method

        try {
            for (var cm : listOrders) {
                OrderMediaController orderMediaHandler = new OrderMediaController(Configs.ORDER_MEDIA_PATH, this);
                orderMediaHandler.setOrder(cm);
                vboxOrder.getChildren().add(orderMediaHandler.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleRejectOrder(Order order) throws IOException {
        TransactionResult result = new TransactionResult();

        if (!order.getStatus().equals(OrderStatus.Rejected)) {
            try {
                var trans = order.getPaymentTransaction();
                var refundParams = new RefundTransaction(
                        "02",
                        String.valueOf(trans.getAmount()),
                        trans.getTxnRef(),
                        trans.getTransactionNo(),
                        trans.getCreatedAt().toString(),
                        order.getName()
                );

                VnPaySubsystemController vnPayService = new VnPaySubsystemController();
                vnPayService.refund(refundParams);

                result.setResult("SUCCESS");
                result.setMessage("REFUND SUCCESS, PLEASE CHECK YOUR BANK");
                order.updateStatus(OrderStatus.Rejected, order.getId());
            } catch (Exception e) {
                result.setResult("REFUND FAILED");
                result.setMessage(e.getMessage());
            }
        } else {
            result.setResult("FAILED");
            result.setMessage("Có lỗi xảy ra, vui lòng liên hệ AIMS TEAM để được hỗ trợ!");
        }

        // Show result screen
        BaseScreenController resultScreen = new ResultScreenController(
                this.stage,
                Configs.RESULT_SCREEN_PATH,
                result.getResult(),
                result.getMessage()
        );
        resultScreen.setPreviousScreen(this);
        resultScreen.setHomeScreenHandler(homeScreenHandler);
        resultScreen.setScreenTitle("Result Screen");
        resultScreen.show();


    }


}
