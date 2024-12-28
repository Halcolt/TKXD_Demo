package controller.home;

import common.exception.MediaNotAvailableException;
import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.media.Media;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;
import controller.common.FXMLScreenController;
import controller.popup.PopupScreenController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MediaController extends FXMLScreenController {

    private static Logger LOGGER = Utils.getLogger(MediaController.class.getName());
    @FXML
    protected ImageView mediaImage;
    @FXML
    protected Label mediaTitle;
    @FXML
    protected Label mediaPrice;
    @FXML
    protected Label mediaAvail;
    @FXML
    protected Spinner<Integer> spinnerChangeNumber;
    @FXML
    protected Button addToCartBtn;
    @FXML
    private Button deleteBtn;

    private Media media;
    private HomeScreenController home;

    public MediaController(String screenPath, Media media, HomeScreenController home) throws SQLException, IOException {
        super(screenPath);
        this.media = media;
        this.home = home;
        deleteBtn.setOnMouseClicked(e -> {
            try {
                deleteProduct();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        addToCartBtn.setOnMouseClicked(event -> {
            try {
                if (spinnerChangeNumber.getValue() > media.getQuantity()) throw new MediaNotAvailableException();
                Cart cart = Cart.getCart();
                // if media already in cart then we will increase the quantity by 1 instead of create the new cartMedia
                CartMedia mediaInCart = home.checkMediaInCart(media);
                if (mediaInCart != null) {
                    mediaInCart.setQuantity(mediaInCart.getQuantity() + 1);
                } else {
                    CartMedia cartMedia = new CartMedia(media, cart, spinnerChangeNumber.getValue(), media.getPrice());
                    cart.getListMedia().add(cartMedia);
                }

                // subtract the quantity and redisplay
                media.setQuantity(media.getQuantity() - spinnerChangeNumber.getValue());
                mediaAvail.setText(String.valueOf(media.getQuantity()));
                home.getNumMediaCartLabel().setText(String.valueOf(cart.getTotalMedia() + " media"));
                PopupScreenController.success("The media " + media.getTitle() + " added to Cart");
            } catch (MediaNotAvailableException exp) {
                try {
                    String message = "Not enough media:\nRequired: " + spinnerChangeNumber.getValue() + "\nAvail: " + media.getQuantity();
                    LOGGER.severe(message);
                    PopupScreenController.error(message);
                } catch (Exception e) {
                    LOGGER.severe("Cannot add media to cart: ");
                }

            } catch (Exception exp) {
                LOGGER.severe("Cannot add media to cart: ");
                exp.printStackTrace();
            }
        });
        setMediaInfo();
    }

    private void deleteProduct() throws SQLException {
        home.removeMedia(this.media);
    }

    /**
     * @return Media
     */
    public Media getMedia() {
        return media;
    }


    /**
     * @throws SQLException
     */
    private void setMediaInfo() throws SQLException {
        // set the cover image of media
        File file = new File(media.getImageURL());
        Image image = new Image(file.toURI().toString());
        mediaImage.setFitHeight(160);
        mediaImage.setFitWidth(152);
        mediaImage.setImage(image);

        mediaTitle.setText(media.getTitle());
        mediaPrice.setText(Utils.getCurrencyFormat(media.getPrice()));
        mediaAvail.setText(Integer.toString(media.getQuantity()));
        spinnerChangeNumber.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1)
        );

        setImage(mediaImage, media.getImageURL());
    }

}
