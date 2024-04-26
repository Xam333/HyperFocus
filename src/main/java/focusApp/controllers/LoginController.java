package focusApp.controllers;

import focusApp.HelloApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class LoginController {
    @FXML
    public Button backButton;
    @FXML
    public Button loginButton;
    @FXML
    public Button confirmButton;
    @FXML
    public TextField userNameTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public TextField regUserNameTextField;
    @FXML
    public PasswordField regPasswordTextField;
    @FXML
    public PasswordField confirmPasswordTextField;
    @FXML
    public Label denyLoginLabel;
    @FXML
    public Label denyRegisterLabel;


    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }



    @FXML
    protected void onLoginButtonClick() throws IOException {
        if(!(Objects.equals(userNameTextField.getText(), "") || Objects.equals(userNameTextField.getText(), "Username")) && !(Objects.equals(passwordTextField.getText(), "") || Objects.equals(passwordTextField.getText(), "Password"))){
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            denyLoginLabel.setText("Username or password incorrect. Please try again.");
        }
    }

    @FXML
    protected void onConfirmButtonClick() throws IOException {
        if(!(Objects.equals(regUserNameTextField.getText(), "") || Objects.equals(regUserNameTextField.getText(), "Username")) && !(Objects.equals(regPasswordTextField.getText(), "") || Objects.equals(regPasswordTextField.getText(), "Password")) && (Objects.equals(regPasswordTextField.getText(), confirmPasswordTextField.getText()))){
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            denyRegisterLabel.setText("This username is already taken/Passwords don't match.");
        }
    }
}