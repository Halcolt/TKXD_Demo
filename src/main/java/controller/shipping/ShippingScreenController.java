package controller.shipping;

import controller.common.BaseScreenController;
import entity.order.Order;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.Configs;
import controller.popup.PopupScreenController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

public class ShippingScreenController extends BaseScreenController implements Initializable {

    @FXML
    private Label screenTitle;

    @FXML
    private TextField name;

    @FXML
    private TextField phone;

    @FXML
    private TextField address;

    @FXML
    private TextField instructions;

    @FXML
    private ComboBox<String> province;

    private Order order;

    public ShippingScreenController(Stage stage, String screenPath, Order order) throws IOException {
        super(stage, screenPath);
        this.order = order;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
        name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstTime.get()) {
                content.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });
        this.province.getItems().addAll(Configs.PROVINCES);
    }

    @FXML
    void submitDeliveryInfo(MouseEvent event) throws IOException, InterruptedException, SQLException {
        // add info to messages
        HashMap<String, String> messages = new HashMap<>();
        messages.put("name", name.getText());
        messages.put("phone", phone.getText());
        messages.put("address", address.getText());
        messages.put("instructions", instructions.getText());
        messages.put("province", province.getValue());

        if (!validateContainLetterAndNoEmpty(name.getText())) {
            PopupScreenController.error("Name is not valid!");
            return;
        }
        if (!validatePhoneNumber(phone.getText())) {
            PopupScreenController.error("Phone is not valid!");
            return;
        }

        if (province.getValue() == null) {
            PopupScreenController.error("Province is empty!");
            return;
        }

        // calculate shipping fees
        int shippingFees = calculateShippingFee(order.getAmount());
        order.setShippingFees(shippingFees);
        order.setName(name.getText());
        order.setPhone(phone.getText());
        order.setProvince(province.getValue());
        order.setAddress(address.getText());
        order.setInstruction(instructions.getText());

        // create delivery method screen
        BaseScreenController deliveryMethodsScreenHandler = new DeliveryMethodsScreenController(this.stage, Configs.DELIVERY_METHODS_PATH, this.order);
        deliveryMethodsScreenHandler.setPreviousScreen(this);
        deliveryMethodsScreenHandler.setHomeScreenHandler(homeScreenHandler);
        deliveryMethodsScreenHandler.setScreenTitle("Delivery method screen");
        deliveryMethodsScreenHandler.show();
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 10)
            return false;
        if (Character.compare(phoneNumber.charAt(0), '0') != 0)
            return false;
        try {
            Long.parseUnsignedLong(phoneNumber);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean validateContainLetterAndNoEmpty(String name) {
        if (name == null)
            return false;
        if (name.trim().length() == 0)
            return false;
        if (!name.matches("^[a-zA-Z ]*$"))
            return false;
        return true;
    }

    private int calculateShippingFee(int amount) {
        Random rand = new Random();
        return (int) (((rand.nextFloat() * 10) / 100) * amount);
    }

    @FXML
    private void handleBack(MouseEvent event) throws IOException {
        navigateBack(Configs.HOME_MEDIA_PATH, "Home Screen");
    }
}
