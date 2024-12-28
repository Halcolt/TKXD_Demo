package controller.order;

import controller.OrderController;
import controller.common.BaseScreenController;
import controller.payment.ResultScreenController;
import entity.order.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Configs;

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
    public OrderController getBController() {
        return (OrderController) super.getBController();
    }
    public void show(BaseScreenController prevScreen) throws SQLException {
        setPreviousScreen(prevScreen);
        setScreenTitle("Order Screen");

        displayOrder();
        show();
    }

    private void displayOrder(){
        vboxOrder.getChildren().clear();
        var listOrders = getBController().getOrders();

        try {
            for (var cm : listOrders) {


                OrderMediaController orderMediaHandler = new OrderMediaController(Configs.ORDER_MEDIA_PATH, this);
                orderMediaHandler.setOrder(cm);

                // add spinner
                vboxOrder.getChildren().add(orderMediaHandler.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRejectOrder(Order order) throws IOException {
        var response = getBController().cancelOrder(order);
        BaseScreenController resultScreen = new ResultScreenController(this.stage, Configs.RESULT_SCREEN_PATH,
                response.getResult(), response.getMessage());

        resultScreen.setPreviousScreen(this);
        resultScreen.setHomeScreenHandler(homeScreenHandler);
        resultScreen.setScreenTitle("Result Screen");
        resultScreen.show();

    }


}