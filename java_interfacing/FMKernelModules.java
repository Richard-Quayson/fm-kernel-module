import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * FMKernelModules
 * this class provides methods to interact with the kernel modules for file and folder operations.
 * It includes methods to create, rename, and delete folders, create, rename, update, and delete files,
 * list files in a directory, get directory information, get file details, and read file content.
 * @authors      Richard Quayson & Thomas Quarshie
 */

public class FMKernelModules {
    // define the paths to the proc files for the kernel module
    public static String CREATE_FOLDER_PROC_FILE = "/proc/create_folder";
    public static String RENAME_FOLDER_PROC_FILE = "/proc/rename_folder";
    public static String DELETE_FOLDER_PROC_FILE = "/proc/delete_folder";
    public static String CREATE_FILE_PROC_FILE = "/proc/create_file";
    public static String RENAME_FILE_PROC_FILE = "/proc/rename_file";
    public static String UPDATE_FILE_PROC_FILE = "/proc/update_file";
    public static String DELETE_FILE_PROC_FILE = "/proc/delete_file";


    /**
     * createFolder
     * this method requests the creation of a folder using the kernel module.
     * 
     * @param folderPath the path of the folder to create.
     * @return void
     */

    public static void createFolder(String folderPath) {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREATE_FOLDER_PROC_FILE))) {
            // write the folder path to the /proc/create_folder file
            writer.write(folderPath);
            System.out.println("Requested the creation of folder: " + folderPath);
        } catch (IOException e) {
            System.err.println("An error occurred while requesting the creation of the folder: " + e.getMessage());
        }
    }


    /**
     * listFilesInDirectory
     * this method lists all files in a directory.
     * 
     * @param folderPath the path of the directory to list files from.
     * @return File[] an array of files in the directory.
     */

    public static File[] listFilesInDirectory(String folderPath) {
        // create a File object for the given directory path
        File directory = new File(folderPath);

        // check if the path is a directory and exists
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles();
            return files;
        } else {
            // return null if the path is not a directory or does not exist
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
        // create a File object for the given directory path
        File directory = new File(directoryPath);
        
        // create a StringBuilder to accumulate the information
        StringBuilder info = new StringBuilder();
        
        if (directory.exists() && directory.isDirectory()) {
            // get directory name
            String directoryName = directory.getName();
            info.append("Directory Name: ").append(directoryName).append("\n");
            
            // calculate total size of files within the directory
            long totalSize = calculateTotalSize(directory);
            info.append("Total Size: ").append(totalSize).append(" bytes\n");
            
            // get last modified date
            long lastModified = directory.lastModified();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastModifiedDate = new Date(lastModified);
            info.append("Date Modified: ").append(dateFormat.format(lastModifiedDate)).append("\n");
            
            // get directory path
            String directoryPathStr = directory.getAbsolutePath();
            info.append("Directory Path: ").append(directoryPathStr).append("\n");
            
            // get directory creation date (requires java.nio.file.Files and java.nio.file.attribute.BasicFileAttributes)
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
     * calculateTotalSize
     * this method calculates the total size of files in a directory.
     * 
     * @param directory the directory to calculate the total size of files from.
     * @return long the total size of files in the directory.
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


    /**
     * renameFolder
     * this method requests the renaming of a folder using the kernel module.
     * 
     * @param srcFolderPath the current path of the folder to rename.
     * @param newName the new name for the folder.
     * @return void
     */

    public static void renameFolder(String srcFolderPath, String newName) {
        
        // prepare the input data by combining srcFolderPath and newName with a newline separator
        String inputData = srcFolderPath + "\n" + newName + "\n";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RENAME_FOLDER_PROC_FILE))) {
            // write the input data to the /proc/rename_folder file
            writer.write(inputData);
            System.out.println("Requested the renaming of folder from '" + srcFolderPath + "' to '" + newName + "'");
        } catch (IOException e) {
            System.err.println("An error occurred while trying to rename the folder: " + e.getMessage());
        }
    }


    /**
     * deleteFolder
     * this method requests the deletion of a folder using the kernel module.
     * 
     * @param folderPath the path of the folder to delete.
     * @return void
     */

    public static void deleteFolder(String folderPath) {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELETE_FOLDER_PROC_FILE))) {
            // Write the folder path you want to delete to the /proc/delete_folder file
            writer.write(folderPath);
            System.out.println("Requested deletion of folder: " + folderPath);
        } catch (IOException e) {
            System.err.println("An error occurred while trying to delete the folder: " + e.getMessage());
        }
    }

    
    /**
     * createFile
     * this method requests the creation of a file using the kernel module.
     * 
     * @param filePath the path of the file to create.
     * @param content the content to write to the file.
     * @return void
     */
    
    public static void createFile(String filePath, String content) {
        
        // combine the file path and content into one string, separated by a newline character
        String inputData = filePath + "\n" + content;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREATE_FILE_PROC_FILE))) {
            writer.write(inputData);
            System.out.println("Data written to " + CREATE_FILE_PROC_FILE);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to " + CREATE_FILE_PROC_FILE + ": " + e.getMessage());
        }
    }


    /**
     * getFileDetails
     * this method retrieves details about a file.
     * 
     * @param filePath the path of the file to get details from.
     * @return String the details of the file.
     */

    public static String getFileDetails(String filePath) {
        // create a File object for the given file path
        File file = new File(filePath);
        
        // create a StringBuilder to accumulate the details
        StringBuilder details = new StringBuilder();
        
        if (file.exists()) {
            // get file name
            String fileName = file.getName();
            details.append("File Name: ").append(fileName).append("\n");
            
            // determine file type (file or directory)
            if (file.isDirectory()) {
                details.append("File Type: Directory\n");
            } else if (file.isFile()) {
                details.append("File Type: File\n");
            }
            
            // get file size in bytes
            long fileSize = file.length();
            details.append("File Size: ").append(fileSize).append(" bytes\n");
            
            // get last modified date
            long lastModified = file.lastModified();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastModifiedDate = new Date(lastModified);
            details.append("Date Modified: ").append(dateFormat.format(lastModifiedDate)).append("\n");
            
            // get file path
            String filePathStr = file.getAbsolutePath();
            details.append("File Path: ").append(filePathStr).append("\n");
            
            // get file creation date (requires java.nio.file.Files and java.nio.file.attribute.BasicFileAttributes)
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

    
    /**
     * readFileContent
     * this method reads the content of a file.
     * 
     * @param filePath the path of the file to read content from.
     * @return String the content of the file.
     * @throws IOException if an I/O error occurs.
     * @return void
     */

    public static String readFileContent(String filePath) throws IOException {
        // use StringBuilder to accumulate the file content
        StringBuilder content = new StringBuilder();
        
        // create a FileReader and BufferedReader to read the file
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            // read the file line by line and append each line to the content
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        
        // return the file content as a string
        return content.toString();
    }

    /**
     * renameFile
     * this method requests the renaming of a file using the kernel module.
     * 
     * @param currentPath the current path of the file to rename.
     * @param newName the new name for the file.
     * @return void
    */

    public static void renameFile(String currentPath, String newName) {
        
        // create the buffer by combining current path and new name with a newline separator
        String buffer = currentPath + "\n" + newName + "\n";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RENAME_FILE_PROC_FILE))) {
            // write the buffer to the /proc/rename_file file
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

    
    /**
     * updateFile
     * this method requests the updating of a file using the kernel module.
     * 
     * @param filePath the path of the file to update.
     * @param newData the new data to write to the file.
     * @param overwriteFlag the flag to determine whether to overwrite the file.
     * @return void
     */

    public static void updateFile(String filePath, String newData, int overwriteFlag) {
        
        // prepare the input data in the format: file_path | new_data | overwrite_flag
        String inputData = filePath + "|" + newData + "|" + overwriteFlag;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UPDATE_FILE_PROC_FILE))) {
            // Write the input data to the /proc/update_file file
            writer.write(inputData);
            System.out.println("Appended line to " + filePath + " using kernel module.");
        } catch (IOException e) {
            System.err.println("An error occurred while updating the file: " + e.getMessage());
        }
    }

    
    /**
     * deleteFile
     * this method requests the deletion of a file using the kernel module.
     * 
     * @param filePath the path of the file to delete.
     * @return void
     */

    public static void deleteFile(String filePath) {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELETE_FILE_PROC_FILE))) {
            // write the file path you want to delete to the /proc/delete_file file
            writer.write(filePath);
            System.out.println("Requested deletion of file: " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while trying to delete the file: " + e.getMessage());
        }
    }
}