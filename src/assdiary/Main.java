package assdiary;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("AssDiary - Ассоциативный дневник");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setScene(new Scene(root, 1000 , 800));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static Stage getMainStage() {
        return mainStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
