package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.database.UserDAO;
import focusApp.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    public ImageView focusAppLogo;



    @FXML
    private void initialize(){
        // Assuming image.png is directly in src/main/resources/
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/focusApp/images/FocusApp_LogoT.png")));
        focusAppLogo.setImage(image);
        focusAppLogo.setFitWidth(190);  // Set the width of the ImageView
        //imageView.setFitHeight(150); // Set the height of the ImageView
        focusAppLogo.setPreserveRatio(true);
    }
    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }


    /* login button in login-view.fxml
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {

        /* create userDAO and attempt to login */
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(userNameTextField.getText(), passwordTextField.getText());

        /* if user != null then login successful and user class returned */
        if (user != null){
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            denyLoginLabel.setText("Username or password incorrect. Please try again.");
        }
    }

    /* confirm button on register-view.fxml
     */
    @FXML
    protected void onConfirmButtonClick() throws IOException {

        /* check two inputted passwords are same */
        if (!Objects.equals(regPasswordTextField.getText(), confirmPasswordTextField.getText())) {
            denyRegisterLabel.setText("Passwords don't match.");
            return;
        }

        /* create userDAO and attempt to create account */
        UserDAO userDAO = new UserDAO();
        User user = userDAO.addUser(regUserNameTextField.getText(), regPasswordTextField.getText());

        if (user != null) {
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            denyRegisterLabel.setText("This username is already taken/Passwords don't match.");
        }
    }
}