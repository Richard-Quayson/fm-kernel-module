## FM Kernel Module Program

### Overview

The FM Kernel Module Program is a Java application designed to interact with custom kernel modules for file management operations. It facilitates the creation, renaming, and deletion of files and folders through a graphical user interface (GUI) built with JavaFX.

### Running the Program

To run the FM Kernel Module Program, follow these steps:

1. **Compile the Kernel Modules**:
    - Navigate to the directory containing the kernel module code.
    - Run `make` to compile all the kernel modules.
    - Give permissions to the script files:
        ```bash
        chmod +x run_km.sh
        chmod +x rm_km.sh
        ```

2. **Insert Kernel Modules**:
    - Execute the script to load all kernel modules:
        ```bash
        ./run_km.sh
        ```

3. **Compile the Java Program**:
    - Navigate to the `App Interface` directory.
    - Compile the Java program using the `javac` command. For example:
        ```bash
        javac -cp .:../lib/* controllers/*.java views/*.java
        ```

4. **Run the Java Program**:
    - Execute the compiled Java program using the `java` command:
        ```bash
        java -cp .:../lib/* Main
        ```

5. **Remove Kernel Modules**:
    - Execute the script to remove all kernel modules:
        ```bash
        ./rm_km.sh
        ```

### File Structure

The FM Kernel Module Program directory structure is as follows:

- **`create_file.c`**: Source code for the kernel module to create a file.
- **`create_folder.c`**: Source code for the kernel module to create a folder.
- **`delete_file.c`**: Source code for the kernel module to delete a file.
- **`delete_folder.c`**: Source code for the kernel module to delete a folder.
- **`rename_file.c`**: Source code for the kernel module to rename a file.
- **`rename_folder.c`**: Source code for the kernel module to rename a folder.
- **`update_file.c`**: Source code for the kernel module to update a file.
- **`create_file.ko`**: Compiled kernel module to create a file.
- **`create_folder.ko`**: Compiled kernel module to create a folder.
- **`delete_file.ko`**: Compiled kernel module to delete a file.
- **`delete_folder.ko`**: Compiled kernel module to delete a folder.
- **`rename_file.ko`**: Compiled kernel module to rename a file.
- **`rename_folder.ko`**: Compiled kernel module to rename a folder.
- **`update_file.ko`**: Compiled kernel module to update a file.
- **`FMKernelModules.java`**: Java source code for the main application.
- **`Main.java`**: Java source code for the main class to run the application.
- **`Makefile`**: Makefile for compiling the kernel modules.
- **`Module.symvers`**: Module symbol versions file.
- **`README.md`**: Readme file providing instructions on how to run the program.
- **`App Interface`**: Directory containing the JavaFX application.
- **`run_km.sh`**: Shell script to insert kernel modules.
- **`rm_km.sh`**: Shell script to remove kernel modules.
- Other kernel module related files and directories.

### Requirements for JavaFX

To run the JavaFX application, ensure you have the following requirements installed:

- Java Development Kit (JDK) version 8 or later
- JavaFX SDK
- Ensure that the JavaFX libraries are included in the classpath during compilation and execution.

### YouTube Link

Watch a demonstration of the FM Kernel Module Program on YouTube:
[FM Kernel Module Program Demo](https://youtu.be/pCd2x2DIoj0)

## Repository Link:
https://github.com/Richard-Quayson/fm-kernel-module.git