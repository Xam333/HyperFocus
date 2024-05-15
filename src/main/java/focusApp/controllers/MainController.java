package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.database.UserDAO;
import focusApp.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import java.util.Objects;

import focusApp.database.Preset;
import focusApp.database.PresetDAO;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;

public class MainController implements Initializable {
    public Button startButton;
    public Pane blockedApplicationPane;
    public StackPane menuStackPane;
    public Button accountButton;
    public Button parentalControlsButton;
    public Button colourSettingsButton;
    public Button soundSettingsButton;
    public VBox parentalControlsSection;
    public PasswordField parentalControlPasswordField;
    public VBox soundSettingsSection;
    public VBox colourSettingsSection;
    public Slider volumeSlider;
    public ComboBox soundOptionsButton;
    public HBox defaultPalette;
    public ComboBox colourOptionsButton;
    public HBox greyScalePalette;
    public HBox redPalette;
    public ToggleSwitch parentalControlToggleButton;
    public VBox passwordSection;
    public PasswordField parentalControlsPasswordField;
    public Button confirmPasswordButton;
    public StackPane turnOffParentalControlsStackPane;
    public StackPane blackOutStackPane;
    public Label denyParentalControlsDisableLabel;
    public VBox accountInformationSection;
    public Label totalTimeFocused;
    public Label userNameLabel;
    public TextField userNameTextField;
    public Button editUserNameButton;
    public Button editPasswordButton;
    public PasswordField passwordTextField;
    public Label passwordLabel;
    public Label accountError;
    public Button abortButton;
    public Button confirmButton;
    public StackPane confirmLogOutStackPane;
    private Boolean isMenuOpen = false;

    private Boolean isPCOpen = false;
    private Boolean isSSOpen = false;
    private Boolean isCSOpen = false;
    private Boolean isAIOpen = false;


    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Slider startTimeSlider;

    @FXML
    private Slider endTimeSlider;

    @FXML
    private ComboBox presetsButton;

    @FXML
    private GridPane blockedIcons;

    public int startTime;
    public int endTime;

    private UserHolder userHolder;
    private User user;
    private UserDAO userDAO;
    private PresetDAO presetDAO;
    private PresetHolder presetHolder;
    private ArrayList<Preset> presets;


