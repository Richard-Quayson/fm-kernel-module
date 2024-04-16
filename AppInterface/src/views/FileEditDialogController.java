package views;

import controllers.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
//import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Files;
//import controllers.MainApp;
//import controllers.util.DateUtil;

/**
 * Dialog to edit details of a File.
 * 
 * @author Thomas Quarshie
 */
public class FileEditDialogController {
	

    @FXML
    private TextField filenameField;
    
    @FXML
    private TextField typeField;
    
 
    @FXML
    private TextField filepathField;
    
    @FXML
    private TextField DateCreatedField;
    
    @FXML
    private TextField DateModifiedField;
    
    @FXML
    private TextField sizeField;



    private Stage dialogStage;
    private Files file;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setFile(Files file) {
        this.file = file;

        filenameField.setText(file.getFilename());
        typeField.setText(file.gettype());
        filepathField.setText(file.getfilepath());
        DateCreatedField.setText(DateUtil.format(file.getCreated()));
        DateModifiedField.setText(DateUtil.format(file.getModified()));
        sizeField.setText(Float.toString(file.getsize()));

        

    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            file.setFilename(filenameField.getText());
            file.setfilepath(filepathField.getText());
            file.settype(typeField.getText());
            file.setCreated(DateUtil.parse(DateCreatedField.getText()));
            file.setModified(DateUtil.parse(DateModifiedField.getText()));
            file.setsize(Float.parseFloat(sizeField.getText()));


            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (filenameField.getText() == null || filenameField.getText().length() == 0) {
            errorMessage += "No valid filename!\n"; 
        }
        if (filepathField.getText() == null || filepathField.getText().length() == 0) {
            errorMessage += "No valid File Path!\n"; 
        }
        if (DateCreatedField.getText() == null || DateCreatedField.getText().length() == 0) {
            errorMessage += "No valid Date!\n";
        } else {
            if (!DateUtil.validDateTime(DateCreatedField.getText())) {
                errorMessage += "No valid birthday. Use the format dd.mm.yyyy!\n";
            }
        }
        if (DateModifiedField.getText() == null || DateModifiedField.getText().length() == 0) {
            errorMessage += "No valid Date!\n";
        } else {
            if (!DateUtil.validDateTime(DateModifiedField.getText())) {
                errorMessage += "No valid Date. Use the format dd.mm.yyyy!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;
        }
    }
    
}
