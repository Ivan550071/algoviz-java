package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.MainView;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        MainView root = new MainView();
        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("Algorithm Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
