package controller.common;

import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.home.HomeScreenController;
import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.media.Media;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class BaseScreenController extends FXMLScreenController {

    protected final Stage stage;
    protected HomeScreenController homeScreenHandler;
    protected Hashtable<String, String> messages;
    private Scene scene;
    private BaseScreenController prev;

    // Constructor
    private BaseScreenController(String screenPath) throws IOException {
        super(screenPath);
        this.stage = new Stage();
    }

    public BaseScreenController(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
    }

    // Navigation methods
    public BaseScreenController getPreviousScreen() {
        return this.prev;
    }

    public void setPreviousScreen(BaseScreenController prev) {
        this.prev = prev;
    }

    public void show() {
        if (this.scene == null) {
            this.scene = new Scene(this.content);
        }
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void setScreenTitle(String string) {
        this.stage.setTitle(string);
    }

    public void forward(Hashtable<String, String> messages) {
        this.messages = messages;
    }

    public void setHomeScreenHandler(HomeScreenController HomeScreenHandler) {
        this.homeScreenHandler = HomeScreenHandler;
    }

    // Cart methods from BaseController
    public CartMedia checkMediaInCart(Media media) {
        return Cart.getCart().checkMediaInCart(media);
    }

    public List<CartMedia> getListCartMedia() {
        return Cart.getCart().getListMedia();
    }
}
