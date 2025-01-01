package controller.cart;

import entity.order.Order;
import entity.order.OrderMedia;
import exception.MediaNotAvailableException;
import exception.PlaceOrderException;
import controller.common.BaseScreenController;
import controller.shipping.ShippingScreenController;
import entity.cart.CartMedia;
import entity.cart.Cart;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import controller.popup.PopupScreenController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

//SRP: Do CartScreenController dam nhan nhieu trach nhiem dong thoi xu ly cac thao tac UI.
//DIP: CartScreenController nen phu thuoc vao cac giao dien thay vi cac lop truc tiep.

public class CartScreenController extends BaseScreenController {

    private static Logger LOGGER = Utils.getLogger(CartScreenController.class.getName());
    @FXML
    VBox vboxCart;
    @FXML
    private ImageView aimsImage;
    @FXML
    private Label pageTitle;
    @FXML
    private Label shippingFees;

    @FXML
    private Label labelAmount;

    @FXML
    private Label labelSubtotal;

    @FXML
    private Label labelVAT;

    @FXML
    private Button btnPlaceOrder;

    //Control Coupling
    //Functional Cohesion
    public CartScreenController(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);

        // fix relative image path caused by fxml
        File file = new File("assets/images/Logo.png");
        Image im = new Image(file.toURI().toString());
        aimsImage.setImage(im);

        // on mouse clicked, we back to home
        aimsImage.setOnMouseClicked(e -> {
            homeScreenHandler.show();
        });

        // on mouse clicked, we start processing place order usecase
        btnPlaceOrder.setOnMouseClicked(e -> {

            try {
                requestOrder();
            } catch (SQLException | IOException exp) {

                exp.printStackTrace();
                throw new PlaceOrderException(Arrays.toString(exp.getStackTrace()).replaceAll(", ", "\n"));
            }

        });
    }


    //Khong xac dinh coupling
    //Functional Cohesion
    /**
     * @return Label
     */
    public Label getLabelAmount() {
        return labelAmount;
    }


    //Khong xac dinh coupling
    //Functional Cohesion
    /**
     * @return Label
     */
    public Label getLabelSubtotal() {
        return labelSubtotal;
    }


    //Khong xac dinh coupling
    //Sequential Cohesion

    //Khong xac dinh coupling
    //Functional Cohesion
    /**
     * @param prevScreen
     * @throws SQLException
     */
    public void show(BaseScreenController prevScreen) throws SQLException {
        setPreviousScreen(prevScreen);
        setScreenTitle("Cart Screen");
        checkAvailabilityOfProduct();
        displayCartWithMediaAvailability();
        super.show();
    }

    public List<CartMedia> getListCartMedia() {
        return Cart.getCart().getListMedia();
    }

    public void checkAvailabilityOfProduct() throws SQLException {
        Cart.getCart().checkAvailabilityOfProduct();
    }

    //Control coupling
    //Procedural Cohesion
    /**
     * @throws SQLException
     * @throws IOException
     */

    public void placeOrder() throws SQLException {
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public Order createOrder() throws SQLException {
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(),
                    cartMedia.getQuantity(),
                    cartMedia.getPrice());
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    public void requestOrder() throws SQLException, IOException {
        try {
            if (getListCartMedia().size() == 0) {
                PopupScreenController.error("You don't have anything to place");
                return;
            }

            placeOrder();

            // display available media
            displayCartWithMediaAvailability();

            // create order
            var order = createOrder();

            // display shipping form
            ShippingScreenController ShippingScreenHandler = new ShippingScreenController(this.stage, Configs.SHIPPING_SCREEN_PATH, order);
            ShippingScreenHandler.setPreviousScreen(this);
            ShippingScreenHandler.setHomeScreenHandler(homeScreenHandler);
            ShippingScreenHandler.setScreenTitle("Shipping Screen");
            ShippingScreenHandler.show();

        } catch (MediaNotAvailableException e) {
            displayCartWithMediaAvailability();
        }
    }


    //Khong xac dinh coupling
    /**
     * @throws SQLException
     */
    public void updateCart() throws SQLException {
        checkAvailabilityOfProduct();
        displayCartWithMediaAvailability();
    }

    public int getCartSubtotal() {
        return Cart.getCart().calSubtotal();
    }

    //Khong xac dinh coupling
    //Functional Cohesion
    void updateCartAmount() {
        // Calculate subtotal and amount
        int subtotal = getCartSubtotal();
        int vat = (int) ((Configs.PERCENT_VAT / 100) * subtotal);
        int amount = subtotal + vat;

        // Update subtotal and amount of Cart
        labelSubtotal.setText(Utils.getCurrencyFormat(subtotal));
        labelVAT.setText(Utils.getCurrencyFormat(vat));
        labelAmount.setText(Utils.getCurrencyFormat(amount));
    }

    @FXML
    private void handleBack(MouseEvent event) throws IOException {
        navigateBack(Configs.HOME_MEDIA_PATH, "Home Screen");
    }


    //Control Coupling
    //Functional Cohesion
    private void displayCartWithMediaAvailability() {
        // clear all old cartMedia
        vboxCart.getChildren().clear();

        // get list media of cart after check availability
        List lstMedia = getListCartMedia();

        try {
            for (Object cm : lstMedia) {

                // display the attribute of vboxCart media
                CartMedia cartMedia = (CartMedia) cm;
                MediaController mediaCartScreen = new MediaController(Configs.CART_MEDIA_PATH, this);
                mediaCartScreen.setCartMedia(cartMedia);

                // add spinner
                vboxCart.getChildren().add(mediaCartScreen.getContent());
            }
            // calculate subtotal and amount
            updateCartAmount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
