package main;

import controller.SimulationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.RoomViewController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SimulationController controller = new SimulationController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SimulationView.fxml"));
        loader.setControllerFactory(param -> {
            if (param == RoomViewController.class) {
                return new RoomViewController(controller);
            }
            try {
                return param.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BorderPane root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Robot Vacuum Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

