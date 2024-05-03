package focusApp.controllers;

import focusApp.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AccountController {
    public StackPane menuStackPane;
    public Label userNameLabel;
    public TextField userNameTextField;
    public Label passwordLabel;
    public PasswordField passwordTextField;
    public Button editPasswordButton;
    public Button editUserNameButton;
    public Button logOutButton;
    public Label totalTimeFocused;
    public Button mainPageButton;
    public StackPane confirmLogOutStackPane;
    public Button abortButton;
    public Button confirmButton;
    public StackPane blackOutStackPane;

    public void onMenuStackPaneEnter(MouseEvent mouseEvent) {
        menuStackPane.setVisible(true);
    }

    public void onMenuStackPaneExit(MouseEvent mouseEvent) {
        menuStackPane.setVisible(false);
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) {
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) {
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) {
    }

    public void onEditPasswordButtonClick(ActionEvent actionEvent) {
        passwordTextField.setEditable(!passwordTextField.isEditable());

    }

    public void onEditUserNameButtonClick(ActionEvent actionEvent) {
        userNameTextField.setEditable(!userNameTextField.isEditable());
    }

    public void onLogOutButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        confirmLogOutStackPane.setVisible(true);

    }

    public void onMainPageButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) mainPageButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onAbortButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(false);
        confirmLogOutStackPane.setVisible(false);
    }

    public void onConfirmButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }
}
