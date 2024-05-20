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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import java.util.Objects;
import java.util.ResourceBundle;

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

    private String originalPresetName;
    private String newPresetName;

    @FXML
    private Button editButton;

    @FXML
    private ImageView editImage;

    private Image editIcon;
    private Image tickIcon;
    private boolean isEditing = false;


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
        // Load images
        editIcon = new Image(getClass().getResourceAsStream("/focusApp/images/editIcon.png"));
        tickIcon = new Image(getClass().getResourceAsStream("/focusApp/images/tickIcon.png"));

        // Set initial image for edit button
        setButtonGraphic(editButton, editIcon, 30, 30);

        loadPresets();
        presetsButton.getSelectionModel().selectFirst();
        originalPresetName = presetsButton.getValue().toString();

        // Add listener to ComboBox editor property
        presetsButton.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            // Update newPresetName whenever the ComboBox editor text changes
            newPresetName = newVal;
        });

        // Initialise start and end time sliders
        startTimeSlider();
        endTimeSlider();

        /* update the blocked list */
        // Get preset name
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        updateBlockList(presetName);

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

        /* update user name text */
        userNameTextField.setText(user.getUserName());
        /* update total focus time */
        totalTimeFocused.setText(Integer.toString(user.getTotalFocusTime()));
    }



    /**
     * Initialises start time slider and listens for updates
     */
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

    /**
     * Initialises end time slider and listens for updates
     */
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

    public void loadPresets() {
        ArrayList<String> presetNames = new ArrayList<>();

        for (Preset preset : presetDAO.getUsersPresets(user.getId())) {
            presetNames.add(preset.getPresetName());
        }

        /* create preset if none exist */
        if (presetNames.isEmpty()) {
            presetDAO.addPreset(user.getId(), "Preset");
            for (Preset preset : presetDAO.getUsersPresets(user.getId())) {
                presetNames.add(preset.getPresetName());
            }
        }

        presetNames.add("New Preset +");

        ObservableList<String> presetsList = FXCollections.observableList(presetNames);

        presetsButton.setItems(presetsList);
    }

    /**
     * when clicking preset in dropdown menu
     */
    public void onPresetsButtonClick() {
        // Get preset name
        String presetName = "";
        if (presetsButton.getSelectionModel().getSelectedItem() != null) {
            presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        }
        originalPresetName = presetName;

        // Check if new preset or existing
        if (presetName.equals("New Preset +")) {
            String newName = "Preset";
            Preset newPreset = presetDAO.addPreset(user.getId(), newName);    // Update name of preset

            if (newPreset != null) {
                // Get the current list of preset names
                ObservableList<String> presetNames = presetsButton.getItems();

                // Remove "New Preset +" from the list
                presetNames.remove("New Preset +");

                // Add the new preset name to the list
                presetNames.add(newPreset.getPresetName());

                // Sort the list alphabetically (optional)
                FXCollections.sort(presetNames);

                // Add "New Preset +" back to the end of the list
                presetNames.add("New Preset +");

                // Update the ComboBox items
                presetsButton.setItems(presetNames);

                // Select the newly created preset
                presetsButton.getSelectionModel().select(newPreset.getPresetName());
            }
        }

        // Update block list
        updateBlockList(presetName);
    }

    public void onDeleteButtonClick() {
        // Get the currently selected preset name
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();

        // Ensure the selected preset is not "New Preset +"
        if (!presetName.equals("New Preset +")) {
            // Delete the selected preset from the database
            presetDAO.deletePresetByName(user.getId(), presetName);

            // Remove the selected preset from the ComboBox items
            loadPresets();
            presetsButton.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onEditButtonClick() {
        if (presetsButton.isEditable()) {
            // Save changes
            saveEditedPresetName();
            // Revert to edit icon
            setButtonGraphic(editButton, editIcon, 30, 30);
        } else {
            // Enable editing
            presetsButton.setEditable(true);
            presetsButton.requestFocus();
            // Change to tick icon
            setButtonGraphic(editButton, tickIcon, 30, 30);
        }
    }

    public void saveEditedPresetName() {
        // Check if ComboBox is editable
        if (presetsButton.isEditable()) {
            // Get the index of the currently selected item
            int selectedIndex = presetsButton.getSelectionModel().getSelectedIndex();

            // Disable editing in the ComboBox
            presetsButton.setEditable(false);

            // Update the database with the edited preset name
            presetDAO.editPresetName(user.getId(), originalPresetName, newPresetName);

            // Reload presets
            loadPresets();
            presetsButton.getSelectionModel().select(selectedIndex);
        }
    }

    @FXML
    private void onComboBoxKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Save changes when Enter is pressed
            saveEditedPresetName();
            // Revert to edit icon
            setButtonGraphic(editButton, editIcon, 30, 30);
        }
    }

    private void setButtonGraphic(Button button, Image image, double width, double height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        button.setGraphic(imageView);
    }


    /**
     * update the blocked items display
     */
    public void updateBlockList(String presetName) {
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
        int imageSize = 25;

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

            // Create a StackPane to add padding around the image
            StackPane stackPane = new StackPane();
            stackPane.setPadding(new Insets(5)); // You can adjust the value as needed
            stackPane.getChildren().add(icon);

            blockedIcons.add(stackPane, column, row);
            column++;
            if (column >= maxColumn) {
                row++;
                column = 0;
            }
        }
    }

    /**
     * Opens blocked applications page
     */
    public void onBlockedApplicationsPaneClick(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) blockedApplicationPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/blocked-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    /**
     * Starts timer with specified start and end times
     */
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

    /**
     * Toggle side menu (visible or not visible)
     */
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
        User login_user = userDAO.login(user.getUserName(), parentalControlsPasswordField.getText());

        /* if user != null then login successful and user class returned */
        if (!Objects.equals(login_user, null)){
            user.setParentalLock(true);
            parentalControlToggleButton.setSelected(false);
            blackOutStackPane.setVisible(false);
            turnOffParentalControlsStackPane.setVisible(false);
            denyParentalControlsDisableLabel.setText("");
            parentalControlsPasswordField.clear();
        } else {
            user.setParentalLock(false);
            denyParentalControlsDisableLabel.setText("* Incorrect password. *");
        }
    }
}
