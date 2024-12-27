import javafx.application.Application;
import javafx.stage.Stage;
import utils.Configs;
import views.screen.home.HomeScreenHandler;
import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            HomeScreenHandler homeHandler = new HomeScreenHandler(primaryStage, Configs.HOME_PATH);
            homeHandler.setScreenTitle("Home Screen");
            homeHandler.setImage();
            homeHandler.show();
        } catch (IOException e) {
            System.err.println("Error initializing the application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
