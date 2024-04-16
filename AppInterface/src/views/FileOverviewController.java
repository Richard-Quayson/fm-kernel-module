package views;

import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import controllers.MainApp;
import controllers.util.DateUtil;
import model.Files;

public class FileOverviewController {
	
	/**
	 * All fields and methods where the fxml file needs access 
	 * must be annotated with @FXML. Actually, only if they are 
	 * private, but it’s better to have them private and mark them 
	 * with the annotation!
	 */
	@FXML
	private TableView<Files> fileTable;
	
	@FXML
	private TableColumn<Files, String> filenameColumn;
	
	@FXML
	private TableColumn<Files, String> typeColumn;
	
	@FXML
	private Label filenameLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label filepathLabel;
	@FXML
	private Label DateCreatedLabel;
	@FXML
	private Label DateModifiedLabel;
	@FXML
	private Label sizeLabel;
	
	// Reference to the main application
	private MainApp mainApp;
	
	/**
	 * The constructor
	 * The constructor is called before the initialize() method.
	 */
public FileOverviewController() {
	
}

/**
 * Initializes the controller class. This method is automatically called
 * after the fxml file has been loaded.
 */
@FXML
private void initialize() {
    // Initialize the file table with the two columns.
    filenameColumn.setCellValueFactory(new PropertyValueFactory<Files, String>("filename"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<Files, String>("type"));
    
    // Clear file details
    showfileDetails(null);
    
    //Listen for selection changes and show the file the person details when changed
    fileTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showfileDetails(newValue));


}


/**
 * Is called by the main application to give a reference back to itself.
 * 
 * @param mainApp
 */
public void setMainApp(MainApp mainApp) {
    this.mainApp = mainApp;

    // Add observable list data to the table
    fileTable.setItems(mainApp.getFileData());
}


/**
 * Fills all text fields to show details about the file.
 * If the specified person is null, all text fields are cleared.
 * 
 * @param person the person or null
 */
private void showfileDetails(Files file) {
    if (file != null) {
        // Fill the labels with info from the person object.
        filenameLabel.setText(file.getFilename());
        typeLabel.setText(file.gettype());
        filepathLabel.setText(file.getfilepath());
        sizeLabel.setText(Float.toString(file.getsize()));
        DateCreatedLabel.setText(DateUtil.format(file.getCreated()));
        DateModifiedLabel.setText(DateUtil.format(file.getModified()));
        

    } else {
        // File is null, remove all the text.
    	filenameLabel.setText("");
    	typeLabel.setText("");
    	filepathLabel.setText("");
    	sizeLabel.setText("");
    	DateCreatedLabel.setText("");
    	DateModifiedLabel.setText("");

    }
}

/*
 * Delete Method
  * Called when the user clicks on the delete button.
 */
@FXML
private void handleDeleteFile() {
    int selectedIndex = fileTable.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
        fileTable.getItems().remove(selectedIndex);
    } else {
        // Nothing selected.
        Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No File Selected");
        alert.setContentText("Please select a File in the table.");

        alert.showAndWait();
    }
}



/**
 * Called when the user clicks the new button. Opens a dialog to edit
 * details for a new file.
 */
@FXML
private void handleNewFile() {
    Files tempfile = new Files();
    boolean okClicked = mainApp.showFileEditDialog(tempfile);
    if (okClicked) {
        mainApp.getFileData().add(tempfile);
    }
}

/**
 * Called when the user clicks the edit button. Opens a dialog to edit
 * details for the selected person.
 */
@FXML
private void handleEditFile() {
    Files selectedfile = fileTable.getSelectionModel().getSelectedItem();
    if (selectedfile != null) {
        boolean okClicked = mainApp.showFileEditDialog(selectedfile);
        if (okClicked) {
            showfileDetails(selectedfile);
        }

    } else {
        // Nothing selected.
        Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No File Selected");
        alert.setContentText("Please select a file in the table.");

        alert.showAndWait();
    }
}

	
}
