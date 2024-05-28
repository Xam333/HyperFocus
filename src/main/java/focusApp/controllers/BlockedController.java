package focusApp.controllers;

import focusApp.HelloApplication;

import focusApp.database.*;
import focusApp.database.PresetDAO;
import focusApp.database.WebsiteDAO;
import focusApp.database.ApplicationDAO;
import focusApp.database.Preset;

import focusApp.models.block.ApplicationItem;
import focusApp.models.block.BlockedItem;
import focusApp.models.block.WebsiteItem;
import focusApp.models.colour.UserConfig;
import focusApp.models.preset.PresetHolder;
import focusApp.models.user.User;
import focusApp.models.user.UserHolder;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;



//Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
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

    boolean changesSaved = true;
    public Button saveButton;
    public Button cancelButton;

    public StackPane blackOutStackPane;
    public StackPane confirmCancelStackPane;



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

    /**
     *
     */
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


    /**
     *
     * @param newWebsiteURL
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

    /**
     *
     * @param newApplicationLocation
     */
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

    /**
     *
     * @param exeLocation
     * @return
     */
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

    /**
     *
     * @return
     */
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
            addAppButton.setDisable(false);
            return selectedFile.getAbsolutePath();
        } else{
            addAppButton.setDisable(true);
            return "File Not Found";
        }
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    protected void onCancelButtonClick() throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            if (UserConfig.FindCSSFile()){
                scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
            } else {
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            }
            stage.setScene(scene);
        }

    }

    /**
     *
     */
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

    /**
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("iconURI"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("URI"));

        syncBlockedApplications();

        // Automatically set as true until change is made
        changesSaved = true;

    }

    /**
     *
     * @param actionEvent
     * @throws IOException
     */
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

        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    /**
     *
     * @param actionEvent
     */
//    Navigate to form page or, open stack pane
    public void onWebsiteButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addWebsiteStackPane.setVisible(true);

    }

    /**
     *
     * @param actionEvent
     */
    public void onApplicationButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addApplicationStackPane.setVisible(true);
    }

    /**
     *
     * @param mouseEvent
     */
    public void onXLabelClick(ActionEvent mouseEvent) {
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

    /**
     *
     * @param actionEvent
     */
//    Store to temp database things, wait for save button to be clicked
    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
//        Get addWebsiteTextField contents
        if(addWebsiteStackPane.isVisible())
        {
            addNewWebsite("https://" + addWebsiteTextField.getText());
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

    /**
     *
     * @param keyEvent
     */
    public void websiteEntered(KeyEvent keyEvent) {
        addWebButton.setDisable(false);
    }

    /**
     *
     */
    public void onFileClick() {
        fileLocation = fileSearch();
        addApplicationTextField.setText(fileLocation);

    }


    @FXML
    private Button confirmButton;

    /**
     *
     * @throws IOException
     */
    public void onConfirmButtonClick() throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    /**
     *
     */
    public void onAbortButtonClick() {
        blackOutStackPane.setVisible(false);
        confirmCancelStackPane.setVisible(false);
    }
}