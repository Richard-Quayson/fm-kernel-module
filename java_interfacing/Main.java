import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 * Main
 * this class tests the functionalities defined in the FMKernelModules class
 * by providing a menu to the user to choose the operation to perform on the file system
 * 
 * @authors         Richard Quayson & Thomas Quarshie      
 */

public class Main {

    public static void main(String[] args) throws IOException {
        // Create a Scanner object to read input from the user
        Scanner scanner = new Scanner(System.in);
        
        // Loop to allow the user to perform multiple operations
        while (true) {
            // display the menu
            System.out.println("Choose an option:");
            System.out.println("1. Create Folder");
            System.out.println("2. List Files in Directory");
            System.out.println("3. Get Directory Info");
            System.out.println("4. Rename Folder");
            System.out.println("5. Delete Folder");
            System.out.println("6. Create File");
            System.out.println("7. Get File Details");
            System.out.println("8. Rename File");
            System.out.println("9. Update File");
            System.out.println("10. Delete File");
            System.out.println("0. Exit");
            
            // read the user's choice
            int choice = scanner.nextInt();
            scanner.nextLine();             // consume newline character
            
            // exit the program if the user chooses 0
            if (choice == 0) {
                System.out.println("Exiting the program.");
                break;
            }
            
            // Perform the chosen operation
            switch (choice) {
                case 1: // create folder
                    System.out.print("Enter the folder path to create: ");
                    String createFolderPath = scanner.nextLine();
                    FMKernelModules.createFolder(createFolderPath);
                    break;
                    
                case 2: // list files in directory
                    System.out.print("Enter the directory path to list files from: ");
                    String listFolderPath = scanner.nextLine();
                    File[] files = FMKernelModules.listFilesInDirectory(listFolderPath);
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile()) {
                                System.out.println("File: " + file.getName());
                            } else if (file.isDirectory()) {
                                System.out.println("Directory: " + file.getName());
                            }
                        }
                    } else {
                        System.out.println("The specified path is not a valid directory.");
                    }
                    break;
                    
                case 3: // get directory info
                    System.out.print("Enter the directory path to get info from: ");
                    String directoryPath = scanner.nextLine();
                    String directoryInfo = FMKernelModules.getDirectoryInfo(directoryPath);
                    System.out.println(directoryInfo);
                    break;
                    
                case 4: // rename folder
                    System.out.print("Enter the current folder path to rename: ");
                    String oldFolderPath = scanner.nextLine();
                    System.out.print("Enter the new folder name: ");
                    String newFolderName = scanner.nextLine();
                    FMKernelModules.renameFolder(oldFolderPath, newFolderName);
                    break;
                    
                case 5: // delete folder
                    System.out.print("Enter the folder path to delete: ");
                    String deleteFolderPath = scanner.nextLine();
                    FMKernelModules.deleteFolder(deleteFolderPath);
                    break;
                    
                case 6: // create file
                    System.out.print("Enter the file path to create: ");
                    String createFilePath = scanner.nextLine();
                    System.out.print("Enter the file content: ");
                    String createFileContent = scanner.nextLine();
                    FMKernelModules.createFile(createFilePath, createFileContent);
                    break;
                    
                case 7: // get file details
                    System.out.print("Enter the file path to get details from: ");
                    String filePath = scanner.nextLine();
                    String fileDetails = FMKernelModules.getFileDetails(filePath);
                    System.out.println(fileDetails);
                    break;
                    
                case 8: // rename file
                    System.out.print("Enter the current file path to rename: ");
                    String oldFilePath = scanner.nextLine();
                    System.out.print("Enter the new file name: ");
                    String newFileName = scanner.nextLine();
                    FMKernelModules.renameFile(oldFilePath, newFileName);
                    break;
                    
                case 9: // update file
                    System.out.print("Enter the file path to update: ");
                    String updateFilePath = scanner.nextLine();
                    System.out.print("Enter the new data to append (or overwrite): ");
                    String updateFileData = scanner.nextLine();
                    System.out.print("Do you want to overwrite the file? Enter 0 for append and 1 for overwrite: ");
                    int overwriteFlag = scanner.nextInt();
                    scanner.nextLine();             // consume newline character
                    FMKernelModules.updateFile(updateFilePath, updateFileData, overwriteFlag);
                    break;
                    
                case 10: // delete file
                    System.out.print("Enter the file path to delete: ");
                    String deleteFilePath = scanner.nextLine();
                    FMKernelModules.deleteFile(deleteFilePath);
                    break;
                    
                case 11: // read file content
                    System.out.print("Enter the file path to read content from: ");
                    String readFilePath = scanner.nextLine();
                    String fileContent = FMKernelModules.readFileContent(readFilePath);
                    System.out.println("File Content:");
                    System.out.println(fileContent);
                    break;
            
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
                    break;
            }
            
            System.out.println();
        }
        
        // close the scanner
        scanner.close();
    }
}