package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.models.BlockedApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class BlockedController implements Initializable {

    public Button backButton;
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
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Assuming image.png is directly in src/main/resources/
        Image userImage = new Image(getClass().getResourceAsStream("/focusApp/images/UserIcon.png"));
        userIcon.setImage(userImage);
        userIcon.setFitWidth(20);  // Set the width of the ImageView
        //userIcon.setFitHeight(150); // Set the height of the ImageView
        userIcon.setPreserveRatio(true);



        Image youtubeImage = new Image(getClass().getResourceAsStream("/focusApp/images/youtubeIcon.png"));
        youtubeIcon.setImage(youtubeImage);
        youtubeIcon.setFitWidth(20);  // Set the width of the ImageView
        //youtubeIcon.setFitHeight(150); // Set the height of the ImageView
        youtubeIcon.setPreserveRatio(true);

        Image redditImage = new Image(getClass().getResourceAsStream("/focusApp/images/redditIcon.png"));
        redditIcon.setImage(redditImage);
        redditIcon.setFitWidth(20);  // Set the width of the ImageView
        //redditIcon.setFitHeight(150); // Set the height of the ImageView
        redditIcon.setPreserveRatio(true);




        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, ImageView>("iconColumn"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, String>("nameColumn"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedApplication, String>("locationColumn"));

        tableView.setItems(testData());

    }
}