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
    public Button accountButton;
    public TableView tableView;
    public TableColumn iconColumn;
    public TableColumn nameColumn;
    public TableColumn locationColumn;
    public Button cancelButton;
    public Button saveButton;
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
    }

    public void onMainPageButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) mainPageButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }
}
