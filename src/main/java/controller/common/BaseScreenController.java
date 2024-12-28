package controller.common;

import controller.BaseController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.home.HomeScreenController;

import java.io.IOException;
import java.util.Hashtable;

public class BaseScreenController extends FXMLScreenController {

    protected final Stage stage;
    protected HomeScreenController homeScreenHandler;
    protected Hashtable<String, String> messages;
    private Scene scene;
    private BaseScreenController prev;
    private BaseController bController;

    //Data Coupling
    private BaseScreenController(String screenPath) throws IOException {
        super(screenPath);
        this.stage = new Stage();
    }


    public BaseScreenController(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
//        accountController = AccountController.getAccountController();
    }

    /**
     * @return BaseScreenController
     */
    public BaseScreenController getPreviousScreen() {
        return this.prev;
    }

    /**
     * @param prev
     */
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


    /**
     * @param string
     */
    public void setScreenTitle(String string) {
        this.stage.setTitle(string);
    }

    /**
     * @return BaseController
     */
    public BaseController getBController() {
        return this.bController;
    }

    /**
     * @param bController
     */
    public void setBController(BaseController bController) {
        this.bController = bController;
    }



    /**
     * @param messages
     */
    public void forward(Hashtable messages) {
        this.messages = messages;
    }


    /**
     * @param HomeScreenHandler
     */
    public void setHomeScreenHandler(HomeScreenController HomeScreenHandler) {
        this.homeScreenHandler = HomeScreenHandler;
    }

}