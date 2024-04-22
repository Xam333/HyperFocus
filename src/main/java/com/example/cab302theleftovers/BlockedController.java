package com.example.cab302theleftovers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class BlockedController {

    public Button loginButton;
    public Button registerButton;
    @FXML
    private ImageView userIcon;

    @FXML
    private void initialize() {
        // Assuming image.png is directly in src/main/resources/
        Image image = new Image(getClass().getResourceAsStream("/UserIcon.png"));
        userIcon.setImage(image);
        userIcon.setFitWidth(20);  // Set the width of the ImageView
        //imageView.setFitHeight(150); // Set the height of the ImageView
        userIcon.setPreserveRatio(true);
    }

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(""));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(""));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }


    public BlockedController(){
    }

}