    public MainController() {
        userHolder = UserHolder.getInstance();
        presetHolder = PresetHolder.getInstance();
        user = userHolder.getUser();
        presetDAO = new PresetDAO();
        presets = presetDAO.getUsersPresets(user.getId());
        userDAO = new UserDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> presetNames = new ArrayList<>();

        for (Preset preset : presetDAO.getUsersPresets(user.getId())) {
            presetNames.add(preset.getPresetName());
        }

        /* create preset if none exist */
        if (presetNames.isEmpty()) {
            presetNames.add(presetDAO.generateNewPreset(user.getId()).getPresetName());
        }


        ObservableList<String> presetsList = FXCollections.observableList(presetNames);

        presetsButton.setItems(presetsList);

        presetsButton.getSelectionModel().selectFirst();

        // Display a sound name in combo box
        soundOptionsButton.getSelectionModel().selectFirst();

        // Display a colour in combo box
        colourOptionsButton.getSelectionModel().selectFirst();

        // Initialise start and end time sliders
        startTimeSlider();
        endTimeSlider();

        /* update the blocked list */
        updateBlockList();

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

    

    public void startTimeSlider() {
        // Listen for changes to the slider and update the label
        startTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int totalMinutes = newVal.intValue();  // Convert slider value to int
            this.startTime = totalMinutes;
            int hours = totalMinutes / 60;  // Get full hours
            int minutes = totalMinutes % 60;  // Get remaining minutes

            // Build the formatted time string
            String formattedTime;
            if (totalMinutes == 0) {
                formattedTime = "Now";
            }
            else if (hours > 0) {
                formattedTime = String.format("%d Hour%s %d Minute%s",
                        hours, hours > 1 ? "s" : "",
                        minutes, minutes > 1 ? "s" : "");
            } else {
                formattedTime = String.format("%d Minute%s", minutes, minutes > 1 ? "s" : "");
            }

            startTimeLabel.setText(formattedTime);  // Update the label with the formatted time

            // Calculate percentage of the slider position
            double percentage = newVal.doubleValue() / startTimeSlider.getMax();

            // Set inline CSS for the track color
            String trackColor = String.format("-fx-background-color: linear-gradient(to right, #59ADFF %f%%, white %f%%);", percentage * 100, percentage * 100);
            startTimeSlider.lookup(".track").setStyle(trackColor);
        });
    }

    public void endTimeSlider() {
        endTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int totalMinutes = newVal.intValue();  // Convert slider value to int
            this.endTime = totalMinutes;
            int hours = totalMinutes / 60;  // Get full hours
            int minutes = totalMinutes % 60;  // Get remaining minutes

            // Build the formatted time string
            String formattedTime;
            if (hours > 0) {
                formattedTime = String.format("%d Hour%s %d Minute%s",
                        hours, hours > 1 ? "s" : "",
                        minutes, minutes > 1 ? "s" : "");
            } else {
                formattedTime = String.format("%d Minute%s", minutes, minutes > 1 ? "s" : "");
            }

            endTimeLabel.setText(formattedTime);  // Update the label with the formatted time

            // Calculate percentage of the slider position
            double percentage = newVal.doubleValue() / endTimeSlider.getMax();

            // Set inline CSS for the track color
            String trackColor = String.format("-fx-background-color: linear-gradient(to right, #59ADFF %f%%, white %f%%);", percentage * 100, percentage * 100);
            endTimeSlider.lookup(".track").setStyle(trackColor);
        });
    }

    /**
     * create new preset and open preset edditor view
     */
    public void onAddPresetClick(ActionEvent actionEvent) throws IOException {

        /* create new preset and add it to holder */
        Preset preset = presetDAO.generateNewPreset(user.getId());
        if (preset == null) {
            throw new Error("generated preset is null");
        }
        presetHolder.setPreset(preset);

        Stage stage = (Stage) blockedApplicationPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/blocked-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    /**
     * when clicking preset in dropdown menu
     */
    public void onPresetsButtonClick() {
        updateBlockList();
    }

    /**
     * update the blocked items display
     */
    public void updateBlockList() {
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();

        System.out.println(presetName);

        Preset currentPreset = null;

        for (Preset preset : presetDAO.getUsersPresets(user.getId())) {
            if (Objects.equals(preset.getPresetName(), presetName)) {
                presetHolder.setPreset(preset);
                currentPreset = preset;
                break;
            }
        }

        if (currentPreset == null) {
            return;
        }

        ArrayList<BlockedItem> blockedItems = new ArrayList<>();


        blockedItems.addAll(presetDAO.getPresetWebsite(currentPreset.getPresetID()));
        blockedItems.addAll(presetDAO.getPresetApplication(currentPreset.getPresetID()));

        int column = 0;
        int row = 0;
        int maxColumn = 6;
        int imageSize = 30;

        blockedIcons.getChildren().clear();

        /* default icon */
        String defaultIcon = "/focusApp/images/defaultIcon.png";

        /* this code is currently slow */
        /* maybe to do with the urls */
        for (BlockedItem item : blockedItems) {
            Image img;
            if (item.getIconURI().endsWith("png")) {
                img = new Image(item.getIconURI().toString(), true);
            } else {
                String url = getClass().getResource(defaultIcon).toString();
                img = new Image(url, true);
            }
            ImageView icon = new ImageView(img);
            icon.setFitWidth(imageSize);
            icon.setFitHeight(imageSize);
            blockedIcons.add(icon, column, row);
            column++;
            if (column >= maxColumn) {
                row++;
                column = 0;
            }
        }
    }

    public void onBlockedApplicationsPaneClick(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) blockedApplicationPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/blocked-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onStartButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/timer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Get the controller of the loaded scene
        TimerController timerController = fxmlLoader.getController();

        // Pass the start time and end time to the timer controller
        timerController.initialize(startTime, endTime);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
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

    public void passwordEntered(KeyEvent keyEvent) {
        confirmPasswordButton.setDisable(false);
    }

    public void onXLabelClick(ActionEvent actionEvent) {
        parentalControlToggleButton.setSelected(true);
        blackOutStackPane.setVisible(false);
        turnOffParentalControlsStackPane.setVisible(false);
        denyParentalControlsDisableLabel.setText("");
        parentalControlsPasswordField.clear();
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

    public void onAbortButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(false);
        confirmLogOutStackPane.setVisible(false);
    }

    public void onConfirmLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
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
}
