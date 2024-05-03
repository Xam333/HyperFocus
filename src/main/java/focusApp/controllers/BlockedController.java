package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.models.BlockedApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class BlockedController implements Initializable {
    public StackPane addWebsiteStackPane;
    public TextField addWebsiteTextField;
    public StackPane addApplicationStackPane;
    public TextField addApplicationTextField;
    public Button addWebButton;
    public Button addAppButton;
    public Button parentalControlsButton;
    public Button colourSettingsButton;
    public Button soundSettingsButton;
    boolean changesSaved = true;
    public Button saveButton;
    public Button cancelButton;
    public StackPane menuStackPane;
    public Button accountButton;
    public StackPane blackOutStackPane;
    public StackPane confirmCancelStackPane;
    public Button abortButton;
    public Button confirmButton;
    @FXML
    private ImageView userIcon;
    @FXML
    private ImageView youtubeIcon;
    @FXML
    private ImageView redditIcon;

    @FXML
    private TableColumn<BlockedApplication, ImageView> iconColumn;

    @FXML
    private TableColumn<BlockedApplication, String> nameColumn;

    @FXML
    private TableColumn<BlockedApplication, String> locationColumn;

    @FXML
    private TableView<BlockedApplication> tableView;

    ObservableList<BlockedApplication> testData()
    {
        BlockedApplication a1 = new BlockedApplication(youtubeIcon, "Youtube", "www.youtube.com");
        BlockedApplication a2 = new BlockedApplication(redditIcon, "Reddit", "www.reddit.com");
        return FXCollections.observableArrayList(a1,a2);
    }
    @FXML
    protected void onCancelButtonClick() throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            stage.setScene(scene);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Assuming image.png is directly in src/main/resources/
//        Image userImage = new Image(getClass().getResourceAsStream("/focusApp/images/UserIcon.png"));
//        userIcon.setImage(userImage);
//        userIcon.setFitWidth(20);  // Set the width of the ImageView
//        //userIcon.setFitHeight(150); // Set the height of the ImageView
//        userIcon.setPreserveRatio(true);



//        Image youtubeImage = new Image(getClass().getResourceAsStream("/focusApp/images/youtubeIcon.png"));
//        youtubeIcon.setImage(youtubeImage);
//        youtubeIcon.setFitWidth(20);  // Set the width of the ImageView
//        //youtubeIcon.setFitHeight(150); // Set the height of the ImageView
//        youtubeIcon.setPreserveRatio(true);
//
//        Image redditImage = new Image(getClass().getResourceAsStream("/focusApp/images/redditIcon.png"));
//        redditIcon.setImage(redditImage);
//        redditIcon.setFitWidth(20);  // Set the width of the ImageView
//        //redditIcon.setFitHeight(150); // Set the height of the ImageView
//        redditIcon.setPreserveRatio(true);


        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, ImageView>("iconColumn"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, String>("nameColumn"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, String>("locationColumn"));

        tableView.setItems(testData());

//        Automatically set as true until change is made
        changesSaved = true;

    }

    public void onMenuStackPaneEnter(MouseEvent actionEvent) {

        menuStackPane.setVisible(true);
    }
    public void onMenuStackPaneExit(MouseEvent mouseEvent) {
        menuStackPane.setVisible(false);
    }


//    Add new websites and applications to database
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        changesSaved = true;
        Stage stage = (Stage) saveButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

//    Navigate to form page or, open stack pane
    public void onWebsiteButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addWebsiteStackPane.setVisible(true);

    }

    public void onApplicationButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addApplicationStackPane.setVisible(true);
    }

//    Navigate to account page
//    #onAccountButtonClick is shared by the nav bar account button,
//    and the side menu account button.
    public void onAccountButtonClick(ActionEvent actionEvent) throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
            Stage stage = (Stage) accountButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/account-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            stage.setScene(scene);
        }
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
//            MAKE SETTINGS PAGE
            Stage stage = (Stage) soundSettingsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            stage.setScene(scene);
        }
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
//            MAKE SETTINGS PAGE
            Stage stage = (Stage) colourSettingsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            stage.setScene(scene);
        }
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
//            MAKE SETTINGS PAGE
            Stage stage = (Stage) parentalControlsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            stage.setScene(scene);
        }
    }

    public void onConfirmButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onAbortButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(false);
        confirmCancelStackPane.setVisible(false);
    }

    public void onXLabelClick(ActionEvent mouseEvent) {
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

//    Store to temp database things, wait for save button to be clicked
    public void onAddButtonClick(ActionEvent actionEvent) {
//        Get addWebsiteTextField contents
        changesSaved = false;
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

    public void websiteEntered(KeyEvent keyEvent) {
        addWebButton.setDisable(false);
    }

    public void onFileClick(ActionEvent actionEvent) {
        addAppButton.setDisable(false);
    }
}