import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FMKernelModules {
    public static String CREATE_FOLDER_PROC_FILE = "/proc/create_folder";
    public static String RENAME_FOLDER_PROC_FILE = "/proc/rename_folder";
    public static String DELETE_FOLDER_PROC_FILE = "/proc/delete_folder";
    public static String CREATE_FILE_PROC_FILE = "/proc/create_file";
    public static String RENAME_FILE_PROC_FILE = "/proc/rename_file";
    public static String UPDATE_FILE_PROC_FILE = "/proc/update_file";
    public static String DELETE_FILE_PROC_FILE = "/proc/delete_file";

    // Main method to call the createFile function
    public static void main(String[] args) {
        // TEST: CREATE FOLDER
        // define the folder path to be created using the kernel module
        String createFolderPath = "/home/richard/Documents/test-folder";
        createFolder(createFolderPath);

        // TEST: RENAME FOLDER
        // define the old and new folder paths to be renamed using the kernel module
        String oldFolderPath = "/home/richard/Documents/test-folder";
        String newFolderName = "test-rename-folder";
        renameFolder(oldFolderPath, newFolderName);

        // TEST: DELETE FOLDER
        // define the folder path to be deleted using the kernel module
        String deleteFolderPath = "/home/richard/Documents/test-rename-folder";
        deleteFolder(deleteFolderPath);

        // TEST: CREATE FILE
        // define the file path and content to be written to the kernel module
        String createFilePath = "/home/richard/Documents/test-file.txt";
        String createFileContent = "This is a test file created using a kernel module.";
        createFile(createFilePath, createFileContent);

        // TEST: RENAME FILE
        // define the old and new file paths to be renamed using the kernel module
        String oldFilePath = "/home/richard/Documents/test-file.txt";
        String newFileName = "test-rename-file.txt";
        renameFile(oldFilePath, newFileName);

        // TEST: UPDATE FILE
        // define the file path, the new data to append, and the overwrite flag
        String updateFilePath = "/home/richard/Documents/test-file.txt";
        String updateFileData = "This is new data appended to the file using a kernel module.";
        int overwriteFlag = 0;      // 0 for append, 1 for overwrite
        updateFile(updateFilePath, updateFileData, overwriteFlag);

        // TEST: DELETE FILE
        // define the file path to be deleted using the kernel module
        String deleteFilePath = "/home/richard/Documents/test-file.txt";
        deleteFile(deleteFilePath);
    }

    // Method to request the creation of a folder using the kernel module
    public static void createFolder(String folderPath) {
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREATE_FOLDER_PROC_FILE))) {
            // Write the folder path to the /proc/create_folder file
            writer.write(folderPath);
            System.out.println("Requested the creation of folder: " + folderPath);
        } catch (IOException e) {
            System.err.println("An error occurred while requesting the creation of the folder: " + e.getMessage());
        }
    }

    // Method to request the renaming of a folder using the kernel module
    public static void renameFolder(String srcFolderPath, String newName) {
        
        // Prepare the input data by combining srcFolderPath and newName with a newline separator
        String inputData = srcFolderPath + "\n" + newName + "\n";
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RENAME_FOLDER_PROC_FILE))) {
            // Write the input data to the /proc/rename_folder file
            writer.write(inputData);
            System.out.println("Requested the renaming of folder from '" + srcFolderPath + "' to '" + newName + "'");
        } catch (IOException e) {
            System.err.println("An error occurred while trying to rename the folder: " + e.getMessage());
        }
    }

    // Method to request the deletion of a folder using the kernel module
    public static void deleteFolder(String folderPath) {
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELETE_FOLDER_PROC_FILE))) {
            // Write the folder path you want to delete to the /proc/delete_folder file
            writer.write(folderPath);
            System.out.println("Requested deletion of folder: " + folderPath);
        } catch (IOException e) {
            System.err.println("An error occurred while trying to delete the folder: " + e.getMessage());
        }
    }
    
    // Method to write file path and content to the kernel module
    public static void createFile(String filePath, String content) {
        
        // Combine the file path and content into one string, separated by a newline character
        String inputData = filePath + "\n" + content;
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREATE_FILE_PROC_FILE))) {
            writer.write(inputData);
            System.out.println("Data written to " + CREATE_FILE_PROC_FILE);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to " + CREATE_FILE_PROC_FILE + ": " + e.getMessage());
        }
    }

    // Method to request the renaming of a file using the kernel module
    public static void renameFile(String currentPath, String newName) {
        
        // Create the buffer by combining current path and new name with a newline separator
        String buffer = currentPath + "\n" + newName + "\n";
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RENAME_FILE_PROC_FILE))) {
            // Write the buffer to the /proc/rename_file file
            writer.write(buffer);
            System.out.println("Successfully sent rename request: " + currentPath + " -> " + newName);
        } catch (IOException e) {
            if (e instanceof java.nio.file.NoSuchFileException) {
                System.err.println("Error: Cannot open " + RENAME_FILE_PROC_FILE + ". The proc file may not exist.");
            } else if (e instanceof java.nio.file.AccessDeniedException) {
                System.err.println("Error: Permission denied. Make sure to run the script with sufficient permissions.");
            } else {
                System.err.println("An error occurred while renaming the file: " + e.getMessage());
            }
        }
    }

    // Method to request updating a file using the kernel module
    public static void updateFile(String filePath, String newData, int overwriteFlag) {
        
        // Prepare the input data in the format: file_path | new_data | overwrite_flag
        String inputData = filePath + "|" + newData + "|" + overwriteFlag;
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UPDATE_FILE_PROC_FILE))) {
            // Write the input data to the /proc/update_file file
            writer.write(inputData);
            System.out.println("Appended line to " + filePath + " using kernel module.");
        } catch (IOException e) {
            System.err.println("An error occurred while updating the file: " + e.getMessage());
        }
    }

    // Method to request the deletion of a file using the kernel module
    public static void deleteFile(String filePath) {
        
        // Try-with-resources to handle the file writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELETE_FILE_PROC_FILE))) {
            // Write the file path you want to delete to the /proc/delete_file file
            writer.write(filePath);
            System.out.println("Requested deletion of file: " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while trying to delete the file: " + e.getMessage());
        }
    }
}