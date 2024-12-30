package controller.order;

import controller.common.BaseScreenController;
import entity.order.Order;
import entity.order.entities.DetailResponse;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import subsystem.vnPay.VnPaySubsystemController;

import java.io.IOException;
import java.sql.SQLException;

public class OrderDetailController extends BaseScreenController {

    @FXML
    private TextField transactionId;

    @FXML
    private TextField bankCode;

    @FXML
    private TextField amount;

    @FXML
    private TextField payDate;

    @FXML
    private TextField transactionType;

    @FXML
    private TextField transactionStatus;

    @FXML
    private TextField orderInfo;

    @FXML
    private TextField promotionCode;

    @FXML
    private TextField promotionAmount;

    private Order order;

    public OrderDetailController(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    public OrderDetailController(Stage stage, String screenPath, Order order) throws IOException {
        super(stage, screenPath);
        this.order = order;
    }

    public void initScreen(DetailResponse response) {
        System.out.println(response.toString());
        transactionId.setText(response.getTransactionId());
        bankCode.setText(response.getBankCode());
        amount.setText(response.getAmount());
        String text = response.getPayDate();
        System.out.println("text: " + text);
        payDate.setText(text);
        transactionType.setText(response.getTransactionType());
        transactionStatus.setText(response.getTransactionStatus());
        orderInfo.setText(response.getOrderInfo());
        promotionCode.setText(response.getPromotionCode());
        promotionAmount.setText(response.getPromotionAmount());
    }

    public void show(BaseScreenController prevScreen) throws SQLException, IOException {
        setPreviousScreen(prevScreen);
        setScreenTitle("Order Detail Screen");

        VnPaySubsystemController vnPayService = new VnPaySubsystemController();
        initScreen(vnPayService.getDetailTransaction(order));

        show();
    }

    public void showScreen() throws IOException {

        stage.setTitle("Order Detail Screen");

        VnPaySubsystemController vnPayService = new VnPaySubsystemController();
        initScreen(vnPayService.getDetailTransaction(order));
        show();
    }
}
