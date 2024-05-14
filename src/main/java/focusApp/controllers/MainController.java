package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.database.IBlockedItemDAO;
import focusApp.database.MockedBlockedItemDAO;
import focusApp.models.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import java.util.Objects;
import java.util.ResourceBundle;

import focusApp.database.Preset;
import focusApp.database.PresetDAO;

import java.util.ArrayList;

public class MainController implements Initializable {
    public Button startButton;
    public Pane blockedApplicationPane;
    public StackPane menuStackPane;
    public Button accountButton;
    public Button parentalControlsButton;
    public Button colourSettingsButton;
    public Button soundSettingsButton;
    private Boolean isMenuOpen = false;

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
    private PresetDAO presetDAO;
    private PresetHolder presetHolder;
    private ArrayList<Preset> presets;


    public MainController() {
        userHolder = UserHolder.getInstance();
        presetHolder = PresetHolder.getInstance();
        user = userHolder.getUser();
        presetDAO = new PresetDAO();
        presets = presetDAO.getUsersPresets(user.getId());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> presetNames = new ArrayList<>();

        for (Preset preset : presetDAO.getUsersPresets(user.getId())) {
            presetNames.add(preset.getPresetName());
        }

        /* create preset if none exist */
        if (presetNames.isEmpty()) {
            presetDAO.addPreset(user.getId(), "Preset");
        }

        presetNames.add("New Preset +");

        ObservableList<String> presetsList = FXCollections.observableList(presetNames);

        presetsButton.setItems(presetsList);

        presetsButton.getSelectionModel().selectFirst();

        // Initialise start and end time sliders
        startTimeSlider();
        endTimeSlider();

        /* update the blocked list */
        // Get preset name
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        System.out.println(presetName);
        updateBlockList(presetName);
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

    /**
     * when clicking preset in dropdown menu
     */
    public void onPresetsButtonClick() {
        // Get preset name
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        System.out.println(presetName);

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

            // Get the updated list of presets from the database
            ArrayList<Preset> userPresets = presetDAO.getUsersPresets(user.getId());

            // Clear the ComboBox items
            presetsButton.getItems().clear();

            // Populate the ComboBox with the updated list of presets
            for (Preset preset : userPresets) {
                presetsButton.getItems().add(preset.getPresetName());
            }

            // Add "New Preset +" back to the end of the ComboBox items
            presetsButton.getItems().add("New Preset +");

            // Select the first preset by default or perform any other desired action
            presetsButton.getSelectionModel().selectFirst();
        }
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
        Stage stage = (Stage) accountButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/account-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) parentalControlsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) colourSettingsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) soundSettingsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

}
