package controller.cart;

import exception.MediaUpdateException;
import exception.ViewCartException;
import entity.cart.Cart;
import entity.cart.CartMedia;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.Configs;
import utils.Utils;
import controller.common.BaseScreenController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

public class MediaController extends BaseScreenController {

    private static Logger LOGGER = Utils.getLogger(MediaController.class.getName());

    @FXML
    protected HBox hboxMedia;

    @FXML
    protected ImageView image;

    @FXML
    protected VBox description;

    @FXML
    protected Label labelOutOfStock;

    @FXML
    protected VBox spinnerFX;

    @FXML
    protected Label title;

    @FXML
    protected Label price;

    @FXML
    protected Label currency;

    @FXML
    protected Button btnDelete;

    private CartMedia cartMedia;
    private Spinner<Integer> spinner;
    private CartScreenController cartScreen;

    // Functional Cohesion
    public MediaController(String screenPath, CartScreenController cartScreen) throws IOException {
        super(cartScreen.getStage(), screenPath);
        this.cartScreen = cartScreen;
        hboxMedia.setAlignment(Pos.CENTER);
    }

    // Functional Cohesion
    public void setCartMedia(CartMedia cartMedia) {
        this.cartMedia = cartMedia;
        setMediaInfo();
    }

    // Functional Cohesion
    private void setMediaInfo() {
        title.setText(cartMedia.getMedia().getTitle());
        price.setText(Utils.getCurrencyFormat(cartMedia.getPrice()));

        // Use setImage from BaseScreenController
        setImage(image, cartMedia.getMedia().getImageURL());
        image.setPreserveRatio(false);
        image.setFitHeight(110);
        image.setFitWidth(92);

        // Add delete button functionality
        btnDelete.setFont(Configs.REGULAR_FONT);
        btnDelete.setOnMouseClicked(e -> {
            try {
                Cart.getCart().removeCartMedia(cartMedia); // update user cart
                cartScreen.updateCart(); // re-display user cart
                LOGGER.info("Deleted " + cartMedia.getMedia().getTitle() + " from the cart");
            } catch (SQLException exp) {
                exp.printStackTrace();
                throw new ViewCartException();
            }
        });

        initializeSpinner();
    }

    // Procedural Cohesion
    private void initializeSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, cartMedia.getQuantity());
        spinner = new Spinner<>(valueFactory);
        spinner.setOnMouseClicked(e -> {
            try {
                int numOfProd = this.spinner.getValue();
                int remainQuantity = cartMedia.getMedia().getQuantity();
                LOGGER.info("NumOfProd: " + numOfProd + " -- remainOfProd: " + remainQuantity);
                if (numOfProd > remainQuantity) {
                    LOGGER.info("Product " + cartMedia.getMedia().getTitle() + " only remains " + remainQuantity + " (required " + numOfProd + ")");
                    labelOutOfStock.setText("Sorry, only " + remainQuantity + " remain in stock");
                    spinner.getValueFactory().setValue(remainQuantity);
                    numOfProd = remainQuantity;
                }

                // Update quantity of mediaCart in userCart
                cartMedia.setQuantity(numOfProd);

                // Update the total of mediaCart
                price.setText(Utils.getCurrencyFormat(numOfProd * cartMedia.getPrice()));

                // Update subtotal and amount of Cart
                cartScreen.updateCartAmount();

            } catch (SQLException e1) {
                throw new MediaUpdateException(Arrays.toString(e1.getStackTrace()).replaceAll(", ", "\n"));
            }
        });
        spinnerFX.setAlignment(Pos.CENTER);
        spinnerFX.getChildren().add(this.spinner);
    }
}
