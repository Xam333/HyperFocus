module focusApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens focusApp to javafx.fxml;
    exports focusApp;
    exports focusApp.database;
    exports focusApp.controllers;
    opens focusApp.controllers to javafx.fxml;
    exports focusApp.models;
    opens focusApp.models to javafx.fxml;
}