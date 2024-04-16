import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        System.out.println();
        System.out.println();

        // TEST: LIST FILES IN DIRECTORY
        // define the folder path to list the files from
        String listFolderPath = "/home/richard/Documents";
        File[] files = listFilesInDirectory(listFolderPath);
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println("File: " + file.getName());
                } else if (file.isDirectory()) {
                    System.out.println("Directory: " + file.getName());
                }
            }
        }
        System.out.println();
        System.out.println();

        // TEST: GET DIRECTORY INFO
        // define the directory path to get information from
        String directoryPath = "/home/richard/Documents";
        String directoryInfo = getDirectoryInfo(directoryPath);
        System.out.println(directoryInfo);
        System.out.println();
        System.out.println();

        // TEST: RENAME FOLDER
        // define the old and new folder paths to be renamed using the kernel module
        String oldFolderPath = "/home/richard/Documents/test-folder";
        String newFolderName = "test-rename-folder";
        renameFolder(oldFolderPath, newFolderName);
        System.out.println();
        System.out.println();

        // TEST: DELETE FOLDER
        // define the folder path to be deleted using the kernel module
        String deleteFolderPath = "/home/richard/Documents/test-rename-folder";
        deleteFolder(deleteFolderPath);
        System.out.println();
        System.out.println();

        // TEST: CREATE FILE
        // define the file path and content to be written to the kernel module
        String createFilePath = "/home/richard/Documents/test-file.txt";
        String createFileContent = "This is a test file created using a kernel module.";
        createFile(createFilePath, createFileContent);
        System.out.println();
        System.out.println();

        // TEST: GET FILE DETAILS
        // define the file path to get details from
        String filePath = "/home/richard/Documents/test-file.txt";
        String fileDetails = getFileDetails(filePath);
        System.out.println(fileDetails);
        System.out.println();
        System.out.println();

        // TEST: RENAME FILE
        // define the old and new file paths to be renamed using the kernel module
        String oldFilePath = "/home/richard/Documents/test-file.txt";
        String newFileName = "test-rename-file.txt";
        renameFile(oldFilePath, newFileName);
        System.out.println();
        System.out.println();

        // TEST: UPDATE FILE
        // define the file path, the new data to append, and the overwrite flag
        String updateFilePath = "/home/richard/Documents/test-file.txt";
        String updateFileData = "This is new data appended to the file using a kernel module.";
        int overwriteFlag = 0;      // 0 for append, 1 for overwrite
        updateFile(updateFilePath, updateFileData, overwriteFlag);
        System.out.println();
        System.out.println();

        // TEST: DELETE FILE
        // define the file path to be deleted using the kernel module
        String deleteFilePath = "/home/richard/Documents/test-file.txt";
        deleteFile(deleteFilePath);
        System.out.println();
        System.out.println();
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

    /**
     * Returns an array of files in the specified directory.
     * 
     * @param folderPath The path of the directory to list the files from.
     * @return An array of files in the directory, or null if the directory does not exist or is not a directory.
     */
    public static File[] listFilesInDirectory(String folderPath) {
        // Create a File object for the given directory path
        File directory = new File(folderPath);

        // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles();
            return files;
        } else {
            // Return null if the path is not a directory or does not exist
            System.out.println("The specified path is not a valid directory: " + folderPath);
            return null;
        }
    }

    /**
     * Gets information about a directory.
     * 
     * @param directoryPath The path of the directory to get information from.
     * @return A string containing the information about the directory.
     */
    public static String getDirectoryInfo(String directoryPath) {
        // Create a File object for the given directory path
        File directory = new File(directoryPath);
        
        // Create a StringBuilder to accumulate the information
        StringBuilder info = new StringBuilder();
        
        if (directory.exists() && directory.isDirectory()) {
            // Get directory name
            String directoryName = directory.getName();
            info.append("Directory Name: ").append(directoryName).append("\n");
            
            // Calculate total size of files within the directory
            long totalSize = calculateTotalSize(directory);
            info.append("Total Size: ").append(totalSize).append(" bytes\n");
            
            // Get last modified date
            long lastModified = directory.lastModified();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastModifiedDate = new Date(lastModified);
            info.append("Date Modified: ").append(dateFormat.format(lastModifiedDate)).append("\n");
            
            // Get directory path
            String directoryPathStr = directory.getAbsolutePath();
            info.append("Directory Path: ").append(directoryPathStr).append("\n");
            
            // Get directory creation date (requires java.nio.file.Files and java.nio.file.attribute.BasicFileAttributes)
            try {
                BasicFileAttributes attrs = Files.readAttributes(directory.toPath(), BasicFileAttributes.class);
                long creationTime = attrs.creationTime().toMillis();
                Date creationDate = new Date(creationTime);
                info.append("Date Created: ").append(dateFormat.format(creationDate)).append("\n");
            } catch (Exception e) {
                info.append("Could not retrieve creation date.\n");
            }
        } else {
            info.append("The specified path is not a valid directory: ").append(directoryPath).append("\n");
        }
        
        return info.toString();
    }

    /**
     * Calculates the total size of all files in a directory recursively.
     * 
     * @param directory The directory to calculate the total size of.
     * @return The total size in bytes.
     */
    private static long calculateTotalSize(File directory) {
        long totalSize = 0;
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    totalSize += file.length();
                } else if (file.isDirectory()) {
                    totalSize += calculateTotalSize(file);
                }
            }
        }
        
        return totalSize;
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

    /**
     * Gets the details of a file or directory.
     * 
     * @param filePath The path of the file or directory.
     * @return A string containing the details of the file or directory.
     */
    public static String getFileDetails(String filePath) {
        // Create a File object for the given file path
        File file = new File(filePath);
        
        // Create a StringBuilder to accumulate the details
        StringBuilder details = new StringBuilder();
        
        if (file.exists()) {
            // Get file name
            String fileName = file.getName();
            details.append("File Name: ").append(fileName).append("\n");
            
            // Determine file type (file or directory)
            if (file.isDirectory()) {
                details.append("File Type: Directory\n");
            } else if (file.isFile()) {
                details.append("File Type: File\n");
            }
            
            // Get file size in bytes
            long fileSize = file.length();
            details.append("File Size: ").append(fileSize).append(" bytes\n");
            
            // Get last modified date
            long lastModified = file.lastModified();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastModifiedDate = new Date(lastModified);
            details.append("Date Modified: ").append(dateFormat.format(lastModifiedDate)).append("\n");
            
            // Get file path
            String filePathStr = file.getAbsolutePath();
            details.append("File Path: ").append(filePathStr).append("\n");
            
            // Get file creation date (requires java.nio.file.Files and java.nio.file.attribute.BasicFileAttributes)
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                long creationTime = attrs.creationTime().toMillis();
                Date creationDate = new Date(creationTime);
                details.append("Date Created: ").append(dateFormat.format(creationDate)).append("\n");
            } catch (Exception e) {
                details.append("Could not retrieve creation date.\n");
            }
        } else {
            details.append("The specified file does not exist: ").append(filePath).append("\n");
        }
        
        return details.toString();
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