package controller.payment;

//import controller.PaymentController;
import controller.common.BaseScreenController;
import entity.cart.Cart;
import entity.invoice.Invoice;
import exception.UnrecognizedException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import subsystem.vnPay.Config;
import utils.Configs;
import entity.payment.PaymentTransaction;
import subsystem.VnPayInterface;
import subsystem.vnPay.VnPaySubsystemController;
import exception.vnPayException.TransactionExceptionHolder;
import utils.enums.OrderStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;
import java.util.Hashtable;


public class PaymentScreenController extends BaseScreenController {

    private Invoice invoice;
    @FXML
    private Label pageTitle;
    @FXML
    private VBox vBox;

    private VnPayInterface vnPayService;



    private Map<String, String> makePayment(Map<String, String> res) {
        Map<String, String> result = new Hashtable<>();
        PaymentTransaction trans;
        try {
            trans = this.vnPayService.makePaymentTransaction(res);
            if (trans != null) trans.save(invoice.getOrder().getId());
            if (trans.getErrorCode().equals("00")) {
                result.put("RESULT", "PAYMENT SUCCESSFUL!");
                result.put("MESSAGE", "You have successfully paid the order!");
                invoice.getOrder().updateStatus(OrderStatus.Paid, invoice.getOrder().getId());
            } else {
                var ex = TransactionExceptionHolder.getInstance().getException(trans.getErrorCode());
                if (ex != null) {
                    result.put("MESSAGE", ex.getMessage());
                    result.put("RESULT", "PAYMENT FAILED!");
                    invoice.getOrder().updateStatus(OrderStatus.Rejected, invoice.getOrder().getId());
                } else {
                    result.put("MESSAGE", "Unknown error, please contact AIMS Team for support.");
                    result.put("RESULT", "PAYMENT FAILED!");
                    invoice.getOrder().updateStatus(OrderStatus.Rejected, invoice.getOrder().getId());
                }
            }
        } catch (UnrecognizedException ex) {
            result.put("MESSAGE", "An error occurred, please contact AIMS Team for support.");
            result.put("RESULT", "PAYMENT FAILED!");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public PaymentScreenController(Stage stage, String screenPath, Invoice invoice) throws IOException {
        super(stage, screenPath);
        this.invoice = invoice;
        this.vnPayService = new VnPaySubsystemController();
        displayWebView(); // Control Coupling //Control Cohesion

    }

    private String getUrlPay() {
        String url;
        try {
            url = this.vnPayService.generatePayUrl(invoice.getAmount(), "Thanh toan hoa don AIMS");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return url;
    }


    // Control Coupling // Data Coupling 
    private void displayWebView() {
        String paymentUrl = getUrlPay();
        WebView paymentView = new WebView();
        WebEngine webEngine = paymentView.getEngine();
        webEngine.load(paymentUrl);
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            handleUrlChanged(newValue);
        });
        vBox.getChildren().clear();
        vBox.getChildren().add(paymentView);
    }


    // Hàm chuyển đổi query string thành Map
//Functional Cohesion
    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    /**
     * Xử lý khi URL thay đổi
     *
     * @param newValue url vnPay return về
     */
// Control Cohesion
    private void handleUrlChanged(String newValue) {
        if (newValue.contains(Config.vnp_ReturnUrl)) {
            try {
                URI uri = new URI(newValue);
                String query = uri.getQuery();

                // Chuyển đổi query thành Map
                Map<String, String> params = parseQueryString(query);

                payOrder(params);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void emptyCart() {
        Cart.getCart().emptyCart();
    }

    /**
     * Thực hiện thanh toán đơn hàng
     *
     * @param res kết quả vnPay trả về
     * @throws IOException
     */
// Control Coupling
// Control Cohesion
    private void payOrder(Map<String, String> res) throws IOException {
        Map<String, String> response = makePayment(res);

        // Create and display the result screen
        BaseScreenController resultScreen = new ResultScreenController(this.stage, Configs.RESULT_SCREEN_PATH,
                response.get("RESULT"), response.get("MESSAGE"));
        emptyCart();
        resultScreen.setPreviousScreen(this);
        resultScreen.setHomeScreenHandler(homeScreenHandler);
        resultScreen.setScreenTitle("Result Screen");
        resultScreen.show();
    }

    @FXML
    private void handleBack(MouseEvent event) throws IOException {
        navigateBack(Configs.HOME_MEDIA_PATH, "Home Screen");
    }



}
