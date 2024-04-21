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

public class HomeController {

    public Button loginButton;
    public Button registerButton;
    @FXML
    private ImageView imageView;

    @FXML
    private void initialize() {
        // Assuming image.png is directly in src/main/resources/
        Image image = new Image(getClass().getResourceAsStream("/FocusApp_LogoT.png"));
        imageView.setImage(image);
        imageView.setFitWidth(190);  // Set the width of the ImageView
        //imageView.setFitHeight(150); // Set the height of the ImageView
        imageView.setPreserveRatio(true);
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


    public HomeController(){
    }



    /**
     * Programmatically selects a contact in the list view and
     * updates the text fields with the contact's information.
     * @param contact The contact to select.
     */


    /**
     * Renders a cell in the contacts list view by setting the text to the contact's full name.
     * @param contactListView The list view to render the cell for.
     * @return The rendered cell.
     */

    /**
     * Synchronizes the contacts list view with the contacts in the database.
     */

}