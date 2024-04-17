#!/bin/bash

# function to list the head of the kernel modules
list_kernel_modules() {
    echo "Listing the head of kernel modules:"
    lsmod | head
}

# change to the kernel module directory
cd fm-kernel-module-code || { echo "Directory not found"; exit 1; }

# build the Makefile
make || { echo "Make command failed"; exit 1; }

# list the kernel modules before inserting
echo "Before inserting kernel modules:"
list_kernel_modules

# insert the kernel modules
sudo insmod create_file.ko
sudo insmod create_folder.ko
sudo insmod delete_file.ko
sudo insmod delete_folder.ko
sudo insmod rename_file.ko
sudo insmod rename_folder.ko
sudo insmod update_file.ko

# list the kernel modules after inserting
echo "After inserting kernel modules:"
list_kernel_modules

# change out of the kernel module directory
cd ..

# change to the java_interfacing directory
cd java_interfacing || { echo "Directory not found"; exit 1; }

# compile the Java programs
javac FMKernelModules.java Main.java || { echo "Compilation failed"; exit 1; }

# run the Java program
java Main || { echo "Java program execution failed"; exit 1; }

echo "Script executed successfully!"
