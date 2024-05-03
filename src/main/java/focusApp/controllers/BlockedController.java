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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

//Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BlockedController implements Initializable {


    public Button saveButton;
    public Button cancelButton;
    public StackPane menuStackPane;
    public Button accountButton;
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
    private void onAddWebsite()
    {
        String newWebsite = showUrlDialog();
        System.out.println("This is a test: " + newWebsite);
        System.out.println(newWebsite);
        String[] info = getWebPageTitleAndImage(newWebsite);
        System.out.println("Title: " + info[0]);
        System.out.println("Image URL: " + info[1]);
    }
    private String showUrlDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Enter URL");

        TextField urlField = new TextField();
        urlField.setPromptText("https://example.com");
        dialog.getDialogPane().setContent(urlField);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return urlField.getText();
            }
            return null;
        });

        // The blocking call that will wait for the dialog to close
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null); // Returns the URL if OK was pressed, otherwise returns null
    }

    /**
     * Retrieves the webpage title and the first image source URL.
     * @param url The URL of the webpage.
     * @return A string array where the first element is the title and the second is the image URL.
     */
    public static String[] getWebPageTitleAndImage(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            // Get the title of the webpage
            String title = doc.title();

            // Attempt to get the URL of the favicon
            String faviconUrl = "No favicon found";
            Elements links = doc.head().select("link[href]");
            for (Element link : links) {
                String relValue = link.attr("rel");
                if (relValue.contains("icon")) {
                    faviconUrl = link.absUrl("href");
                    break;
                }
            }

            return new String[]{title, faviconUrl};
        } catch (IOException e) {
            System.err.println("Error fetching the webpage: " + e.getMessage());
            return new String[]{"Error retrieving title", "Error retrieving favicon"};
        }
    }


    @FXML
    protected void onCancelButtonClick() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
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

    }

    public void onMenuStackPaneEnter(MouseEvent actionEvent) {

        menuStackPane.setVisible(true);
    }
    public void onMenuStackPaneExit(MouseEvent mouseEvent) {
        menuStackPane.setVisible(false);
    }


//    Add new websites and applications to database
    public void onSaveButtonClick(ActionEvent actionEvent) {
    }

//    Navigate to form page or, open stack pane
    public void onWebsiteButtonClick(ActionEvent actionEvent) {
    }

    public void onApplicationButtonClick(ActionEvent actionEvent) {
    }

//    Navigate to account page
//    #onAccountButtonClick is shared by the nav bar account button,
//    and the side menu account button.
    public void onAccountButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) accountButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/account-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) {
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) {
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) {
    }
}