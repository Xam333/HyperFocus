package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.database.UserDAO;
import focusApp.models.block.BlockedItem;
import focusApp.models.colour.ColourControl;
import focusApp.models.colour.ColourPaletteKeys;
import focusApp.models.colour.UserConfig;
import focusApp.models.preset.PresetHolder;
import focusApp.models.timer.Notification;
import focusApp.models.user.User;
import focusApp.models.user.UserHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import focusApp.database.Preset;
import focusApp.database.PresetDAO;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.ToggleSwitch;

import javax.imageio.ImageIO;

public class MainController implements Initializable {
    public Button startButton;
    public Pane blockedApplicationPane;
    public StackPane menuStackPane;
    public VBox parentalControlsSection;
    public VBox soundSettingsSection;
    public VBox colourSettingsSection;
    public Slider volumeSlider;
    public ComboBox soundOptionsButton;
    public ToggleSwitch parentalControlToggleButton;
    public PasswordField parentalControlsPasswordField;
    public Button confirmPasswordButtonParentalControls;
    public StackPane turnOffParentalControlsStackPane;
    public StackPane blackOutStackPane;
    public Label denyParentalControlsDisableLabel;
    public VBox accountInformationSection;
    public Label totalTimeFocused;
    public TextField userNameTextField;
    public Button editUserNameButton;
    public Button editPasswordButton;
    public PasswordField passwordTextField;
    public Label accountError;
    public Button confirmButton;
    public StackPane confirmLogOutStackPane;




    @FXML
    private ComboBox presetsButton;

    @FXML
    private GridPane blockedIcons;

    @FXML
    private PasswordField passwordAuth;

    @FXML
    private Label denyPasswordAuth;

    @FXML
    private StackPane passwordAuthStackPane;

    @FXML
    private Button confirmPasswordButtonPasswordAuth;

    @FXML
    private Label presetError;

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

    private Image editIcon;
    private Image tickIcon;


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

