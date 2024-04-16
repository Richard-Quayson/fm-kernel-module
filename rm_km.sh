#!/bin/bash

echo "Listing the head of kernel modules before removing the specified modules:"
# list the head of kernel modules before removing the specified modules
lsmod | head

# remove the specified kernel modules
sudo rmmod create_file
sudo rmmod create_folder
sudo rmmod delete_file
sudo rmmod delete_folder
sudo rmmod rename_file
sudo rmmod rename_folder
sudo rmmod update_file

echo "Listing the head of kernel modules after removing the specified modules:"
# list the head of kernel modules after removing the specified modules
lsmod | head

echo "Kernel modules removed successfully!"
