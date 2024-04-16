## FM Kernel Module Program

### Overview

The FM Kernel Module Program is a Java application designed to interact with custom kernel modules for file management operations. It facilitates the creation, renaming, and deletion of files and folders through a graphical user interface (GUI) built with JavaFX.

### Running the Program

To run the FM Kernel Module Program, follow these steps:

1. **Compile the Kernel Modules**:
    - Navigate to the directory containing the kernel module code.
    - Run `make` to compile all the kernel modules.

2. **Insert Kernel Modules**:
    - Insert all the compiled kernel modules using the `sudo insmod` command. For example:
        ```bash
        sudo insmod create_file.ko
        sudo insmod create_folder.ko
        ...
        ```

3. **Compile the Java Program**:
    - Compile the Java program using the `javac` command. For example:
        ```bash
        javac FMKernelModules.java Main.java
        ```

4. **Run the Java Program**:
    - Execute the compiled Java program using the `java` command:
        ```bash
        java Main
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
- **`FMKernelModules.java`**: Java source code for the main application.
- **`Main.java`**: Java source code for the main class to run the application.
- **`Makefile`**: Makefile for compiling the kernel modules.
- **`Module.symvers`**: Module symbol versions file.
- **`README.md`**: Readme file providing instructions on how to run the program.
- Other kernel module related files and directories.

### Conclusion

The FM Kernel Module Program offers a seamless integration of user-friendly file management operations with custom kernel modules. By following the provided instructions, users can efficiently compile and run the program to perform various file and folder operations via a simple and intuitive graphical interface.