        // Add listener to ComboBox editor property
        presetsButton.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            // Update newPresetName whenever the ComboBox editor text changes
            newPresetName = newVal;
        });

        // Initialise start and end time sliders
        setSlider(OffsetSlider, OffsetLabel, TimeID.StartTime);
        setSlider(DurationSlider, DurationLabel, TimeID.EndTime);

        /* update the blocked list */
        // Get preset name
        String presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        try {
            updateBlockList(presetName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }

        // Check if there is a saved value for the alarm, if not display alarm1.
        if (SelectedSound == null){
            soundOptionsButton.getSelectionModel().selectFirst();
        } else {
            soundOptionsButton.getSelectionModel().select(SelectedSound);
        }
        // Check if there is a value for the volume.
        if (SelectedVolume != null){
            volumeSlider.setValue(SelectedVolume);
        }

        // Load a new instance of the colour control class.
        ColourPalette = new ColourControl();
        setColourPalette();


        // Only show enter passcode if parental controls is being turned off
        parentalControlToggleButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (!isNowSelected) {
                // Show password dialog
                blackOutStackPane.setVisible(true);
                turnOffParentalControlsStackPane.setVisible(true);
            } else{
                blackOutStackPane.setVisible(false);
                turnOffParentalControlsStackPane.setVisible(false);
                /* sets parental lock */
                user.setParentalLock(true);
            }
        });

        /* update user name text */
        userNameTextField.setText(user.getUserName());
        /* update total focus time */
        long time = user.getTotalFocusTime();
        totalTimeFocused.setText(String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60));
        /* set the parental lock button */
        parentalControlToggleButton.setSelected(user.getParentalLcok());
    }
    private static Object SelectedSound;
    private static Double SelectedVolume;
    private static ColourControl ColourPalette;

    @FXML
    private ColorPicker PrimaryColour;
    @FXML
    private ColorPicker SecondaryColour;
    @FXML
    private ColorPicker TertiaryColour;
    @FXML
    private ColorPicker BackgroundColour;
    private void setColourPalette(){
        HashMap<ColourPaletteKeys, ColorPicker> PaletteFormat =  getColourPaletteFormat();
        for(ColourPaletteKeys PaletteKey : ColourPaletteKeys.values()){
            if (PaletteFormat.containsKey(PaletteKey) && ColourPalette.getCurrentColourPalette().containsKey(PaletteKey)){
                Color LoadedColour = ColourPalette.getCurrentColourPalette().get(PaletteKey);
                PaletteFormat.get(PaletteKey).setValue(LoadedColour);
            }
        }
    }
    @FXML
    private void onColourPicker() {
        ColourPalette.LoadColourPalette(getAllColours());
        setColourPalette();
    }
    @FXML
    private void onDefaultButton(){
        ColourPalette.LoadDefaultColourPalette();
        setColourPalette();
    }
    private HashMap<ColourPaletteKeys, ColorPicker> getColourPaletteFormat(){
        return new HashMap<>() {{
            put(ColourPaletteKeys.Primary, PrimaryColour);
            put(ColourPaletteKeys.Secondary, SecondaryColour);
            put(ColourPaletteKeys.Tertiary, TertiaryColour);
            put(ColourPaletteKeys.Background, BackgroundColour);
        }};
    }
    private HashMap<ColourPaletteKeys, Color> getAllColours(){
        return new HashMap<>(){{
            put(ColourPaletteKeys.Primary, PrimaryColour.getValue());
            put(ColourPaletteKeys.Secondary, SecondaryColour.getValue());
            put(ColourPaletteKeys.Tertiary, TertiaryColour.getValue());
            put(ColourPaletteKeys.Background, BackgroundColour.getValue());
        }};
    }

    private enum TimeID{
        StartTime, EndTime
    }

    @FXML
    private Label OffsetLabel;

    @FXML
    private Label DurationLabel;

    @FXML
    private Slider OffsetSlider;

    @FXML
    private Slider DurationSlider;

    public void setSlider(Slider TimerSlider, Label SliderLabel, TimeID TID) {
        // Listen for changes to the slider and update the label
        TimerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int totalMinutes = newVal.intValue();  // Convert slider value to int

            switch (TID){
                case StartTime -> startTime = totalMinutes;
                case EndTime -> endTime = totalMinutes;
            }

            int hours = totalMinutes / 60;  // Get full hours
            int minutes = totalMinutes % 60;  // Get remaining minutes

            // Build the formatted time string
            String formattedTime;
            if (totalMinutes == 0 && TimerSlider == OffsetSlider) {
                formattedTime = "Now";
            }
            else if (hours > 0) {
                formattedTime = String.format("%d Hour%s %d Minute%s",
                        hours, hours > 1 ? "s" : "",
                        minutes, minutes > 1 ? "s" : "");
            } else {
                formattedTime = String.format("%d Minute%s", minutes, minutes > 1 ? "s" : "");
            }

            SliderLabel.setText(formattedTime);  // Update the label with the formatted time

            // Calculate percentage of the slider position
            double percentage = newVal.doubleValue() / TimerSlider.getMax();

            // Set inline CSS for the track color
            String[] CalledColours = ColourPalette.getHexFromPalette(ColourPaletteKeys.Primary, ColourPaletteKeys.Background);
            String trackColor = String.format("-fx-background-color: linear-gradient(to right, " + CalledColours[0] + " %f%%, " + CalledColours[1] + " %f%%);", percentage * 100, percentage * 100);
            TimerSlider.lookup(".track").setStyle(trackColor);
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
    public void onPresetsButtonClick() throws IOException, ImageReadException {
        // Get preset name
        String presetName = "";
        if (presetsButton.getSelectionModel().getSelectedItem() != null) {
            presetName = presetsButton.getSelectionModel().getSelectedItem().toString();
        }
        if (!presetsButton.isEditable()) {
            originalPresetName = presetName;
        }

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
            if (saveEditedPresetName()) {
                // Revert to edit icon
                setButtonGraphic(editButton, editIcon, 30, 30);
            }
        } else {
            // Enable editing
            originalPresetName = presetsButton.getValue().toString();
            presetsButton.setEditable(true);
            presetsButton.requestFocus();
            // Change to tick icon
            setButtonGraphic(editButton, tickIcon, 30, 30);

            ArrayList<String> presetNames = new ArrayList<>();
            presetNames.add(presetsButton.getSelectionModel().getSelectedItem().toString());

            ObservableList<String> presetsList = FXCollections.observableList(presetNames);
            presetsButton.setItems(presetsList);
        }
    }

    public boolean saveEditedPresetName() {
        // Check if ComboBox is editable
        if (presetsButton.isEditable()) {
            // Update the database with the edited preset name
            boolean res = presetDAO.editPresetName(user.getId(), originalPresetName, newPresetName);

            /* check if new name was unique */
            if (res) {
                // Get the index of the currently selected item
                int selectedIndex = presetsButton.getSelectionModel().getSelectedIndex();

                // Disable editing in the ComboBox
                presetsButton.setEditable(false);

                // Reload presets
                loadPresets();
                int index = presetsButton.getItems().indexOf(newPresetName);
                presetsButton.getSelectionModel().select(index);

                presetError.setManaged(false);
                presetError.setText("");
                return true;
            } else {
                presetError.setManaged(true);
                presetError.setText("Preset name must be unique");
            }
        }
        return false;
    }

    @FXML
    private void onComboBoxKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Save changes when Enter is pressed
            if (saveEditedPresetName()) {
                // Revert to edit icon
                setButtonGraphic(editButton, editIcon, 30, 30);
            }
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
    public void updateBlockList(String presetName) throws IOException, ImageReadException {
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
            } else if (item.getIconURI().endsWith("ico")) {
                // Download the .ico file
                File icoFile = File.createTempFile("favicon", ".ico");
                FileUtils.copyURLToFile(new URL(item.getIconURI()), icoFile);

                // Convert the .ico file to a BufferedImage
                BufferedImage bufferedImage = Imaging.getBufferedImage(icoFile);

                // Create a temporary file for the converted image
                File tempFile = File.createTempFile("temp_image", ".png");
                tempFile.deleteOnExit();
                ImageIO.write(bufferedImage, "png", tempFile);

                // Load the image using the temporary file path
                img = new Image(tempFile.toURI().toString(), true);  // true for background loading
            }else {
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
    public void onBlockedApplicationsPaneClick() throws IOException {
        Stage stage = (Stage) blockedApplicationPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/blocked-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    /**
     * Starts timer with specified start and end times
     */
    public void onStartButtonClick() throws IOException {
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/timer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Get the controller of the loaded scene
        TimerController timerController = fxmlLoader.getController();

        // Pass the start time and end time to the timer controller and the Notification alarm.
        timerController.initialize(startTime, endTime, new Notification(soundOptionsButton.getValue().toString(), (float) volumeSlider.getValue()));

        // Remember the Alarm and the volume level that was chosen.
        SelectedSound = soundOptionsButton.getSelectionModel().getSelectedItem();
        SelectedVolume = volumeSlider.getValue();

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    private boolean SideMenuOpen = false;
    /**
     * Toggle side menu (visible or not visible)
     */
    @FXML
    private void toggleMenu() {
        // Open and Close the menu.
        UpdateSideMenu(!SideMenuOpen);
    }
    private void UpdateSideMenu(boolean Control){
        menuStackPane.setVisible(Control);
        SideMenuOpen = Control;
    }

    /**
     * Stores what side menu item is open.
     */
    private MenuAttribute MenuItemOpened = null;
    private enum MenuAttribute{
        AccountInformation, ParentalControls, ColourSettings, SoundSettings
    }
    private void MenuControl(VBox Section, boolean Visible){
        Section.setManaged(Visible);
        Section.setVisible(Visible);
    }
    private void MenuAttributeControl(MenuAttribute Attribute){

        MenuControl(accountInformationSection, false);
        MenuControl(parentalControlsSection, false);
        MenuControl(colourSettingsSection, false);
        MenuControl(soundSettingsSection, false);

        if (MenuItemOpened == Attribute){
            MenuItemOpened = null;
            return;
        }

        MenuItemOpened = Attribute;

        switch (Attribute) {
            case AccountInformation -> MenuControl(accountInformationSection, true);
            case ParentalControls -> MenuControl(parentalControlsSection, true);
            case ColourSettings -> MenuControl(colourSettingsSection, true);
            case SoundSettings -> MenuControl(soundSettingsSection, true);
        }
    }
    public void onAccountButtonClick(){MenuAttributeControl(MenuAttribute.AccountInformation);}
    public void onParentalControlsButtonClick(){MenuAttributeControl(MenuAttribute.ParentalControls);}
    public void onColourSettingsButtonClick(){MenuAttributeControl(MenuAttribute.ColourSettings);}
    public void onSoundSettingsButtonClick(){MenuAttributeControl(MenuAttribute.SoundSettings);}

    public void passwordEnteredParentalControls(KeyEvent keyEvent) {
        confirmPasswordButtonParentalControls.setDisable(false);
    }

    public void passwordEnteredPasswordAuth(KeyEvent keyEvent) {
        confirmPasswordButtonPasswordAuth.setDisable(false);
    }

    public void onXLabelClickPasswordAuth() {
        blackOutStackPane.setVisible(false);
        passwordAuthStackPane.setVisible(false);
        denyPasswordAuth.setText("");
        passwordAuth.clear();
    }

    public void onXLabelClickParentalControls() {
        parentalControlToggleButton.setSelected(true);
        blackOutStackPane.setVisible(false);
        turnOffParentalControlsStackPane.setVisible(false);
        denyParentalControlsDisableLabel.setText("");
        parentalControlsPasswordField.clear();
    }



    public void onEditUserNameButtonClick() {
        if (userNameTextField.isEditable()) {
            if (userDAO.updateName(user.getId(), userNameTextField.getText())) {
                user.setUserName(userNameTextField.getText());
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

    public void onEditPasswordButtonClick() {
        if (passwordTextField.isEditable()) {
            if (passwordTextField.getText().isEmpty()) {
                accountError.setText("* Password must not be blank *");
                accountError.setManaged(true);
                return;
            }
            blackOutStackPane.setVisible(true);
            passwordAuthStackPane.setVisible(true);
            accountError.setText("");
            accountError.setManaged(false);
        } else {
            passwordTextField.clear();
            passwordTextField.setEditable(true);
            editPasswordButton.setText("SAVE");
        }
    }

    private void ShowPane(boolean Display){
        blackOutStackPane.setVisible(Display);
        confirmLogOutStackPane.setVisible(Display);
    }
    public void onLogOutButton() { ShowPane(true); }
    public void onAbortButtonClick(){ ShowPane(false); }

    public void onConfirmLogOutButtonClick() throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    public void onPasswordAuthenticationCheck() {
        // Check if password is correct
        User login_user = userDAO.login(user.getUserName(), passwordAuth.getText());

        /* if user != null then login successful and user class returned */
        if (!Objects.equals(login_user, null)){
            /* update password */
            if (userDAO.changePassword(user.getId(), passwordTextField.getText())) {
                blackOutStackPane.setVisible(false);
                passwordAuthStackPane.setVisible(false);
                denyPasswordAuth.setText("");
                passwordAuth.clear();
                passwordTextField.setText("Place holder");
                editPasswordButton.setText("EDIT");
                passwordTextField.setEditable(false);
            } else {
                denyPasswordAuth.setText("* Something went wrong when updating password *");
            }
        } else {
            denyPasswordAuth.setText("* Incorrect password. *");
        }
    }

    public void onConfirmParentalControlsButtonClick() {
        // Check if password is correct
        User login_user = userDAO.login(user.getUserName(), parentalControlsPasswordField.getText());

        /* if user != null then login successful and user class returned */
        if (!Objects.equals(login_user, null)){
            parentalControlToggleButton.setSelected(false);

            blackOutStackPane.setVisible(false);
            turnOffParentalControlsStackPane.setVisible(false);
            denyParentalControlsDisableLabel.setText("");
            parentalControlsPasswordField.clear();
            /* disable the parental lock */
            user.setParentalLock(false);
        } else {
            denyParentalControlsDisableLabel.setText("* Incorrect password. *");
        }
    }
}
