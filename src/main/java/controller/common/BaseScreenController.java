package controller.common;

import controller.home.HomeScreenController;
import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.media.Media;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class BaseScreenController {

    protected FXMLLoader loader;
    protected AnchorPane content;
    protected final Stage stage;
    protected HomeScreenController homeScreenHandler;
    protected Hashtable<String, String> messages;
    private Scene scene;
    private BaseScreenController prev;

    // Constructor
    public BaseScreenController(Stage stage, String screenPath) throws IOException {
        this.loader = new FXMLLoader(getClass().getResource(screenPath));
        this.loader.setController(this);
        this.content = loader.load();
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

    // Navigation methods
    public BaseScreenController getPreviousScreen() {
        return this.prev;
    }

    public void setPreviousScreen(BaseScreenController prev) {
        this.prev = prev;
    }

    /**
     * Hiển thị màn hình hiện tại.
     */
    public void show() {
        if (this.scene == null) {
            this.scene = new Scene(this.content);
        }
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void setScreenTitle(String title) {
        this.stage.setTitle(title);
    }

    public void forward(Hashtable<String, String> messages) {
        this.messages = messages;
    }

    public void setHomeScreenHandler(HomeScreenController HomeScreenHandler) {
        this.homeScreenHandler = HomeScreenHandler;
    }

    // FXML methods from FXMLScreenController
    public AnchorPane getContent() {
        return this.content;
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }

    public void setImage(ImageView imageView, String path) {
        File file = new File(path);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    // Cart methods from BaseController
    public CartMedia checkMediaInCart(Media media) {
        return Cart.getCart().checkMediaInCart(media);
    }

    public List<CartMedia> getListCartMedia() {
        return Cart.getCart().getListMedia();
    }

    /**
     * Điều hướng về màn hình trước đó hoặc quay về màn hình chính.
     * @param fallbackScreenPath Đường dẫn màn hình mặc định.
     * @param fallbackScreenTitle Tiêu đề màn hình mặc định.
     * @throws IOException Nếu không thể tải màn hình.
     */
    public void navigateBack(String fallbackScreenPath, String fallbackScreenTitle) throws IOException {
        if (this.prev != null) {
            this.prev.show();
        } else {
            BaseScreenController fallbackScreen = new BaseScreenController(this.stage, fallbackScreenPath);
            fallbackScreen.setScreenTitle(fallbackScreenTitle);
            fallbackScreen.show();
        }
    }
}
