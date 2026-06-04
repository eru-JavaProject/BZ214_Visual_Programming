module com.example.robotsimulation {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.robotsimulation to javafx.fxml;
    opens com.example.robotsimulation.controller to javafx.fxml;

    exports com.example.robotsimulation;
}