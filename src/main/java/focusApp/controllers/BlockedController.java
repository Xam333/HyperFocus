package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.models.UserHolder;

import focusApp.database.*;
import focusApp.models.ApplicationItem;
import focusApp.models.WebsiteItem;
import focusApp.database.PresetDAO;
import focusApp.database.WebsiteDAO;
import focusApp.database.ApplicationDAO;
import focusApp.database.Preset;
import focusApp.models.User;
import focusApp.models.BlockedItem;

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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

//Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class BlockedController implements Initializable {

    private String fileLocation;
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
    private Boolean isMenuOpen = false;

    @FXML
    private TextField getAddWebsiteTextField;
    @FXML
    private ImageView userIcon;
    @FXML
    private ImageView youtubeIcon;
    @FXML
    private ImageView redditIcon;

    @FXML
    private TableColumn<BlockedItem, String> iconColumn;

    @FXML
    private TableColumn<BlockedItem, String> nameColumn;

    @FXML
    private TableColumn<BlockedItem, String> locationColumn;

    @FXML
    private TableView<BlockedItem> tableView;

    private IBlockedItemDAO blockedDAO;
    private PresetDAO presetDAO;
    private ApplicationDAO applicationDAO;
    private WebsiteDAO websiteDAO;
    private Preset currentPreset;
    private UserHolder userHolder;
    private User user;
    private ArrayList<BlockedItem> blockedItems;

    public BlockedController(){
        blockedDAO = new MockedBlockedItemDAO();
        presetDAO = new PresetDAO();
        applicationDAO = new ApplicationDAO();
        websiteDAO = new WebsiteDAO();
        userHolder = UserHolder.getInstance();
        blockedItems = new ArrayList<BlockedItem>();

        /* only using one preset per user for now */

        /* get preset for user and if none create new preset */

        /* check if user logged in */
        user = userHolder.getUser();
        if (user == null) {
            throw new Error("User is null");
        }

        /* get all presets and if a preset exists use that otherwise create new preset */
        ArrayList<Preset> presets = presetDAO.getUsersPresets(user.getId());
        if (presets.isEmpty()) {
            currentPreset = presetDAO.addPreset(user.getId(), "Main");
        } else {
            currentPreset = presets.get(0);
        }

        /* populate blockedItems list from preset */
        blockedItems.addAll(presetDAO.getPresetWebsite(currentPreset.getPresetID()));
        blockedItems.addAll(presetDAO.getPresetApplication(currentPreset.getPresetID()));
    }

    /*REDUNDANT
    @FXML
    private void onAddWebsite() //REDUNDANT
    {
        String newWebsite = showUrlDialog();
        System.out.println("This is a test: " + newWebsite);
        System.out.println(newWebsite);
        String[] info = getWebPageTitleAndImage(newWebsite);
        System.out.println("Title: " + info[0]);
        System.out.println("Image URL: " + info[1]);
    }

    private String showUrlDialog() { //REDUNDANT
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
    } //REDUNDANT
    */

    @FXML
    private void addNewWebsite(String newWebsiteURL)
    {
        String[] info = getWebPageTitleAndImage(newWebsiteURL);
        WebsiteItem websiteItem = websiteDAO.addWebsite(info[1], info[2], info[0]);
//        blockedDAO.addApplication((new BlockedApplication(info[0], info[1], info[2])));
//        syncBlockedApplications();

        if (websiteItem == null) {
            websiteItem = websiteDAO.getWebsite(websiteDAO.getWebsiteID(info[1]));

            System.out.println("Website already exists adding to preset.");
        } else {
            /* logging */
            System.out.println("Image URL: " + info[0]);
            System.out.println("Name: " + info[1]);
            System.out.println("Web/File Link: " + info[2]);
        }

        System.out.println("Image URL: " + info[0]);
        System.out.println("Name: " + info[1]);
        System.out.println("Web/File Link: " + info[2]);

        blockedItems.add(websiteItem);

        syncBlockedApplications();
    }

    @FXML
    private void addNewApplication(String newApplicationLocation)
    {
        String[] info = getExecutableTitleAndIcon(newApplicationLocation);
        ApplicationItem applicationItem = applicationDAO.addApplication(info[1], info[2], info[0]);
//        blockedDAO.addApplication((new BlockedApplication(info[0], info[1], info[2])));
//        syncBlockedApplications();

        if (applicationItem == null) {
            applicationItem = applicationDAO.getApplication(applicationDAO.getApplicationID(info[1]));
        }

        System.out.println("Image URL: " + info[0]);
        System.out.println("Name: " + info[1]);
        System.out.println("Web/File Link: " + info[2]);

        blockedItems.add(applicationItem);

        syncBlockedApplications();
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

            return new String[]{faviconUrl, title, url};
        } catch (IOException e) {
            System.err.println("Error fetching the webpage: " + e.getMessage());
            return new String[]{"Error retrieving title", "Error retrieving favicon"};
        }
    }

    //C:\Program Files (x86)\Nisscan\NDS II 2.56\NDS II 2_56.exe
    public static String[] getExecutableTitleAndIcon(String exeLocation)
    {
        String regex = "([^\\\\]+)";
        ArrayList<String> matches = new ArrayList<>();

        // Compile the regex
        Pattern pattern = Pattern.compile(regex);

        System.out.println("Matches in path: " + exeLocation);
        Matcher matcher = pattern.matcher(exeLocation);
        int count = 1;
        while (matcher.find())
        {
            System.out.println("Segment " + count + ": " + matcher.group(1));
            matches.add(matcher.group(1));
            count++;
        }

        System.out.println("This is the Application: " + matches.get(matches.size()-2));
        String foundTitle = matches.get(matches.size()-2);

        String fileIcon = "https://winaero.com/blog/wp-content/uploads/2018/12/file-explorer-folder-libraries-icon-18298.png";

        return new String[]{fileIcon, foundTitle, exeLocation};
    }

    public String fileSearch() {
        JFrame frame = new JFrame("Open File");

        // Create a file chooser.
        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to open at the current directory.
        fileChooser.setCurrentDirectory(new File("."));

        // Open the file chooser dialog.
        int result = fileChooser.showOpenDialog(frame);

        // If the user selects a file.
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file.
            File selectedFile = fileChooser.getSelectedFile();
            // Display the path of the selected file.
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
        }
        else return "Not Found";
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

    public void syncBlockedApplications()
    {
//        ObservableList<BlockedApplication> dataList = blockedDAO.dataList(blockedDAO.getAllApplications());

        ObservableList<BlockedItem> dataList = FXCollections.observableArrayList(blockedItems);

        tableView.setItems(dataList);


        iconColumn.setCellValueFactory(new PropertyValueFactory<>("iconURI"));

        iconColumn.setCellFactory(tc -> new TableCell<BlockedItem, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Set the image URL and properties
                    imageView.setImage(new Image(url, true));  // true for background loading
                    imageView.setFitHeight(30); // Adjust size as needed
                    imageView.setFitWidth(30);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });
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


        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("iconURI"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("URI"));

        syncBlockedApplications();

