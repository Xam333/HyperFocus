module com.example.cab302theleftovers {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cab302theleftovers to javafx.fxml;
    exports com.example.cab302theleftovers;
}