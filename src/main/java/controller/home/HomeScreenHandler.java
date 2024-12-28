package controller.home;

import common.exception.ViewCartException;
import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.media.Media;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import controller.common.BaseScreenHandler;
import controller.cart.CartScreenHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class HomeScreenHandler extends BaseScreenHandler implements Initializable {

    public static Logger LOGGER = Utils.getLogger(HomeScreenHandler.class.getName());

    @FXML
    private Label numMediaInCart;

    @FXML
    private ImageView aimsImage;

    @FXML
    private ImageView cartImage;

    @FXML
    private VBox vboxMedia1;

    @FXML
    private VBox vboxMedia2;

    @FXML
    private VBox vboxMedia3;

    @FXML
    private HBox hboxMedia;

    @FXML
    private SplitMenuButton splitMenuBtnSearch;

    @FXML
    private HBox pageNumberHbox;

    private List homeItems;

    public HomeScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    private int numberMediaPerPage = 12;
    private int currentPage;

//Functional Cohesion
    public Label getNumMediaCartLabel() {
        return this.numMediaInCart;
    }


//Functional Cohesion

    @Override
//Sequential Cohesion
    public void show() {
        numMediaInCart.setText(String.valueOf(Cart.getCart().getListMedia().size()) + " media");
        super.show();
    }

    public CartMedia checkMediaInCart(Media media) {
        return Cart.getCart().checkMediaInCart(media);
    }

    @Override
// Control Coupling
// Control Cohesion
    public void initialize(URL arg0, ResourceBundle arg1) {
        numberMediaPerPage = 12;
        currentPage = 1;
        loadScreen();

        aimsImage.setOnMouseClicked(e -> {
            addMediaHome(GetMediaPaged(currentPage));
        });

        cartImage.setOnMouseClicked(e -> {

            try {
                var cartScreen = new CartScreenHandler(this.stage, Configs.CART_SCREEN_PATH);
                cartScreen.setHomeScreenHandler(this);
//                cartScreen.setBController(new ViewCartController());
                cartScreen.show(this);
            } catch (IOException | SQLException e1) {
                throw new ViewCartException(Arrays.toString(e1.getStackTrace()).replaceAll(", ", "\n"));
            }
        });

        addMediaHome(this.homeItems);
        addMenuItem(0, "Book", splitMenuBtnSearch);
        addMenuItem(1, "DVD", splitMenuBtnSearch);
        addMenuItem(2, "CD", splitMenuBtnSearch);
    }


    public List<Media> getAllMedia() throws SQLException {
        return new Media().getAllMedia();
    }

    private void loadScreen() {
        try {
            List medium = getAllMedia();
            this.homeItems = new ArrayList<>();
            for (Object object : medium) {
                Media media = (Media) object;
                MediaHandler m1 = new MediaHandler(Configs.HOME_MEDIA_PATH, media, this);
                this.homeItems.add(m1);
            }
        } catch (SQLException | IOException e) {
            LOGGER.info("Errors occured: " + e.getMessage());
            e.printStackTrace();
        }

        int numberOfPage = (int) Math.ceil((double) homeItems.size() / numberMediaPerPage);

        for (int i = 0; i < numberOfPage; i++){
            Button button = new Button(String.valueOf(i + 1));
            button.setOnMouseClicked(e -> {
                int pageIndex = Integer.parseInt(button.getText()) - 1;
                List mediaPaged = GetMediaPaged(pageIndex);
                addMediaHome(mediaPaged);
                currentPage = pageIndex + 1;
            });
            pageNumberHbox.getChildren().add(button);
        }
    }

    //Data Coupling
//Data Cohesion
    public void setImage() {
        // fix image path caused by fxml
        File file1 = new File(Configs.IMAGE_PATH + "/" + "Logo.png");
        Image img1 = new Image(file1.toURI().toString());
        aimsImage.setImage(img1);

        File file2 = new File(Configs.IMAGE_PATH + "/" + "cart.png");
        Image img2 = new Image(file2.toURI().toString());
        cartImage.setImage(img2);
    }

    public List GetMediaPaged(int pageNumber) {
        int startIndex = pageNumber * numberMediaPerPage;
        int lastIndex = Math.min(startIndex + numberMediaPerPage, homeItems.size());
        List mediaPaged = new ArrayList<>(homeItems.subList(startIndex, lastIndex));
        return mediaPaged;
    }

    /**
     * @param items
     */
//Data Coupling
//Data Cohesion
    public void addMediaHome(List items) {
        ArrayList mediaItems = (ArrayList) ((ArrayList) items).clone();
        hboxMedia.getChildren().forEach(node -> {
            VBox vBox = (VBox) node;
            vBox.getChildren().clear();
        });
        while (!mediaItems.isEmpty()) {
            hboxMedia.getChildren().forEach(node -> {
                int vid = hboxMedia.getChildren().indexOf(node);
                VBox vBox = (VBox) node;
                while (vBox.getChildren().size() < 3 && !mediaItems.isEmpty()) {
                    MediaHandler media = (MediaHandler) mediaItems.get(0);
                    vBox.getChildren().add(media.getContent());
                    mediaItems.remove(media);
                }
            });
            return;
        }
    }

    /**
     * @param position
     * @param text
     * @param menuButton
     */

// Data Coupling
// Data Cohesion
    private void addMenuItem(int position, String text, MenuButton menuButton) {
        MenuItem menuItem = new MenuItem();
        Label label = new Label();
        label.prefWidthProperty().bind(menuButton.widthProperty().subtract(31));
        label.setText(text);
        label.setTextAlignment(TextAlignment.RIGHT);
        menuItem.setGraphic(label);
        menuItem.setOnAction(e -> {
            // empty home media
            hboxMedia.getChildren().forEach(node -> {
                VBox vBox = (VBox) node;
                vBox.getChildren().clear();
            });

            // filter only media with the choosen category
            List filteredItems = new ArrayList<>();
            homeItems.forEach(me -> {
                MediaHandler media = (MediaHandler) me;
                if (media.getMedia().getTitle().toLowerCase().startsWith(text.toLowerCase())) {
                    filteredItems.add(media);
                }
            });

            // fill out the home with filted media as category
            addMediaHome(filteredItems);
        });
        menuButton.getItems().add(position, menuItem);
    }

    public void removeMedia(Media media) throws SQLException {
        homeItems.remove(media);
        media.removeMedia();
        pageNumberHbox.getChildren().clear();
        loadScreen();
        addMediaHome(GetMediaPaged(currentPage - 1));
    }

}
