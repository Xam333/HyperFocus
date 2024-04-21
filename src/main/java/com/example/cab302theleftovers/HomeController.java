package com.example.cab302theleftovers;

import javafx.fxml.FXML;
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

public class HomeController {

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
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private VBox contactContainer;
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