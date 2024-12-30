package controller.shipping;

//import controller.PlaceOrderController;
import controller.common.BaseScreenController;
import controller.invoice.InvoiceScreenController;
import entity.invoice.Invoice;
import entity.order.Order;
import entity.shipping.Shipment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.Configs;

import java.io.IOException;

public class DeliveryMethodsScreenController extends BaseScreenController {

    private Order order;

    @FXML
    private RadioButton placeRushOrderValue;

    @FXML
    private RadioButton placeOrderValue;

    @FXML
    private TextField deliveryInstruction;

    @FXML
    private TextField shipmentDetail;

    @FXML
    private DatePicker deliveryTime;

    @FXML
    private Label errorProvince;

    @FXML
    private Button updateDeliveryMethodInfoButton;

    public DeliveryMethodsScreenController(Stage stage, String screenPath, Order order) throws IOException {
        super(stage, screenPath);
        this.order = order;
    }


    private Invoice createInvoice(Order order) {
        order.createOrderEntity();
        return new Invoice(order);
    }


    @FXML
    private void updateDeliveryMethodInfo(MouseEvent event) throws IOException {
        String deliveryInstructionString = deliveryInstruction.getText();
        String shipmentDetailString = shipmentDetail.getText();
        String deliveryDateString = deliveryTime.getValue() != null ? deliveryTime.getValue().toString() : "";

        int typeDelivery = placeRushOrderValue.isSelected() ? Configs.PLACE_RUSH_ORDER : Configs.PALCE_ORDER;

        var shipment = new Shipment(typeDelivery);
        shipment.setShipmentDetail(shipmentDetailString);
        shipment.setDeliveryTime(deliveryDateString);
        shipment.setDeliveryInstruction(deliveryInstructionString);

        validatePlaceRushOrderData(shipment);
        order.setShipment(shipment);

        // create invoice
        Invoice invoice = createInvoice(order);

        // create invoice screen
        BaseScreenController InvoiceScreenHandler = new InvoiceScreenController(this.stage, Configs.INVOICE_SCREEN_PATH, invoice);
        InvoiceScreenHandler.setPreviousScreen(this);
        InvoiceScreenHandler.setHomeScreenHandler(homeScreenHandler);
        InvoiceScreenHandler.setScreenTitle("Invoice Screen");
        InvoiceScreenHandler.show();
    }



    /**
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleBack(MouseEvent event) throws IOException {
        // Back to previous screen
        BaseScreenController ShippingScreenHandler = new ShippingScreenController(this.stage, Configs.SHIPPING_SCREEN_PATH,
                this.order);
        ShippingScreenHandler.setPreviousScreen(this);
        ShippingScreenHandler.setHomeScreenHandler(homeScreenHandler);
        ShippingScreenHandler.setScreenTitle("Shipping Screen");
        ShippingScreenHandler.show();
    }


    /**
     * @param event
     */
    @FXML
    private void handleDeliveryType(ActionEvent event) {
        if (placeOrderValue.isSelected()) {
            deliveryInstruction.setDisable(true);
            shipmentDetail.setDisable(true);
            deliveryTime.setDisable(true);
        } else if (placeRushOrderValue.isSelected()) {
            deliveryInstruction.setDisable(false);
            shipmentDetail.setDisable(false);
            deliveryTime.setDisable(false);
        }
        handleProvinceError(event);
    }


    /**
     * @param event
     */
    @FXML
    private void handleProvinceError(ActionEvent event) {
        String province = new String(order.getProvince());

        errorProvince.setVisible(false);
        deliveryInstruction.setDisable(true);
        shipmentDetail.setDisable(true);
        deliveryTime.setDisable(true);
        updateDeliveryMethodInfoButton.setDisable(false);

        if (!province.equals("Hà Nội")) {
            if (placeRushOrderValue.isSelected()) {
                errorProvince.setVisible(true);
                deliveryInstruction.setDisable(true);
                shipmentDetail.setDisable(true);
                deliveryTime.setDisable(true);
                updateDeliveryMethodInfoButton.setDisable(true);
            } else {
                updateDeliveryMethodInfoButton.setDisable(false);
                deliveryInstruction.setDisable(true);
                shipmentDetail.setDisable(true);
                deliveryTime.setDisable(true);
            }
        } else {
            if (placeRushOrderValue.isSelected()) {
                errorProvince.setVisible(false);
                deliveryInstruction.setDisable(false);
                shipmentDetail.setDisable(false);
                deliveryTime.setDisable(false);
                updateDeliveryMethodInfoButton.setDisable(false);
            } else {
                updateDeliveryMethodInfoButton.setDisable(false);
                deliveryInstruction.setDisable(true);
                shipmentDetail.setDisable(true);
                deliveryTime.setDisable(true);
                errorProvince.setVisible(false);
            }
        }
    }

    public static void validatePlaceRushOrderData(Shipment deliveryData) {
        if (deliveryData.getShipType() == utils.Configs.PLACE_RUSH_ORDER) {
            // validate
        }
    }
}