//        Automatically set as true until change is made
        changesSaved = true;

    }

    @FXML
    private void toggleMenu() {
        if (isMenuOpen) {
            // Close the menu
            menuStackPane.setVisible(false);
            isMenuOpen = false;
        } else {
            // Open the menu
            menuStackPane.setVisible(true);
            isMenuOpen = true;
        }
    }


//    Add new websites and applications to database
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {

        for (BlockedItem item : blockedItems) {
            if (item.getClass() == WebsiteItem.class) {
                System.out.println(item.getName() + " is website");
                presetDAO.addWebsitePreset(currentPreset.getPresetID(), item.getID());
            } else if (item.getClass() == ApplicationItem.class) {
                System.out.println(item.getName() + " is application");
                presetDAO.addApplicationPreset(currentPreset.getPresetID(), item.getID());
            }

        }

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
    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
//        Get addWebsiteTextField contents
        if(addWebsiteStackPane.isVisible())
        {
            addNewWebsite(addWebsiteTextField.getText());
        }
        else
        {
            addNewApplication(fileLocation);
        }

        changesSaved = false;
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

    public void websiteEntered(KeyEvent keyEvent) {
        addWebButton.setDisable(false);
    }

    public void onFileClick(ActionEvent actionEvent) {
        fileLocation = fileSearch();
        addApplicationTextField.setText(fileLocation);
        addAppButton.setDisable(false);
    }
}