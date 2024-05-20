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
import focusApp.models.PresetHolder;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;



//Jsoup
import org.controlsfx.control.ToggleSwitch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class BlockedController implements Initializable {

    public VBox accountInformationSection;
    public Label totalTimeFocused;
    public Label userNameLabel;
    public TextField userNameTextField;
    public Button editUserNameButton;
    public Label passwordLabel;
    public PasswordField passwordTextField;
    public Button editPasswordButton;
    public Label accountError;
    public VBox parentalControlsSection;
    public ToggleSwitch parentalControlToggleButton;
    public VBox colourSettingsSection;
    public ComboBox colourOptionsButton;
    public HBox defaultPalette;
    public HBox greyScalePalette;
    public HBox redPalette;
    public VBox soundSettingsSection;
    public ComboBox soundOptionsButton;
    public Slider volumeSlider;
    public StackPane confirmLogOutStackPane;
    public Button abortLogOutButton;
    public Button confirmLogOutButton;
    public StackPane turnOffParentalControlsStackPane;
    public PasswordField parentalControlsPasswordField;
    public Label denyParentalControlsDisableLabel;
    public Button confirmPasswordButton;
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
    private Boolean isPCOpen = false;
    private Boolean isSSOpen = false;
    private Boolean isCSOpen = false;
    private Boolean isAIOpen = false;


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
    private UserDAO userDAO;
    private PresetHolder presetHolder;
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
        presetHolder = PresetHolder.getInstance();
        blockedItems = new ArrayList<BlockedItem>();
        userDAO = new UserDAO();

        /* only using one preset per user for now */

        /* get preset for user and if none create new preset */

        /* check if user logged in */
        user = userHolder.getUser();
        if (user == null) {
            throw new Error("User is null");
        }

        currentPreset = presetHolder.getPreset();
        if (currentPreset == null) {
            throw new Error("Preset is null");
        }

        /* get all presets and if a preset exists use that otherwise create new preset */

        /* populate blockedItems list from preset */
        blockedItems.addAll(presetDAO.getPresetWebsite(currentPreset.getPresetID()));
        blockedItems.addAll(presetDAO.getPresetApplication(currentPreset.getPresetID()));
    }


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
                    try {
                        // Download the .ico file
                        File icoFile = File.createTempFile("favicon", ".ico");
                        FileUtils.copyURLToFile(new URL(url), icoFile);

                        // Convert the .ico file to a BufferedImage
                        BufferedImage bufferedImage = Imaging.getBufferedImage(icoFile);

                        // Create a temporary file for the converted image
                        File tempFile = File.createTempFile("temp_image", ".png");
                        tempFile.deleteOnExit();
                        ImageIO.write(bufferedImage, "png", tempFile);

                        // Load the image using the temporary file path
                        Image fxImage = new Image(tempFile.toURI().toString(), true);  // true for background loading
                        imageView.setImage(fxImage);
                        imageView.setFitHeight(30); // Adjust size as needed
                        imageView.setFitWidth(30);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                    } catch (IOException | ImageReadException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {




        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("iconURI"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("URI"));

        syncBlockedApplications();

        // Automatically set as true until change is made
        changesSaved = true;

        // Display a sound name in combo box
        soundOptionsButton.getSelectionModel().selectFirst();

        // Display a colour in combo box
        colourOptionsButton.getSelectionModel().selectFirst();

        // Only show enter passcode if parental controls is being turned off
        parentalControlToggleButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (!isNowSelected) {
                // Show password dialog
                blackOutStackPane.setVisible(true);
                turnOffParentalControlsStackPane.setVisible(true);
            } else{
                blackOutStackPane.setVisible(false);
                turnOffParentalControlsStackPane.setVisible(false);
            }
        });
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
        if (isAIOpen) {
            // Close the menu
            accountInformationSection.setManaged(false);
            accountInformationSection.setVisible(false);
            isAIOpen = false;
        } else {
            // Open the menu
            accountInformationSection.setManaged(true);
            accountInformationSection.setVisible(true);
            isAIOpen = true;

            // Close all other menus
            parentalControlsSection.setManaged(false);
            parentalControlsSection.setVisible(false);
            isPCOpen = false;

            colourSettingsSection.setManaged(false);
            colourSettingsSection.setVisible(false);
            isCSOpen = false;

            soundSettingsSection.setManaged(false);
            soundSettingsSection.setVisible(false);
            isSSOpen = false;
        }
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) throws IOException {
        if (isPCOpen) {
            // Close the menu
            parentalControlsSection.setManaged(false);
            parentalControlsSection.setVisible(false);
            isPCOpen = false;
        } else {
            // Open the menu
            parentalControlsSection.setManaged(true);
            parentalControlsSection.setVisible(true);
            isPCOpen = true;

            // Close all other menus
            accountInformationSection.setManaged(false);
            accountInformationSection.setVisible(false);
            isAIOpen = false;

            colourSettingsSection.setManaged(false);
            colourSettingsSection.setVisible(false);
            isCSOpen = false;

            soundSettingsSection.setManaged(false);
            soundSettingsSection.setVisible(false);
            isSSOpen = false;
        }
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        if (isCSOpen) {
            // Close the menu
            colourSettingsSection.setManaged(false);
            colourSettingsSection.setVisible(false);
            isCSOpen = false;
        } else {
            // Open the menu
            colourSettingsSection.setManaged(true);
            colourSettingsSection.setVisible(true);
            isCSOpen = true;

            // Close all other menus
            accountInformationSection.setManaged(false);
            accountInformationSection.setVisible(false);
            isAIOpen = false;

            parentalControlsSection.setManaged(false);
            parentalControlsSection.setVisible(false);
            isPCOpen = false;

            soundSettingsSection.setManaged(false);
            soundSettingsSection.setVisible(false);
            isSSOpen = false;
        }
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        if (isSSOpen) {
            // Close the menu
            soundSettingsSection.setManaged(false);
            soundSettingsSection.setVisible(false);
            isSSOpen = false;
        } else {
            // Open the menu
            soundSettingsSection.setManaged(true);
            soundSettingsSection.setVisible(true);
            isSSOpen = true;

            // Close all other menus
            accountInformationSection.setManaged(false);
            accountInformationSection.setVisible(false);
            isAIOpen = false;

            parentalControlsSection.setManaged(false);
            parentalControlsSection.setVisible(false);
            isPCOpen = false;

            colourSettingsSection.setManaged(false);
            colourSettingsSection.setVisible(false);
            isCSOpen = false;
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
        parentalControlToggleButton.setSelected(true);
        turnOffParentalControlsStackPane.setVisible(false);
        denyParentalControlsDisableLabel.setText("");
        parentalControlsPasswordField.clear();

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

    public void onEditUserNameButtonClick(ActionEvent actionEvent) {
        if (userNameTextField.isEditable()) {
            if (userDAO.updateName(user.getId(), userNameTextField.getText())) {
                user.setUserName(userNameTextField.getText());
                userHolder.setUser(user);
                accountError.setText("");
                accountError.setManaged(false);
            } else {
                accountError.setText("Username is already taken");
                accountError.setManaged(true);
                return;
            }
            editUserNameButton.setText("EDIT");
        } else {
            editUserNameButton.setText("SAVE");
        }
        userNameTextField.setEditable(!userNameTextField.isEditable());
    }

    public void onEditPasswordButtonClick(ActionEvent actionEvent) {
        passwordTextField.setEditable(!passwordTextField.isEditable());
    }

    public void onLogOutButton(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        confirmLogOutStackPane.setVisible(true);
    }

    public void onConfirmLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void passwordEntered(KeyEvent keyEvent) {
        confirmPasswordButton.setDisable(false);
    }

    public void onConfirmParentalControlsButtonClick(ActionEvent actionEvent) {
        // Check if password is correct

//        UserDAO userDAO = new UserDAO();
//        User user = userDAO.login(userNameTextField.getText(), passwordTextField.getText());

        /* if user != null then login successful and user class returned */
        if (Objects.equals(parentalControlsPasswordField.getText(), "1234")){
            parentalControlToggleButton.setSelected(false);
            blackOutStackPane.setVisible(false);
            turnOffParentalControlsStackPane.setVisible(false);
            denyParentalControlsDisableLabel.setText("");
            parentalControlsPasswordField.clear();
        } else {
            denyParentalControlsDisableLabel.setText("* Incorrect password. *");
        }
    }

    public void onAbortLogOutButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(false);
        confirmLogOutStackPane.setVisible(false);
    }
}