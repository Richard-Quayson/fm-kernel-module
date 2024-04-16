package controllers;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Files;
import views.FileEditDialogController;
import views.FileOverviewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Files> fileData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("File System Interface");
        
        initRootLayout();
        showFileOverview();
        
    }

    public MainApp() {
        // Add some sample data
        fileData.add(new Files("Hans", "Directory"));
        fileData.add(new Files("Ruth", "Directory"));
        fileData.add(new Files("Heinz", "Directory"));
        fileData.add(new Files("Cornelia", "Directory"));
        fileData.add(new Files("Werner", "Directory"));
        fileData.add(new Files("Lydia", "Directory"));
        fileData.add(new Files("Anna", "Directory"));
        fileData.add(new Files("Stefan", "Directory"));
        fileData.add(new Files("Martin", "Directory"));
    }

    public ObservableList<Files> getFileData() {
        return fileData;
    }
    
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("../views/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFileOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("../views/FileOverview.fxml"));
            AnchorPane fileOverview = (AnchorPane) loader.load();
            
            // Setting the file Overview into the center of the root layout 
            rootLayout.setCenter(fileOverview);
            
            
            // Giving the controller access to the main app
            FileOverviewController controller = loader.getController();
            controller.setMainApp(this);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Opens a dialog to edit details for the specified file. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     * 
     * @param file the file object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showFileEditDialog(Files file) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("../views/FileEditDialog.fxml"));
            
            // Set the controller factory
            loader.setControllerFactory(clazz -> {
                if (clazz == FileEditDialogController.class) {
                    return new FileEditDialogController();
                } else {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit File");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            FileEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setFile(file);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
